package com.example.brainyquote;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.Toast;


//Base class for common UI features such as
//menus, search bars, settings. Eliminates
//redundant code.
public abstract class BaseActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        MenuItem item = menu.findItem(R.id.menu_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        
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
				query = query.replaceAll("[^a-zA-Z\\s]","");
    	    	String [] queryTextSplit = query.split(" ");
    	    	intent.putExtra("queryTextSplit", queryTextSplit);
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
				// activity, the Up button is shown.
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.launch_fav_activity:
				Intent intent = new Intent(getBaseContext(), FavQuotesScreen.class);
				startActivity(intent);
				return true;
			case R.id.menu_share:
				Intent shareIntent = new Intent();
		        shareIntent.setAction(Intent.ACTION_SEND);
		        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		        shareIntent.setType("text/plain");
		        startActivity(Intent.createChooser(shareIntent, "share using"));
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

}
