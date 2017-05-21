package edu.brown.cs.sbelete.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import edu.brown.cs.sbelete.databaseconnection.DatabaseConnector;
import edu.brown.cs.sbelete.databaseconnection.SqlType;

/**
 * Stores and returns blue mix account info is SQL data table.
 * 
 * @author DaniRyoo
 *
 */
public class AccountAccessObject extends DatabaseConnector {

  public AccountAccessObject() {
    setPath("data/watson.sqlite3");
    assert isValidDatabase();
  }

  /**
   * update new account info in csv file to sal file
   * 
   * @param sPath
   */
  public void updateNewCSVFile(String sPath) {
    Path path = Paths.get(sPath);
    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> {
        try {
          insert(line);
        } catch (SQLException e) {
          System.out.println("Warning: This account already exits.");
        }
      });
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void insert(String line) throws SQLException {
    String[] list = line.split(",");
    if (list.length == 2) {
      String statement = "INSERT INTO watson (account, password)  VALUES (?, ?);";
      // params.add(null);
      // types.add(SqlType.INTEGER);
      Object[] params = {list[0], list[1]};
      SqlType[] types = {SqlType.TEXT, SqlType.TEXT};
      insertQuery(statement, params, types);
    }
  }

  public List<List<String>> getAllAccounts() {
    String statement = "SELECT account, password FROM watson";
    String[] params = {};
    SqlType[] types = {};
    List<List<String>> result = selectQuery(statement, params, types, 2);

    return result;
  }

  public List<String> getNextAccount(String account) {
    List<String> toReturn;
    int id = 1;
    if (account != null) {
      String statement = "SELECT id FROM watson WHERE account =?";
      String[] params = {account};
      SqlType[] types = {SqlType.TEXT};
      List<List<String>> result = selectQuery(statement, params, types, 1);
      id = Integer.parseInt(result.get(0).get(0));
      String statement2 = "SELECT COUNT(account) FROM watson";
      Object[] params2 = {};
      SqlType[] types2 = {};
      List<List<String>> result2 = selectQuery(statement2, params2, types2, 1);
      int size = Integer.parseInt(result2.get(0).get(0));
      id = (id % size) + 1;
    }
    String statement3 = "SELECT account, password FROM watson WHERE id =?";
    Object[] params3 = {id};
    SqlType[] types3 = {SqlType.INTEGER};
    List<List<String>> result3 = selectQuery(statement3, params3, types3, 2);
    toReturn = result3.get(0);
    return toReturn;
  }
}
