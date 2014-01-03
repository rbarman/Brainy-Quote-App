package com.example.brainyquote;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.example.brainyquote.Tools.WriteFavQuoteTask;
import com.example.brainyquote.Tools.DeleteFavTask;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//This is the activity launched when the user selects the randomButton on main activity.
public class RandomQuote extends BaseActivity {

	ArrayList<String> topics = new ArrayList<String>();
	ArrayList<String> randomQuotes = new ArrayList<String>();
	TextView textView;
	ImageButton share;
	ImageButton star;
	View view;
	int toggle = 0;
	int currentIndex = -1;
	int quotePlaceHolder = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_quote);
		
		textView = (TextView) findViewById(R.id.textView);
		textView.setTextSize(fontSize);
		view = (View) findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		share = (ImageButton)findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT, sharingQuote);
				shareIntent.setType("text/plain");
				startActivity(Intent.createChooser(shareIntent, "Share via"));
			}
		});
		
		star = (ImageButton)findViewById(R.id.star);
		star.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = textView.getText().toString();
				String[] quoteAndDir = {text, appDir};
				if (toggle == 0) { 	
					star.setImageResource(R.drawable.btn_star_big_on);				
					new WriteFavQuoteTask().execute(quoteAndDir);
					toggle = 1;
				} else if (toggle == 1) {
					star.setImageResource(R.drawable.btn_star_big_off);
					new DeleteFavTask().execute(quoteAndDir);
					toggle = 0;
				}
			}
		});
		
		
		getTopics(topics);
		
		// execute the async task
		new GetQuote().execute();
	}

	OnTouchListener viewSwiped = new OnSwipeTouchListener() {

		// on every swipe to the right we will get the previous random quote.
		public void onSwipeRight() {

			if (currentIndex > 0) {
				currentIndex--;
				Toast.makeText(RandomQuote.this,
						"Swipe to Right : Previous Random Quote Coming!",
						Toast.LENGTH_SHORT).show();
				textView.setText(randomQuotes.get(currentIndex));
				Tools.setShareQuote(textView.getText().toString());
				changeStarIfFavorite(textView, star);
			} else {
				Toast.makeText(RandomQuote.this,
						"Swipe to Right : No more previous Quotes :(",
						Toast.LENGTH_SHORT).show();
			}
		}

		// on every swipe to the left we will get the next random quote.
		public void onSwipeLeft() {

			if (currentIndex < quotePlaceHolder) {
				currentIndex++;
				Toast.makeText(RandomQuote.this,
						"Swipe to Left : Next Random Quote Coming!",
						Toast.LENGTH_SHORT).show();
				textView.setText(randomQuotes.get(currentIndex));
				Tools.setShareQuote(textView.getText().toString());
				changeStarIfFavorite(textView, star);
			} else {
				Toast.makeText(RandomQuote.this,
						"Swipe to Left : New Random Quote Coming!",
						Toast.LENGTH_SHORT).show();
				
				new GetQuote().execute();				
			}
		}

		public void onSwipeBottom() {

		}

		public void onSwipeTop() {

		}
	};


	// need to create an AsyncTask so that the UI thread does not have to do
	// extra work
	// if we make the UI thread to the Jsoup.connect, the application will
	// crash.

	private class GetQuote extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String topic = topics.get((int) (Math.random() * (topics
					.size() - 1)));
			Document doc;
			try {
				String url = "http://www.brainyquote.com/quotes/topics/topic_"
						+ topic + ".html";
				// http://www.brainyquote.com/quotes/topics/topic_age.html
				doc = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.get();
				Elements quote = doc
						.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");

				int randIndex = (int) (Math.random() * quote.size() - 1);

				randomQuotes.add("\"" + quote.get(randIndex).text() + "\""
						+ "\n\n      - " + author.get(randIndex).text());
				currentIndex++;
				quotePlaceHolder++;
				String formattedQuote = String.format("\"%s\" \n\n      - %s",
						quote.get(randIndex).text(), author.get(randIndex)
								.text());

				return formattedQuote;
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String title) {

			textView.setText(title);
			Tools.setShareQuote(textView.getText().toString());
			changeStarIfFavorite(textView, star);
			if (quotePlaceHolder == 0 || quotePlaceHolder == 1) {
				showCustomToast(quotePlaceHolder);
			}
		}
	}
}
