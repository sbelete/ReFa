package edu.brown.cs.sbelete.dataminer;

import edu.brown.cs.sbelete.databaseconnection.DatabaseConnector;

/**
 * Return or update keys for different sources.
 * 
 * @author DaniRyoo
 *
 */
public class KeyAccessObject extends DatabaseConnector {
  public KeyAccessObject() {
    setPath("data/preprocess/keys.sqlite3");

  }
}
