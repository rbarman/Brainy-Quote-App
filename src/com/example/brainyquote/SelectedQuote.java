package com.example.brainyquote;

import com.example.brainyquote.Tools.DeleteFavTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SelectedQuote extends BaseActivity {
	ImageButton star;
	boolean isFavorite = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_quote);
		
		Intent intent = getIntent();
		String quote = intent.getStringExtra(FavQuotesScreen.QUOTE_MESSAGE);
		TextView textView = (TextView) findViewById(R.id.quoteTextView);
		textView.setText(quote);
		String text = textView.getText().toString();
		final String[] quoteAndDir = {text, appDir};
		
		star = (ImageButton) findViewById(R.id.star);
		star.setImageResource(R.drawable.btn_star_big_on);
		star.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				if(isFavorite == true) {
					star.setImageResource(R.drawable.btn_star_big_off);
					new DeleteFavTask().execute(quoteAndDir);
					Toast.makeText(getApplicationContext(), "Quote is removed from favorites!", Toast.LENGTH_SHORT).show();
					isFavorite = false;
				}
				else if(isFavorite == false) {
					star.setImageResource(R.drawable.btn_star_big_on);
					new WriteFavQuoteTask().execute(quoteAndDir);
					Toast.makeText(getApplicationContext(), "Quote is added to favorites!", Toast.LENGTH_SHORT).show();
					isFavorite = true;
				}
			}
		});
		
	}
}
