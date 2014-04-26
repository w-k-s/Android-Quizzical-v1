package com.asfour.quizzical;

import com.asfour.quizzical.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Multiple Choice Question Objects.
 * 
 * @author Waqqas
 * 
 */
public class Question
{

    private String question = "";
    private List<Answer> answers = null;

    /**
     * 
     * @param question
     *            Question
     */
    public Question(String question)
    {
	this.question = question;
	answers = new ArrayList<Answer>();
    }

    public Question()
    {
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

    /**
     * Adds an answer choice to anwswer list.
     * 
     * @param answer
     * @return boolean value indicating whether answer was added to list or not.
     */
    public boolean addAnswer(Answer answer)
    {
	return answers.add(answer);
    }

    /**
     * 
     * @param i
     *            index
     * @return returns the answer choice stored at given index.
     */
    public Answer getAnswer(int i)
    {
	return answers.get(i);
    }

    /**
     * 
     * @return answer list as an array.
     */
    public Answer[] getAnswers()
    {
	return answers.toArray(new Answer[answers.size()]);
    }
    
    public void shuffleAnswer(){
	Collections.shuffle(this.answers);
    }
    
    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return String.format("Question[Question: %s, Answers: %s]", question,answers );
    }
}
