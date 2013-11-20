package com.example.brainyquote;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creates an action bar. 
        ActionBar actionBar = getActionBar();
        
        //sets the home button. 
        actionBar.setDisplayHomeAsUpEnabled(true); 
        
        final Button searchButton = (Button) findViewById(R.id.searchButton);
        final Button randomButton = (Button) findViewById(R.id.randomButton);
        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        final ImageView logo = (ImageView)findViewById(R.id.logo);
       
        //onClickListener for searchButton...
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//logo, searchButton, and randomButton will become invisible.
            	logo.setVisibility(4);
            	searchButton.setVisibility(4);
            	randomButton.setVisibility(4);
            	
            	//search dialog will become visible and expanded and will have a :hint value. 
            	searchView.setVisibility(0);
            	searchView.setQueryHint("Search Brainy Quote");
            	//searchView.setSubmitButtonEnabled(true);
            	searchView.setIconified(false); 
            	
            	//OnQueryTextListener created for SearchView
            	//this will track when the user either enters text or submits inputted text!. 
            	final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            	    @Override
            	    public boolean onQueryTextChange(String newText) {
            	        // Do something
            	        return true;
            	    }

            	    @Override
            	    public boolean onQueryTextSubmit(String query) {
            	        Toast toast = Toast.makeText(getApplicationContext(), query + " Quotes coming soon...", Toast.LENGTH_LONG);
            	        toast.setGravity(Gravity.CENTER, 0, 0);
            	        toast.show();
            	        return true;
            	    }
            	};

            	searchView.setOnQueryTextListener(queryTextListener);            	
            	
            }
        });
        //end of searchButton onClickListener...
        
      
        
        //onClickListener for randomButton....
        randomButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//create a toast..
				 Toast.makeText(getApplicationContext(), "Random Quote Will be Generated...", Toast.LENGTH_LONG).show();
				 
			}
		});        
    }

	@Override
	public void onBackPressed() {
		//action listener on when the backButton is pressed. 
		 final Button searchButton = (Button) findViewById(R.id.searchButton);
	     final SearchView searchView = (SearchView) findViewById(R.id.searchView);
	     final ImageView logo = (ImageView)findViewById(R.id.logo);
		 final Button randomButton = (Button) findViewById(R.id.randomButton);

	     //all below become visible again. 
	     searchButton.setVisibility(0);
	     logo.setVisibility(0);
	     randomButton.setVisibility(0);
	     //searchView becomes invisible.
	     searchView.setVisibility(4);
		
		return;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        
        //setting hint value via .java.... should be able to do in .XML....
        searchView.setQueryHint("Search Brainy Quote!");
        return true;
    }
    
}
