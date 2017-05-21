package edu.brown.cs.sbelete.run;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.brown.cs.sblete.articleobjects.Article;
import edu.brown.cs.sblete.articleobjects.Paragraph;
import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * front end.
 * 
 * @author dthuku
 *
 */
public class Gui {

  /**
   * GSON.
   */
  public static final Gson GSON = new Gson();

  /**
   * Constructor for Gui.
   * 
   */
  public Gui() {

  }

  /**
   * runs gui.
   * 
   * @param port
   *          -number
   */
  public void runSparkServer(int port) {
    Spark.port(port);
    Spark.secure("data/keystore", "abc123", null, null);
    // Setup Spark
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Home page to download the javascript
    Spark.get("/home", new HomeHandler(), freeMarker);
    Spark.post("/url", new URLHandler());
    Spark.post("/paragraph", new ParagraphHandler());

  }

  /**
   * Handler for /home.
   */
  private class HomeHandler implements TemplateViewRoute {

    /**
     * Supplies page title.
     *
     * @param req
     *          unused
     * @param res
     *          unused
     * @return ModelAndView
     */
    @Override
    public ModelAndView handle(Request req, Response res) {
      return null;
    }
  }

  /**
   * Handler for /auto.
   */
  private class URLHandler implements Route {

    /**
     * Url.
     *
     * @param req
     *          request
     * @param res
     *          unused
     * @return link scores
     */
    @Override
    public synchronized Object handle(final Request req, final Response res) {

      try {
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(req.body());

        Article article = new Article(json.get("url").getAsString());
        System.out.println(json.get("url").getAsString());
        boolean valid = false;
        /*
         * return GSON.toJson(ImmutableMap.of("valid", false, "scores",
         * ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "avg",
         * ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "error", true));
         */
        // http://userpath.co/blog/a-simple-way-to-run-https-on-localhost/

        return GSON.toJson(ImmutableMap.of("valid", valid, "scores",
            article.getWatsonResults().getAll(), "avg", article.getAvg(),
            "error", false));

      } catch (Exception e) {
        System.out.println("ERROR: Handler for URL - " + e.toString());
        e.printStackTrace();
      }

      return GSON.toJson(ImmutableMap.of("valid", false, "scores",
          ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "avg",
          ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "error", true));
    }
  }

  private class ParagraphHandler implements Route {
    @Override
    public synchronized Object handle(final Request req, final Response res) {
      // [valid : [boolean], scores : [[doubles]] ];

      try {
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(req.body());
        List<Boolean> alob = new ArrayList<>();
        List<List<Double>> alodl = new ArrayList<>();
        Paragraph temp;
        int count = 0;
        for (JsonElement paragraph : json.get("paragraphs").getAsJsonArray()) {
          count++;
          temp = new Paragraph(paragraph.getAsString());
          alob.add(count % 3 == 0);
          alodl.add(temp.getWatsonResults().getAll());
          // alodl.add(ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        }

        System.out.println(
            GSON.toJson(ImmutableMap.of("valid", ImmutableList.copyOf(alob),
                "scores", ImmutableList.copyOf(alodl), "error", true)));
        return GSON.toJson(ImmutableMap.of("valid", ImmutableList.copyOf(alob),
            "scores", ImmutableList.copyOf(alodl), "error", true));
      } catch (Exception e) {
        System.out.println("ERROR: Handler for Paragraphs - " + e.toString());
        e.printStackTrace();
      }

      return GSON.toJson(ImmutableMap.of("valid", false, "scores",
          ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "error", true));
    }
  }

  /**
   * @return freemarker engine
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * Exception printer for spark.
   */
  private static class ExceptionPrinter implements ExceptionHandler {

    /**
     * Default status.
     */
    private static final int STATUS = 500;

    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(STATUS);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
