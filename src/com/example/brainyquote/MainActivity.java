package com.example.brainyquote;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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
            	//search dialog will become visible and expand. 
            	searchView.setVisibility(0);
            	searchView.setIconified(false);	
            }
        });
        
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
        return true;
    }
    
}
