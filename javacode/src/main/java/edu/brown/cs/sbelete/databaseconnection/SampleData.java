package edu.brown.cs.sbelete.databaseconnection;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

/**
 * Test class to build initial database and load initial data
 * 
 * @author DaniRyoo
 *
 */
public class SampleData {
  @Test
  public void buildSampleData() throws IOException, SQLException {
    Preprocessor pp = new Preprocessor();
    String statement = "CREATE TABLE IF NOT EXISTS main (" + "link TEXT,"
        + "sentiment DOUBLE," + "anger DOUBLE," + "joy DOUBLE,"
        + "sadness DOUBLE," + "disgust DOUBLE," + "fear DOUBLE," + "c1l1 TEXT,"
        + "c1l2 TEXT," + "c1l3 TEXT," + "c1l4 TEXT," + "c1l5 TEXT,"
        + "c1score DOUBLE," + "wordpress INTEGER," + "real INTEGER,"
        + "PRIMARY KEY (link));";
    DataAccessObject dao = new DataAccessObject();
    dao.buildTable(statement);
  }
}
