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
        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        final ImageView logo = (ImageView)findViewById(R.id.logo);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//logo and searchButton will become invisible.
            	logo.setVisibility(4);
            	searchButton.setVisibility(4);
            	//search dialog will become visible and expand. 
            	searchView.setVisibility(0);
            	searchView.setIconified(false);
            	
            }
        });
    }

	@Override
	public void onBackPressed() {
		//action listener on when the backButton is pressed. 
		 final Button searchButton = (Button) findViewById(R.id.searchButton);
	     final SearchView searchView = (SearchView) findViewById(R.id.searchView);
	     final ImageView logo = (ImageView)findViewById(R.id.logo);
	     
	     searchButton.setVisibility(0);
	     logo.setVisibility(0);
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
