package edu.brown.cs.sbelete.databaseconnection;

import java.util.List;

import edu.brown.cs.sbelete.processor.Category;
import edu.brown.cs.sbelete.processor.WatsonResults;
import edu.brown.cs.sblete.articleobjects.Article;

/**
 * DataAccessObject for the main database. Extends DBConnector.
 * 
 * @author DaniRyoo
 *
 */
public class DataAccessObject extends DatabaseConnector {

  public DataAccessObject() {
    setPath("data/preprocess/main.sqlite3"); // hard coded for now
    assert isValidDatabase();
  }

  public void addArticle(String url, boolean real) {
    if (exists(url)) {
      System.out.println("This url already exists in database:" + url);
    } else {
      WatsonResults result;
      Article article = new Article(url);
      try {
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
          int wp = article.getWordPress();

          Object[] params = {url, result.getSentiment(), result.getAnger(),
              result.getJoy(), result.getSadness(), result.getDisgust(),
              result.getFear(), levelsArr[0], levelsArr[1], levelsArr[2],
              levelsArr[3], levelsArr[4], score, wp, t};
          System.out.println(params[0]);
          System.out.println(params[1]);
          System.out.println(params[2]);
          System.out.println(params[3]);
          System.out.println(params[4]);

          SqlType[] types = {SqlType.TEXT, SqlType.DOUBLE, SqlType.DOUBLE,
              SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE,
              SqlType.TEXT, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT,
              SqlType.TEXT, SqlType.DOUBLE, SqlType.INTEGER, SqlType.INTEGER};

          insertQuery(statement, params, types);
        }
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * query the database for average scores across all emotions and sentiment.
   * 
   * @param level1
   *          category level
   * @return result set
   */

  public List<String> getAvg(String level1) {
    // TODO : Return the average scores for articles around
    // this category.
    String query = "SELECT AVG(sentiment), AVG(anger), AVG(joy), AVG(sadness), "
        + "AVG(disgust), AVG(fear) FROM main WHERE c1l1 = ?";
    SqlType[] types = {SqlType.TEXT};
    Object[] params = {level1};
    List<List<String>> result = selectQuery(query, params, types, 6);
    return result.get(0);
  }

  public boolean exists(String url) {
    String query = "SELECT real FROM main WHERE link = ? LIMIT 1";
    SqlType[] types = {SqlType.TEXT};
    Object[] params = {url};
    List<List<String>> result = selectQuery(query, params, types, 1);
    if (result.size() != 0) {
      return true;
    }
    return false;
  }
}
