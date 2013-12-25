package com.example.brainyquote;

import java.io.File;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends BaseActivity {

	Button searchButton;
	Button randomButton;
	SearchView searchView;
	ImageView logo;
	String queryText = null;
	boolean textChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	    setContentView(R.layout.activity_main);
		searchView = (SearchView)findViewById(R.id.searchView);
        searchButton = (Button) findViewById(R.id.searchButton);
        randomButton = (Button) findViewById(R.id.randomButton);
        logo = (ImageView)findViewById(R.id.logo);
        
        
        randomButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//May seem a little redundant, since user knows that he/she requested random quote
				//Toast.makeText(getApplicationContext(), "Random Quote Will be Generated...", Toast.LENGTH_SHORT).show();				//

				//Now a new intent will be created to go to the RandomQuote.java activity! 
				Intent intent = new Intent(getBaseContext(), RandomQuote.class);
				startActivity(intent);				 
			}
		});        
    
        searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchView.setIconified(false);		
				if(textChanged == true) {
					launchSpecificQuoteActivity(queryText);
				}
			}
		});
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        	
    	    @Override
    	    public boolean onQueryTextChange(String newText) {
    	    	textChanged = true;
    	        queryText = newText;
    	        return true;
    	    }

    	    @Override
    	    public boolean onQueryTextSubmit(String query) {    	    		
    	        launchSpecificQuoteActivity(query);
    	        return true;
    	    }
    	};
    	searchView.setOnQueryTextListener(queryTextListener);
    }

    public void launchSpecificQuoteActivity(String queryText) {
    	
    	queryText = queryText.replaceAll("[^a-zA-Z\\s]","");
    	String [] queryTextSplit = queryText.split(" ");
    	//regex statement gets rid of all non letter characters.
        Toast toast = Toast.makeText(getApplicationContext(), "Searching for " + queryText + "...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        
        //Now a new intent will be created to go to the SpecificQuote.java activity! 
		Intent intent = new Intent(getBaseContext(), SpecificQuote.class);
		
		//we will pass the value of query as a string variable called queryText to the SpecificQuote activity
		//so the SpecificQuote activity can use the queryText as the search parameter. 
		intent.putExtra("queryTextSplit", queryTextSplit);
		intent.putExtra("queryText", queryText);
		startActivity(intent);	
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
