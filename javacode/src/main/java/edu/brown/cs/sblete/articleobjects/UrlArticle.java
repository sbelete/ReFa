package edu.brown.cs.sblete.articleobjects;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;
import edu.brown.cs.sbelete.databaseconnection.DatabaseConnector;
import edu.brown.cs.sbelete.databaseconnection.SqlType;

public class UrlArticle extends Content {
  private static final String DATA_INSERT = "INSERT INTO main VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?);";
  private static final SqlType[] DATA_INSERT_TYPE = {SqlType.TEXT,
      SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE, SqlType.DOUBLE,
      SqlType.DOUBLE, SqlType.DOUBLE, SqlType.TEXT, SqlType.TEXT, SqlType.TEXT,
      SqlType.TEXT, SqlType.TEXT, SqlType.DOUBLE, SqlType.INTEGER,
      SqlType.INTEGER};

  public UrlArticle(String url) throws Exception {
    super(); // Calls the constructor for Content

    Document doc = Jsoup.connect(url).get();

    String text = getParagraphs(url, doc);
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    messageDigest.update(text.trim().getBytes("UTF-16"));
    setKey(new String(messageDigest.digest()));

    setWordPress(getWordPress(doc));

    fill(text); // Fills in watson_scores, categories, and category_score
    valid(); // Fills in validity_score
  }

  public UrlArticle(String url, int flag) throws Exception {
    super(); // Calls the constructor for Content
    if (DataAccessObject.exists(url)) {
      return;
    }
    Document doc = Jsoup.connect(url).get();

    String text = getParagraphs(url, doc);
    setKey(url);
    setWordPress(getWordPress(doc));

    fill(text); // Fills in watson_scores, categories, and category_score
    store(flag);
  }

  private String getParagraphs(String url, Document doc) throws IOException {
    StringBuilder sb = new StringBuilder();
    Elements paragraphs = doc.select("p");
    for (Element p : paragraphs) {
      if (p.text().split(" ").length > 7) {
        sb.append(p.text());
        sb.append(" ");
      }
    }

    return sb.toString();
  }

  public static int getWordPress(Document doc) {
    if (Pattern.compile("wordpress", Pattern.CASE_INSENSITIVE)
        .matcher(doc.toString()).find()) {
      return 1;
    }

    return 0;
  }

  private void store(int flag) {
    try {
      // Insert the new validity_score into the database
      // Prep before submitting an insert
      List<Double> scores = getWatsonScores();
      Object[] insert_params = {getKey(), scores.get(0), scores.get(1),
          scores.get(2), scores.get(3), scores.get(4), scores.get(5),
          getCategories(0), getCategories(1), getCategories(2),
          getCategories(3), getCategories(4), getCategoryScore(),
          getWordPress(), flag};

      // Performing the insert
      DatabaseConnector.setPath("data/preprocess/main.sqlite3");
      if (DatabaseConnector.isValidDatabase()) {
        DatabaseConnector.insertQuery(DATA_INSERT, insert_params,
            DATA_INSERT_TYPE);
      }

    } catch (Exception e) {
      System.out
          .println("ERROR: Couldn't add url to the database - " + e.toString());
      System.out.println("ERROR: URL -" + getKey());
    }
  };

}
