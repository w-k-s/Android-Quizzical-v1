package com.asfour.quizzical;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.asfour.quizzical.R;
import com.asfour.utilities.NetworkUtilities;

/**
 * Downloads categories, displays them to the user as a list.
 * 
 * @author Waqqas
 * 
 */
public class CategoryListActivity extends Activity implements
	OnItemClickListener
{

    static final String TAG = CategoryListActivity.class.getSimpleName();
    static final String CATEGORIES_URL = "http://asfour-quizzical.appspot.com/categories";
    static final String QUESTIONS_URL = "http://asfour-quizzical.appspot.com/questions?category=%s";
    static final String JSON_CATEGORY_NAME = "Name";
    
    private TextView titleTextView;
    private TextView downloadMessageTextView;
    private ProgressBar downloadingProgressBar;
    private ListView categoryListView;
    private LinearLayout downloadingLayout;

    private boolean categoriesDownloaded = false;
    /**
     * Initializes ListView
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	

	initView();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        
        if(!categoriesDownloaded){
            new DownloadCategoriesTask().execute();
        }
    }
    
    private void initView(){
	setContentView(R.layout.layout_category_list);
	titleTextView = (TextView) findViewById(R.id.textview_title);
	downloadMessageTextView = (TextView) findViewById(R.id.textview_download_message);
	downloadingProgressBar = (ProgressBar) findViewById(R.id.progressbar_downloading);
	categoryListView = (ListView) findViewById(R.id.listview_categories);
	downloadingLayout = (LinearLayout) findViewById(R.id.layout_downloading);
	
	titleTextView.setText(R.string.app_name);
	
	Typeface titleFont = Typeface.createFromAsset(getAssets(), "Bender-Inline.otf");
	Typeface chalkFont = Typeface.createFromAsset(getAssets(),
		"ArchitectsDaughter.ttf");
	
	titleTextView.setTypeface(titleFont);
	downloadMessageTextView.setTypeface(chalkFont);
	categoryListView.setOnItemClickListener(this);
	
    }
    
    /**
     * Event Handler when a category is clicked. Loads questions and starts
     * QuestionActivity
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
	    long id)
    {

	if (position < QuizTimeApplication.getCachedCategoriesList().size())
	{
	    // get category name
	    String selectedCategory = QuizTimeApplication
		    .getCachedCategoriesList().get(position);

	    // build URL
	    String sourceURL = getQuestionsUrlForCategory(selectedCategory);

	    // load questions from URL
	    new ParseQuestionsTask().execute(sourceURL, selectedCategory);
	} else
	    Log.e(TAG, "ArrayIndexOutOfBoundsException");
    }
    
    private String getQuestionsUrlForCategory(String category){
	    return String.format(QUESTIONS_URL, category);
	}

    /**
     * Downlaods categories asynchronously
     * 
     * @author Waqqas
     * 
     */
    private class DownloadCategoriesTask extends
	    AsyncTask<Void, Integer, Boolean>
    {

	//private ProgressDialog progressDialog;

	@Override
	protected void onPreExecute()
	{
	    boolean isOnline = NetworkUtilities.isConnectedToInternet(getApplicationContext());
	    if(!isOnline){
		LinearLayout.LayoutParams progressBarParams = (LinearLayout.LayoutParams) downloadingProgressBar.getLayoutParams();
		LinearLayout.LayoutParams downloadMessageParams = (LinearLayout.LayoutParams) downloadMessageTextView.getLayoutParams();
		progressBarParams.weight = 0;
		downloadMessageParams.weight = 1;
		downloadMessageTextView.setText("No Internet Connection");
		this.cancel(true);
	    }
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{

	    Log.e(TAG, QuizTimeApplication.getCachedCategoriesList().toString());
	    // check if categories have been cached.
	    boolean categoriesCached = !QuizTimeApplication
		    .categoriesListIsEmpty();
	    if (!categoriesCached)
	    {
		// if cached copy of cateogory list does not exist,
		// get categories.
		Log.e(TAG, "Retrieving categories");
		List<String> categories = getCategories(QUESTIONS_URL);

		// if content downloaded
		if (!categories.isEmpty())
		{
		    QuizTimeApplication.setCategoriesCache(categories);
		    categoriesCached = true;
		}
	    }
	    // if categories have been cached, return true.
	    return categoriesCached;
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
	    super.onPostExecute(result);
	    // done downloading. hide progress dialog.
	    //progressDialog.dismiss();

	    if (!result)
	    {
		// if categories could not be downloaded, display error message
		// to user.
		Toast.makeText(CategoryListActivity.this,
			R.string.err_fetching_categories, Toast.LENGTH_LONG)
			.show();
		categoryListView.setAdapter(new CategoryListAdapter(CategoryListActivity.this,Collections.<String>emptyList()));
		return;
	    }

	    RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) titleTextView.getLayoutParams();
	    titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    downloadingLayout.setVisibility(View.GONE);
	    // if categories downloaded, set adapter to list view.
	    // adapter subclasses in order to use custom fonts in listView.
	    CategoryListAdapter adapter = new CategoryListAdapter(
		    CategoryListActivity.this,
		    QuizTimeApplication.getCachedCategoriesList());

	    categoryListView.setAdapter(adapter);
	    categoriesDownloaded = true;
	}

	@Override
	protected void onCancelled(Boolean result)
	{
	    // TODO Auto-generated method stub
	   // super.onCancelled(result);
//	    if(this.progressDialog != null)
//		this.progressDialog.dismiss();
	}

	
	/**
	 * retrieves HTML from given URL
	 * 
	 * @param uri
	 *            URL
	 * @return HTML retrieved
	 */
	private List<String> getCategories(String uri)
	{
	    try
	    {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(CATEGORIES_URL);
		HttpResponse response = client.execute(request);

		String categoriesJsonString = EntityUtils.toString(response
			.getEntity());
		JSONParser parser = new JSONParser();
		JSONArray categoriesJsonArray = (JSONArray) parser.parse(categoriesJsonString);;

		List<String> categories = new ArrayList<String>();
		for (int i = 0; i < categoriesJsonArray.size(); i++)
		{
		    JSONObject o = (JSONObject) categoriesJsonArray.get(i);
		    String categoryName = (String) o.get(JSON_CATEGORY_NAME);
		    categories.add(categoryName);
		}
		return categories;
	    } catch (Exception e)
	    {
		Log.e(CategoryListActivity.TAG, ""+e.getMessage());
		return Collections.<String> emptyList();
	    }
	}

    }

    /**
     * Uses SAXParser to read questions XML file. QuestionList is cached.
     * 
     * @author Waqqas
     * 
     */
    private class ParseQuestionsTask extends AsyncTask<String, Integer, String>
    {
	private ProgressDialog progressDialog;

	@Override
	protected void onPreExecute()
	{
	   if(!NetworkUtilities.isConnectedToInternet(getApplicationContext())){
	       Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_LONG).show();
	       return;
	   }
	    
	    // show progress dialog to indicate questions are being downloaded.
	    progressDialog = ProgressDialog.show(CategoryListActivity.this,
		    "Loading", "Downloading Questions", true);
	    progressDialog.setCancelable(true);
	}

	/**
	 * @return Name of selected category
	 */
	@Override
	protected String doInBackground(String... params)
	{
	    // if cached copy of questions exist, don't download.
	    if (!QuizTimeApplication.questionsCachedFor(params[1]))
	    {
		// get questions list from parsed XML
		ArrayList<Question> questions = parseXML(params[0]);
		if (questions != null && !questions.isEmpty())
		{
		    // cache questionsList for the category if questions list is
		    // not empty or null.
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
	    // questions loaded. hide progress dialog.
	    progressDialog.dismiss();

	    if (result == null)
	    {
		// if downloading unsuccessful, show error message to user.
		Toast.makeText(CategoryListActivity.this,
			R.string.err_fetching_questions, Toast.LENGTH_LONG)
			.show();
		return;
	    } else
	    {
		// set selected category in application to transfer data to
		// QuestionActivity,
		QuizTimeApplication.setSelectedCategory(result);

		// start QuestionActivity.
		Intent questionIntent = new Intent(CategoryListActivity.this,
			QuestionActivity.class);
		startActivity(questionIntent);
	    }
	}

	/**
	 * Uses SAXParser to parse online XML file and extract questions.
	 * 
	 * @param uri
	 *            URL where questions are stored
	 * @return list of questions.
	 */
	private ArrayList<Question> parseXML(String uri)
	{
	    try
	    {
		URL url = new URL(uri);

		// Load parser.
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader reader = sp.getXMLReader();

		// set XML Parsing event handler
		XMLContentHandler handler = new XMLContentHandler();
		reader.setContentHandler(handler);

		// parse xml
		reader.parse(new InputSource(url.openStream()));

		// return extracted questions list
		return handler.getQuestions();

	    } catch (MalformedURLException mue)
	    {
		Log.e(TAG, mue.getMessage());
		return null;
	    } catch (SAXException se)
	    {
		Log.e(TAG, se.getMessage());
		return null;
	    } catch (ParserConfigurationException pce)
	    {
		Log.e(TAG, pce.getMessage());
		return null;
	    } catch (IOException ioe)
	    {
		Log.e(TAG, ioe.getMessage());
		return null;
	    }

	}

    }

}
