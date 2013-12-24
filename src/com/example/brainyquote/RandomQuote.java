package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.brainyquote.Tools.CheckQuoteTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;
import com.example.brainyquote.Tools.DeleteFavTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

//This is the activity launched when the user selects the randomButton on main activity.
public class RandomQuote extends BaseActivity {

	ArrayList<String> categories = new ArrayList<String>();
	ArrayList<String> randomQuotes = new ArrayList<String>();
	TextView textView;
	ImageButton star;
	View view;
	int toggle = 0;
	int currentIndex = -1;
	int quotePlaceHolder = -1;
	String appDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_quote);

		getActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP
						| ActionBar.DISPLAY_SHOW_CUSTOM);
		View view = View.inflate(getApplicationContext(),
				R.layout.custom_actionbar, null);
		getActionBar().setCustomView(view);

		textView = (TextView) findViewById(R.id.textView);
		view = (View) findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		star = (ImageButton) findViewById(R.id.star);
		appDir = getFilesDir().getAbsolutePath().toString();
		getCategories();

		// Star is initially off. Pressing it will toggle it
		// on or off (0 is off, 1 is on)
		star.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (toggle == 0) {
					String[] quoteAndDir = { textView.getText().toString(),
							appDir };
					star.setImageResource(R.drawable.btn_star_big_on);
					new WriteFavQuoteTask().execute(quoteAndDir);
					toggle = 1;
				} else if (toggle == 1) {
					String[] quoteAndDir = { textView.getText().toString(),
							appDir };
					star.setImageResource(R.drawable.btn_star_big_off);
					new DeleteFavTask().execute(quoteAndDir);
					toggle = 0;
				}
			}
		});
		// execute the async task
		new GetQuote().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateFavButton();
	}

	public void updateFavButton() {

		String[] quoteAndDir = { textView.getText().toString(), appDir };
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

	OnTouchListener viewSwiped = new OnSwipeTouchListener() {

		// on every swipe to the right we will get the next random quote.
		public void onSwipeRight() {

			if (currentIndex > 0) {
				currentIndex--;
				Toast.makeText(RandomQuote.this,
						"Swipe to Right : Previous Random Quote Coming!",
						Toast.LENGTH_SHORT).show();
				textView.setText(randomQuotes.get(currentIndex));
				Tools.setShareQuote(textView.getText().toString());
				updateFavButton();
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
				updateFavButton();
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

	public void getCategories() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open(
					"categories.txt")));
			String word;
			while ((word = br.readLine()) != null) {
				categories.add(word);
			}
		} catch (IOException ioe) {
		}
	}

	// need to create an AsyncTask so that the UI thread does not have to do
	// extra work
	// if we make the UI thread to the Jsoup.connect, the application will
	// crash.

	private class GetQuote extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String topic = categories.get((int) (Math.random() * (categories
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
			updateFavButton();

			if (quotePlaceHolder == 0 || quotePlaceHolder == 1) {
				showCustomToast(quotePlaceHolder);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		MenuItem item = menu.findItem(R.id.menu_share);
		shareActionProvider = (ShareActionProvider) item.getActionProvider();
		return true;
	}
}
