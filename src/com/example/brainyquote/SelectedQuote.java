package com.example.brainyquote;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public class SelectedQuote extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_quote);
		
		Intent intent = getIntent();
		String quote = intent.getStringExtra(FavQuotesScreen.QUOTE_MESSAGE);
		TextView textView = (TextView) findViewById(R.id.quoteTextView);
		textView.setText(quote);
	}
}
