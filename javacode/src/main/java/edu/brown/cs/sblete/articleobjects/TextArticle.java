package edu.brown.cs.sblete.articleobjects;

import java.security.MessageDigest;

public class TextArticle extends Content {

	/**
	 * takes in article's text and a flag for whether the host website is
	 * credible or not.
	 *
	 * @param text
	 *            article's text
	 * @param wp
	 *            whether host website is credible
	 * @throws Exception
	 */
	public TextArticle(String text, int wp) throws Exception {
		super(); // Calls the constructor for Content

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(text.trim().getBytes("UTF-16"));
		setKey(new String(messageDigest.digest()));
		setWordPress(wp);

		fill(text); // Fills in watson_scores, categories, and category_score
		valid(); // Fills in validity_score
	}
}
