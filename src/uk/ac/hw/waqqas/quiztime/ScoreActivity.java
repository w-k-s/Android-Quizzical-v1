package uk.ac.hw.waqqas.quiztime;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

/**Displays the users score as a percentage.
 * 
 * @author Waqqas
 *
 */
public class ScoreActivity extends Activity {

	private static final int ANIMATION_DURATION_SECONDS = 2;
	private float percentage = 0;
	private int[] colors;
	private String[] comments;
	private int counter = 0;

	
	private TextView textviewScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_score);
		
		initialiseUI();
		
		//Color array.
		//Perctentage is displayed in different colours, 
		//starting from red if the percentage is low
		//going till green if the percentage is high.
		colors = new int[]{getResources().getColor(R.color.red),
				getResources().getColor(R.color.orange),
				getResources().getColor(R.color.yellow),
				getResources().getColor(R.color. green),
				getResources().getColor(R.color.light_blue)};

		
		//Comments are displayed along with percentage.
		//Percentage mapped to comments array.
		comments = new String[]{"Better luck next time"/*HAHA Sucker!*/,
		                      "Better luck next time :("/*emoticons make everyone happy*/,
		                      "Not bad :)",
		                      "Well Done!",
		                      "WOW!" /*OMG! NERD!*/};
		
		//Get users percentage
		percentage = QuizTimeApplication.getScore();
		
		//Use countdown timer to animate percentage incrementing from 0 to user's score.
		ScoreAnimation animateScore = new ScoreAnimation(5000, ANIMATION_DURATION_SECONDS * 10);
		animateScore.start();
	}

	private void initialiseUI()
	{
		//initialise textviews
		TextView textviewHeading = (TextView) findViewById(R.id.textview_score_heading);
		textviewScore = (TextView) findViewById(R.id.textview_score);
		
		//set font of heading
		Typeface headingFont = Typeface.createFromAsset(getAssets(), "scribble_box_demo.ttf");
		textviewHeading.setTypeface(headingFont);
		
		//set font of score
		Typeface scoreFont = Typeface.createFromAsset(getAssets(), "ArchitectsDaughter.ttf");
		textviewScore.setTypeface(scoreFont);
		
	}
	
	/**Animates percentage incrementing from 0 to score.
	 * 
	 * @author Waqqas
	 *
	 */
	public class ScoreAnimation extends CountDownTimer
	{

		public ScoreAnimation(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}


		@Override
		public void onFinish() {
			
			
		}

		/**
		 * Increments a counter from 0 to earned percentage
		 * Sets textview to display counter.
		 * Maps percentage to colour and comment
		 */
		public void onTick(long millisUntilFinished) {
			if(counter <= percentage)
			{
				//map percentage to colour.
				textviewScore.setTextColor(colors[counter/25]);
				textviewScore.setText(""+counter+"%");
				
				if(counter==percentage)
				{
					//map percentage to comment.
					int index = (int) percentage/25;
					Toast.makeText(ScoreActivity.this,comments[index],Toast.LENGTH_SHORT).show();
				}
				
				counter++;
			}
				
		}
		
	}
}
