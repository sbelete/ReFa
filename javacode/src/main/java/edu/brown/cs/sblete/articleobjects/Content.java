package edu.brown.cs.sblete.articleobjects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

import edu.brown.cs.sbelete.dataanalysis.ModelActivator;
import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;
import edu.brown.cs.sbelete.databaseconnection.DatabaseConnector;
import edu.brown.cs.sbelete.databaseconnection.SqlType;
import edu.brown.cs.sbelete.processor.Category;
import edu.brown.cs.sbelete.processor.LanguageProcessor;
import edu.brown.cs.sbelete.processor.WatsonResults;

public class Content {
	private int wordPress;
	private String key; // encrypted key for storage in database
	private List<Double> watson_scores; // watson emotion scores
	private List<String> categories; // categories of article
	private double category_score; // score of category and article closeness
	private double validity_score; // confidence that the article is valid

	private final String VALID_QUERY = "SELECT validity FROM temp_valid_scores WHERE key = ?";
	private final String VALID_INSERT = "INSERT INTO temp_valid_scores VALUES (?, ?);";

	private final String WATSON_QUERY = "SELECT sentiment, anger, joy, sadness, "
			+ "disgust, fear, category_1, category_2, category_3, category_4, category_5, "
			+ "category_score, wp FROM cached_watson_results WHERE key == ?;";
	private final String WATSON_INSERT = "INSERT INTO cached_watson_results VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?);";
	private final SqlType[] WATSON_INSERT_TYPES = { SqlType.TEXT, SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE,
			SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT,
			SqlType.TEXT, SqlType.DOUBLE, SqlType.INTEGER };

	public Content() throws Exception {
		watson_scores = new ArrayList<>();
		categories = new ArrayList<>();
		// Use this before database interactions
		// TODO: DatabaseConnector.isValidDatabase();
		DatabaseConnector.setPath("data/user_articles.sqlite3");
	}

	protected void fill(String body) throws Exception {
		// Precautionary catch statement to allow functionality in the case of
		// malfunctioning database
		try {
			List<String> query = query();
			// Database has the article so fill in the private variables
			if (query != null) {

				// Iterate through and set all of the emotion scores
				for (int i = 0; i < 6; i++) {
					watson_scores.add(Double.parseDouble(query.get(i)));
				}

				// Iterate through and set all of the categories
				for (int i = 6; i < 11; i++) {
					categories.add(query.get(i));
				}

				// Set the category score
				category_score = Double.parseDouble(query.get(11));

				// Set the wordPress (don't really need to do this)
				wordPress = Integer.parseInt(query.get(12));

				return; // Return since the query contained a result
			}

		} catch (Exception e) {
			System.out.println("ERROR: Query resulted in  - " + e.toString());
		}

		// Use IBM Watson to get the results for the text
		WatsonResults temp_wr = LanguageProcessor.analyzeText(body);

		if (temp_wr == null) {
			throw new Exception();
		}

		// Similar to above set watson results to the appropriate variables
		watson_scores = temp_wr.getAll();

		// Set the categories from the watson results
		try {
			Category temp_c = temp_wr.getCategories().get(0);
			categories = temp_c.getLevels();
			// Set the category_score from the watson results
			category_score = temp_c.getScore();
		} catch (Exception e) {
			// Set the category_score from the watson results
			category_score = 0.0;
		}

		// Precautionary catch statement to allow functionality in the case of
		// malfunctioning database
		try {
			insert(); // Insert the results into the database
		} catch (Exception e) {
			System.out.println("ERROR: Insert resulted in - " + e.toString());
		}
	}

	public String getCategories(int i) {
		// Allows for iteration over categories without worry of out of bounds
		// exception
		try {
			return categories.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	private void insert() throws SQLException {
		// Prep before submitting an insert
		Object[] params = { key, watson_scores.get(0), watson_scores.get(1), watson_scores.get(2), watson_scores.get(3),
				watson_scores.get(4), watson_scores.get(5), getCategories(0), getCategories(1), getCategories(2),
				getCategories(3), getCategories(4), category_score, wordPress };

		// Performing the insert

		DatabaseConnector.setPath("data/user_articles.sqlite3");
		if (DatabaseConnector.isValidDatabase()) {
			DatabaseConnector.insertQuery(WATSON_INSERT, params, WATSON_INSERT_TYPES);
		}
	}

	private List<String> query() {
		// Prep before submitting a query
		SqlType[] types = { SqlType.TEXT };
		Object[] params = { key };

		// Performing the query

		List<List<String>> result;
		DatabaseConnector.setPath("data/user_articles.sqlite3");
		if (DatabaseConnector.isValidDatabase()) {
			result = DatabaseConnector.selectQuery(WATSON_QUERY, params, types, 13);
		} else {
			result = null;
		}
		// Check to see if key resulted in any return
		if (result != null && !result.isEmpty()) {
			// return the the data for the key in the database
			return result.get(0);
		}

		return null; // The key isn't in the database
	}

	protected void valid() {
		try {
			// Prep before submitting a query
			SqlType[] query_types = { SqlType.TEXT };
			Object[] query_params = { key };
			List<List<String>> result;

			DatabaseConnector.setPath("data/user_articles.sqlite3");
			if (DatabaseConnector.isValidDatabase()) {
				result = DatabaseConnector.selectQuery(VALID_QUERY, query_params, query_types, 1);
			} else {
				result = null;
			}
			// Check to see if key resulted in any return
			if (result != null && !result.isEmpty()) {
				// set validity score if key is in the database
				validity_score = Double.parseDouble(result.get(0).get(0));
				System.out.println(validity_score);
				return; // Return since the query contained a result
			}
		} catch (Exception e) {
			System.out.println("ERROR: Query for valid() resulted in  - " + e.toString());
		}

		// If the key isn't in the database
		try {
			validity_score = ModelActivator.predict(this);

			// Insert the new validity_score into the database
			// Prep before submitting an insert
			SqlType[] insert_types = { SqlType.TEXT, SqlType.DOUBLE };
			Object[] insert_params = { key, validity_score };
			System.out.println(validity_score);
			// Performing the insert
			DatabaseConnector.setPath("data/user_articles.sqlite3");
			if (DatabaseConnector.isValidDatabase()) {
				System.out.println("HERE");
				DatabaseConnector.insertQuery(VALID_INSERT, insert_params, insert_types);
				System.out.println("OUT");
			}
		} catch (Exception e) {
			System.out.println("ERROR: Insert for valid() resulted in  - " + e.toString());
		}
	}

	public String getKey() {
		return key;
	}

	public int getWordPress() {
		return wordPress;
	}

	public List<Double> getWatsonScores() {
		return ImmutableList.copyOf(watson_scores);
	}

	public List<String> getCategories() {
		return ImmutableList.copyOf(categories.stream().filter(Predicates.notNull()).iterator());
	}

	public double getCategoryScore() {
		return category_score;
	}

	public double getValidityScore() {
		return validity_score;
	}

	public List<Double> getAvg() {
		DataAccessObject dao = new DataAccessObject();

		try {
			List<String> averages = dao.getAvg(getCategories(0));
			return ImmutableList.of(Double.parseDouble(averages.get(0)), Double.parseDouble(averages.get(1)),
					Double.parseDouble(averages.get(2)), Double.parseDouble(averages.get(3)),
					Double.parseDouble(averages.get(4)), Double.parseDouble(averages.get(5)));
		} catch (Exception e) {
			System.out.println("ERROR: Getting Average resulted in - " + e.toString());
			return ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	// public void setValidityScore(double vs) {
	// this.validity_score = vs;
	// }

	public void setWordPress(int wp) {
		this.wordPress = wp;
	}
}
