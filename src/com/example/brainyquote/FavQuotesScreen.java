package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.brainyquote.Tools.DeleteAllFavsTask;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class FavQuotesScreen extends Activity {
	
	private String quotesDir = getFilesDir().getAbsolutePath().toString();
	private ArrayList<String> quotes = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav_quotes_screen);
		// Show the Up button in the action bar.
		setupActionBar();
		
		new GetFavQuotesTask().execute();
	}

	//returns a string array of fav quotes to be used with some view
	private class GetFavQuotesTask extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			
			try {
				//get directory of fav files and list the interior files ending with ".txt"
				File dir = new File(getFilesDir().getAbsolutePath());
				File[] dirFiles = dir.listFiles(new FilenameFilter() {
				    public boolean accept(File dir, String name) {
				        return name.endsWith(".txt");
				    }
				});
								
				if (dirFiles.length == 0) {
					return quotes;
				} else {
					//For each file in dirFiles, get the text String and
					//add it to the quote array list.
					for (int i = 0; i < dirFiles.length; i++) {
						StringBuilder fullQuote = new StringBuilder();
						FileReader fr = new FileReader(dirFiles[i].getAbsolutePath());
						BufferedReader textReader = new BufferedReader(fr);
						String line = textReader.readLine();
						
						while (line != null) {
							fullQuote.append(line);
							fullQuote.append("\n");
							line = textReader.readLine();
						}					
						quotes.add(fullQuote.toString());
						textReader.close();
					}
					return quotes;
				}
			} catch (IOException e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> quotes) {
			
			super.onPostExecute(quotes);
			populateView(quotes);
		}
		
	}
	
	private void populateView(ArrayList<String> quotes) {
		
		if (quotes.isEmpty()) {
			View noQuoteTextView = (View) findViewById(R.id.noQuoteTextView);
			noQuoteTextView.setVisibility(View.VISIBLE);
		} else {
			//build adapter using the quotes array list
			adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, quotes);
					
			//configure list view
			list = (ListView) findViewById(R.id.listViewMain);
			list.setAdapter(adapter);
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
		getMenuInflater().inflate(R.menu.fav_quotes_screen, menu);
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

	//deletes all favorites stored in quotesDir
	//and refreshes listView
	public void deleteAllFavorites(View view) {
		
		new DeleteAllFavsTask().execute(quotesDir);
		quotes.clear();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown.
			NavUtils.navigateUpFromSameTask(this);
			return true;    		
		}
		return super.onOptionsItemSelected(item);
	}

}
