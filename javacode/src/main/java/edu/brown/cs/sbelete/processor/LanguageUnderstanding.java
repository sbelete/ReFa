package edu.brown.cs.sbelete.processor;

/**
 * natural language understanding.
 * 
 * @author daltonleshan
 *
 */
public interface LanguageUnderstanding {
	/**
	 * analyzes given text and returns its attributes
	 * 
	 * @param paragraph
	 *            string
	 * @return attributes for the string
	 */
	public WatsonResults analyzeText(String paragraph) throws Exception;

	/**
	 * analyzes given article and returns its attributes
	 * 
	 * @param url
	 *            string
	 * @return attributes for the article
	 */
	public WatsonResults analyzeUrl(String url) throws Exception;

}
