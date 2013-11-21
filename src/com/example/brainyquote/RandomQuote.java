package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.renderscript.Element;
import android.support.v4.app.NavUtils;

public class RandomQuote extends Activity {
	

	//This is the activity launched when the user selects the randomButton on main activity.
	Handler handler;
	TextView textView;
	ArrayList<String> categories = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_quote);
		// Show the Up button in the action bar.
		setupActionBar();
	
		handler = new Handler();
		textView = (TextView)findViewById(R.id.textView);
		//execute the async task
		new GetCategories().execute();
		
	}
	//need to create an AsyncTask so that the UI thread does not have to do extra work
	//if we make the UI thread to the Jsoup.connect, the application will crash.
	//currently I am just pulling the title from Google as a simple test. 
	private class GetCategories extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			 
			 BufferedReader br = null;
			    try {
			        br = new BufferedReader(new InputStreamReader(getAssets().open("categories.txt")));
			        String word;
			        while((word=br.readLine()) != null){
			            categories.add(word); 
			        }
			        //categories ArrayList now has all of the different categories within categories.txt
			        String topic = categories.get((int) (Math.random() * (categories.size() -1)));
			        Document doc;
			         try {
			        	 String url = "http://www.brainyquote.com/quotes/topics/topic_" + topic + ".html";
			        	 //sample url for age quote : http://www.brainyquote.com/quotes/topics/topic_age.html
			                doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
			                
			                
			             Elements elm = doc.select(".boxyPaddingBig span.bqQuoteLink a"); 
			             
			             return elm.get((int) (Math.random()*elm.size()-1)).text();
			         } catch (IOException e) {
			                return null;
			            }
			    }
			    catch(IOException e) {
			        return null;
			    }
		}
		@Override
		protected void onPostExecute(String title){
			TextView textView = (TextView)findViewById(R.id.textView);
			textView.setText(title);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.random_quote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
