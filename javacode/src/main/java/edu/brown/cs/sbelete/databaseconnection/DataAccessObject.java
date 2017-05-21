package edu.brown.cs.sbelete.databaseconnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import edu.brown.cs.sbelete.processor.Category;
import edu.brown.cs.sbelete.processor.LanguageProcessor;
import edu.brown.cs.sbelete.processor.WatsonResults;
import edu.brown.cs.sblete.articleobjects.Article;
import edu.brown.cs.sblete.articleobjects.Content;
import edu.brown.cs.sblete.articleobjects.UrlArticle;

/**
 * DataAccessObject for the main database. Extends DBConnector.
 * 
 *
 */
public class DataAccessObject extends DatabaseConnector {

	private LanguageProcessor lp;

	public DataAccessObject() {
		lp = new LanguageProcessor();
		setPath("data/preprocess/main.sqlite3"); // hard coded for now
		assert isValidDatabase();
		createReportsTable();
	}

	public DataAccessObject(String path) {
		lp = new LanguageProcessor();
		setPath(path);
		assert isValidDatabase();
		createReportsTable();
	}

	public void updatePath(String path) {
	}

	public void addArticleToSql(Article article, String url, boolean real) {
		try {
			WatsonResults result;

			result = article.getWatsonResults();
			if (result != null) {
				String statement = "INSERT INTO main VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

				List<Category> categories = result.getCategories();
				if (categories.isEmpty()) {
					return;
				}

				Category one = categories.get(0);
				List<String> levels = one.getLevels();
				String[] levelsArr = new String[5];
				for (int i = 0; i < Math.min(5, levels.size()); i++) {
					levelsArr[i] = levels.get(i);
				}

				Double score = one.getScore();
				int t = 0;
				if (!real) {
					t = 1;
				}
				int wp = 0;

				Object[] params = { url, result.getSentiment(),
						result.getAnger(), result.getJoy(),
						result.getSadness(), result.getDisgust(),
						result.getFear(), levelsArr[0], levelsArr[1],
						levelsArr[2], levelsArr[3], levelsArr[4], score, wp, t };

				SqlType[] types = { SqlType.TEXT, SqlType.DOUBLE,
						SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE,
						SqlType.DOUBLE, SqlType.DOUBLE, SqlType.TEXT,
						SqlType.TEXT, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT,
						SqlType.DOUBLE, SqlType.INTEGER, SqlType.INTEGER };

				DatabaseConnector.insertQuery(statement, params, types);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addArticle(String url, boolean real) {
		if (exists(url)) {
			System.out.println("This url already exists in database:" + url);
		} else {
			Article article = new Article(url);
			addArticleToSql(article, url, real);
		}
	}

	/**
	 * query the database for average scores across all emotions and sentiment.
	 * 
	 * @param level1
	 *            category level
	 * @return result set
	 */
	public List<String> getAvg(String level1) {
		// TODO : Return the average scores for articles around this category
		String query = "SELECT AVG(sentiment), AVG(anger), AVG(joy), AVG(sadness), "
				+ "AVG(disgust), AVG(fear) FROM main WHERE c1l1 = ? AND real = 0;";
		SqlType[] types = { SqlType.TEXT };
		Object[] params = { level1 };
		List<List<String>> result = selectQuery(query, params, types, 6);
		return result.get(0);
	}

	/**
	 * creates table for reported links.
	 */
	private void createReportsTable() {
		String statement = "CREATE TABLE IF NOT EXISTS reports ( link TEXT, sentiment DOUBLE, "
				+ "anger DOUBLE, joy DOUBLE, sadness DOUBLE, disgust DOUBLE, fear DOUBLE, "
				+ "c1l1 TEXT, c1l2 TEXT, c1l3 TEXT, c1l4 TEXT, c1l5 TEXT, c1score DOUBLE, "
				+ "wordpress INTEGER, numReports INTEGER, PRIMARY KEY (link));";
		try {
			buildTable(statement);
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * inserts a reported link into our reported links database
	 * 
	 * @param url
	 *            link to insert
	 */
	/*
	public static void reportLink(String url, List<Object> values) {
		assert (values.size() == 14) : "incorrect number of values to be inserted into reports table";
		String statement = "INSERT INTO reports VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		Object[] params = { url, values.get(0), values.get(1), values.get(2),
				values.get(3), values.get(4), values.get(5), values.get(6),
				values.get(7), values.get(8), values.get(9), values.get(10),
				values.get(11), values.get(12), values.get(13), 0 };
		SqlType[] types = { SqlType.TEXT, SqlType.DOUBLE, SqlType.DOUBLE,
				SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE,
				SqlType.TEXT, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT,
				SqlType.TEXT, SqlType.DOUBLE, SqlType.INTEGER, SqlType.INTEGER };
		try {
			int t = checkThreshold(url);
			if (t < 20) {
				params[14] = t + 1;
				DatabaseConnector.insertQuery(statement, params, types);
			} else {
				addReportedLink(url, params, types);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * inserts a reported link into our reported links database
	 * 
	 * @param url
	 *            link to insert
	 */
	public static void reportLink(String url, boolean rating) {
		DatabaseConnector.setPath("data/user_articles.sqlite3");
		if (!DatabaseConnector.isValidDatabase()) {
			return;
		}
		Object[] query_param = { url };
		SqlType[] query_type = { SqlType.TEXT };
		List<List<String>> result = DatabaseConnector.selectQuery(
				"SELECT true,false FROM report_article WHERE key = ?;",
				query_param, query_type, 2);
		int truth = 0;
		int fake = 0;

		if (rating) {
			truth += 1;
		} else {
			fake += 1;
		}

		if (result != null && !result.isEmpty()&& result.get(0).get(0) != null
				&& !result.get(0).get(0).isEmpty()) {
			if (rating) {
				truth = Integer.parseInt(result.get(0).get(1)) + 1;
				fake = Integer.parseInt(result.get(0).get(2));
			} else {
				truth = Integer.parseInt(result.get(0).get(1));
				fake = Integer.parseInt(result.get(0).get(2) + 1);
			}
		}
		Object[] insert_params = { url, truth, fake };
		SqlType[] insert_types = { SqlType.TEXT, SqlType.INTEGER,
				SqlType.INTEGER };
		

		try {
			if (truth - fake > 20) {
				new UrlArticle(url, 0);
			} else if (fake - truth > 20) {
				new UrlArticle(url, 1);
			}
		} catch (Exception e) {
			System.out.println("ERROR: Adding url to database - "
					+ e.toString());
		}

		DatabaseConnector.setPath("data/user_articles.sqlite3");
		if (!DatabaseConnector.isValidDatabase()) {
			return;
		}
		try {
			DatabaseConnector.insertQuery(
					"INSERT OR REPLACE INTO report_article VALUES(?,?,?);",
					insert_params, insert_types);
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	/**
	 * checks the number of times this article url has been reported
	 * 
	 * @param url
	 *            whose threshold to check
	 * @return number of times url has been reported
	 */
	private static int checkThreshold(String url) {
		String query = "SELECT numReports FROM reports WHERE link = ?;";
		SqlType[] types = { SqlType.TEXT };
		Object[] params = { url };
		List<String> result = selectQuery(query, params, types, 1).get(0);
		if (result.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(result.get(0));
	}

	public static void replaceWordpress() throws SQLException, IOException {
		String query = "SELECT link FROM main";
		SqlType[] types = {};
		Object[] params = {};
		List<List<Object>> wp = new ArrayList<>();
		List<List<String>> result = selectQuery(query, params, types, 1);
		SqlType[] types2 = { SqlType.INTEGER, SqlType.TEXT };
		String statement = "UPDATE main SET wordpress = ? " + "WHERE link = ?";
		int c = 3653;
		String link;
		int wop;
		for (int i = 16843; i < result.size(); i++) {
			link = result.get(i).get(0);
			wop = UrlArticle.getWordPress(Jsoup.connect(link).timeout(0).get());
			Object[] params2 = { wop, link };
			//replaceQuery(statement, params2, types2);
			System.out.println(i + ": " + wop + " " + link);
		}
	}

	/**
	 * adds reported link to the main database
	 * 
	 * @param url
	 *            to add to main
	 * @param params
	 *            scores of the url article
	 * @param types
	 *            types of data
	 * @throws SQLException
	 *             execption
	 */
	private static void addReportedLink(String url, Object[] params,
			SqlType[] types) throws SQLException {
		String statement = "INSERT INTO main VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		params[14] = 1;// fake
		DatabaseConnector.insertQuery(statement, params, types);
	}

	public void addArticleToSql(Content c, String uniqueKey, boolean real) {
		try {

			String statement = "INSERT INTO main VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

			/*
			 * if (c.getCategories().isEmpty()) { return; }
			 * 
			 * 
			 * Category one = categories.get(0); List<String> levels =
			 * one.getLevels(); String[] levelsArr = new String[5]; for (int i =
			 * 0; i < Math.min(5, levels.size()); i++) { levelsArr[i] =
			 * levels.get(i); }
			 */

			int t = 0;
			if (!real) {
				t = 1;
			}

			List<Double> alod = c.getWatsonScores();
			Object[] params = { uniqueKey, alod.get(0), alod.get(1),
					alod.get(2), alod.get(3), alod.get(4), alod.get(5),
					c.getCategories(0), c.getCategories(1), c.getCategories(2),
					c.getCategories(3), c.getCategories(4),
					c.getCategoryScore(), c.getWordPress(), t };

			SqlType[] types = { SqlType.TEXT, SqlType.DOUBLE, SqlType.DOUBLE,
					SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE,
					SqlType.DOUBLE, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT,
					SqlType.TEXT, SqlType.TEXT, SqlType.DOUBLE,
					SqlType.INTEGER, SqlType.INTEGER };

			DatabaseConnector.insertQuery(statement, params, types);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean exists(String url) {
		String query = "SELECT real FROM main WHERE link = ? LIMIT 1";
		SqlType[] types = { SqlType.TEXT };
		Object[] params = { url };
		List<List<String>> result = selectQuery(query, params, types, 1);
		if (result.size() != 0) {
			return true;
		}
		return false;
	}
}
