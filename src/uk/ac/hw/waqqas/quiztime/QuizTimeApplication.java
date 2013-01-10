package uk.ac.hw.waqqas.quiztime;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

/**
 * Singleton class used to pass data between activities and to store cached list
 * of categories and questions.
 * 
 * @author Waqqas
 * 
 */
public class QuizTimeApplication extends Application {

	private static QuizTimeApplication instance;
	private static ArrayList<String> categoriesCache;
	private static HashMap<String, ArrayList<MCQuestion>> questionsCache;
	private static String selectedCategory;
	private static float score;

	@Override
	public void onCreate() {
		super.onCreate();

		instance = new QuizTimeApplication();
		
		//initialise categories and questions caches.
		categoriesCache = new ArrayList<String>();
		questionsCache = new HashMap<String, ArrayList<MCQuestion>>();
	}

	//returns singleton instance of Application.
	public static QuizTimeApplication getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return true if category list cache is empty, otherwise false.
	 */
	public static boolean categoriesListIsEmpty() {
		return (categoriesCache.isEmpty());
	}

	/**Adds category to cached list of category.
	 * @param categoryName name of category
	 * @return true if category added to cached list of categories, otherwise false;
	 */
	public static boolean addCategoryToCache(String categoryName) {
		return categoriesCache.add(categoryName);
	}

	/**
	 * @return cached list of categories.
	 */
	public static ArrayList<String> getCachedCategoriesList() {
		return categoriesCache;
	}

	/**
	 * 
	 * @param category name of category.
	 * @return true if questions for a particular category have been cached.
	 */
	public static boolean questionsCachedFor(String category) {
		return questionsCache.containsKey(category);
	}

	/**
	 * 
	 * @param category name of category
	 * @return caches questions list for cateogory
	 */
	public static ArrayList<MCQuestion> getCachedQuestions(String category) {
		return questionsCache.get(category);
	}

	/**Caches question list for a category in a hashmap.
	 * key is the name of the category.
	 * value is the questions list.
	 * 
	 * @param category name of category
	 * @param questions questions list
	 */
	public static void cacheQuestions(String category,
			ArrayList<MCQuestion> questions) {
		questionsCache.put(category, questions);
	}

	/**
	 * Sets the category the user wishes to answer questions for.
	 * @param categoryKey category selected by user.
	 */
	public static void setSelectedCategory(String categoryKey) {
		selectedCategory = categoryKey;
	}

	/**
	 * 
	 * @return name of category the user wants to answer questions for.
	 */
	public static String getSelectedCategory() {
		return selectedCategory;
	}

	/**
	 * Sets the score of the user
	 * @param s score
	 */
	public static void setScore(float s) {
		score = s;
	}

	/**
	 * 
	 * @return score of user
	 */
	public static float getScore() {
		return score;
	}
}
