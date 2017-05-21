package edu.brown.cs.sbelete.processor;

import java.util.List;

/**
 * gets all results from Watson.
 * 
 * @author daltonleshan
 *
 */
public interface wResults {
	/**
	 * 
	 * @return sentiment score
	 */
	public double getSentiment();

	/**
	 * 
	 * @return anger score
	 */
	public double getAnger();

	/**
	 * 
	 * @return joy score
	 */
	public double getJoy();

	/**
	 * 
	 * @return disgust score
	 */
	public double getDisgust();

	/**
	 * 
	 * @return sadness score
	 */
	public double getSadness();

	/**
	 * 
	 * @return fear score
	 */
	public double getFear();

	/**
	 * 
	 * @return top category object of an article, category is a wrapper
	 */
	public Category getTopCategory();

	/**
	 * 
	 * @return all categories an article falls in
	 */
	public List<Category> getCategories();
}
