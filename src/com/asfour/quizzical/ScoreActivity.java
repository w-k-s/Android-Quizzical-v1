package com.asfour.quizzical;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.asfour.quizzical.R;

/**
 * Displays the users score as a percentage.
 * 
 * @author Waqqas
 * 
 */
public class ScoreActivity extends Activity
{

    private static final long MILLISECONDS_PER_TICK = 50;
    private float percentage = 0;
    private int[] colors;

    private TextView textviewScore;
    private TextView textviewGrade;
    
    private static final String[] grades = {"D","C","B","A","A+"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.layout_score);

	colors = new int[] { getResources().getColor(R.color.red),
		getResources().getColor(R.color.orange),
		getResources().getColor(R.color.yellow),
		getResources().getColor(R.color.green),
		getResources().getColor(R.color.light_blue) };

	// Get users percentage
	percentage = QuizTimeApplication.getScore();

	initialiseUI();

	ScoreAnimation animateScore = new ScoreAnimation(percentage,
		(long) (percentage * MILLISECONDS_PER_TICK*1.4),
		MILLISECONDS_PER_TICK);
	animateScore.start();
    }

    private void initialiseUI()
    {
	// initialise textviews
	
	TextView textviewHeading = (TextView) findViewById(R.id.textview_score_heading);
	textviewScore = (TextView) findViewById(R.id.textview_score);
	textviewGrade = (TextView) findViewById(R.id.textview_grade);

	// set font of heading
	Typeface headingFont = Typeface.createFromAsset(getAssets(),
		"scribble_box_demo.ttf");
	textviewHeading.setTypeface(headingFont);
	textviewGrade.setTypeface(headingFont);

	// set font of score
	Typeface scoreFont = Typeface.createFromAsset(getAssets(),
		"ArchitectsDaughter.ttf");
	textviewScore.setTypeface(scoreFont);

    }
    
    private String getGrade(float percentage){
	return grades[(int)percentage/25];
    }

    /**
     * Animates percentage incrementing from 0 to score.
     * 
     * @author Waqqas
     * 
     */
    public class ScoreAnimation extends CountDownTimer
    {
	private int counter = 0;
	private float score = 0;

	public ScoreAnimation(float score, long millisInFuture,
		long countDownInterval)
	{
	    super(millisInFuture, countDownInterval);
	    this.score = score;
	}

	@Override
	public void onFinish()
	{
	    
	    textviewGrade.setText(String.format("[ %s ]", getGrade(score)));
	    textviewGrade.setVisibility(View.VISIBLE);
	    Animation scoreAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.score);
	    textviewGrade.startAnimation(scoreAnimation);
	}

	/**
	 * Increments a counter from 0 to earned percentage Sets textview to
	 * display counter. Maps percentage to colour and comment
	 */
	public void onTick(long millisUntilFinished)
	{
	    // map percentage to colour.
	    if(counter <= score){
		textviewScore.setTextColor(colors[counter / 25]);
	    	textviewScore.setText("" + counter + "%");
	    	counter++;
	    	
	    }
	}

    }
}
