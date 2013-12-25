package com.example.brainyquote;


import com.example.brainyquote.Tools.CheckQuoteTask;
import com.example.brainyquote.Tools.DeleteFavTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//Base class for common variables and UI
//features such as menus, search bars, settings. 
//Eliminates redundant code.
public abstract class BaseActivity extends Activity {
	
	String appDir;

	// quote used for sharing on google+, texting, etc.
	// Modified by subclasses once a quote is shown on screen
	protected static String sharingQuote = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP
						| ActionBar.DISPLAY_SHOW_CUSTOM);
		View view = View.inflate(getApplicationContext(),
				R.layout.custom_actionbar, null);
		getActionBar().setCustomView(view);
		
		appDir = getFilesDir().getAbsolutePath().toString();
		
		 try {
		        ViewConfiguration config = ViewConfiguration.get(this);
		        java.lang.reflect.Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
		        if(menuKeyField != null) {
		            menuKeyField.setAccessible(true);
		            menuKeyField.setBoolean(config, false);
		        }
		    } catch (Exception ex) {
		        // Ignore
		    }
		
		
		SearchView searchView = (SearchView) findViewById(R.id.searchView);
		final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				if(!query.isEmpty())
					launchSpecificQuoteActivity(query);
				else {}
				return true;
			}
		};
		searchView.setOnQueryTextListener(queryTextListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.launch_fav_activity:
			Intent intent = new Intent(getBaseContext(), FavQuotesScreen.class);
			startActivity(intent);
			break;
		case R.id.menu_share:
			Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, sharingQuote);
			shareIntent.setType("text/plain");
			startActivity(Intent.createChooser(shareIntent, "Share via"));
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
		if (quotePlaceHolder == 0) {
			text.setText("Swipe left to see the next quote");
			image.setImageResource(R.drawable.arrow_left);
		} else {
			text.setText("Swipe right to see the previous quote");
			image.setImageResource(R.drawable.arrow_right);
		}
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
	public void launchSpecificQuoteActivity(String queryText) {

		queryText = queryText.replaceAll("[^a-zA-Z\\s]", "");
		String[] queryTextSplit = queryText.split(" ");
		// regex statement gets rid of all non letter characters.
		Toast toast = Toast.makeText(getApplicationContext(), "Searching for "
				+ queryText + "...", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		// Now a new intent will be created to go to the SpecificQuote.java
		// activity!
		Intent intent = new Intent(getBaseContext(), SpecificQuote.class);

		// we will pass the value of query as a string variable called queryText
		// to the SpecificQuote activity
		// so the SpecificQuote activity can use the queryText as the search
		// parameter.
		intent.putExtra("queryTextSplit", queryTextSplit);
		intent.putExtra("queryText", queryText);
		startActivity(intent);
	}

}
