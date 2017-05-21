package edu.brown.cs.sbelete.databaseconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import edu.brown.cs.sbelete.dataminer.GoodScraper;
import edu.brown.cs.sbelete.processor.AccountAccessObject;
import edu.brown.cs.sbelete.processor.LanguageProcessor;

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

  public Preprocessor() {
    updatePath = "data/update.csv";
    df = new SimpleDateFormat("yyyy-MM-dd");
  }

  private void setLastUpdateDay() {
    try {
      BufferedReader bfr = new BufferedReader(
          new FileReader(new File(updatePath)));
      String line = bfr.readLine();
      line = bfr.readLine();
      Date date = (Date) df.parse(line);
      updated = date;
    } catch (IOException | ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void update() {
    setLastUpdateDay();
    Date today = Calendar.getInstance().getTime();
    String sToday = df.format(today);
    if (sToday.equals(df.format(updated))) {
      System.out.println("Database Already up-to-date");
    } else if (!today.before(updated)) {
      System.out.println("Last updated: " + df.format(updated));
      System.out.println("Today       : " + sToday);
      LanguageProcessor.create();
      updateHelper(sToday);
      System.out.println("Database Updated");
    }
  }

  private void updateHelper(String today) {
    fetchCSV(today);
    updateDate(today);
  }

  public void uploadUrlList(String filepath, boolean real) {
    DataAccessObject dao = new DataAccessObject();
    Path path = Paths.get(filepath);
    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(url -> {
        dao.addArticle(url, real);
      }); // true means good
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void updateDate(String date) {
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

  public void fetchCSV(String today) {
    try {
      Date dToday = (Date) df.parse(today);
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(updated);
      while (calendar.getTime().before(dToday)) {
        Date result = calendar.getTime();
        String sResult = df.format(result);
        fetchCSVforDate(sResult);
        calendar.add(Calendar.DATE, 1);
      }
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void fetchCSVforDate(String today) {
    try {
      System.out.println(today);
      String fName = "data/update/good_@.csv";
      fName = fName.replace("@", today);
      GoodScraper.update(fName, today);
      uploadUrlList(fName, true);
      Files.delete(Paths.get(fName));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // String fName2 = "data/update/bad_@.csv";
    // GoodScraper.update(fName2, today);
    // uploadUrlList(fName2, true);
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
}
