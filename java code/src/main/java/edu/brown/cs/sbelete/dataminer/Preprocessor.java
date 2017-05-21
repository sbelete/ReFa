package edu.brown.cs.sbelete.dataminer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;
import edu.brown.cs.sbelete.processor.AccountAccessObject;

public class Preprocessor {

  private static String updatePath;
  /**
   * DateFormat
   */
  private DateFormat df;
  /**
   * Last updated date
   */
  private Date updated;
  private List<String> analysis;

  public Preprocessor() {
    updatePath = "data/update.csv";
    df = new SimpleDateFormat("yyyy-MM-dd");
  }

  public void update() {
    setLastUpdateDay();
    Date today = Calendar.getInstance().getTime();
    String sToday = df.format(today);
    if (sToday.equals(df.format(updated))) {
      System.out.println("Database Already up-to-date");
    } else if (!today.before(updated)) {
      updateHelper(sToday);
      System.out.println("Database Updated");
      System.out.println("Last updated: " + df.format(updated));
      System.out.println("Today       : " + sToday);
    }
  }

  @SuppressWarnings("deprecation")
  public void loadInitialData() throws UnsupportedEncodingException {
    GoodScraper.connect();
    FakeScraper.connect();
    String sDate = "2016-03-15";
    Date date;
    try {
      date = (Date) df.parse(sDate);
      for (int i = 0; i < 13; i++) {
        sDate = df.format(date);
        System.out.println(sDate);
        // GoodScraper.updateNYTArticles(sDate, 100);
        // GoodScraper.updateWSJArticles(sDate, 100);
        String[] ymd = sDate.split("-");
        String year = ymd[0];
        String month = ymd[1];
        String day = ymd[2];
        if (month.charAt(0) == '0') {
          month = month.replace("0", "");
          System.out.println("modified" + month);
        }
        String fName = "data/urls/good/wsj_@.csv";
        String name = year + "_" + month + "_" + day;
        fName = fName.replaceAll("@", name);
        fName = String.format(fName, URLEncoder.encode(fName, "UTF-8"));
        System.out.println(fName);
        uploadUrlList(fName, true);
        date.setMonth(date.getMonth() + 1);
      }
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void updateHelper(String date) {
    updateCSV(date);
  }

  private void setLastUpdateDay() {
    try {
      BufferedReader bfr = new BufferedReader(
          new FileReader(new File(updatePath)));
      String line = bfr.readLine();
      line = bfr.readLine();
      Date date = (Date) df.parse(line);
      updated = date;
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void uploadUrlList(String filepath, boolean good) {
    Path path = Paths.get(filepath);
    DataAccessObject dao = new DataAccessObject();
    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(url -> dao.addArticle(url, good)); // true means good
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void buildWatsonData() {
    try {
      String statement = "CREATE TABLE IF NOT EXISTS watson("
          + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "account TEXT UNIQUE,"
          + "password TEXT);";
      AccountAccessObject aao = new AccountAccessObject();
      aao.buildTable(statement);
      aao.updateNewCSVFile("data/watsonKey.csv");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void updateCSV(String date) {
    try {
      File file = new File(updatePath);
      BufferedWriter wr = new BufferedWriter(new FileWriter(file, false));
      wr.append("Last updated:");
      wr.append("\n");
      wr.append(date);
      wr.append("\n");
      wr.flush();
      wr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void process(String line) {
    String[] command = line.split(" ");
    if (line.equals("update database")) {
      update();
    } else if (line.equals("analyze")) {
      analysis = getAnalysis();
      for (String li : analysis) {
        System.out.println(li);
      }
    } else if (command[0].equals("watson")) {
      if (command.length == 2) {
        AccountAccessObject aao = new AccountAccessObject();
        aao.updateNewCSVFile(command[1]);
      }
    } else {
      System.out.println("ERROR: Invalid Command Line");
    }
  }

  private List<String> getAnalysis() {
    // TODO Auto-generated method stub
    return null;
  }
}
