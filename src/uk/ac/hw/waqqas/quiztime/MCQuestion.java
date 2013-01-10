package uk.ac.hw.waqqas.quiztime;

import java.util.ArrayList;

/**Multiple Choice Question Objects.
 * 
 * @author Waqqas
 *
 */
public class MCQuestion {

	private String question = "";
	private ArrayList<String> answers = null;
	private int correctAnswer = -1;
	
	/**
	 * 
	 * @param question Question
	 */
	public MCQuestion(String question) {
		this.question = question;
		
		answers = new ArrayList<String>();
	}

	public MCQuestion() {
		this("");
	}

	/**
	 * 
	 * @return question
	 */
	public String getQuestion()
	{
		return question;
	}
	
	/**
	 * 
	 * @param question
	 */
	public void setQuestion(String question)
	{
		this.question = question;
	}
	
	/**Adds an answer choice to anwswer list.
	 * 
	 * @param answer
	 * @return boolean value indicating whether answer was added to list or not.
	 */
	public boolean addAnswer(String answer)
	{
		return answers.add(answer);
	}
	
	/**
	 * 
	 * @param i index
	 * @return returns the answer choice stored at given index.
	 */
	public String getAnswer(int i)
	{
		return answers.get(i);
	}
	
	/**
	 * 
	 * @return answer list as an array.
	 */
	public String[] getAnswers()
	{
		return answers.toArray(new String[1]);
	}
	
	/**sets the index of the correct answer from answer list
	 * 
	 * @param index index of answer in list
	 */
	public void setCorrectAnswer(int index)
	{
		correctAnswer = index;
	}
	
	/**
	 * 
	 * @return index of correct answer in answer list.
	 */
	public int getCorrectAnswer()
	{
		return correctAnswer;
	}
	
	/**
	 * 
	 * @param i index of seleected answer
	 * @return true if the selected answer is correct.
	 */
	public boolean isCorrectAnswer(int i)
	{
		return correctAnswer == (i);
	}
}
