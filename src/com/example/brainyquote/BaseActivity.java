package com.example.brainyquote;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;


//Base class for common variables and UI
//features such as menus, search bars, settings. 
//Eliminates redundant code.
public abstract class BaseActivity extends Activity {
	
	ShareActionProvider shareActionProvider;
	//quote used for sharing on google+, texting, etc.
	//Modified by subclasses once a quote is shown on screen
	protected static String sharingQuote = "";
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
        
        MenuItem item = menu.findItem(R.id.menu_share);
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        
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
				break;
			case R.id.launch_fav_activity:
				Intent intent = new Intent(getBaseContext(), FavQuotesScreen.class);
				startActivity(intent);
				break;
			case R.id.menu_share:
				Intent shareIntent = new Intent();
		        shareIntent.setAction(Intent.ACTION_SEND);
		        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingQuote);
		        shareIntent.setType("text/plain");
		        shareActionProvider.setShareIntent(shareIntent);
		        break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	public void showCustomToast(int quotePlaceHolder) {
		LayoutInflater inflater = getLayoutInflater();
		 
		View layout = inflater.inflate(R.layout.custom_toast,
		  (ViewGroup) findViewById(R.id.custom_toast_layout_id));

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		TextView text = (TextView) layout.findViewById(R.id.text);
		if(quotePlaceHolder == 0) {
			text.setText("Swipe left to see the next quote");
			image.setImageResource(R.drawable.arrow_left);
		}
		else {
			text.setText("Swipe right to see the previous quote");
			image.setImageResource(R.drawable.arrow_right);
		}
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();	
	}
}
