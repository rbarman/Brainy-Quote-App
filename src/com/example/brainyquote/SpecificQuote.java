package com.example.brainyquote;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SpecificQuote extends Activity {
	
	TextView textView;
	Button previousButton;
	Button nextButton;
	String queryText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_quote);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		queryText = intent.getExtras().getString("queryText");
		
	
		textView = (TextView)findViewById(R.id.textView);
//		previousButton = (Button)findViewById(R.id.previousButton);
//		nextButton = (Button)findViewById(R.id.nextButton);
		
		//execute the AsyncTask
		//InitialSearch will determine if we have an author or topic query.
		//then from InitialSearch we will start other respective AsyncTasks.
		new InitialSearch().execute();	
	}
	
	private class InitialSearch extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				//we have queryText already.. 
				//first run an author search...
				//sample author url : http://www.brainyquote.com/quotes/authors/m/mark_twain.html
				
				//splits queryText into 2 parts for first and last name;
				String [] authorName = queryText.split(" ");
				if(authorName.length > 1) {
					String authorUrl = "http://www.brainyquote.com/quotes/authors/" 
							+ authorName[0].charAt(0) + "/" + authorName[0] + "_" + authorName[1] + ".html";
							
					Document doc = Jsoup.connect(authorUrl).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
						
					return queryText;
				}
				else if (authorName.length == 1){
					//user has entered a string with 1 word. 
					String authorUrl = "http://www.brainyquote.com/quotes/authors/" + authorName[0].charAt(0) + "/" 
							+ authorName[0] + ".html";
					Document doc = Jsoup.connect(authorUrl).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
					
					return queryText;
				}
				else {
					//user has entered nothing. (just spaces)
					return "You have not entered anything";
				}
				
				
				
			}catch(IOException ioe) {
				//if queryText gets us an invalid author page with author search, we are put here. 
				return "error";
			}
			
			//return null;
		}
		
		@Override
		protected void onPostExecute(String message){
			
			if(message.equals("error")){
				//we now know that the search query the user entered does NOT represent an author
				//the search query entered something that represents a keyword / topic. 
				//we can start a new AsyncTask that will run a keyword search on the searchQuery.
				
				new KeywordSearch().execute();
				
			}
			else {
				textView.setText("You are searching for " + message + " quotes");
				
				//we have determined that the user has entered search query that represents an author
				//Brainy Quote has 2 possibilites for authors : quotes BY the author and quotes ABOUT the author.
				//we use the pop up menu to let the user determine which category he wants. 
				 PopupMenu popup = new PopupMenu(SpecificQuote.this, textView);
		            //second parameter is the "anchor";
		            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu()); 
		            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
		            	
		                public boolean onMenuItemClick(MenuItem item) { 
		                
		                	switch(item.getItemId()) {
		                	case R.id.aboutAuthor:
		                		new AboutAuthorSearch().execute();
		                		break;
		                	case R.id.byAuthor:
		                		new ByAuthorSearch().execute();
		                		break;	                	
		                	}
		                	return true;
		                }  
		               });  	     
		               popup.show();
			}	
		}
	}

	private class KeywordSearch extends AsyncTask<Void, Void, String> {
		//this method will run a keyword search. 
		
		//for now I will do a single word keyword search.. (assume queryText is a single word...).
		@Override
		protected String doInBackground(Void... params) {
			try{
				//sample keyword search : http://www.brainyquote.com/quotes/keywords/random.html
				String url = "http://www.brainyquote.com/quotes/keywords/" + queryText + ".html";
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");
				
				return quote.get(0).text() + "\n\n--" + author.get(0).text();
				
			} catch(IOException ioe) {
				return "ERROR!  INVALID SEARCH";
			}
			
		}
		@Override
		protected void onPostExecute(String quote) {
			textView.setText(quote);
		}
	}
	
	private class ByAuthorSearch extends AsyncTask<Void, Void, String> {
		//this method will return a quote BY the author
		@Override
		protected String doInBackground(Void... params) {
			
			try{
				String [] authorName = queryText.split(" ");
				String authorUrl = "http://www.brainyquote.com/quotes/authors/" 
						+ queryText.charAt(0) + "/" + authorName[0] + "_" + authorName[1] + ".html";
				
				Document doc = Jsoup.connect(authorUrl).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				
				Elements quotes = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				return quotes.get(0).text();
				//only returns the first quote.
				//TODO: need to somehow increment when user presses next button. 
				
			}catch(IOException exception){
				
			}			
			return null;
		}
		@Override
		protected void onPostExecute(String quote) {
			textView.setText(quote);
		}
	}

	private class AboutAuthorSearch extends AsyncTask<Void, Void, String> {
		//this method will return a quote ABOUT the author.
		@Override
		protected String doInBackground(Void... params) {
			
			try{
				String [] authorName = queryText.split(" ");
				//Sample keyword search : http://www.brainyquote.com/quotes/keywords/mark_twain.html
				String url = "http://www.brainyquote.com/quotes/keywords/" + authorName[0] 
						+ "_" + authorName[1] + ".html";
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");
				
				return quote.get(0).text() + "\n\n--" + author.get(0).text();
				
			} catch(IOException exception){
				
			}
			
			
			return null;
		}
		@Override
		protected void onPostExecute(String quote){
			textView.setText(quote);
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
		getMenuInflater().inflate(R.menu.specific_quote, menu);
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
