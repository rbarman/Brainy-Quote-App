package com.example.brainyquote;

import com.example.brainyquote.Tools.DeleteFavTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 * SelectedQuote Activity will start when the user clicks on a 
 * quote the ListView in FavQuotesScreen.
 * This activity will display the quote and author text.
 */
public class SelectedQuote extends BaseActivity {
	ImageButton star;
	ImageButton share;
	/*
	 * isFavorite will change within star.onClickListener()
	 * and will be used to determine the ImageResource of ImageButton star
	 */
	boolean isFavorite = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_quote);
		/*
		 * When the user clicks on an item (quote) in the ListView of 
		 * FavQuotesScreen, the FavQuotesScreen creates a new intent
		 * to start SelectedQuote Activity and passes quote and author
		 * values in a string, QUOTE_MESSAGE
		 */
		Intent intent = getIntent();
		final String quote = intent.getStringExtra(FavQuotesScreen.QUOTE_MESSAGE);
		/*
		 * Declare, initialize, and set size + font for textView.
		 * User sees quote and author text in textView
		 */
		TextView textView = (TextView) findViewById(R.id.quoteTextView);
		textView.setText(quote);
		textView.setTextSize(fontSize);
		String text = textView.getText().toString();
		final String[] quoteAndDir = {text, appDir};
		
		/*
		 * ImageButton star represents the favorite button
		 * onCreate star should be "on" because the quote
		 * will initially be a favorite.
		 */
		star = (ImageButton) findViewById(R.id.star);
		star.setImageResource(R.drawable.btn_star_big_on);
		/*
		 * User clicks on star button
		 * If a quote is already favorited, quote is deleted and star is "off"
		 * If a quote is not a favorite (even click number on star), 
		 * quote is added to favorites and star if "on"
		 */
		star.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				if(isFavorite == true) {
					star.setImageResource(R.drawable.btn_star_big_off);
					/*
					 * DeleteFavTask is an async task more described in Tools.java
					 * Finds the saved file that represents the displayed quote 
					 * and deletes it. 
					 * Quote is no longer a favorite. 
					 */
					new DeleteFavTask().execute(quoteAndDir);
					Toast.makeText(getApplicationContext(), "Quote is removed from favorites!", Toast.LENGTH_SHORT).show();
					isFavorite = false;
				}
				else if(isFavorite == false) {
					star.setImageResource(R.drawable.btn_star_big_on);
					/*
					 * WriteFavQuoteTask is asyn task more described in Tools.java
					 * It will save the quote in a file in appDir. 
					 * Quote will be saved as a favorite.
					 */
					new WriteFavQuoteTask().execute(quoteAndDir);
					Toast.makeText(getApplicationContext(), "Quote is added to favorites!", Toast.LENGTH_SHORT).show();
					isFavorite = true;
				}
			}
		});
		
		share = (ImageButton) findViewById(R.id.share);
		/*
		 * User clicks on the share button. 
		 * User will be able to choose from different 
		 * options available on the user's phone to share currently displayed quote.
		 */
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Create an intent to share the currently displayed quote.
				 * .setAction to set a sending intent that will open up a 
				 * partially full activity where the user can choose an option
				 * to share the currently displayed quote. 
				 */
				Toast.makeText(getApplicationContext(), "clicked",
						Toast.LENGTH_SHORT).show();
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT, quote);
				shareIntent.setType("text/plain");
				startActivity(Intent.createChooser(shareIntent, "Share via"));
			}
		});
		
	}
}
