package edu.brown.cs.sbelete.processor;

import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.ibm.watson.developer_cloud.service.exception.ForbiddenException;

/**
 * connects to the Watson api and analyzes input text.
 * 
 * @author daltonleshan
 *
 */
public class LanguageProcessor {

  private static AccountAccessObject aao;

  private static EmotionOptions emotion;
  private static CategoriesOptions categories;
  private static SentimentOptions sentiments;
  private static Features features;

  private String account;
  private String password;

  private static List<NaturalLanguageUnderstanding> servicePool = new ArrayList<>();

  /**
   * constructor.
   */
  public static void create() {
    aao = new AccountAccessObject();

    emotion = new EmotionOptions.Builder().build();
    categories = new CategoriesOptions();
    sentiments = new SentimentOptions.Builder().build();
    features = new Features.Builder().emotion(emotion).categories(categories)
        .sentiment(sentiments).build();

    AnalyzeOptions parameters = new AnalyzeOptions.Builder().text("test")
        .features(features).build();

    NaturalLanguageUnderstanding temp;
    for (List<String> usr_pwd : aao.getAllAccounts()) {
      temp = new NaturalLanguageUnderstanding(
          NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27, usr_pwd.get(0),
          usr_pwd.get(1));
      try {
        temp.analyze(parameters).execute();
        servicePool.add(temp);
      } catch (ForbiddenException e) {
        System.out
            .println("ERROR: Not added user and password " + e.toString());
      }
    }
    System.out.println(servicePool.size());
    // service = new NaturalLanguageUnderstanding(
    // NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27, null, null);
    // service = new NaturalLanguageUnderstanding(
    // NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
    // "8a7b4a5d-305c-4873-94b4-a617f0545cd3", "0i8UgVqCZG33");
    // account = null;
    // aao = new AccountAccessObject();
    // updateAccount();

  }

  static int count = 0;

  /**
   * analyzes given text and returns its attributes
   * 
   * @param paragraph
   *          string
   * @return attributes for the string
   */
  public static WatsonResults analyzeText(String paragraph) throws Exception {
    AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(paragraph)
        .features(features).returnAnalyzedText(true).build();
    count++;
    try {
      AnalysisResults results = servicePool.get(0).analyze(parameters)
          .execute();
      System.out
          .println(results.getEmotion().getDocument().getEmotion().getAnger());
      return new WatsonResults(results);
    } catch (ForbiddenException e) {
      servicePool.remove(0);
      return LanguageProcessor.analyzeText(paragraph);
    } catch (NullPointerException e) {
      System.out.println("ERROR: Out of working acccounts " + e.toString());
      return null;
    }

  }

  /**
   * analyzes given article and returns its attributes
   * 
   * @param url
   *          string
   * @return attributes for the article
   */
  public static WatsonResults analyzeUrl(String url) throws Exception {
    WatsonResults toReturn = null;
    try {
      AnalyzeOptions parameters = new AnalyzeOptions.Builder().url(url)
          .features(features).build();
      AnalysisResults results = servicePool.get(0).analyze(parameters)
          .execute();
      toReturn = new WatsonResults(results);
    } catch (ForbiddenException e) {
      e.printStackTrace();
      System.out.println("Limit reached");
      // updateAccount();
    }
    return toReturn;
  }

  /*
   * private static void updateAccount() { aao = new AccountAccessObject();
   * List<String> next = aao.getNextAccount(account); account = next.get(0);
   * password = next.get(1); service = new NaturalLanguageUnderstanding(
   * NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27, account, password);
   * System.out.println("account changed to " + account + " " + password); }
   */
}
