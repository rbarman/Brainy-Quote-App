package com.example.brainyquote;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.example.brainyquote.Tools.DeleteFavTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class SpecificQuote extends BaseActivity {

	TextView textView;
	ImageButton share;
	ImageButton star;
	ImageButton bookmark;
	String queryText;
	String url = "";
	String query;
	String searchType = null;
	// possible types are aboutAuthor, byAuthor, tag
	int index = 0;
	int pageNum = 1;
	int quotesOnPage = 0; // number of quotes on page
	int toggle = 0;
	View view;
	boolean foundInitials = false; // foundInitials is true when
									// checkIfContainsInitials(String s) find
									// that s has initials
	boolean foundTopic = false; // foundTopic is true when checkIfTopic(String
								// s) finds that s is a topic.
	boolean noSuchPage = false; // noSuchPage is true in the onPostExecute of
								// TagSearch if "error" is returned => invalid
								// url
	boolean newPage = false; // newPage is true when we got to the previous or
								// next page.
	boolean wentPreviousPage = false; // wentPreviousPage is true when we go to
										// the previous page. main function will
										// be to recalc index.
	boolean bookmarkClicked = false;
	Document doc = null;
	Elements author = null;
	Elements quote = null;
	ArrayList<String> topics = new ArrayList<String>();
	String[] queryTextSplit;
	private static final String TAG = "SpecificQuoteActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_quote);

		Intent intent = getIntent();
		queryText = intent.getExtras().getString("queryText");
		queryTextSplit = intent.getExtras().getStringArray("queryTextSplit");

		view = (View) findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		textView = (TextView) findViewById(R.id.textView);
		textView.setTextSize(fontSize);
		
		share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "clicked",
						Toast.LENGTH_SHORT).show();
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT, sharingQuote);
				shareIntent.setType("text/plain");
				startActivity(Intent.createChooser(shareIntent, "Share via"));
			}
		});

		star = (ImageButton) findViewById(R.id.star);
		star.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = textView.getText().toString();
				String[] quoteAndDir = { text, appDir };
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
		
		bookmark = (ImageButton) findViewById(R.id.bookmark);
		bookmark.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bookmarkClicked = true;
				bookmark.setBackgroundColor(0xffffff00);
					//sets Background to Yellow.
				Toast.makeText(SpecificQuote.this,"Quote Bookmarked!", Toast.LENGTH_SHORT).show();
			}
		});
		getTopics(topics);
		// execute the AsyncTask
		// InitialSearch will determine if we have an author or tag query.
		// then from InitialSearch we will start other respective AsyncTasks.
		new InitialSearch().execute();
	}

	public void startTaskOnSwipe() {
		if (searchType.equals("tag"))
			new TagSearch().execute();
		else if (searchType.equals("byAuthor") && foundInitials == false)
			new ByAuthorSearch().execute();
		else if (searchType.equals("byAuthor") && foundInitials == true)
			new ByAuthorSearch().execute();
		else if (searchType.equals("aboutAuthor") && foundInitials == true)
			new ByAuthorSearch().execute();
		else
			new AboutAuthorSearch().execute();
	}

	OnTouchListener viewSwiped = new OnSwipeTouchListener() {
		public void onSwipeRight() {
			// on every swipe to the right we will get the previous quote.
			if (noSuchPage == false) {
				if (index > 0) {
					// user is going to previous quote.
					Toast.makeText(SpecificQuote.this,
							"Swipe to Right : Previous Quote Coming!",
							Toast.LENGTH_SHORT).show();
					index--;
					startTaskOnSwipe();
				} else if (index == 0 && pageNum > 1) {
					// In this case the user is going back to a previous page of
					// quotes.
					Toast.makeText(SpecificQuote.this,
							"Swipe to Right : Previous Quote Coming!",
							Toast.LENGTH_SHORT).show();
					pageNum--;
					newPage = true;
					wentPreviousPage = true;
					startTaskOnSwipe();
				} else {
					// In this case the user is attempting to go back but the
					// user is at the first quote of first page.
					Toast.makeText(SpecificQuote.this,
							"Swipe to Right : No more previous Quotes :(",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		public void onSwipeLeft() {
			// on every swipe to the left we will get the next quote.
			if (noSuchPage == false) {
				index++;
				if (index == quotesOnPage) { 
					// we are here if we go to a new page of quotes.
					pageNum++;
					index = 0; // index is set to zero to get first quote on new
								// page url
					newPage = true;
				}
				else {
				}
				Toast.makeText(SpecificQuote.this,
						"Swipe to Left : Next Quote Coming!",
						Toast.LENGTH_SHORT).show();
				startTaskOnSwipe();
			}
		}

		public void onSwipeBottom() {

		}

		public void onSwipeTop() {

		}
	};

	public void getDocumentAndModifyElements(String url) {
		// ONLY when we are on a New Page (newPage == true)
		// gets a new Document [getDocument()]
		// modifies author, quote, and quotesOnPage [modifyElements()]

		if (newPage == true) {
			doc = getDocument(url);
			modifyElements();
		} else {
		}
		newPage = false;
	}

	public Document getDocument(String url) {
		// connects to a url and returns a Document.
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
					.get();
			return doc;
		} catch (IOException ioe) {
			return null;
		}
	}

	public void modifyElements() {
		// if there is a document then we select elements from the document.
		// quote and author will take these elements
		// quotesOnPage will be updated and index will be recalculated if the
		// user went to a previous page.
		if (doc != null) {
			quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
			author = doc.select(".boxyPaddingBig span.bodybold a");
			quotesOnPage = quote.size();
			if (wentPreviousPage == true) {
				index = quotesOnPage - 1;
				wentPreviousPage = false;
			}
		} else {
		}
	}

	public void generateUrl() {
		//searchType will be set to its appropriate value within the appropriate async task. 
		if(newPage == true) {
			if (searchType.equals("tag")) 
				writeTagUrl();			
			else if (searchType.equals("byAuthor") && foundInitials == false)
				writeAuthorUrl();
			else if (searchType.equals("byAuthor") && foundInitials == true)
				writeAuthorWithInitialsUrl();
			else if (searchType.equals("aboutAuthor") && foundInitials == true)
				writeAuthorWithInitialsUrl();
			else
				writeTagUrl();
			addHTMLtoUrl();
		}
	}
	public void addHTMLtoUrl() {
		// this method will add ".html" to url added based on the page number.
		if (pageNum == 1)
			url = url + ".html";
		else {
			if (foundTopic == true)
				// topic urls are slightly different for pages > 1
				url = url + pageNum + ".html";
			else
				url = url + "_" + pageNum + ".html";
		}
	}

	public void writeAuthorWithInitialsUrl() {
		// writes a url without ".html" for authors with initials based on how
		// many initials are in the first name. Currently either 2 or 3 initials
		// in first name.
		if (queryTextSplit[0].length() == 2)
			// http://www.brainyquote.com/quotes/authors/c/c_s_lewis.html
			url = "http://www.brainyquote.com/quotes/authors/"
					+ queryTextSplit[0].charAt(0) + "/"
					+ queryTextSplit[0].charAt(0) + "_"
					+ queryTextSplit[0].charAt(1);
		else
			url = "http://www.brainyquote.com/quotes/authors/"
					+ queryTextSplit[0].charAt(0) + "/"
					+ queryTextSplit[0].charAt(0) + "_"
					+ queryTextSplit[0].charAt(1) + "_"
					+ queryTextSplit[0].charAt(2);
		for (int i = 1; i < queryTextSplit.length; i++)
			url = url + "_" + queryTextSplit[i];
	}

	public void writeAuthorUrl() {
		// writes a url without ".html" for authors
		url = "http://www.brainyquote.com/quotes/authors/"
				+ queryTextSplit[0].charAt(0) + "/" + queryTextSplit[0];
		for (int i = 1; i < queryTextSplit.length; i++)
			url = url + "_" + queryTextSplit[i];
	}

	public void writeTagUrl() {
		// writes a tag url based on if the tag is a keyword or topic.
		if (foundTopic == true) {
			// tag is a topic
			url = "http://www.brainyquote.com/quotes/topics/topic_"
					+ queryTextSplit[0];
		} else {
			// tag is a keyword
			url = "http://www.brainyquote.com/quotes/keywords/"
					+ queryTextSplit[0];
			for (int i = 1; i < queryTextSplit.length; i++)
				url = url + "_" + queryTextSplit[i];
		}
	}

	private class SearchWithInitials extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			searchType = "byAuthor";
			generateUrl();
			try {
				Document doc = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.get();
				return "";
			} catch (IOException ioe) {
				// we are here if the first name or string value of queryText
				// when converted to be read as initials
				// results in an invalid author search. This means that
				// queryText can be a tag or an invalid search parameter.
				return "error";
			}
		}

		@Override
		protected void onPostExecute(String message) {
			if (message.equals("error")) {
				new TagSearch().execute();
			}
			else {

				textView.setText("You are searching for " + message + " quotes");
				PopupMenu popup = new PopupMenu(SpecificQuote.this, textView);
				// second parameter is the "anchor";
				popup.getMenuInflater().inflate(R.menu.popup_menu,
						popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {

						switch (item.getItemId()) {
						case R.id.aboutAuthor:
							// new
							// AboutAuthorSearch().execute(generateAuthorWithInitialsUrl(queryText));
							// TODO: temporary fix.
							new AboutAuthorSearch().execute();
							break;
						case R.id.byAuthor:
							new ByAuthorSearch().execute();
							break;
						}
						return true;
					}
				});
				popup.show();
			}
		}
	}

	private class InitialSearch extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			newPage = true;
			searchType = "byAuthor";
			//foundTopic == false && foundInitials == false;
			generateUrl();
			
			try {
				// first run an author search...
				Document doc = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.get();
				return "";

			} catch (IOException ioe) {
				// if queryText gets us an invalid author page with author
				// search, we are put here.
				// queryText can be a tag or an author with initials in the
				// first name.
				if (checkIfTopic(queryTextSplit[0])) {
					return "tag";
				}
				if (checkIfContainsInitials(queryTextSplit[0])) {
					return "found initials";
				}
				return "tag";
			}
		}

		@Override
		protected void onPostExecute(String message) {

			if (message.equals("tag")) {

				// we now know that the search query the user entered does NOT
				// represent an author
				// the search query entered something that represents a keyword
				// / topic.
				// we can start a new AsyncTask that will run a keyword search
				// on the searchQuery.
				new TagSearch().execute();

			} else if (message.equals("found initials")) {

				new SearchWithInitials().execute();
			} else {

				textView.setText("You are searching for " + message + " quotes");

				// we have determined that the user has entered search query
				// that represents an author
				// Brainy Quote has 2 possibilities for authors : quotes BY the
				// author and quotes ABOUT the author.
				// we use the pop up menu to let the user determine which
				// category he wants.
				PopupMenu popup = new PopupMenu(SpecificQuote.this, textView);
				// second parameter is the "anchor";
				popup.getMenuInflater().inflate(R.menu.popup_menu,
						popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {

						switch (item.getItemId()) {
						case R.id.aboutAuthor:
							new AboutAuthorSearch().execute();
							break;
						case R.id.byAuthor:
							new ByAuthorSearch().execute();
							break;
						}
						return true;
					}
				});
				popup.show();
			}
		}
	}

	private class TagSearch extends AsyncTask<Void, Void, String> {
		// this method will run a Tag search.
		@Override
		protected String doInBackground(Void... params) {

			searchType = "tag";
			generateUrl(); 
			getDocumentAndModifyElements(url);
			if (doc == null)
				return "error";
			else {
				Log.i(TAG,"INDEX : " + index
						+ " quotesOnPage : " + quotesOnPage
						+ " PAGE : " + pageNum + "\n\n URL : " + url);
				return quote.get(index).text() + "\n\n--"
						+ author.get(index).text();
			}
		}

		@Override
		protected void onPostExecute(String quote) {
			if (quote.equals("error")) {
				noSuchPage = true;
				textView.setText("ERROR!  INVALID SEARCH" + "\n\n URL : " + url);
			} else {
				textView.setText(quote);
				Tools.setShareQuote(textView.getText().toString());
				changeStarIfFavorite(textView, star);
				if ((pageNum == 1 && index == 0)
						|| (pageNum == 1 && index == 1))
					showCustomToast(index);
			}
		}
	}

	private class ByAuthorSearch extends AsyncTask<Void, Void, String> {
		// this method will return a quote BY the author
		@Override
		protected String doInBackground(Void... params) {

			searchType = "byAuthor";
			generateUrl();
			getDocumentAndModifyElements(url);
			// doc will never be null here.
			Log.i(TAG,"INDEX : " + index
					+ " quotesOnPage : " + quotesOnPage
					+ " PAGE : " + pageNum + "\n\n URL : " + url);
			return quote.get(index).text() + "\n\n--" + queryText;
		}

		@Override
		protected void onPostExecute(String quote) {
			textView.setText(quote);
			Tools.setShareQuote(textView.getText().toString());
			changeStarIfFavorite(textView, star);

			if ((pageNum == 1 && index == 0) || (pageNum == 1 && index == 1))
				showCustomToast(index);
		}
	}

	private class AboutAuthorSearch extends AsyncTask<Void, Void, String> {
		// this method will return a quote ABOUT the author.
		@Override
		protected String doInBackground(Void... params) {

			searchType = "aboutAuthor";
			generateUrl();
			getDocumentAndModifyElements(url);
			if (doc == null)
				return "error";
			else
				Log.i(TAG,"INDEX : " + index
						+ " quotesOnPage : " + quotesOnPage
						+ " PAGE : " + pageNum + "\n\n URL : " + url);
				return quote.get(index).text() + "\n\n--"
						+ author.get(index).text();
		}

		@Override
		protected void onPostExecute(String quote) {

			if (quote.equals("error")) {
				textView.setText("There are no quotes about " + queryText
						+ ". Sorry!" + "\n\n URL : " + url);
			} else {
				textView.setText(quote);
				Tools.setShareQuote(textView.getText().toString());
				changeStarIfFavorite(textView, star);
				if ((pageNum == 1 && index == 0)
						|| (pageNum == 1 && index == 1))
					showCustomToast(index);
			}
		}
	}

	public boolean checkIfTopic(String str) {
		for (String s : topics) {
			if (s.equalsIgnoreCase(str)) {
				foundTopic = true;
				return true;
			}
		}
		return false;
	}

	public boolean checkIfContainsInitials(String str) {
		if (queryTextSplit[0].length() == 2 || queryTextSplit[0].length() == 3) {
			foundInitials = true;
			return true;
		} else
			return false;
	}

}