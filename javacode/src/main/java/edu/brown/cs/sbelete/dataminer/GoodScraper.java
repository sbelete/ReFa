package edu.brown.cs.sbelete.dataminer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Scraps good article urls from multiple sources.
 * 
 * @author DaniRyoo
 *
 */
public class GoodScraper {

  private static BufferedWriter wr;
  private static String date;

  public static void connect(String fName, String d) {
    date = d;
    try {
      File file = new File(fName);
      wr = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      System.out.println("Invalid date");
    }
  }

  private static void append(String url) throws IOException {
    wr.append(url);
    wr.append("\n");
  }

  private static void close() {
    try {
      wr.flush();
      wr.close();
    } catch (IOException e) {
      System.out.println("invalid writer");
    }
  }

  public static void updateNYTArticles(int num) {
    int c = 1;
    String[] ymd = date.split("-");
    String year = ymd[0];
    String month = ymd[1];
    String day = ymd[2];
    if (month.charAt(0) == '0') {
      month = month.replace("0", "");
    }
    if (day.charAt(0) == '0') {
      day = day.replace("0", "");
    }
    int d = Integer.parseInt(day);
    String sURL = "https://api.nytimes.com/svc/archive/v1/" + year + "/" + month
        + ".json";
    String apiKey = "a1fe537a74ae42c2a18edf0bdd75f4f9";
    sURL += "?api-key=" + apiKey;
    URL url;
    JsonObject articleArray;
    try {
      url = new URL(sURL);
      HttpURLConnection request = (HttpURLConnection) url.openConnection();
      request.connect();
      JsonParser jp = new JsonParser();
      articleArray = jp
          .parse(new InputStreamReader((InputStream) request.getContent()))
          .getAsJsonObject();
      JsonArray docs = articleArray.get("response").getAsJsonObject()
          .get("docs").getAsJsonArray();
      for (int i = d * 101; i < docs.size(); i++) {
        String rl = docs.get(i).getAsJsonObject().get("web_url").getAsString();
        append(rl);
        c++;
        if (c > num) {
          break;
        }
      }
    } catch (MalformedURLException e) {
      System.out.println("malURL");
    } catch (IOException e1) {
      System.out.println("IO");
    }
  }

  public static void updateGDNArticles(int num) {
    String sURL = "http://content.guardianapis.com/search?from-date=" + date
        + "&to-date=" + date;
    String apiKey = "6e37bbf6-e484-4ed6-aeba-9a3ba9a54135";
    sURL += "&api-key=" + apiKey;
    URL url;
    JsonObject articleArray;
    int c = 1;
    try {
      url = new URL(sURL);
      HttpURLConnection request = (HttpURLConnection) url.openConnection();
      request.connect();
      JsonParser jp = new JsonParser();
      articleArray = jp
          .parse(new InputStreamReader((InputStream) request.getContent()))
          .getAsJsonObject();
      JsonObject response = articleArray.get("response").getAsJsonObject();
      JsonArray results = response.get("results").getAsJsonArray();
      for (int i = 0; i < results.size(); i++) {
        String rl = results.get(i).getAsJsonObject().get("webUrl")
            .getAsString();
        append(rl);
        c++;
        if (c > num) {
          break;
        }
      }
    } catch (MalformedURLException e) {
    } catch (IOException e1) {
    }
  }

  public static void updateWSJArticles(int num) {
    int c = 1;
    String[] ymd = date.split("-");
    String year = ymd[0];
    String month = ymd[1];
    String day = ymd[2];
    if (month.charAt(0) == '0') {
      month = month.replace("0", "");
    }
    String address = year + "-" + month + "-" + day;
    try {
      String main = "http://www.wsj.com/public/page/archive-@.html";
      main = main.replaceAll("@", address);
      main = String.format(main, URLEncoder.encode(main, "UTF-8"));
      Document doc = Jsoup.connect(main).get();
      Element archive = doc.getElementById("archivedArticles");
      Elements hs = archive.getElementsByTag("h2");
      for (Element h : hs) {
        String url = h.getElementsByTag("a").first().attr("href");
        if (url != null) {
          append(url);
          c++;
          if (c > num) {
            break;
          }
        }
      }
    } catch (IOException e) {
      System.out.println("not valid");
    }
  }

  public static void updateABCArticles(String date, int num) {
    int count = 1;
    try {
      String fName = "data/urls/good/abc_@.csv";
      fName = fName.replaceAll("@", date);
      File file = new File(fName);
      BufferedWriter wr = new BufferedWriter(new FileWriter(file));

      for (int i = 1; i < 10; i++) {
        String url = "http://www.abc.net.au/news/archive/?date=x&page=@";
        url = url.replaceAll("x", date);
        int c = 0;
        url = url.replaceAll("@", Integer.toString(i));
        Document doc = Jsoup.connect(url).timeout(0).get();
        Elements hs = doc.getElementsByTag("h3");

        for (Element h : hs) {
          Element e = h.getElementsByTag("a").first();
          if (e != null) {
            String rl = e.attr("href");
            if (rl.contains(date) && !rl.contains("90-seconds")) {
              rl = "http://www.abc.net.au" + rl;
              wr.append(rl);
              wr.append("\n");
              System.out.println(rl);
              count++;
              c++;
              if (count > num) {
                System.out.println("reached " + num);
                break;
              }
            }
          }
        }
        System.out.println(i + ": " + c);
      }
      wr.flush();
      System.out.println(count);
    } catch (MalformedURLException e) {
      System.out.println("malURL");
    } catch (IOException e1) {
      System.out.println("IO");
    }
  }

  public static void addAllWikiFeaturedArticles() {
    String main = "https://en.wikipedia.org/wiki/Wikipedia:Featured_articles";
    try {
      File file = new File("data/wiki.csv");
      BufferedWriter wr = new BufferedWriter(new FileWriter(file));
      Document doc = Jsoup.connect(main).get();
      Element content = doc.getElementById("mw-content-text");
      Elements trs = content.getElementsByTag("tr");
      System.out.println(trs.size());
      Element tr3 = trs.get(2);
      Elements as = tr3.getElementsByTag("a");
      int count = 0;
      for (Element a : as) {
        String url = a.attr("href");
        String[] sList = url.split("/");
        if (sList[1].equals("wiki")) {
          count++;
          url = "https://en.wikipedia.org" + url;
          System.out.println(count + " - " + url);
          wr.append(url);
          wr.append("\n");
        }
      }
      wr.flush();
      wr.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void updateBBTArticles() {
    File file = new File("data/urls/good/breitbart.csv");
    BufferedWriter wr;
    int count = 0;
    try {
      wr = new BufferedWriter(new FileWriter(file));
      for (int i = 0; i < 13; i++) {
        int c = 0;
        String main = "http://www.breitbart.com/news/source/breitbart-news/page/@/";
        String page = Integer.toString(i * 10 + 2);
        main = main.replaceAll("@", page);
        System.out.println(main);
        Document doc = Jsoup.connect(main).timeout(0).get();
        Elements as = doc.getElementsByTag("article");
        System.out.println("as " + as.size());
        for (Element a : as) {
          Element e = a.getElementsByTag("a").first();
          String url = e.attr("href");
          System.out.println(url);
          wr.append(url);
          wr.append("\n");
          count++;
          c++;
          if (c > 100) {
            break;
          }
        }
        main = "http://www.breitbart.com/news/source/breitbart-news/page/@/";
        page = Integer.toString(i * 10 + 3);
        main = main.replaceAll("@", page);
        System.out.println(main);
        doc = Jsoup.connect(main).timeout(0).get();
        as = doc.getElementsByTag("article");
        System.out.println("as " + as.size());
        for (Element a1 : as) {
          Element e = a1.getElementsByTag("a").first();
          String url = e.attr("href");
          System.out.println(url);
          wr.append(url);
          wr.append("\n");
          count++;
          c++;
          if (c > 100) {
            break;
          }
        }
      }
      wr.flush();
      System.out.println("counts:" + count);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public static void update(String fName, String date) {
    connect(fName, date);
    updateNYTArticles(2);
    updateGDNArticles(2);
    updateWSJArticles(2);
    close();
  }
}
