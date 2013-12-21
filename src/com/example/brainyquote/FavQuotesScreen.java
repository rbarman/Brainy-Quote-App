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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class FavQuotesScreen extends BaseActivity {
	
	private String quotesDir = "";
	private ArrayList<String> quotes = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ListView list;
	View noQuoteTextView;
	Button clearButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav_quotes_screen);
		// Show the Up button in the action bar.
		setupActionBar();
		
		list = (ListView) findViewById(R.id.listViewMain);
		quotesDir = getFilesDir().getAbsolutePath().toString();
		noQuoteTextView = (View) findViewById(R.id.noQuoteTextView);
		
		new GetFavQuotesTask().execute();
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
	public void deleteAllFavorites(View view) {
		
		new DeleteAllFavsTask().execute(quotesDir);
		quotes.clear();
		adapter.notifyDataSetChanged();
		noQuoteTextView.setVisibility(View.VISIBLE);
	}
}
