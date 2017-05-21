package edu.brown.cs.sbelete.run;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.opencsv.CSVReader;

import edu.brown.cs.sbelete.dataanalysis.ModelActivator;
import edu.brown.cs.sbelete.databaseconnection.Preprocessor;
import edu.brown.cs.sbelete.processor.AccountAccessObject;
import edu.brown.cs.sbelete.processor.LanguageProcessor;

import edu.brown.cs.sblete.articleobjects.Content;
import edu.brown.cs.sblete.articleobjects.UrlArticle;

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
	 *            An array of command line arguments
	 */
	public static void main(String[] args) {
		new Main(args).run();
	}

	/**
	 * Protected main class
	 * 
	 * @param args
	 *            arguments for main
	 */
	private Main(String[] args) {
		this.args = args;
	}

	/**
	 * Runs the program
	 */
	private void run() {
		/*
		 * try { BufferedReader buf = new BufferedReader(new
		 * FileReader("data/test")); String tmp; Content temp; double c = 0.0;
		 * double t = 0.0; LanguageProcessor.create(getAccounts()); while ((tmp
		 * = buf.readLine()) != null) { // tmp = buf.readLine().trim();
		 * 
		 * tmp = tmp.trim(); try { System.out.println(tmp); temp = new
		 * UrlArticle(tmp);
		 * 
		 * if (temp.getValidityScore() > .2) { c += 1.0; } t += 1.0; } catch
		 * (Exception e) {
		 * 
		 * } } System.out.println("Percent Valid: " + (c / t) * 100);
		 * buf.close(); } catch (Exception e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); }
		 * 
		 * System.exit(1);
		 */
		
		// Parse command line arguments
		OptionParser parser = new OptionParser();

		parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(DEFAULT_PORT);
		OptionSet options = parser.parse(args);

		LanguageProcessor.create(getAccounts());

		Gui g = new Gui();

		// Start Gui
		g.runSparkServer((int) options.valueOf("port"));

		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
			String line = new String();
			line = bfr.readLine();
			while (line != null) {
				String[] command = line.split(" ");
				if (line.equals("update database")) {
					pp = new Preprocessor();
					pp.update();
				} else if (command[0].equals("watson")) {
					if (command.length == 2) {
						AccountAccessObject aao = new AccountAccessObject();
						aao.updateNewCSVFile(command[1]);
					}

				} else if (line.equals("reload")) {
					LanguageProcessor.reload(getAccounts());

				} else if (line.equals("generate")) {
					System.out.println("generating model...");
					ModelActivator.generateModel();
				} else {
					System.out.println("ERROR:");
				}
				line = bfr.readLine();
			}
		} catch (IOException e) {
			System.out.println("ERROR:");
		}

	}

	private Collection<List<String>> getAccounts() {
		Map<String, List<String>> accounts = new HashMap<>();
		CSVReader csv;
		try {
			csv = new CSVReader(new FileReader("data/watson_accounts.csv"));
			for (String[] i : csv.readAll()) {
				accounts.put(i[0], ImmutableList.of(i[0], i[1]));
			}
			csv.close();
		} catch (Exception e) {
			System.out.println("ERROR: Issue with reading the CSV - " + e.toString());
		}
		return ImmutableList.copyOf(accounts.values());
	}

}
