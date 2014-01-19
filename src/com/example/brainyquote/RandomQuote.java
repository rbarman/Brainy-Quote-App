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

/*This is the activity launched when the user selects the randomButton on main activity.
 * It will display random quotes to the user. 
 */
public class RandomQuote extends BaseActivity {

	//stores all BrainyQuote topics from a call to getTopics
	ArrayList<String> topics = new ArrayList<String>();
	/*
	 * randomQuotes holds all previous random quotes
	 * so that the user can swipe back to see older quotes 
	 * and forward to see more current quotes that are not new. 
	 */
	ArrayList<String> randomQuotes = new ArrayList<String>();
	TextView textView;
	ImageButton share;
	ImageButton star;
	View view;
	int toggle = 0;
	/*
	 * currentIndex represents the index value of randomQuotes
	 * that the user is currently on.
	 */
	int currentIndex = -1;
	/*
	 * quotePlaceHolder represents the max index value of a quote 
	 * that the user has seen. 
	 * The difference between currentIndex and quotePlaceholder
	 * is explained more in onSwipeTouchListener.
	 */
	int quotePlaceHolder = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_quote);
		
		//textView will display quote and author text
		textView = (TextView) findViewById(R.id.textView);
		textView.setTextSize(fontSize);
		//an onSwipeTouchListener will be used on view
		view = (View) findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);

		share = (ImageButton)findViewById(R.id.share);
		/*
		 * User clicks on the share button. 
		 * User will be able to choose from different 
		 * options available on the user's phone to share currently displayed quote.
		 */
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
				/*
				 * Create an intent to share the currently displayed quote.
				 * .setAction to set a sending intent that will open up a 
				 * partially full activity where the user can choose an option
				 * to share the currently displayed quote. 
				 */
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT, sharingQuote);
				shareIntent.setType("text/plain");
				startActivity(Intent.createChooser(shareIntent, "Share via"));
			}
		});
		
		star = (ImageButton)findViewById(R.id.star);
		/*
		 * User clicks on the star button
		 * Prior to clicking, the star is either on or off
		 * depending on if it is a favorite or not (toggle == 0 || toggle == 1)
		 * If quote is not a favorite, quote will be saved as favorite.
		 * If quote is a favorite, quote will be deleted from favorites.
		 */
		star.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//text = quote + author text. 
				String text = textView.getText().toString();
				String[] quoteAndDir = {text, appDir};
				//quote is not a favorite
				if (toggle == 0) { 	
					star.setImageResource(R.drawable.btn_star_big_on);	
					/*
					 * WriteFavQuoteTask is asyn task more described in Tools.java
					 * It will save the quote in a file in appDir. 
					 * Quote will be saved as a favorite.
					 */
					new WriteFavQuoteTask().execute(quoteAndDir);
					toggle = 1;
				} 
				//quote is already favorite
				else if (toggle == 1) {
					star.setImageResource(R.drawable.btn_star_big_off);
					/*
					 * DeleteFavTask is an async task more described in Tools.java
					 * Finds the saved file that represents the displayed quote 
					 * and deletes it. 
					 * Quote is no longer a favorite. 
					 */
					new DeleteFavTask().execute(quoteAndDir);
					toggle = 0;
				}
			}
		});

		// gets all topics from assets/categories.txt and puts in topics
		getTopics(topics);
		
		/*
		 * Using JSoup to parse BrainyQuote is too much 
		 * for the UI thread and will cause the app to crash
		 * thus it is necessary to use an async task. (GetQuote)
		 */
		new GetQuote().execute();
	}

	/*
	 * OnSwipeTouchListener.java detects swipes to 
	 * right, left, up, or down. 
	 */
	OnTouchListener viewSwiped = new OnSwipeTouchListener() {

		/*
		 * Swipe to the right will result in displaying previous quote.
		 * currentIndex --;
		 * (non-Javadoc)
		 * @see com.example.brainyquote.OnSwipeTouchListener#onSwipeRight()
		 */
		public void onSwipeRight() {

			if (currentIndex > 0) {
				currentIndex--;
				Toast.makeText(RandomQuote.this,
						"Swipe to Right : Previous Random Quote Coming!",
						Toast.LENGTH_SHORT).show();
				//set textView to currentIndex of randomQuotes
				textView.setText(randomQuotes.get(currentIndex));
				Tools.setShareQuote(textView.getText().toString());
				//check if quote is already a favorite
				changeStarIfFavorite(textView, star);
			} 
			//User is on the first quote and can not go back in randomQuotes
			else {
				Toast.makeText(RandomQuote.this,
						"Swipe to Right : No more previous Quotes :(",
						Toast.LENGTH_SHORT).show();
			}
		}

		/*on every swipe to the left we will get the next random quote.
		 * currentIndex++;
		 * (non-Javadoc)
		 * @see com.example.brainyquote.OnSwipeTouchListener#onSwipeLeft()
		 */
		public void onSwipeLeft() {
			/*
			 * currentIndex is the index of currently displayed quote
			 * quotePlaceHolder is index of newest displayed quote. 
			 * ex) quotePlaceHolder and currentIndex can be 4 if user 
			 * swiped left continuously. However if user swiped right once,
			 * currentIndex -- = 3 while currentIndex = 4. If the user swipes
			 * left in this scenario, the user will want to see the next quote in 
			 * randomQuotes, not a new quote. Below if statement is for this situation  
			 */
			if (currentIndex < quotePlaceHolder) {
				currentIndex++;
				Toast.makeText(RandomQuote.this,
						"Swipe to Left : Next Random Quote Coming!",
						Toast.LENGTH_SHORT).show();
				textView.setText(randomQuotes.get(currentIndex));
				Tools.setShareQuote(textView.getText().toString());
				changeStarIfFavorite(textView, star);
			} 
			/*
			 * currentIndex == quotePlaceHolder
			 * User is on max index and wants to see a new
			 * random quote, execute GetQuote().
			 */
			else {
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

	/*
	 * GetQuote async task will... 
	 * 1. choose a random topic from topics
	 * 2. Connect topic url with random topic
	 * 3. Parse : get quote and author values as Elements
	 * 4. choose random index value from quote Elements
	 * 5. add random quote to randomQuotes and display it.
	 * 6. currentIndex++; quotePlaceHolder++;
	 */
	private class GetQuote extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			//chose a random topic
			String topic = topics.get((int) (Math.random() * (topics
					.size() - 1)));
			Document doc;
			/*
			 * connect to a url with random topic
			 * ex) http://www.brainyquote.com/quotes/topics/topic_age.html
			 */
			try {
				String url = "http://www.brainyquote.com/quotes/topics/topic_"
						+ topic + ".html";
				doc = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.get();
				/*
				 * quote and author hold quote and author text respectively
				 */
				Elements quote = doc
						.select(".boxyPaddingBig span.bqQuoteLink a");
				Elements author = doc.select(".boxyPaddingBig span.bodybold a");
				
				/*
				 * choose random quote from (Elements) quote and add to randomQuotes
				 */
				int randIndex = (int) (Math.random() * quote.size() - 1);
				randomQuotes.add("\"" + quote.get(randIndex).text() + "\""
						+ "\n\n      - " + author.get(randIndex).text());
				//new max index + user on new max index
				currentIndex++;
				quotePlaceHolder++;
				String formattedQuote = String.format("\"%s\" \n\n      - %s",
						quote.get(randIndex).text(), author.get(randIndex)
								.text());
				//return value will be sent to onPostExecute(String returnValue);
				return formattedQuote;
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String quoteAndAuthor) {
			//user sees quoteAndAuthor in textView
			textView.setText(quoteAndAuthor);
			Tools.setShareQuote(textView.getText().toString());
			changeStarIfFavorite(textView, star);
			/*
			 * User will see instructional toast message if
			 * user is on 1st or 2nd quote. 
			 */
			if (quotePlaceHolder == 0 || quotePlaceHolder == 1) {
				showCustomToast(quotePlaceHolder);
			}
		}
	}
}
