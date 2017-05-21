package edu.brown.cs.sbelete.processor;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionScores;

/**
 * holds results from watson.
 * 
 * @author daltonleshan
 *
 */
public class WatsonResults implements wResults{

	private EmotionScores emotions;
	private List<CategoriesResult> categories;
	private double sentiment;

	/**
	 * constructor.
	 * 
	 * @param results
	 *            Analysis results from Watson
	 */
	public WatsonResults(AnalysisResults results) {
		emotions = results.getEmotion().getDocument().getEmotion();
		sentiment = results.getSentiment().getDocument().getScore();
		categories = results.getCategories();
	}

	/**
	 * 
	 * @return sentiment score
	 */
	public double getSentiment() {
		return sentiment;
	}

	/**
	 * 
	 * @return anger score
	 */
	public double getAnger() {
		return emotions.getAnger();
	}

	/**
	 * 
	 * @return joy score
	 */
	public double getJoy() {
		return emotions.getJoy();
	}

	/**
	 * 
	 * @return disgust score
	 */
	public double getDisgust() {
		return emotions.getDisgust();
	}

	/**
	 * 
	 * @return sadness score
	 */
	public double getSadness() {
		return emotions.getSadness();
	}

	/**
	 * 
	 * @return fear score
	 */
	public double getFear() {
		return emotions.getFear();
	}

	/**
	 * 
	 * @return top 3 categories that a given article or paragraph falls in
	 */
	public List<Category> getCategories() {
		List<Category> toReturn = new ArrayList<>();
		for (CategoriesResult result : categories) {
			toReturn.add(new Category(result));
		}
		return toReturn;
	}

	/**
	 * @return array of all score
	 */
	public List<Double> getAll() {
		return ImmutableList.of(getSentiment(), getAnger(), getJoy(), getDisgust(), getSadness(), getFear());
	}

	/**
	 * gets the top category.
	 * 
	 * @return top category that a given article falls into.
	 */
	public Category getTopCategory() {
		/*
		 * use this to get the top category which could then be used to get
		 * average scores across that category
		 */
		assert (!categories.isEmpty()) : "No categories were found";
		CategoriesResult r = categories.get(0);
		return new Category(r);
	}
}
