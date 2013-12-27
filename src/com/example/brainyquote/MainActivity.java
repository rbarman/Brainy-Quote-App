package com.example.brainyquote;

import android.os.Bundle;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

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
        searchButton = (Button) findViewById(R.id.searchButton);
        randomButton = (Button) findViewById(R.id.randomButton);
        logo = (ImageView)findViewById(R.id.logo);
        
        randomButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//Now a new intent will be created to go to the RandomQuote.java activity! 
				Intent intent = new Intent(getBaseContext(), RandomQuote.class);
				startActivity(intent);				 
			}
		});  
    }

	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);

		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    final SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    searchView.setQueryHint("Search BrainyQuote");
	    
        searchButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				searchView.setIconified(false);
				if(textChanged == true)
					launchSpecificQuoteActivity(queryText);
			}
		}); 
	    
      final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
    	
	    @Override
	    public boolean onQueryTextChange(String newText) {
	        return true;
	    }

	    @Override
	    public boolean onQueryTextSubmit(String query) {    	    		
	        launchSpecificQuoteActivity(query);
	        return true;
	    }
	};
	searchView.setOnQueryTextListener(queryTextListener);
		return true;
	}
}
