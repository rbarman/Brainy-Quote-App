package com.example.brainyquote;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class SelectedQuote extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_quote);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		String quote = intent.getStringExtra(FavQuotesScreen.QUOTE_MESSAGE);
		TextView textView = (TextView) findViewById(R.id.quoteTextView);
		textView.setText(quote);
	}
}
