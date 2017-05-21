package edu.brown.cs.sbelete.dataminer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FakeScraper implements ArticleScraper {

  public static void connect() {
  }

  public static void getAllStuppid() throws IOException {
    File file = new File("data/stuppid.csv");
    int count = 0;
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (int i = 2; i < 21; i++) {
      String main = "http://stuppid.com/category/news/page/@/";
      main = main.replaceAll("@", Integer.toString(i));
      Document doc = Jsoup.connect(main).get();
      Elements divs = doc.getElementsByTag("article");
      System.out.println(divs.size());
      for (Element div : divs) {
        count++;
        Element e = div.getElementsByTag("a").first();
        String url = e.attr("href");
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
    System.out.println("counts:" + count);
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
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
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
        wr.append(url);
        wr.append("\n");
      }
    }
    wr.flush();
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
  public static void getAll70News() throws IOException, ParseException {
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

  public static void getAllFromLaughSen() throws IOException {
    String main = "https://www.laughsend.com/news/archived-news.php";
    Document doc = Jsoup.connect(main).get();
    Element div = doc.getElementById("body-lhs");
    Elements as = div.getElementsByTag("a");
    // System.out.println(as.size());
    int count = 0;
    File file = new File("data/laughsen.csv");
    BufferedWriter wr = new BufferedWriter(new FileWriter(file));
    for (Element a : as) {
      String url = a.attr("href");
      url = "https://www.laughsend.com/news/" + url;
      Document doc2 = Jsoup.connect(url).get();
      Element div2 = doc2.getElementById("body-lhs");
      Elements as2 = div2.getElementsByTag("a");
      String rl = "";

      for (Element a2 : as2) {
        String url2 = a2.attr("href");
        String[] list = url2.split("_");
        if (list.length >= 2) {
          if (!rl.equals(url2)) {
            rl = url2;
            // System.out.println(url2);
            count++;
            String in = "https://www.laughsend.com/" + url2;
            Document doc3 = Jsoup.connect(in).get();
            Element div3 = doc3.getElementById("body-lhs");
            Element as3 = div3.getElementsByTag("a").first();
            String fn = as3.attr("href");
            wr.append(fn);
            wr.append("\n");
          }
        }
      }
      wr.flush();
    }
    System.out.println("counts:" + count);
  }

  @Override
  public void update(String date) {
  }

}
