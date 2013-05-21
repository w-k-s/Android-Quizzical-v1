package uk.ac.hw.waqqas.quiztime;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**Splashscreen displayed when application is first launched.
 * 
 * @author Waqqas
 *
 */
public class SplashActivity extends Activity {

	private static final int SPLASH_DURATION = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set layout
		setContentView(R.layout.layout_splash);
		
		//use handler to start CategoryListActivity after splash duration time.
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			public void run()
			{
				//Start CategoryListActivity.
				Intent mainIntent = new Intent(SplashActivity.this,
						CategoryListActivity.class);
				startActivity(mainIntent);
			}
		}, SPLASH_DURATION);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		//when app is paused, finish this activity.
		this.finish();
	}
	
	/*
	private void initialiseUI()
	{
		//initialise textviews
		TextView textviewAppName = (TextView) findViewById(R.id.textview_appname);
		TextView textviewDevName = (TextView) findViewById(R.id.textview_developer);
		
		//set app name text and font
		Typeface appNameFont = Typeface.createFromAsset(getAssets(), "Bender-Inline.otf");
		textviewAppName.setTypeface(appNameFont);
		
		//set developer name and font
		Typeface devNameFont = Typeface.createFromAsset(getAssets(), "QuaverSerif.otf");
		textviewDevName.setTypeface(devNameFont);
	}*/
	
}
