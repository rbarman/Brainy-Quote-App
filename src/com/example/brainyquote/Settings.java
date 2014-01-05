package com.example.brainyquote;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;

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
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.findItem(R.id.settings).setVisible(false);
		return true;
	}
	
	public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		 		
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

        	// Load the preferences from XML resource
        	addPreferencesFromResource(R.xml.preferences);
        	
        	Preference quoteSizePref = (Preference) findPreference("quote_font_pref");
        	quoteSizePref.setSummary(((ListPreference) quoteSizePref).getEntry().toString());
        }

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			
			Preference pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            pref.setSummary(((ListPreference) pref).getEntry().toString());
            
		}
		
		@Override
		public void onResume() {
			
		    super.onResume();
		    getPreferenceScreen().getSharedPreferences()
		            .registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			
		    super.onPause();
		    getPreferenceScreen().getSharedPreferences()
		            .unregisterOnSharedPreferenceChangeListener(this);
		}
	}

}
