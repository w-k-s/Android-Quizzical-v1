package com.asfour.quizzical;

import com.asfour.quizzical.R;

public class Answer
{
    private final String text;
    private final boolean correct;

    public Answer(String text, boolean correct)
    {
	this.text = text;
	this.correct = correct;
    }

    public String getText()
    {
	return text;
    }

    public boolean isCorrect()
    {
	return correct;
    }
    
    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return String.format("Answer[text: %s,correct: %s]",text,correct);
    }
}
