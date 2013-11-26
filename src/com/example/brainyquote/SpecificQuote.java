package com.example.brainyquote;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SpecificQuote extends Activity {
	
	TextView textView;
	ImageButton prevButton;
	ImageButton nextButton;
	String queryText;
	int index = 0;
	String searchType = null;
		//possible types are aboutAuthor, byAuthor, keyword
	int pageNum = 0;
	int quoteNum = 0; //number of quotes
	View view;

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
		prevButton = (ImageButton)findViewById(R.id.prevButton);
		nextButton = (ImageButton)findViewById(R.id.nextButton);
		//onClickListener for nextButton;
		nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				index++;
				
				if(searchType.equals("keyword")) 
					new KeywordSearch().execute();
				else if(searchType.equals("byAuthor")) 
					new ByAuthorSearch().execute();
				else
					new AboutAuthorSearch().execute();
			}
		});

		//onClickListener for prevButton;
		prevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (index > 0) {
					index--;
					
					if(searchType.equals("keyword")) 
						new KeywordSearch().execute();
					else if(searchType.equals("byAuthor")) 
						new ByAuthorSearch().execute();
					else
						new AboutAuthorSearch().execute();
				}
				else if(index == 0 && pageNum > 1) {
					index = quoteNum - 1;
					pageNum --;
					
					if(searchType.equals("keyword")) 
						new KeywordSearch().execute();
					else if(searchType.equals("byAuthor")) 
						new ByAuthorSearch().execute();
					else
						new AboutAuthorSearch().execute();
				}
			}
		});
		
		
		//execute the AsyncTask
		//InitialSearch will determine if we have an author or topic query.
		//then from InitialSearch we will start other respective AsyncTasks.
		new InitialSearch().execute();	
	}
	
	OnTouchListener viewSwiped = new OnSwipeTouchListener() {
		public void onSwipeRight(){
			//on every swipe to the right we will get the previous quote. 

			if (index > 0) {
				Toast.makeText(SpecificQuote.this, "Swipe to Right : Previous Quote Coming!", Toast.LENGTH_SHORT).show();
				index--;
				
				if(searchType.equals("keyword")) 
					new KeywordSearch().execute();
				else if(searchType.equals("byAuthor")) 
					new ByAuthorSearch().execute();
				else
					new AboutAuthorSearch().execute();
			}
			else if(index == 0 && pageNum > 1) {
				Toast.makeText(SpecificQuote.this, "Swipe to Right : Previous Quote Coming!", Toast.LENGTH_SHORT).show();
				index = quoteNum - 1;
				pageNum --;
				
				if(searchType.equals("keyword")) 
					new KeywordSearch().execute();
				else if(searchType.equals("byAuthor")) 
					new ByAuthorSearch().execute();
				else
					new AboutAuthorSearch().execute();
			}
			else {
				Toast.makeText(SpecificQuote.this, "Swipe to Right : No more previous Quotes :(",Toast.LENGTH_SHORT).show();
			}
			
		}
		public void onSwipeLeft(){
			//on every swipe to the left we will get the next quote.
			index++;
			Toast.makeText(SpecificQuote.this, "Swipe to Left : Next Quote Coming!", Toast.LENGTH_SHORT).show();
			if(searchType.equals("keyword")) 
				new KeywordSearch().execute();
			else if(searchType.equals("byAuthor")) 
				new ByAuthorSearch().execute();
			else
				new AboutAuthorSearch().execute();
			
		}
		public void onSwipeBottom() {
			
		}
		public void onSwipeTop() {
			
		}
	};
	
	private class InitialSearch extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				//we have queryText already.. 
				//first run an author search...
				//sample author url : http://www.brainyquote.com/quotes/authors/m/mark_twain.html
				
				
				String[] authorName = queryText.split(" ");
				String url = "http://www.brainyquote.com/quotes/authors/" + authorName[0].charAt(0) + "/" + authorName[0];
				int i = 1;
				while(i < authorName.length) {
					url = url + "_" + authorName[i];
					i++;
				}
				url = url + ".html";
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
				return queryText;
				
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
				searchType = "keyword";
				
				if (index > quoteNum -1) {
					pageNum++;
					index = 0;
				}
				else{}
				
				String[] keywords = queryText.split(" ");

				String url = "http://www.brainyquote.com/quotes/keywords/" + keywords[0];

				int i = 1;
				while(i < keywords.length) {
					url = url + "_" + keywords[i];
					i++;
				}
				
				if(pageNum == 1) {
					//http://www.brainyquote.com/quotes/keywords/random.html
					url = url + ".html";
				}
				else {
					//http://www.brainyquote.com/quotes/keywords/random_2.html
					url = url + "_" + pageNum + ".html";
				}
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
			
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");				
				quoteNum = quote.size() -1;
				return quote.get(index).text() + "\n\n--" + author.get(index).text() + "\n\n INDEX : " + index;

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
			//first page : http://www.brainyquote.com/quotes/authors/m/mark_twain.html
			//2nd   page : http://www.brainyquote.com/quotes/authors/m/mark_twain_2.html
			try{
				searchType = "byAuthor";
				String [] authorName = queryText.split(" ");
				if (index > quoteNum -1 ) {
					pageNum++;
					index = 0;
				}
				else{}
			
				String url = "http://www.brainyquote.com/quotes/authors/" + authorName[0].charAt(0) + "/" + authorName[0];
				int i = 1;
				while(i < authorName.length) {
					url = url + "_" + authorName[i];
					i++;
				}
								
				if(pageNum == 1) {
					url = url + ".html";
				}
				else {
					url = url + "_" + pageNum +  ".html";
				}
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

	private class AboutAuthorSearch extends AsyncTask<Void, Void, String> {
		//this method will return a quote ABOUT the author.
		@Override
		protected String doInBackground(Void... params) {
			
			try{
				searchType = "aboutAuthor";
				
				if (index > quoteNum -1 ) {
					pageNum++;
					index = 0;
				}
				else{}
				//Sample keyword search : http://www.brainyquote.com/quotes/keywords/mark_twain.html
				String[] keywords = queryText.split(" ");

				String url = "http://www.brainyquote.com/quotes/keywords/" + keywords[0];

				int i = 1;
				while(i < keywords.length) {
					url = url + "_" + keywords[i];
					i++;
				}
				
				if(pageNum == 1) {
					//http://www.brainyquote.com/quotes/keywords/random.html
					url = url + ".html";
				}
				else {
					//http://www.brainyquote.com/quotes/keywords/random_2.html
					url = url + "_" + pageNum + ".html";
				}
				Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
			
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");
//				
				quoteNum = quote.size() -1;
				return quote.get(index).text() + "\n\n--" + author.get(index).text() + "\n\n INDEX : " + index;
				
				
			} catch(IOException exception){
					return "error";
				}
			
		}
		@Override
		protected void onPostExecute(String quote){
			
			if(quote.equals("error") && index == 0) {
				textView.setText("No available quotes about " + queryText + "!");
			}
			else if (quote.equals("error") && index > 0){
				textView.setText("Sorry there are no more available quotes about " + queryText + "!");
			}
			
			else {
				textView.setText(quote);
			}
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
