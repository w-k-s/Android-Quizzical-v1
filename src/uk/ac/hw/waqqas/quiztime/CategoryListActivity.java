package uk.ac.hw.waqqas.quiztime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**Downloads categories, displays them to the user as a list.
 * 
 * @author Waqqas
 *
 */
public class CategoryListActivity extends Activity implements OnItemClickListener {

	static final String TAG = CategoryListActivity.class.getSimpleName();
	static final String QUESTIONS_URL = "http://waqqasquiz.web44.net/questions/";

	private ListView categoryListView;

	/**
	 * Initializes ListView
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_category_list);
		
		//initialise listview
		categoryListView = (ListView) findViewById(R.id.listview_categories);
		categoryListView.setOnItemClickListener(this);
		
		//load font
		Typeface font = Typeface.createFromAsset(getAssets(), "ArchitectsDaughter.ttf"); 
		
		//set default list view text
		TextView textviewEmpty = new TextView(this);
		textviewEmpty.setText("Categories could not be downloaded.\nPlease check your internet connection.");
		textviewEmpty.setTypeface(font);
		
		categoryListView.setEmptyView(textviewEmpty);
		
		//set font on select category textview
		TextView selectCategoryTextView = (TextView) findViewById(R.id.textview_select_category);
		selectCategoryTextView.setTypeface(font);
		
		//download categories
		new DownloadCategoriesTask().execute();

	}


	/**
	 * Event Handler when a category is clicked.
	 * Loads questions and starts QuestionActivity
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		
		if(position < QuizTimeApplication.getCachedCategoriesList().size())
		{
			//get category name
			String selectedCategory = QuizTimeApplication.getCachedCategoriesList().get(position);
			
			//build URL
			String sourceURL = QUESTIONS_URL+selectedCategory+".xml";
			
			//load questions from URL
			new ParseQuestionsTask().execute(sourceURL,selectedCategory);
		}else
			Log.e(TAG,"ArrayIndexOutOfBoundsException");
	}
	
	/**Downlaods categories asynchronously
	 * 
	 * @author Waqqas
	 *
	 */
	private class DownloadCategoriesTask extends AsyncTask<Void, Integer, Boolean> {

		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			//show progress dialog
			progressDialog = ProgressDialog.show(CategoryListActivity.this,"Loading","Downloading Categories",true);
			progressDialog.setCancelable(true);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {

			Log.e(TAG,QuizTimeApplication.getCachedCategoriesList().toString());
			
			//check if categories have been cached.
			if (QuizTimeApplication.categoriesListIsEmpty())
			{
				//if cached copy of cateogory list does not exist, 
				//get categories.
				Log.e(TAG,"Retrieving categories");
				String html = getHTML(QUESTIONS_URL);
				
				//if content downloaded
				if(!html.equals(""))
				{
					//extract category names from html
					//add to category list
					populateCategories(html);
				}
				else
					return false;
			}
			//if categories have been cached, return true.
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//done downloading. hide progress dialog.
			progressDialog.dismiss();
			
			if(!result)
			{
				//if categories could not be downloaded, display error message to user.
				Toast.makeText(CategoryListActivity.this, R.string.err_fetching_categories, Toast.LENGTH_LONG).show();
				return;
			}
			
			//if categories downloaded, set adapter to list view.
			//adapter subclasses in order to use custom fonts in listView.
			CategoryListAdapter adapter = new CategoryListAdapter(CategoryListActivity.this,QuizTimeApplication.getCachedCategoriesList());
			
			categoryListView.setAdapter(adapter);
		}

		/**
		 * retrieves HTML from given URL
		 * @param uri URL
		 * @return HTML retrieved
		 */
		private String getHTML(String uri) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(uri);
				HttpResponse response = client.execute(request);

				String html = "";

				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line);
				}
				in.close();
				html = str.toString();
				return html;
			} catch (IOException ioe) {
				Log.e(CategoryListActivity.TAG, ioe.getMessage());
				return "";
			}
		}

		/**Uses Regex to extract the names of hyperlinked XML files.
		 * Adds the name of the files to the categories list.
		 * 
		 * @param htmlFile HTML from which hyperlinked XML files must be extracted.
		 */
		private void populateCategories(String htmlFile) {
			// The part of the regex that is in paranthesis is stored in group.
			Pattern p = Pattern.compile("href=\"([A-Za-z]+?).xml\"");
			Matcher m = p.matcher(htmlFile);
			while (m.find()) { // Find each match in turn;
				String name = m.group(1);
				QuizTimeApplication.addCategoryToCache(name);
			}
		}

	}

	/**Uses SAXParser to read questions XML file.
	 * QuestionList is cached.
	 * 
	 * @author Waqqas
	 *
	 */
	private class ParseQuestionsTask extends AsyncTask<String, Integer, String>
	{
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			//show progress dialog to indicate questions are being downloaded.
			progressDialog = ProgressDialog.show(CategoryListActivity.this,"Loading","Downloading Questions",true);
			progressDialog.setCancelable(true);
		}
		
		/**
		 * @return Name of selected category
		 */
		@Override
		protected String doInBackground(String... params)
		{
			//if cached copy of questions exist, don't download.
			if(!QuizTimeApplication.questionsCachedFor(params[1]))
			{
				//get questions list from parsed XML
				ArrayList<MCQuestion> questions = parseXML(params[0]);
				if (questions!=null && !questions.isEmpty())
				{
					//cache questionsList for the category if questions list is not empty or null.
					QuizTimeApplication.cacheQuestions(params[1], questions);
					return params[1];
				}
				return null;	
			}
			return params[1];
		}
		

		@Override
		protected void onPostExecute(String result)
		{
			//questions loaded. hide progress dialog.
			progressDialog.dismiss();
			
			if(result==null)
			{
				//if downloading unsuccessful, show error message to user.
				Toast.makeText(CategoryListActivity.this, R.string.err_fetching_questions, Toast.LENGTH_LONG).show();
				return;
			}else
			{
				//set selected category in application to transfer data to QuestionActivity,
				QuizTimeApplication.setSelectedCategory(result);
				
				//start QuestionActivity.
				Intent questionIntent = new Intent(CategoryListActivity.this,QuestionActivity.class);
				startActivity(questionIntent);
			}
		}
		
		/**Uses SAXParser to parse online XML file and extract questions.
		 * 
		 * @param uri URL where questions are stored
		 * @return list of questions.
		 */
		private ArrayList<MCQuestion> parseXML(String uri)
		{
			try{
				URL url = new URL(uri);
				
				//Load parser.
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader reader = sp.getXMLReader();
				
				//set XML Parsing event handler
				XMLContentHandler handler = new XMLContentHandler();
				reader.setContentHandler(handler);
				
				//parse xml
				reader.parse(new InputSource(url.openStream()));
				
				//return extracted questions list
				return handler.getQuestions();
				
			}catch(MalformedURLException mue)
			{
				Log.e(TAG,mue.getMessage());
				return null;
			} catch (SAXException se) {
				Log.e(TAG,se.getMessage());
				return null;
			} catch (ParserConfigurationException pce) {
				Log.e(TAG, pce.getMessage());
				return null;
			} catch (IOException ioe) {
				Log.e(TAG, ioe.getMessage());
				return null;
			}
		
		}
	}

	
}
