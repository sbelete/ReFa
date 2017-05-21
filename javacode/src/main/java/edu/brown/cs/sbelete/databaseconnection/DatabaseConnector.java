package edu.brown.cs.sbelete.databaseconnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will be able to add and retrieve values from our database.
 * 
 * @author dthuku
 *
 */
public class DatabaseConnector {

  private static Connection connection = null;

  public DatabaseConnector() {
  }

  /**
   * 
   * @return validity of the db connection
   */
  public static boolean validConn() {
    return (connection != null);
  }

  /**
   * sets the path to a db.
   * 
   * @param databasePath
   *          path to db
   */
  public static void setPath(String databasePath) {

    try {
      Class.forName("org.sqlite.JDBC");

      String urlToDb = "jdbc:sqlite:" + databasePath;
      try {

        connection = DriverManager.getConnection(urlToDb);
        // these two lines tell the database to enforce foreign
        // keys during operations, and should be present
        Statement stat = connection.createStatement();
        stat.executeUpdate("PRAGMA foreign_keys = ON;");

      } catch (SQLException e) {
        connection = null;
      }
    } catch (ClassNotFoundException e) {
      connection = null;

    }
  }

  /**
   * 
   * @return validity of the db
   */
  public static boolean isValidDatabase() {
    return connection != null;
  }

  /**
   * resets a db
   */
  public static void resetDatabase() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        connection = null;
      }
    }
  }

  /**
   * handles all select queries.
   * 
   * @param statement
   *          query
   * @param params
   *          to be queried
   * @param types
   *          data types in params
   * @param numCols
   *          to be returned
   * @return list of stringified results
   */
  public static List<List<String>> selectQuery(String statement,
      Object[] params, SqlType[] types, int numCols) {
    if (connection != null) {
      try (PreparedStatement prep = connection.prepareStatement(statement)) {
        for (int i = 0; i < params.length; i++) {
          setPrep(i + 1, prep, types[i], params[i]);
        }

        try (ResultSet rs = prep.executeQuery()) {
          List<List<String>> resultSetItems = new ArrayList<List<String>>();
          while (rs.next()) {
            List<String> currentItemAsList = new ArrayList<String>();
            int currentCol = 1; // set to 1, as only one column
            // matters
            while (currentCol <= numCols) {
              currentItemAsList.add(rs.getString(currentCol));
              currentCol++;
            }

            resultSetItems.add(currentItemAsList);
          }

          return resultSetItems;
        }

      } catch (SQLException e) {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * handles all insert queries.
   * 
   * @param statement
   *          query
   * @param params
   *          to be queried
   * @param types
   *          data types in params to be returned
   */
  public static void insertQuery(String statement, Object[] params,
      SqlType[] types) throws SQLException {
    if (connection != null) {
      PreparedStatement prep = connection.prepareStatement(statement);
      for (int i = 0; i < params.length; i++) {
        setPrep(i + 1, prep, types[i], params[i]);
      }
      prep.addBatch();
      prep.executeBatch();
    }
  }

  public static void replaceQuery(String statement, Object[] params,
      SqlType[] types) throws SQLException {
    if (connection != null) {
      PreparedStatement prep = connection.prepareStatement(statement);
      for (int i = 0; i < params.length; i++) {
        setPrep(i + 1, prep, types[i], params[i]);
      }
      prep.executeUpdate();
    }
  }

  /**
   * sets prepared statements.
   * 
   * @param i
   *          prep num
   * @param prep
   *          prepared statement
   * @param type
   *          sqltype
   * @param o
   *          object
   * @throws SQLException
   *           exception
   */
  private static void setPrep(int i, PreparedStatement prep, SqlType type,
      Object o) throws SQLException {
    switch (type) {
      case TEXT:
        prep.setString(i, (String) o);
        break;
      case DOUBLE:
        prep.setDouble(i, (Double) o);
        break;
      case INTEGER:
        prep.setInt(i, (Integer) o);
        break;
      default :
        break;
    }
  }

  /**
   * creates table.
   * 
   * @param statement
   *          statement
   * @throws IOException
   *           exception
   * @throws SQLException
   *           exeption
   */
  public void buildTable(String statement) throws IOException, SQLException {
    try {
      PreparedStatement prep;
      prep = connection.prepareStatement(statement);
      prep.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
