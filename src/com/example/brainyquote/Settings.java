package com.example.brainyquote;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class Settings extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings1);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Displays the pref fragment of preference objects
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new PrefsFragment())
			.commit();
	}
	
	public static class PrefsFragment extends PreferenceFragment {
		 
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

        	// Load the preferences from XML resource
        	addPreferencesFromResource(R.xml.preferences);
        }
	}

}
