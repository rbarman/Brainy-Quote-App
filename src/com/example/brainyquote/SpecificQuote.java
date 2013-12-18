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
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SpecificQuote extends Activity {
	
	TextView textView;
	String queryText;
	int index = 0;
	String searchType = null;
		//possible types are aboutAuthor, byAuthor, tag
	int pageNum = -1;
	int quoteNum = 0; //number of quotes
	View view;
	boolean foundInitials = false;
	ArrayList<String> topics = new ArrayList<String>();
	boolean foundTopic = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_quote);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		queryText = intent.getExtras().getString("queryText");
		
		view = (View)findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		textView = (TextView)findViewById(R.id.textView);
		
		//execute the AsyncTask
		//InitialSearch will determine if we have an author or topic query.
		//then from InitialSearch we will start other respective AsyncTasks.
		getTopics();
		new InitialSearch().execute((generateAuthorUrl(queryText)));	
	}
	
	OnTouchListener viewSwiped = new OnSwipeTouchListener() {
		public void onSwipeRight(){
			//on every swipe to the right we will get the previous quote. 

			if (index > 0) {
				Toast.makeText(SpecificQuote.this, "Swipe to Right : Previous Quote Coming!", Toast.LENGTH_SHORT).show();
				index--;
				
				if(searchType.equals("tag")) 
					new TagSearch().execute(generateTagUrl(queryText));
				else if(searchType.equals("byAuthor") && foundInitials == false) 
					new ByAuthorSearch().execute(generateAuthorUrl(queryText));
				else if(searchType.equals("byAuthor") && foundInitials == true)
					new ByAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
				else if(searchType.equals("aboutAuthor") && foundInitials == true)
					new AboutAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
				else
					new AboutAuthorSearch().execute(generateTagUrl(queryText));
			}
			else if(index == 0 && pageNum > 1) {
				Toast.makeText(SpecificQuote.this, "Swipe to Right : Previous Quote Coming!", Toast.LENGTH_SHORT).show();
				index = quoteNum - 1;
				pageNum --;
				
				if(searchType.equals("tag")) 
					new TagSearch().execute(generateTagUrl(queryText));
				else if(searchType.equals("byAuthor") && foundInitials == false) 
					new ByAuthorSearch().execute(generateAuthorUrl(queryText));
				else if(searchType.equals("byAuthor") && foundInitials == true)
					new ByAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
				else if(searchType.equals("aboutAuthor") && foundInitials == true)
					new AboutAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
				else
					new AboutAuthorSearch().execute(generateTagUrl(queryText));
			}
			else {
				Toast.makeText(SpecificQuote.this, "Swipe to Right : No more previous Quotes :(",Toast.LENGTH_SHORT).show();
			}
		}
		public void onSwipeLeft(){
			//on every swipe to the left we will get the next quote.
			index++;
			Toast.makeText(SpecificQuote.this, "Swipe to Left : Next Quote Coming!", Toast.LENGTH_SHORT).show();
			if(searchType.equals("tag")) 
				new TagSearch().execute(generateTagUrl(queryText));
			else if(searchType.equals("byAuthor") && foundInitials == false) 
				new ByAuthorSearch().execute(generateAuthorUrl(queryText));
			else if(searchType.equals("byAuthor") && foundInitials == true)
				new ByAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
			else if(searchType.equals("aboutAuthor") && foundInitials == true)
				new AboutAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
			else
				new AboutAuthorSearch().execute(generateTagUrl(queryText));
		}
		public void onSwipeBottom() {
			
		}
		public void onSwipeTop() {
			
		}
	};
	
	public String generateAuthorWithInitialsUrl(String queryText) {
		String url ="";
		String[] authorName = queryText.split(" ");
	
		if(authorName[0].length() == 2)
			url = "http://www.brainyquote.com/quotes/authors/" + 
					authorName[0].charAt(0) + "/" + authorName[0].charAt(0) + "_" + authorName[0].charAt(1);
		else 
			url = "http://www.brainyquote.com/quotes/authors/" + 
					authorName[0].charAt(0) + "/" + authorName[0].charAt(0) + "_" + authorName[0].charAt(1) + "_" + authorName[0].charAt(2);
		for(int i = 1; i < authorName.length; i++)
			url = url + "_" + authorName[i];

		if (pageNum == 1) 
			url = url + ".html";
		else 
			url = url + "_" + pageNum +  ".html";
	
		//sample : CS LEWIS
		//http://www.brainyquote.com/quotes/authors/c/c_s_lewis.html
		return url;
	}
	public String generateAuthorUrl(String queryText) {
		String url ="";
		String[] authorName = queryText.split(" ");
		url = "http://www.brainyquote.com/quotes/authors/" + authorName[0].charAt(0) + "/" + authorName[0];
		for(int i = 1; i < authorName.length; i++)
			url = url + "_" + authorName[i];
		if (index > quoteNum -1 ) {
			pageNum++;
			index = 0;
		}
		else{}
				
		if (pageNum == 1) 
				url = url + ".html";
		else 
				url = url + "_" + pageNum +  ".html";
		if (authorName[0].length() == 2 || authorName[0].length() == 3)
			foundInitials = true;
		return url;		
	}
	
	public String generateTagUrl(String queryText) {
	
		String[] keywords = queryText.split(" ");
		String url = "";
		    
	    for(String s : topics) {
	    	if(s.equalsIgnoreCase(keywords[0])) {
	    		foundTopic = true;
	    		break;
	    	}
	    }
		    
		   if (foundTopic == true) {
		   	url = "http://www.brainyquote.com/quotes/topics/topic_" + keywords[0];
		   	if (index > quoteNum -1) {
				pageNum++;
				index = 0;
			}
			else{}
		    if(pageNum == 1)
		    	url = url + ".html";
		    else
		    	url = url + pageNum + ".html";
		   }
		   else {
		   	url = "http://www.brainyquote.com/quotes/keywords/" + keywords[0];
			for(int i = 1; i < keywords.length; i++)
				url = url + "_" + keywords[i];
			
			if (index > quoteNum -1) {
				pageNum++;
				index = 0;
			}
			else{}
			
			if(pageNum == 1) 
				url = url + ".html";
			else 					
				url = url + "_" + pageNum + ".html";
		   }
		        
		return url;	
	}
	
	private class SearchWithInitials extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String url = params[0];
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				return "";
			} catch(IOException ioe) {
				return "error";
			}
		}
		@Override
		protected void onPostExecute(String message) {
			if(message.equals("error"))
				new TagSearch().execute(generateTagUrl(queryText));
			else {

				textView.setText("You are searching for " + message + " quotes");
				PopupMenu popup = new PopupMenu(SpecificQuote.this, textView);
		            //second parameter is the "anchor";
		            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu()); 
		            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
		            	
		                public boolean onMenuItemClick(MenuItem item) { 
		                
		                	switch(item.getItemId()) {
		                	case R.id.aboutAuthor:
		                		//new AboutAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
		                		//TODO: temporary fix.
		                		new AboutAuthorSearch().execute(generateTagUrl(queryText));
		                		break;
		                	case R.id.byAuthor:
		                		new ByAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
		                		break;	                	
		                	}
		                	return true;
		                }  
		               });  	     
		               popup.show();
			}	
		}
	}
	
	private class InitialSearch extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			try {
				//first run an author search...
				String url = params[0] + ".html";
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				return "";
				
			} catch(IOException ioe) {
				//if queryText gets us an invalid author page with author search, we are put here. 
				if(foundInitials == true) 
					return "found initials";
				return "error";
			}			
		}
		
		@Override
		protected void onPostExecute(String message){
			
			if(message.equals("error")){
				//we now know that the search query the user entered does NOT represent an author
				//the search query entered something that represents a keyword / topic. 
				//we can start a new AsyncTask that will run a keyword search on the searchQuery.
				
				new TagSearch().execute(generateTagUrl(queryText));
				
			}
			else if (message.equals("found initials")) {
				new SearchWithInitials().execute(generateAuthorWithInitialsUrl(queryText));
				
			}
			else {

				textView.setText("You are searching for " + message + " quotes");
				
				//we have determined that the user has entered search query that represents an author
				//Brainy Quote has 2 possibilities for authors : quotes BY the author and quotes ABOUT the author.
				//we use the pop up menu to let the user determine which category he wants. 
				 PopupMenu popup = new PopupMenu(SpecificQuote.this, textView);
		            //second parameter is the "anchor";
		            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu()); 
		            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
		            	
		                public boolean onMenuItemClick(MenuItem item) { 
		                
		                	switch(item.getItemId()) {
		                	case R.id.aboutAuthor:
		                		new AboutAuthorSearch().execute(generateTagUrl(queryText));
		                		break;
		                	case R.id.byAuthor:
		                		new ByAuthorSearch().execute(generateAuthorUrl(queryText));
		                		break;	                	
		                	}
		                	return true;
		                }  
		               });  	     
		               popup.show();
			}				
		}
	}

	private class TagSearch extends AsyncTask<String, Void, String> {
		//this method will run a keyword search. 
		@Override
		protected String doInBackground(String... params) {
			try{
				searchType = "tag";
				String url = params[0];
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");				
				quoteNum = quote.size() -1;
				return quote.get(index).text() + "\n\n--" + author.get(index).text() + "\n\n INDEX : " 
						+ index + "\n\n PAGE : " + pageNum;
						

			} catch(IOException ioe) {
				
				return "ERROR!  INVALID SEARCH";
				}
		}
		@Override
		protected void onPostExecute(String quote) {
			
				textView.setText(quote);
		}
	}
	
	private class ByAuthorSearch extends AsyncTask<String, Void, String> {
		//this method will return a quote BY the author
		@Override
		protected String doInBackground(String... params) {
			//first page : http://www.brainyquote.com/quotes/authors/m/mark_twain.html
			//2nd   page : http://www.brainyquote.com/quotes/authors/m/mark_twain_2.html
			try{
				searchType = "byAuthor";
				String url = params[0];
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();				
				Elements quotes = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				quoteNum = quotes.size() -1 ;
				return quotes.get(index).text() + "\n\n--" + queryText + "\n\n INDEX : " + index + "\n\n PAGE : " + pageNum;
					
		} catch(IOException exception){
				
				}			
			return null;
		}
		@Override
		protected void onPostExecute(String quote) {
			textView.setText(quote);
		}
	}

	private class AboutAuthorSearch extends AsyncTask<String, Void, String> {
		//this method will return a quote ABOUT the author.
		@Override
		protected String doInBackground(String... params) {
			
			try{
				searchType = "aboutAuthor";
				String url = params[0];
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();			
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");
	
				quoteNum = quote.size() -1;
				return quote.get(index).text() + "\n\n--" + author.get(index).text() + "\n\n INDEX : " + index;	
										
			} catch(IOException exception){
					return "error";
				}
		}
		@Override
		protected void onPostExecute(String quote){
			
			if(quote.equals("error")) 
				textView.setText("No available quotes about " + queryText + "!");
			else 
				textView.setText(quote);
		}
	}
	
	public void getTopics() {
		BufferedReader br = null;
	    try {
	        br = new BufferedReader(new InputStreamReader(getAssets().open("categories.txt")));
	        String word;
	        while((word=br.readLine()) != null){
	            topics.add(word); 
	        } 
	    } catch (IOException e) {}
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
    	    	
				query = query.replaceAll("[^a-zA-Z\\s]","");
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
