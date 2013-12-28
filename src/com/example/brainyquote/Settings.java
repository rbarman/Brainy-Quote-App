package com.example.brainyquote;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class Settings extends BaseActivity {
	
	String settingsDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	//TODO
	//will create a fonts file in the app's "System" folder
	//that stores a font size integer. This will then be referenced
	//by random and specific quote activities to adjust the quote font size
	public void changeQuoteFont(View view) {
		settingsDir = getFilesDir().getAbsolutePath().toString() + "/System";
	}
}
