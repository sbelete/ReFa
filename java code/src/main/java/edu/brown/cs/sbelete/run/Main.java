package edu.brown.cs.sbelete.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.brown.cs.sbelete.dataminer.Preprocessor;
import edu.brown.cs.sbelete.processor.LanguageProcessor;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * The Main class of our project. This is where execution begins.
 *
 * @author sbelete
 */
public final class Main {

  /**
   * Default server for Spark
   */
  private static final int DEFAULT_PORT = 4567;

  /**
   * Arguments passed to main
   */
  private String[] args;

  /**
   * Preprocess to update database through commandline
   */
  private Preprocessor pp;

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  /**
   * Protected main class
   * 
   * @param args
   *          arguments for main
   */
  private Main(String[] args) {
    this.args = args;
  }

  /**
   * Runs the program
   */
  private void run() {

    // Parse command line arguments
    OptionParser parser = new OptionParser();

    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    Gui g = new Gui();

    // Start Gui
    g.runSparkServer((int) options.valueOf("port"));

    try {
      LanguageProcessor p = new LanguageProcessor();
      p.analyzeUrl(
          "http://abcnews.go.com/US/authorities-debating-charges-wikileaks-assange/story?id=46924024&cid=clicksource_4380645_1_hero_headlines_headlines_hed");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
      String line = new String();
      line = bfr.readLine();
      while (line != null) {
        pp = new Preprocessor();
        pp.process(line);
        // String[] command = line.split(" ");
        // if (line.equals("update database")) {
        // pp.update();
        // } else if (command[0].equals("watson")) {
        // if (command.length == 2) {
        // AccountAccessObject aao = new AccountAccessObject();
        // aao.updateNewCSVFile(command[1]);
        // }
        // } else {
        // System.out.println("ERROR:");
        // }
        line = bfr.readLine();
      }
    } catch (IOException e) {
      System.out.println("ERROR:");
    }
  }

}
