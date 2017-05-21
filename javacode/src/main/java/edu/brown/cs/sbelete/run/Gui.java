package edu.brown.cs.sbelete.run;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;
import edu.brown.cs.sblete.articleobjects.Paragraph;
import edu.brown.cs.sblete.articleobjects.TextArticle;
import edu.brown.cs.sblete.articleobjects.UrlArticle;
import freemarker.template.Configuration;

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
	 *            -number
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
		Spark.post("/article", new ArticleHandler());
		Spark.post("/paragraph", new ParagraphHandler());
		Spark.post("/link", new LinkHandler());
		Spark.post("/review", new ReviewHandler());
	}

	/**
	 * Handler for /home.
	 */
	private class HomeHandler implements TemplateViewRoute {

		/**
		 * Supplies page title.
		 *
		 * @param req
		 *            unused
		 * @param res
		 *            unused
		 * @return ModelAndView
		 */
		@Override
		public ModelAndView handle(Request req, Response res) {
			return null;
		}
	}

	/**
	 * Handler for /article.
	 */
	private class ArticleHandler implements Route {

		/**
		 * Article.
		 *
		 * @param req
		 *            request
		 * @param res
		 *            unused
		 * @return link scores
		 */
		@Override
		public synchronized Object handle(final Request req, final Response res) {

			try {
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(req.body());

				StringBuilder sb = new StringBuilder();
				// Article article = new
				// Article(json.get("article").getAsString());
				for (JsonElement text : json.get("article").getAsJsonArray()) {
					sb.append(text.getAsString());
					sb.append(" ");
				}
				System.out.println(sb.toString());
				TextArticle text_article;
				if (json.get("wp").getAsInt() > 0) {
					text_article = new TextArticle(sb.toString(), 1);
				} else {
					text_article = new TextArticle(sb.toString(), 0);
				}

//				return GSON.toJson(ImmutableMap.<String, Object> builder()
//						.put("valid", text_article.getValidityScore())
//						.put("scores", text_article.getWatsonScores())
//						.put("avg", text_article.getAvg())
//						.put("categories", text_article.getCategories())
//						.put("cscore", text_article.getCategoryScore())
//						.put("error", false));
				return GSON.toJson(ImmutableMap.<String, Object>of("valid", text_article.getValidityScore(),
						"scores", text_article.getWatsonScores(),
						"avg", text_article.getAvg(),
						"categories", text_article.getCategories(),
						"cscore", text_article.getCategoryScore()));

			} catch (Exception e) {
				System.out.println("ERROR: Handler for Article - " + e.toString());
				System.err.println(e);
			}

			// Might want to return 1.0
			return GSON.toJson(ImmutableMap.of("valid", 1.0, "scores", ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
					"avg", ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "error", true));


		}
	}

	private class ParagraphHandler implements Route {
		@Override
		public synchronized Object handle(final Request req, final Response res) {
			// [valid : [boolean], scores : [[doubles]] ];

			try {
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(req.body());
				List<Double> alod = new ArrayList<>();
				List<List<Double>> alodl = new ArrayList<>();
				Paragraph temp;

				for (JsonElement paragraph : json.get("paragraphs").getAsJsonArray()) {
					try {
						temp = new Paragraph(paragraph.getAsString());
						alod.add(temp.getValidityScore());
						alodl.add(temp.getWatsonScores());

					} catch (Exception e) {
						alod.add(-1.0);
						alodl.add(ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
					}
				}

				System.out.println(GSON.toJson(ImmutableMap.of("valid", ImmutableList.copyOf(alod), "scores",
						ImmutableList.copyOf(alodl), "error", false)));

				return GSON.toJson(ImmutableMap.of("valid", ImmutableList.copyOf(alod), "scores",
						ImmutableList.copyOf(alodl), "error", false));
			} catch (Exception e) {
				System.out.println("ERROR: Handler for Paragraphs - " + e.toString());
				e.printStackTrace();
			}

			return GSON.toJson(ImmutableMap.of("valid", ImmutableList.of(0.0), "scores",
					ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "error", true));
		}
	}

	/**
	 * Handler for /article.
	 */
	private class LinkHandler implements Route {

		@Override
		public synchronized Object handle(final Request req, final Response res) {
			// [valid : [boolean], scores : [[doubles]] ];

			try {
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(req.body());
				List<Double> alod = new ArrayList<>();
				UrlArticle temp;

				for (JsonElement link : json.get("links").getAsJsonArray()) {
					try {
						temp = new UrlArticle(link.getAsString());
						alod.add(temp.getValidityScore());
					} catch (Exception e) {
						alod.add(-1.0);
					}
				}

				System.out.println(GSON.toJson(ImmutableMap.of("valid", ImmutableList.copyOf(alod), "error", false)));

				return GSON.toJson(ImmutableMap.of("valid", ImmutableList.copyOf(alod), "error", false));
			} catch (Exception e) {
				System.out.println("ERROR: Handler for Paragraphs - " + e.toString());
				e.printStackTrace();
			}

			return GSON.toJson(ImmutableMap.of("valid", ImmutableList.of(0.0), "scores",
					ImmutableList.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), "error", true));
		}
	}

	/**
	 * Handler for /review.
	 */
	private class ReviewHandler implements Route {

		/**
		 * Article.
		 *
		 * @param req
		 *            request
		 * @param res
		 *            unused
		 * @return link scores
		 */
		@Override
		public synchronized Object handle(final Request req, final Response res) {

			try {
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(req.body());
				String url = json.get("baseURL").getAsString();
				boolean rating = json.get("isFalse").getAsBoolean();
				List<String> categories = new ArrayList<String>();
				JsonArray cats = json.get("categories").getAsJsonArray();
				for (int i = 0; i < cats.size(); i++) {
					categories.add(cats.get(0).getAsString());
				}
				JsonArray scores = json.get("scores").getAsJsonArray();
				List<Object> features = new ArrayList<Object>();
				for (int i = 0; i < scores.size(); i++) {
					features.add(scores.get(i).getAsDouble());
				}
				for (int i = 0; i < categories.size(); i++) {
					features.add(categories.get(i));
				}
				for (int i = categories.size(); i < 5; i++) {
					features.add(null);
				}
				//features.add(json.get("c1score").getAsDouble());
				//features.add(json.get("wordpress").getAsInt());
				// Need to uncomment new UrlHandler(url, 1/0) to 
				// Actually add to the database
				DataAccessObject.reportLink(url, rating);
				return GSON.toJson(ImmutableMap.of("error", false));

			} catch (Exception e) {
				System.out.println("ERROR: Handler for Rating - " + e.toString());
				e.printStackTrace();
			}

			return GSON.toJson(ImmutableMap.of("error", true));
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
			System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
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
