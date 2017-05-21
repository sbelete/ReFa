package edu.brown.cs.sbelete.processor;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;

import edu.brown.cs.sbelete.databaseconnection.DataAccessObject;

/**
 * data structure for categoty results.
 *
 * @author dthuku
 *
 */
public class Category {

  private CategoriesResult result;
  private List<String> levels;
  private double score;

  /**
   * constructor. 
   *
   * @param result
   *          watson result
   */
  public Category(CategoriesResult result) {
    this.result = result;
    levels = new ArrayList<>();
    processCategories();
    setScore();
  }

  /**
   * splits category levels and adds them to a list.
   */
  private void processCategories() {
    // get the category label
    String label = result.getLabel();
    // different levels of obtained category
    String[] labelsArray = label.split("/");
    // first label is an empty string that's why we omit it
    for (int i = 1; i < labelsArray.length; i++) {
      levels.add(labelsArray[i]);
    }
  }

  /**
   * sets category score based off Watson results.
   */
  private void setScore() {
    score = result.getScore();
  }

  /**
   *
   *
   * @return category score
   */
  public double getScore() {
    return score;
  }

  /**
   *
   * @return levels of this category.
   */
  public List<String> getLevels() {
    return levels;
  }

  /**
   *
   * @return average scores of all articles in this category, or an empty list
   *         if there are no articles of this type in the preprocessed database
   */
  public List<Double> getAvgScores() {
    assert (!levels.isEmpty()) : "This article has no category";
    DataAccessObject db = new DataAccessObject();
    List<String> averages = db.getAvg(levels.get(0));
    assert (averages.size() == 6) : "Not all average scores for this category were returned";
    System.out.println(averages);
    if (averages.get(0) == null) {
      return ImmutableList.of();
    }
    return ImmutableList.of(Double.parseDouble(averages.get(0)),
        Double.parseDouble(averages.get(1)),
        Double.parseDouble(averages.get(2)),
        Double.parseDouble(averages.get(3)),
        Double.parseDouble(averages.get(4)),
        Double.parseDouble(averages.get(5)));
  }
  
  /**
  *
  * @return average scores of all articles in this category, or an empty list
  *         if there are no articles of this type in the preprocessed database
  */
 public List<Double> getAvgScores(String catagory) {
   assert (!levels.isEmpty()) : "This article has no category";
   DataAccessObject db = new DataAccessObject();
   
   List<String> averages = db.getAvg(levels.get(0));
   assert (averages.size() == 6) : "Not all average scores for this category were returned";
   System.out.println(averages);
   if (averages.get(0) == null) {
     return ImmutableList.of();
   }
   return ImmutableList.of(Double.parseDouble(averages.get(0)),
       Double.parseDouble(averages.get(1)),
       Double.parseDouble(averages.get(2)),
       Double.parseDouble(averages.get(3)),
       Double.parseDouble(averages.get(4)),
       Double.parseDouble(averages.get(5)));
 }
}
