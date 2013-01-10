package uk.ac.hw.waqqas.quiztime;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**Displays unique random question to users.
 * Highlights answers.
 * Updates scores.
 * Starts ScoreActivity when user has answered all questions.
 * 
 * @author Waqqas
 *
 */
public class QuestionActivity extends Activity implements OnClickListener {

	private static final String TAG = QuestionActivity.class.getSimpleName();
	private static final int FONT_SIZE_NORMAL = 12;
	private static final int FONT_SIZE_BIG = 20;
	
	// UI Components:
	private TextView textviewQuestion;
	private Button[] buttons;
	private ArrayList<MCQuestion> questionsList;
	private MCQuestion question;
	private int[] randomiser;
	private int questionsAsked = 0;
	private float numCorrect = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_question);
		
		//load question list of selected category.
		questionsList = QuizTimeApplication.getCachedQuestions(QuizTimeApplication.getSelectedCategory());
	
		//if questions list is empty, show error message and end activity.
		if(questionsList.isEmpty())
		{
			Toast.makeText(this, R.string.err_no_questions, Toast.LENGTH_LONG).show();
			finish();
		}
		
		//initialise UI
		initialiseUI();
		
		//display first random question.
		question = getRandomQuestion();
		if(question!=null)
			displayQuestion();
		else
			Log.e(TAG,"Question is null");
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		
		//if activity is paused, end activtiy.
		finish();
	}

	private void initialiseUI() {
		Typeface font = Typeface.createFromAsset(getAssets(),
				"ArchitectsDaughter.ttf");

		//initialise question text view and buttons.
		textviewQuestion = (TextView) findViewById(R.id.tv_question);
		textviewQuestion.setTypeface(font);

		Button buttonA = (Button) findViewById(R.id.btn_A);
		Button buttonB = (Button) findViewById(R.id.btn_B);
		Button buttonC = (Button) findViewById(R.id.btn_C);
		Button buttonD = (Button) findViewById(R.id.btn_D);

		//store buttons in an array for convenience
		buttons = new Button[]{ buttonA, buttonB, buttonC, buttonD };

		for (Button b : buttons)
		{
			//use custom font in buttons
			b.setTypeface(font);
			//add event handler.
			b.setOnClickListener(this);
		}
	}
	
	/**
	 * -Method uses an array of ordered numbers from 0-NumOfQuestions (array called randomiser)
	 * -Gets a number (randomNumber) at a random index from the randomiser.
	 * -returns randomNumbered question from question list.
	 * -random Index has been used. Used indexes are placed at end of randomiser.
	 * -next random index is a number from 0 to unused length of Randomiser.
	 * 
	 * @return unique random question from list. If all questions have been asked, returns null.
	 */
	private MCQuestion getRandomQuestion()
	{
		//create randomiser.
		if(randomiser == null)
		{
			//randomiser is ordered list of numbers from 0 to number of questions.
			randomiser = new int[questionsList.size()];
			for(int i=0;i<randomiser.length;i++)
				randomiser[i] = i;
		}
		
		if(questionsAsked < questionsList.size())
		{
			//increment number of questions asked.
			questionsAsked++;
			//calculate unused length of randomiser.
			int unusedLengthOfRandomiser = randomiser.length - questionsAsked;
			
			//get random index from 0 to unused length of randomiser.
			int randomIndex = (int) (Math.random()*(unusedLengthOfRandomiser+1));
			
			//get random number from randomiser.
			int randomNumber = randomiser[randomIndex];
			
			//get unique random question.
			MCQuestion randomQuestion = questionsList.get(randomNumber);
			
			//put used random index at end of randomiser.
			randomiser[randomIndex] = randomiser[unusedLengthOfRandomiser];
			randomiser[unusedLengthOfRandomiser] = randomNumber;
			
			return randomQuestion;
			
		}else
			//if all questions have been returned, return null;
			return null;
	}
	
	/**
	 * Displays question in textview and choices in buttons.
	 */
	private void displayQuestion()
	{
		textviewQuestion.setText(question.getQuestion());
		for(int i=0;i<question.getAnswers().length;i++)
			buttons[i].setText(question.getAnswer(i));
		
	}
	
	/**
	 * Event handler when user clicks on a choice.
	 */
	@Override
	public void onClick(View v)
	{
		//disable buttons to prevent further touches.
		setButtonsEnabled(false);
		
		//get index of correct aswer and selected answer.
		//each button has a tag corresponding to the index of the answer choice.
		final int answer = Integer.parseInt(v.getTag().toString());
		final int correct = question.getCorrectAnswer();
		
		//if selected answer index = correct answer index, increment score
		if(answer == correct)
			numCorrect++;
		
		//highlight correct and incorrect answer.
		highlightAnswer(answer,correct,false);
		
		//wait 1 second so user can see correct answer.
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			
			@Override
			public void run()
			{
				//get next unique random question.
				MCQuestion next = getRandomQuestion();
				if(next != null)
				{
					//set question as next question
					question = next;
					
					//unhighlight answers.
					highlightAnswer(answer,correct,true);
					
					//display the next question
					displayQuestion();
					
					//don't forget to re-enable the buttons.
					setButtonsEnabled(true);
				}else
				{
					//if all questions asked, calculate percentage.
					float percentage = 100*(numCorrect/questionsList.size());
					
					//set Application score variable to pass data to scoreActivity.
					QuizTimeApplication.setScore(percentage);
					
					//start ScoreActivity.
					Intent scoreIntent = new Intent(QuestionActivity.this,ScoreActivity.class);
					startActivity(scoreIntent);
				}
			}
			
		}, 1000);
		
		
	
	}
	
	/**
	 * Sets all answer choice buttons to enabled/disabled.
	 * @param enable true to enable, false to disable.
	 */
	private void setButtonsEnabled(boolean enable)
	{
		for(Button b: buttons)
			b.setEnabled(enable);
	}
	
	/**Changes the size and color of selected answer and correct answer.
	 * Correct answer appears in green. Selected answer, if incorrect, appears in red.
	 * 
	 * @param answer index of button with select answer
	 * @param correct index of button with correct answer
	 * @param undo false to do highlighting effects, true to undo effects.
	 */
	private void highlightAnswer(int answer, int correct, boolean undo)
	{
		if(undo)
		{
			for(Button button : buttons)
			{
				//if undo flag specified, restore all buttons to original state.
				button.setTextColor(getResources().getColor(R.color.white));
				button.setTextSize(FONT_SIZE_NORMAL);
			}
			return;
		}
		
		//highlight correct answer in green.
		buttons[correct].setTextColor(getResources().getColor(R.color.green));
		buttons[correct].setTextSize(FONT_SIZE_BIG);
		if(answer!=correct)
		{
			//if users answer wasn't correct, highlight it in red.
			buttons[answer].setTextColor(getResources().getColor(R.color.red));
			buttons[answer].setTextSize(FONT_SIZE_BIG);
		}
		
	}
	
}