package com.example.brainyquote;

import java.io.File;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends BaseActivity {

	Button searchButton;
	Button randomButton;
	SearchView searchView;
	ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        
        searchButton = (Button) findViewById(R.id.searchButton);
        randomButton = (Button) findViewById(R.id.randomButton);
        searchView = (SearchView) findViewById(R.id.searchView);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        
        //setting hint value via .java.... should be able to do in .XML....
        searchView.setQueryHint("Search Brainy Quote");
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getApplicationContext(), "WE CLICKED", Toast.LENGTH_SHORT).show();
				searchView.setIconified(false);
				
			}
		});
        
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        	
    	    @Override
    	    public boolean onQueryTextChange(String newText) {
    	        // Do something
    	        return true;
    	    }

    	    @Override
    	    public boolean onQueryTextSubmit(String query) {
    	    	
    	    	query = query.replaceAll("[^a-zA-Z\\s]","");
    	    	//regex statement gets rid of all non letter characters.
    	        Toast toast = Toast.makeText(getApplicationContext(), "Searching for " + query + "...", Toast.LENGTH_SHORT);
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
}
