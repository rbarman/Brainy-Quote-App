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
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class RandomQuote extends Activity {
	

	//This is the activity launched when the user selects the randomButton on main activity.
	
	TextView textView;
	Button nextRandom;
	ArrayList<String> categories = new ArrayList<String>();
	ArrayList<String> randomQuotes = new ArrayList<String>();
	View view;
	int currentIndex = -1;
	int quotePlaceHolder = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_quote);
		// Show the Up button in the action bar.
		setupActionBar();		
		textView = (TextView)findViewById(R.id.textView);
		nextRandom = (Button)findViewById(R.id.nextRandom);
		
		view = (View)findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		
		//on Click Listener for nextRandom Button
		nextRandom.setOnClickListener(new View.OnClickListener(){
			//goal is to replace the current quote with a new random quote every time the user presses nextRandom.
			@Override
			public void onClick(View v) {
				
				textView.setText(""); //clear the current textView because we will replace it with a new Quote.
				Toast.makeText(getApplicationContext(), "New Random Quote Coming...", Toast.LENGTH_SHORT).show();				//
				//execute the async task to get a new quote...!
				new GetQuote().execute();
			}			
		});

		//execute the async task
		new GetQuote().execute();		
	}
	
	OnTouchListener viewSwiped = new OnSwipeTouchListener() {
		public void onSwipeRight(){
			//TODO : implement a swipe to right where the user can see the previous random qutoe? 
			//on every swipe to the left we will get the next random quote. 
			Toast.makeText(RandomQuote.this, "Swipe to Right : Previous Random Quote Coming!", Toast.LENGTH_SHORT).show();
			//textView.setText()
			if(currentIndex != -1 || currentIndex != 0) {
				currentIndex --;
				textView.setText(randomQuotes.get(currentIndex));
			}
			
		}
		public void onSwipeLeft(){
			//on every swipe to the left we will get the next random quote.
			if(currentIndex < quotePlaceHolder) {
				Toast.makeText(RandomQuote.this, "Swipe to Left : Next Random Quote Coming!", Toast.LENGTH_SHORT).show();
				currentIndex ++;
				textView.setText(randomQuotes.get(currentIndex));
			}
			else {
				Toast.makeText(RandomQuote.this, "Swipe to Left : New Random Quote Coming!", Toast.LENGTH_SHORT).show();
				new GetQuote().execute();
			}
			
			
			//textView.setText(""); //clear the current textView because we will replace it with a new Quote.

			//execute the async task to get a new quote...!
			
		}
		public void onSwipeBottom() {
			
		}
		public void onSwipeTop() {
			
		}
	};
	
	//need to create an AsyncTask so that the UI thread does not have to do extra work
	//if we make the UI thread to the Jsoup.connect, the application will crash.
	
	private class GetQuote extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			 
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
			                
			                
			             Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
			             Elements author = doc.select(".boxyPaddingBig span.bodybold a");
			             
			             int randIndex = (int) (Math.random()*quote.size()-1);
			             
			             randomQuotes.add(quote.get(randIndex).text() +"\n\n - "
					             + author.get(randIndex).text());
			             currentIndex ++;
			             quotePlaceHolder ++;
			             
			             return quote.get(randIndex).text() +"\n\n - "
			             + author.get(randIndex).text();
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
		// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        
        //setting hint value via .java.... should be able to do in .XML....
        searchView.setQueryHint("Search Brainy Quote!");
        
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
    	    @Override
    	    public boolean onQueryTextChange(String newText) {
    	        // Do something
    	        return true;
    	    }

    	    @Override
    	    public boolean onQueryTextSubmit(String query) {
    	    	
   
    	        Toast toast = Toast.makeText(getApplicationContext(), query + " Quotes coming soon...", Toast.LENGTH_SHORT);
    	        toast.setGravity(Gravity.CENTER, 0, 0);
    	        toast.show();
    	        
    	      //Now a new intent will be created to go to the SpecificQuote.java activity! 
				Intent intent = new Intent(getBaseContext(), SpecificQuote.class);
				
				//we will pass the value of query as a string variable called queryText to the SpecificQuote activity
				//so the SpecificQuote activity can use the queryText as the search parameter. 
				intent.putExtra("queryText", query);
				startActivity(intent);	
    	        
    	        return true;
    	    }
    	    
    	};
    	searchView.setOnQueryTextListener(queryTextListener);
        
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
