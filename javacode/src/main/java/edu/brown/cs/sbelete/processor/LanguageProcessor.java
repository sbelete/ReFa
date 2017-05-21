package edu.brown.cs.sbelete.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
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

  public static void create(Collection<List<String>> accounts) {
    aao = new AccountAccessObject();
    emotion = new EmotionOptions.Builder().build();
    categories = new CategoriesOptions();
    sentiments = new SentimentOptions.Builder().build();
    features = new Features.Builder().emotion(emotion).categories(categories)
        .sentiment(sentiments).build();

    AnalyzeOptions parameters = new AnalyzeOptions.Builder().text("test")
        .features(features).build();
    NaturalLanguageUnderstanding temp;

    for (List<String> usr_pwd : accounts) {
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
    if (servicePool.isEmpty()) {
      System.out.println("ERROR: No accounts have any queries left");
    } else {
      System.out.println("Number of working services: " + servicePool.size());
    }
  }

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
    if (servicePool.isEmpty()) {
      System.out.println("ERROR: No accounts have any queries left");
    } else {
      System.out.println("Number of working services: " + servicePool.size());
    }

  }

  public static void reload(Collection<List<String>> accounts) {
    // aao = new AccountAccessObject();
    // emotion = new EmotionOptions.Builder().build();
    // categories = new CategoriesOptions();
    // sentiments = new SentimentOptions.Builder().build();
    // features = new
    // Features.Builder().emotion(emotion).categories(categories).sentiment(sentiments).build();
    servicePool.clear();
    AnalyzeOptions parameters = new AnalyzeOptions.Builder().text("test")
        .features(features).build();
    NaturalLanguageUnderstanding temp;

    for (List<String> usr_pwd : accounts) {
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
    if (servicePool.isEmpty()) {
      System.out.println("ERROR: No accounts have any queries left");
    } else {
      System.out.println("Number of working services: " + servicePool.size());
    }
  }

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
    try {
      AnalysisResults results = servicePool.get(0).analyze(parameters)
          .execute();
      System.out
          .println(results.getEmotion().getDocument().getEmotion().getAnger());
      return new WatsonResults(results);
    } catch (ForbiddenException e) {
      servicePool.remove(0);
      return LanguageProcessor.analyzeText(paragraph);
    } catch (NullPointerException | IndexOutOfBoundsException e) {
      System.out.println("ERROR: Out of working acccounts " + e.toString());
      return null;
    } catch (BadRequestException e) {
      System.out.println("ERROR: Bad Request Exception");
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

    AnalyzeOptions parameters = new AnalyzeOptions.Builder().url(url)
        .features(features).returnAnalyzedText(true).build();
    try {
      AnalysisResults results = servicePool.get(0).analyze(parameters)
          .execute();
      return new WatsonResults(results);
    } catch (ForbiddenException e) {
      servicePool.remove(0);
      return LanguageProcessor.analyzeUrl(url);
    } catch (NullPointerException | IndexOutOfBoundsException e) {
      System.out.println("ERROR: Out of working acccounts " + e.toString());
      return null;
    }
  }
}
