package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.brainyquote.Tools.CheckQuoteTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;
import com.example.brainyquote.Tools.DeleteFavTask;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

//This is the activity launched when the user selects the randomButton on main activity.
public class RandomQuote extends Activity {	
	
	ArrayList<String> categories = new ArrayList<String>();
	ArrayList<String> randomQuotes = new ArrayList<String>();
	TextView textView;
	ImageButton star;
	View view;
	int toggle = 0;
	int currentIndex = -1;
	int quotePlaceHolder = -1;
	String appDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_quote);
		// Show the Up button in the action bar.
		setupActionBar();		
		
		textView = (TextView)findViewById(R.id.textView);
		view = (View)findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		star = (ImageButton)findViewById(R.id.star);
		appDir = getFilesDir().getAbsolutePath().toString();
		getCategories();
		
		//Star is initially off. Pressing it will toggle it 
		//on or off (0 is off, 1 is on)
		star.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (toggle == 0) {
					String[] quoteAndDir = {textView.getText().toString(), appDir};
					star.setImageResource(R.drawable.btn_star_big_on);					
					new WriteFavQuoteTask().execute(quoteAndDir);
					toggle = 1;
				} else if (toggle == 1){
					String[] quoteAndDir = {textView.getText().toString(), appDir};
					star.setImageResource(R.drawable.btn_star_big_off);
					new DeleteFavTask().execute(quoteAndDir);
					toggle = 0;
				}
			}
		});

		//execute the async task
		new GetQuote().execute();		
	}
	
	public void updateFavButton() {
		
		String[] quoteAndDir = {textView.getText().toString(), appDir};
		try {
			if (new CheckQuoteTask().execute(quoteAndDir).get()) {
				star.setImageResource(R.drawable.btn_star_big_on);
				toggle = 1;
			} else {
				star.setImageResource(R.drawable.btn_star_big_off);
				toggle = 0;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	OnTouchListener viewSwiped = new OnSwipeTouchListener() {
		
		//on every swipe to the right we will get the next random quote. 
		public void onSwipeRight() {
			
			if(currentIndex > 0) {
				currentIndex--;
				Toast.makeText(RandomQuote.this, "Swipe to Right : Previous Random Quote Coming!", Toast.LENGTH_SHORT).show();
				textView.setText(randomQuotes.get(currentIndex));
				updateFavButton();
			} else {
				Toast.makeText(RandomQuote.this, "Swipe to Right : No more previous Quotes :(", Toast.LENGTH_SHORT).show();
			}
		}
		//on every swipe to the left we will get the next random quote.
		public void onSwipeLeft(){
			
			if(currentIndex < quotePlaceHolder) {
				currentIndex ++;
				Toast.makeText(RandomQuote.this, "Swipe to Left : Next Random Quote Coming!", Toast.LENGTH_SHORT).show();
				textView.setText(randomQuotes.get(currentIndex));
				updateFavButton();
			} else {
				Toast.makeText(RandomQuote.this, "Swipe to Left : New Random Quote Coming!", Toast.LENGTH_SHORT).show();
				new GetQuote().execute();
			}	
		}
		public void onSwipeBottom() {
			
		}
		public void onSwipeTop() {
			
		}
	};
	
	//need to create an AsyncTask so that the UI thread does not have to do extra work
	//if we make the UI thread to the Jsoup.connect, the application will crash.
	
	public void getCategories() {
		BufferedReader br = null;
		 try {
			 br = new BufferedReader(new InputStreamReader(getAssets().open("categories.txt")));
			 String word;
			 while((word=br.readLine()) != null) {
				 categories.add(word); 
			 } 
		 }catch(IOException ioe) {}
	}
	
	private class GetQuote extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			String topic = categories.get((int) (Math.random() * (categories.size() -1)));
			Document doc;
			try {
				String url = "http://www.brainyquote.com/quotes/topics/topic_" + topic + ".html";
				//sample url for age quote : http://www.brainyquote.com/quotes/topics/topic_age.html
				doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();			                			                
				Elements quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");
			             
				int randIndex = (int) (Math.random()*quote.size()-1);
			        	
				randomQuotes.add("\"" + quote.get(randIndex).text() + "\"" +"\n\n      - "
					 + author.get(randIndex).text());
				currentIndex ++;
				quotePlaceHolder ++;
				String formattedQuote = String.format("\"%s\" \n\n      - %s",
						quote.get(randIndex).text(), author.get(randIndex).text());
			           
				return formattedQuote;
			 }
			 catch(IOException e) {
				 return null;
			 }
		}
		@Override
		protected void onPostExecute(String title){
			
			TextView textView = (TextView)findViewById(R.id.textView);
			textView.setText(title);
			updateFavButton();
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
		case R.id.launch_fav_activity:
    		Intent intent = new Intent(getBaseContext(), FavQuotesScreen.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
