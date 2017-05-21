package edu.brown.cs.sblete.articleobjects;

import java.security.MessageDigest;

public class Paragraph extends Content {

	public Paragraph(String text) throws Exception {
		super();

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(text.trim().getBytes("UTF-16"));
		setKey(new String(messageDigest.digest()));
		setWordPress(0); // what was word press zero equals what???
		
		fill(text);
		//setValidityScore(1.0);
		// valid(); // The Option to use valid() for pargraph
	}

}
