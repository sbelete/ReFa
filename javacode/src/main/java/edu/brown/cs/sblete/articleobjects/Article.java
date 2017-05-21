package edu.brown.cs.sblete.articleobjects;

import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.brown.cs.sbelete.processor.LanguageProcessor;
import edu.brown.cs.sbelete.processor.WatsonResults;

/**
 * Article wrapper to parse the paragraph and get wordPress.
 *
 * @author DaniRyoo
 *
 */
public class Article {

  private String url;
  private WatsonResults watsonResults;
  private int wordPress;
  private String body;
  private Document doc;

  /**
   * constructor.
   *
   *
   * @param sUrl
   *          article link
   */
  public Article(String sUrl) {
    url = sUrl;
    try {
      doc = Jsoup.connect(url).timeout(30000).get();
      wordPress = findWordPress();
      body = getParagraphs();
      watsonResults = LanguageProcessor.analyzeText(body);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getWordPress() {
    return wordPress;
  }

  /**
   * gets paragraphs in this article.
   *
   * @param url
   *          article link
   * @return paragraph string
   */
  public String getParagraphs() {
    String toReturn = "";
    Elements paragraphs = doc.select("p");
    for (Element p : paragraphs) {
      if (p.text().split(" ").length > 7) {
        toReturn += p.text();
      }
    }
    return toReturn;
  }

  public String getBody() {
    if (body == null) {
      body = getParagraphs();
    }
    return body;
  }

  /**
   * Getter for WatsonResutls
   *
   * @return WatsonResults for the article
   */
  public WatsonResults getWatsonResults() {
    return watsonResults;
  }

  /**
   * @return array of average score
   */
  public List<Double> getAvg() {
    return this.watsonResults.getCategories().get(0).getAvgScores();
  }

  /**
   * getter for wordpress value
   *
   * @return integer
   */
  public int findWordPress() {
    if (Pattern.compile("wordpress", Pattern.CASE_INSENSITIVE)
        .matcher(doc.toString()).find()) {
      return 1;
    }
    return 0;
  }

  /**
   * gets the category that this article falls under.
   *
   * @return category
   */
  public String getCategory() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   *
   * @return watson results
   */
  public Object getScores() {
    return this.watsonResults.getAll();
  }

  /**
   *
   * @return url for this article
   */
  public String getUrl() {
    return url;
  }

}
