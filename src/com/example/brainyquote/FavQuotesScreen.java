package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.example.brainyquote.Tools.DeleteAllFavsTask;
import com.example.brainyquote.Tools.DeleteFavTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class FavQuotesScreen extends BaseActivity {
	
	public final static String QUOTE_MESSAGE = "com.example.brainyquote.MESSAGE";
	private String quotesDir = "";
	private ArrayList<String> quotes = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ListView list;
	int numSelected = 0;
	View noQuoteTextView;
	ShareActionProvider shareProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav_quotes_screen);
		setupActionBar();
		
		list = (ListView) findViewById(R.id.listViewMain);
		quotesDir = getFilesDir().getAbsolutePath().toString();
		noQuoteTextView = (View) findViewById(R.id.noQuoteTextView);
		
		new GetFavQuotesTask().execute();
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
				
				TextView noteClicked = (TextView) viewClicked;
				String message = noteClicked.getText().toString();
				
				Intent intent = new Intent(FavQuotesScreen.this, SelectedQuote.class);
				intent.putExtra(QUOTE_MESSAGE, message);
				
			    startActivity(intent);
			}
		});
		
		list.setChoiceMode(list.CHOICE_MODE_MULTIPLE_MODAL);
		list.setMultiChoiceModeListener(new MultiChoiceModeListener() {

		    @Override
		    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		        
		    	//update the counter in the CAB
		    	if (checked) {
		    		numSelected++;
		    		mode.setTitle(numSelected + " Selected");
		    	} else {
		    		numSelected--;
		    		mode.setTitle(numSelected + " Selected");
		    	}
		    }
		    
		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		    	
		        // Respond to clicks on the actions in the CAB
		        switch (item.getItemId()) {
		            case R.id.delete:
		            	//Finds all positions of selected items in listview and
		            	//gets text at each position. Next, delete the quotes using 
		            	//their text and the quote directory.
		            	SparseBooleanArray selectedItems = list.getCheckedItemPositions();
		            	for (int i = 0; i < selectedItems.size(); i++) {
		            		if (selectedItems.get(i)) {
		            			String quoteText = (list.getItemAtPosition(i).toString());
				            	String[] quoteAndDir = {quoteText, quotesDir};
				            	DeleteFavTask favDelete = new DeleteFavTask();
				            	favDelete.execute(quoteAndDir);
		            		}
		            	}
		            	
		                mode.finish(); // Action picked, so close the CAB
		                return true;
		            case R.id.menu_item_share:
		            	//gets positions of selected items and builds a string 
		            	//using all the quote text at each position
		            	StringBuilder quotes = new StringBuilder();
		            	quotes.append("");
		            	SparseBooleanArray selectedQuotes = list.getCheckedItemPositions();
		            	
		            	for (int i = 0; i < selectedQuotes.size(); i++) {
		            		if (selectedQuotes.get(i)) {
		            			String quoteText = (list.getItemAtPosition(i).toString());
				            	quotes.append(quoteText + "\n\n");
		            		}
		            	}
		            	Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		            	sendIntent.putExtra(Intent.EXTRA_TEXT, quotes.toString());
		            	sendIntent.setType("text/plain");
		            	startActivity(Intent.createChooser(sendIntent, "Share via"));
		            	mode.finish();
		            default:
		                return false;
		        }
		    }

		    @Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		    	
		        // Inflate the menu for the CAB
		    	numSelected = 0;
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.context, menu);
		        		        
		        return true;
		    }

		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        
		    	//update the listview for any deletions the user made
		    	new GetFavQuotesTask().execute();
		    }

		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

		        return false;
		    }
		});
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		new GetFavQuotesTask().execute();
	}

	//returns a string array of fav quotes to be used with some view
	private class GetFavQuotesTask extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			
			quotes.clear();
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
					Collections.reverse(quotes);
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
		
		noQuoteTextView.setVisibility(View.INVISIBLE);
		//build adapter using the quotes array list
		adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, quotes);
					
		//configure list view		
		list.setAdapter(adapter);
		if (quotes.isEmpty()) {
			noQuoteTextView.setVisibility(View.VISIBLE);
		}
		
	}

	//deletes all favorites stored in quotesDir
	//and refreshes listView
	public void deleteAllFavorites() {
		
		new DeleteAllFavsTask().execute(quotesDir);
		quotes.clear();
		adapter.notifyDataSetChanged();
		noQuoteTextView.setVisibility(View.VISIBLE);
	}
}
