package edu.brown.cs.sbelete.dataanalysis;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;
import edu.brown.cs.sbelete.databaseconnection.DatabaseConnector;
import edu.brown.cs.sblete.articleobjects.Article;
import edu.brown.cs.sblete.articleobjects.Content;
import edu.brown.cs.sblete.articleobjects.Paragraph;
import spark.resource.ClassPathResource;

public class ModelActivator {

	final static String[] args = { "--vanilla" };
	final static Rengine rengine = new Rengine(args, false, null);

	final static String CURRENT_WORKING_DIRECTORY = System.getProperty("user.dir");

	/**
	 * This method generates a R model object to be used on preprocessed data to
	 * predict whether future articles are fake or real
	 */
	public static void generateModel() {

		// get the r script
		ClassPathResource rScript = new ClassPathResource("/rscripts/generateModel.R");

		try {
			rengine.eval(String.format("wd <- '%s'", CURRENT_WORKING_DIRECTORY + "/data/preprocess/main.sqlite3"));
			rengine.eval(String.format("source('%s')", rScript.getFile().getAbsolutePath()));
			System.out.println("Done Making the Model");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// returns probability it is real
	public static Double predict(Article a) {
		truncateTable();
		String uniqueKey = "key";
		// add to the table to predict.sqlite3 to be accessed from R
		DataAccessObject dao = new DataAccessObject("predict.sqlite3");

		dao.addArticleToSql(a, uniqueKey, false);

		// get the r script
		ClassPathResource rScript = new ClassPathResource("/rscripts/predict.R");
		try {
			rengine.eval(String.format("wd <- '%s'", CURRENT_WORKING_DIRECTORY + "/predict.sqlite3"));
			rengine.eval(String.format("link <- '%s'", uniqueKey));
			rengine.eval(String.format("source('%s')", rScript.getFile().getAbsolutePath()));
			REXP result = rengine.eval("real");
			System.out.println("Result: " + result.asDouble());

			return result.asDouble();
			// return result.asDouble();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1.0;

	}

	// returns probability it is real
	public static synchronized Double predict(Content c) {
		synchronized (ModelActivator.class) {
			truncateTable();
			String uniqueKey = "key";
			// add to the table to predict.sqlite3 to be accessed from R
			DataAccessObject dao = new DataAccessObject("predict.sqlite3");

			dao.addArticleToSql(c, uniqueKey, false);

			// get the r script
			ClassPathResource rScript = new ClassPathResource("/rscripts/predict.R");
			try {
				rengine.eval(String.format("wd <- '%s'", CURRENT_WORKING_DIRECTORY + "/predict.sqlite3"));
				rengine.eval(String.format("link <- '%s'", uniqueKey));
				rengine.eval(String.format("source('%s')", rScript.getFile().getAbsolutePath()));
				REXP result = rengine.eval("real");
				System.out.println("Result: " + result.asDouble());

				return result.asDouble();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return -1.0;
		}
	}

	public static void truncateTable() {
		Connection connection;
		try {
			Class.forName("org.sqlite.JDBC");

			String urlToDb = "jdbc:sqlite:" + "predict.sqlite3";
			try {

				connection = DriverManager.getConnection(urlToDb);
				// these two lines tell the database to enforce foreign
				// keys during operations, and should be present
				Statement stat = connection.createStatement();
				stat.executeUpdate("DELETE from main");

			} catch (SQLException e) {
				System.out.println(e);
				connection = null;
			}
		} catch (ClassNotFoundException e) {
			connection = null;

		}
	}

}
