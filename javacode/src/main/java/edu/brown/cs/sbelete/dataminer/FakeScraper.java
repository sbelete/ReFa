package edu.brown.cs.sbelete.dataminer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.websocket.common.io.http.HttpResponseHeaderParser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;

public class FakeScraper implements ArticleScraper {

  private static DataAccessObject dao;
  private static BufferedWriter wr;
  private static String date;

  private static Set<String> visitedURLs = new HashSet<>();

  public static void connect(String d) {
    date = d;
    try {
      String fName = "data/update/bad_@.csv";
      fName = fName.replaceAll("@", date);
      File file = new File(fName);
      wr = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      System.out.println("Invalid date");
    }
  }

  public static void close() throws IOException {
    wr.flush();
    wr.close();
  }

  private static void append(String url) throws IOException {
    wr.append(url);
    wr.append("\n");
  }

  public static List<String> getFakeNewsArticlesWithDepth(String domain,
      String currentURL, int n, String fullDomain) throws HTTPException {
    if (!currentURL.startsWith("//") && !currentURL.contains("print")) {
      System.out.println(currentURL);
      Document doc;
      try {
        doc = Jsoup.connect(currentURL).get();

        Elements articles = doc.getElementsByTag("a");
        List<String> toReturn = new ArrayList<>();
        for (Element article : articles) {
          if (article != null) {
            // Element articleURLTagFirst =
            // article.getElementsByTag("a").first();
            // if (articleURLTagFirst != null) {
            String url = article.attr("href");
            if (n >= 1 && !url.contains("video") && !url.contains("slideshow")
                && url.contains(".html")
                && (url.contains("2016") || url.contains("2017"))) {
              if (url.contains(domain) && !url.equals(currentURL)
                  && !url.equals(fullDomain)) {
                if (!visitedURLs.contains(url)) {
                  visitedURLs.add(url);
                  getFakeNewsArticlesWithDepth(domain, url, n - 1, fullDomain);
                }

              } else if (url.startsWith("/")) {
                url = fullDomain + url;
                if (!visitedURLs.contains(url)) {
                  visitedURLs.add(url);
                  getFakeNewsArticlesWithDepth(domain, url, n - 1, fullDomain);
                }
              }

            }
          }
        }

        // }
        toReturn.addAll(visitedURLs);
        return toReturn;
      }

      catch (IOException e) {
        return null;

      } catch (IllegalArgumentException e) {
        return null;

      }
    } else
      return null;

  }

  public static void cleanUp(String readFilePath, String writeFilePath)
      throws IOException {
    Path path = Paths.get(readFilePath);
    Set<String> set = new HashSet<>();

    try (BufferedReader reader = Files.newBufferedReader(path)) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        set.add(line);
      }

    }

    Path path2 = Paths.get(writeFilePath);
    try (BufferedWriter writer = Files.newBufferedWriter(path2)) {
      for (String line : set) {
        writer.write(line);
        writer.newLine();
      }

    }

  }

  public static List<String> readFakeDomains(String readFilePath,
      String writeFilePath) throws IOException {
    Path path = Paths.get(readFilePath);
    List<String> toReturn = new ArrayList<>();
    System.out.println("path: " + path);
    int current = 0;
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        current++;
        System.out.println(current);
        String domain = line;
        List<String> temp = new ArrayList<String>();

        // process each line in some way
        line = "https://www." + domain;
        try {
          temp = getFakeNewsArticlesWithDepth(domain, line, 1, line);
          toReturn.addAll(temp);
        } catch (HTTPException e) {
          // try http
          line = "http://www." + domain;
          try {
            temp = getFakeNewsArticlesWithDepth(domain, line, 1, line);
            toReturn.addAll(temp);
          } catch (HTTPException e1) {
            // TODO Auto-generated catch block
            System.out.println("coundnt read");
          }
        }
      }
    }
    return toReturn;

  }

  public static void writeFakeLinks(String filePath, List<String> links)
      throws IOException {
    Path path = Paths.get(filePath);
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      for (String line : links) {
        System.out.println("Writing: " + line + "\n");
        writer.write(line);
        writer.newLine();
      }

    }
  }

  public static void getAllStreetSide() throws IOException {
    File file = new File("data/streetside.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 1; i < 70; i++) {
      System.out.println(i);
      String main = "http://success-street.com/page/@/";
      main = main.replaceAll("@", Integer.toString(i));
      Document doc = Jsoup.connect(main).timeout(0).get();
      Elements divs = doc.getElementsByClass("mh-loop-header");
      System.out.println(divs.size());
      for (Element div : divs) {
        count++;
        Element e = div.getElementsByTag("a").first();
        String url = e.attr("href");
        System.out.println(url);
        append(url);
      }
    }
    System.out.println("counts:" + count);
  }

  public static void getAllBizstandard() throws IOException {
    File file = new File("data/urls/bad/bizsnews.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 1; i < 52; i++) {
      String main = "http://bizstandardnews.com/page/@/";
      main = main.replaceAll("@", Integer.toString(i));
      System.out.println(main);
      Document doc = Jsoup.connect(main).get();
      Elements hs = doc.select(".entry-title");
      System.out.println("hs " + hs.size());
      for (Element h : hs) {
        count++;
        Element e = h.getElementsByTag("a").first();
        String url = e.attr("href");
        System.out.println(url);
        append(url);
      }
    }
    System.out.println("counts:" + count);
  }

  public static void getAll8News() throws IOException {
    File file = new File("data/urls/bad/8news.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 1; i < 32; i++) {
      String main = "http://now8news.com/page/@/";
      main = main.replaceAll("@", Integer.toString(i));
      System.out.println(main);
      Document doc = Jsoup.connect(main).get();
      Elements hs = doc.select(".content-list-title");
      System.out.println("hs " + hs.size());
      for (Element h : hs) {
        count++;
        Element e = h.getElementsByTag("a").first();
        String url = e.attr("href");
        System.out.println(url);
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
  }

  public static void getAllDailyCurrant() throws IOException {
    File file = new File("data/urls/bad/currant.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 2; i < 41; i++) {
      String main = "http://dailycurrant.com/page/@/";
      String p = Integer.toString(i);
      main = main.replaceAll("@", p);
      main = String.format(main, URLEncoder.encode(main, "UTF-8"));
      System.out.println(main);
      Document doc = Jsoup.connect(main).timeout(0)
          .userAgent(
              "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36")
          .get();
      ;
      Elements divs = doc.getElementsByClass("persistent-content");
      System.out.println("divs " + divs.size());
      for (Element d : divs) {
        count++;
        Element h2 = d.getElementsByTag("h2").first();
        Element e = h2.getElementsByTag("a").first();
        String url = e.attr("href");
        System.out.println(url);
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
  }

  public static void getAllNYEvening() throws IOException {
    File file = new File("data/urls/bad/nyevening.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 1; i < 141; i++) {
      String main = "http://thenewyorkevening.com/category/news/page/@/";
      String p = Integer.toString(i);
      main = main.replaceAll("@", p);
      System.out.println(main);
      Document doc = Jsoup.connect(main).get();
      Elements divs = doc.getElementsByClass("infinite-post");
      System.out.println("divs " + divs.size());
      for (Element d : divs) {
        count++;
        Element e = d.getElementsByTag("a").first();
        String url = e.attr("href");
        System.out.println(url);
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
  }

  public static void getAllNeonNettle() throws IOException {
    File file = new File("data/urls/bad/neon.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 1; i < 54; i++) {
      String main = "http://www.neonnettle.com/news/page=@";
      String p = Integer.toString(i);
      main = main.replaceAll("@", p);
      System.out.println(main);
      Document doc = Jsoup.connect(main).get();
      Elements ls = doc.getElementsByClass("middleSection");
      System.out.println("ls " + ls.size());
      for (Element l : ls) {
        count++;
        Element e = l.getElementsByTag("a").first();
        String url = e.attr("href");
        url = "http://www.neonnettle.com/news/" + url;
        System.out.println(url);
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
  }

  public static void getAllEmpire() throws IOException {
    File file = new File("data/empire.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 2; i < 250; i++) {
      String main = "http://empirenews.net/page/@/";
      main = main.replaceAll("@", Integer.toString(i));
      System.out.println(main);
      Document doc = Jsoup.connect(main).get();
      Elements hs = doc.getElementsByTag("h2");
      System.out.println("hs " + hs.size());
      for (Element h : hs) {
        count++;
        Element e = h.getElementsByTag("a").first();
        String url = e.attr("href");
        System.out.println(url);
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
  }

  @SuppressWarnings("deprecation")
  public static void getAll70News()
      throws IOException, ParseException, java.text.ParseException {
    File file = new File("data/70news.csv");
    int count = 0;
    String sDate = "2016-05-15";
    Date date;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    date = (Date) df.parse(sDate);
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 1; i < 365; i++) {
      try {
        date.setDate(date.getDate() + 1);
        String main = "https://70news.wordpress.com/x/y/z/";

        String[] dateList = df.format(date).split("-");
        System.out
            .println(dateList[0] + " " + dateList[1] + " " + dateList[2] + " ");
        main = main.replaceAll("x", dateList[0]).replaceAll("y", dateList[1])
            .replaceAll("z", dateList[2]);
        System.out.println(main);
        main = String.format(main, URLEncoder.encode(main, "UTF-8"));
        Document doc = Jsoup.connect(main).get();
        Element content = doc.getElementById("content");
        Elements divs = content.getElementsByClass("event-text");
        for (Element div : divs) {
          Elements hs = div.getElementsByTag("h3");
          for (Element h : hs) {
            Element e = h.getElementsByTag("a").first();
            if (e != null) {
              String url = e.attr("href");
              if (url.contains("https://70news.wordpress.com/")) {
                System.out.println(url);
                wr.append(url);
                wr.append("\n");
                count++;
              }
            }
          }
        }
      } catch (org.jsoup.HttpStatusException e) {
        System.out.println("not valid");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
  }

  @Override
  public void update(String date) {
  }

}
