package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.example.brainyquote.Tools.CheckQuoteTask;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//Base class for common variables and UI
//features such as menus, search bars, settings. 
//Eliminates redundant code.
public abstract class BaseActivity extends Activity {

	/*
	 * fontSize represents an integer that will control the size of font that the can
	 * be changed from preferences
	 */
	protected int fontSize;
	/*
	 * toggle represents a value that will be changed in
	 * changeStarIfFavorite((TextView textView, ImageButton star)) It will
	 * determine if the favorite star should be lit up or not lit up.
	 */
	protected int toggle;
	/*
	 * appDir represents the directory where BrainyQuote files are stored on the phone
	 */
	protected static String appDir;
	/*
	 * quote used for sharing on google+, texting, etc. Modified by subclasses
	 * once a quote is shown on screen
	 */
	protected static String sharingQuote = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// sets default preferences if never modified by user
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// gets the current preferences values
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		fontSize = Integer.parseInt(preferences.getString("quote_font_pref",
				"20"));
		appDir = getFilesDir().getAbsolutePath().toString();

		getActionBar();

		/*
		 * Below Try Catch is a hack to make the overflow menu viewable
		 * on phones with a hardware menu button. Phones with a hardware menu button
		 * will naturally not be able to see an overflow menu in the action bar without this hack
		 * Now if the user were to click on the hardware menu button, the user will see the 
		 * action overflow menu open up in the action bar as opposed to the bottom of the screen.
		 */
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			java.lang.reflect.Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		//setting the hint value of searchView.
		searchView.setQueryHint("Search BrainyQuote");
		/*
		 * onQueryTextListener used to launch the SpecificQuoteActivity 
		 * once the user has pressed the submit button on pop up keyboard button.
		 */
		final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				/*
				 * query is the text the user types in the searchView prior
				 * to clicking on the keyboard submit button.
				 */
				launchSpecificQuoteActivity(query);
				return true;
			}
		};
		searchView.setOnQueryTextListener(queryTextListener);
		return true;
	}
	
	/*
	 * onOptionsItemSelected(MenuItem item) is called when clicking on an item 
	 * in the action bar. Specifically in this callback, it is used to register
	 * clicks on items within in the action overflow menu (Favorites and Settings)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * switching based on values in res/menu/main.xml
		 * A selected menu item will result in an intent to
		 * start the respective activity. 
		 * ("Favorites" option -> FavQuotesScreen Activity) 
		 */
		switch (item.getItemId()) {
		//"Favorites" option
		case R.id.launch_fav_activity:
			Intent intent = new Intent(getBaseContext(), FavQuotesScreen.class);
			startActivity(intent);
			break;
		//"Settings" option
		case R.id.settings:
			Intent settingsIntent = new Intent(getBaseContext(), Settings.class);
			startActivity(settingsIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setupActionBar() {
		/*
		 * On activities other than the MainActivity, the app icon in action bar
		 * will have a left facing caret. When clicked, the user come back to 
		 * the MainActivity.
		 * A callback to onOptionsSelected() is android.R.id.home
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/*
	 * Activity is at the top of the Activity stack,
	 * User can fully see and interact with the activity
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/*
	 * Shows a custom toast message (made in res/layout/custom_toast.xml)
	 * It will be showed on the first and second quote (index == 1 && index == 2)
	 * It is an instructional toast message that will let the user know that
	 * the can swipe to the left or right to get to previous or new quotes. 
	 */
	public void showCustomToast(int index) {

		LayoutInflater inflater = getLayoutInflater();
		//View layout will represent res/layout/custom_toast.xml
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) findViewById(R.id.custom_toast_layout_id));
		//R.id.image and R.id.text are values within custom_toast.xml
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		TextView text = (TextView) layout.findViewById(R.id.text);
		/*
		 * The following if / else statement will appropriately change
		 * the text and image to let the user swipe to the left or right. 
		 */
		if (index == 0) {
			text.setText("Swipe left to see the next quote");
			image.setImageResource(R.drawable.arrow_left);
		} else {
			text.setText("Swipe right to see the previous quote");
			image.setImageResource(R.drawable.arrow_right);
		}
		//creating a toast that will use the View layout
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
	/*
	 * launchSpecificQuoteActivity(String queryText)
	 * will... launch the SpecificQuoteActivtiy and pass the 
	 * String value of queryText to the SpecificQuoteActivity.
	 * The string value of queryText will be modified to get rid of
	 * non letter characters and more than one consecutive spaces. 
	 * (These are simple typing mistakes that the user may make)
	 */
	public void launchSpecificQuoteActivity(String queryText) {
		
		/*
		 * regex statement gets rid of all non letter characters.
		 * All characters not a-A A-Z will be replaced with ""
		 */
		queryText = queryText.replaceAll("[^a-zA-Z\\s]", "");
		//removes consecutive spaces and replaces with one space
		queryText = queryText.trim().replaceAll(" +", " ");
		/*splits queryText by space and puts in queryTextSplit
		 * queryTextSplit will be an array that holds each
		 * word value of queryText.
		 * (queryText) "Mark Twain" becomes 
		 * (queryTextSplit) {"Mark","Twain"} 
		 */
		String[] queryTextSplit = queryText.split(" ");

		//Do nothing if queryTextSplit[0] is empty (queryText is nothing)
		if (queryTextSplit.length == 1 && queryTextSplit[0].isEmpty())
			return;

		Toast toast = Toast.makeText(getApplicationContext(), "Searching for "
				+ queryText + "...", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		// New intent created to go to the SpecificQuote.java activity!
		Intent intent = new Intent(getBaseContext(), SpecificQuote.class);
		/*
		 * We will pass queryText as string variable "queryText" and
		 * queryTextSplit as string variable "queryTextSplit"
		 * to the SpecificQuote Activity by .putExtra("someName",value );
		 * SpecificQuote Activity will use queryTextSplit to build urls
		 * and use queryText for AuthorSearches to display author name. 
		 */
		intent.putExtra("queryTextSplit", queryTextSplit);
		intent.putExtra("queryText", queryText);
		startActivity(intent);
	}

	// uses CheckQuoteTask async task to set star to the appropriate resource.
	/*
	 * changeStarIfFavorite(TextView textView, ImageButton star)
	 * is called in RandomQuoteActivity and SpecificQuote activity
	 * to appropriately change the star image if the quote is 
	 * favourited or not. 
	 * The actual check is done by calling CheckQuoteTask async task located in 
	 * Tools.java 
	 */
	public void changeStarIfFavorite(TextView textView, ImageButton star) {
		//array with quote(textView.getText()) and file directory (appDir)
		String[] quoteAndDir = { textView.getText().toString(), appDir };
		
		/*
		 * CheckQuoteTask is an async task that will return a
		 * boolean value. True if the quote (quoteandDir[0])
		 * is already stored / favorited and false if not.
		 */
		try {
			if (new CheckQuoteTask().execute(quoteAndDir).get()) {
				star.setImageResource(R.drawable.btn_star_big_on);
				toggle = 1;
			} else {
				star.setImageResource(R.drawable.btn_star_big_off);
				toggle = 0;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Gets all topics from "categories.txt" and puts in ArrayList<String> topics
	 * SpecificQuote Activity will use topics to determine if a queryText is a 
	 * topic and then build a respective url.
	 */
	public void getTopics(ArrayList<String> topics) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open(
					"categories.txt")));
			String word;
			while ((word = br.readLine()) != null) {
				topics.add(word);
			}
		} catch (IOException ioe) {
		}
	}
}
