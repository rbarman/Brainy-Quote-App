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
import com.example.brainyquote.Tools.DeleteFavTask;
import com.example.brainyquote.Tools.WriteFavQuoteTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SpecificQuote extends BaseActivity {

	TextView textView;
	String queryText;
	String appDir;
	String url = "";
	String searchType = null;
	// possible types are aboutAuthor, byAuthor, tag
	int index = 0;
	int pageNum = -1;
	int quoteNum = 0; // number of quotes
	int toggle = 0;
	View view;
	boolean foundInitials = false;
	boolean foundTopic = false;
	boolean nextPage = false;
	boolean first = false;
	Document doc = null;
	Elements author = null;
	Elements quote = null;
	ImageButton star;
	ArrayList<String> topics = new ArrayList<String>();
	String[] queryTextSplit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_quote);
		// Show the Up button in the action bar.
		setupActionBar();

		Intent intent = getIntent();
		queryText = intent.getExtras().getString("queryText");
		queryTextSplit = intent.getExtras().getStringArray("queryTextSplit");

		view = (View) findViewById(R.id.view);
		view.setOnTouchListener(viewSwiped);
		textView = (TextView) findViewById(R.id.textView);
		star = (ImageButton) findViewById(R.id.star);
		appDir = getFilesDir().getAbsolutePath().toString();

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
		// execute the AsyncTask
		// InitialSearch will determine if we have an author or tag query.
		// then from InitialSearch we will start other respective AsyncTasks.
		getTopics();
		new InitialSearch().execute((generateAuthorUrl()));
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

	public void startTaskOnSwipe() {
		if (searchType.equals("tag"))
			new TagSearch().execute(generateTagUrl());
		else if (searchType.equals("byAuthor") && foundInitials == false)
			new ByAuthorSearch().execute(generateAuthorUrl());
		else if (searchType.equals("byAuthor") && foundInitials == true)
			new ByAuthorSearch()
					.execute(generateAuthorWithInitialsUrl());
		else if (searchType.equals("aboutAuthor") && foundInitials == true)
			new AboutAuthorSearch()
					.execute(generateAuthorWithInitialsUrl());
		else
			new AboutAuthorSearch().execute(generateTagUrl());
	}

	OnTouchListener viewSwiped = new OnSwipeTouchListener() {
		public void onSwipeRight() {
			// on every swipe to the right we will get the previous quote.

			if (index > 0) {
				Toast.makeText(SpecificQuote.this,
						"Swipe to Right : Previous Quote Coming!",
						Toast.LENGTH_SHORT).show();
				index--;
				startTaskOnSwipe();
			} else if (index == 0 && pageNum > 1) {
				Toast.makeText(SpecificQuote.this,
						"Swipe to Right : Previous Quote Coming!",
						Toast.LENGTH_SHORT).show();
				index = quoteNum - 1;
				pageNum--;
				startTaskOnSwipe();
			} else {
				Toast.makeText(SpecificQuote.this,
						"Swipe to Right : No more previous Quotes :(",
						Toast.LENGTH_SHORT).show();
			}
		}

		public void onSwipeLeft() {
			// on every swipe to the left we will get the next quote.
			index++;
			Toast.makeText(SpecificQuote.this,
					"Swipe to Left : Next Quote Coming!", Toast.LENGTH_SHORT)
					.show();
			startTaskOnSwipe();
		}

		public void onSwipeBottom() {

		}

		public void onSwipeTop() {

		}
	};

	public Document getDocument() {
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
		if (doc != null) {
			quote = doc.select(".boxyPaddingBig span.bqQuoteLink a");
			author = doc.select(".boxyPaddingBig span.bodybold a");
			quoteNum = quote.size();
		} else {
		}
	}

	public void getDocumentAndModifyElements() {
		if (nextPage = true) {
			doc = getDocument();
			modifyElements();
		} else {
		}
		nextPage = false;
	}

	public String generateAuthorWithInitialsUrl() {

		if (first == true)
			pageNum--;
		else {
		}
		first = false;
		if (index == quoteNum) {
			//we are here if we go to a new page of quotes. 
			pageNum++;
			index = 0;
			nextPage = true;
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
			return addHTMLtoUrl(url);
		} else 
			return url;
	}

	public String addHTMLtoUrl(String url) {
		if (pageNum == 1)
			url = url + ".html";
		else
			if(foundTopic == true)
				//topic urls are slightly different for pages > 1
				url = url + pageNum + ".html";
			else
				url = url + "_" + pageNum + ".html";
		return url;
	}
	public String generateAuthorUrl() {

		if (queryTextSplit[0].length() == 2 || queryTextSplit[0].length() == 3)
			foundInitials = true;
		else {}
		
		if (index == quoteNum) {
			//we are here if we go to a new page of quotes. g
			pageNum++;
			index = 0;
			nextPage = true;
			url = "http://www.brainyquote.com/quotes/authors/"
					+ queryTextSplit[0].charAt(0) + "/" + queryTextSplit[0];
			for (int i = 1; i < queryTextSplit.length; i++)
				url = url + "_" + queryTextSplit[i];
			return addHTMLtoUrl(url);
		} else 
			return url;
		
	}

	public String generateTagUrl() {

		if (first == true && foundTopic == true)
			pageNum--;
		else {}
		first = false;
		
		if(index == quoteNum) {
		pageNum++;
		index = 0;
		nextPage = true;
		if (foundTopic == true) {
			//tag is a topic
			url = "http://www.brainyquote.com/quotes/topics/topic_"
					+ queryTextSplit[0];
			return addHTMLtoUrl(url);
		}
		else {
			//tag is a keyword
			url = "http://www.brainyquote.com/quotes/keywords/"
			+ queryTextSplit[0];
			for (int i = 1; i < queryTextSplit.length; i++)
				url = url + "_" + queryTextSplit[i];
			return addHTMLtoUrl(url);
		}
	}
	else
		return url;
	}

	private class SearchWithInitials extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				url = params[0];
				Document doc = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.get();
				return "";
			} catch (IOException ioe) {
				return "error";
			}
		}

		@Override
		protected void onPostExecute(String message) {
			if (message.equals("error"))
				new TagSearch().execute(generateTagUrl());
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
							new AboutAuthorSearch()
									.execute(generateTagUrl());
							break;
						case R.id.byAuthor:
							new ByAuthorSearch()
									.execute(generateAuthorWithInitialsUrl());
							break;
						}
						return true;
					}
				});
				popup.show();
			}
		}
	}

	private class InitialSearch extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				// first run an author search...
				url = params[0];
				Document doc = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.get();
				return "";

			} catch (IOException ioe) {
				// if queryText gets us an invalid author page with author
				// search, we are put here.
				//queryText can be a tag or an author with initials in the first name. 
				first = true;
				checkIfTopic(queryTextSplit[0]);
				if (foundInitials == true)
					return "found initials";
				return "error";
			}
		}

		@Override
		protected void onPostExecute(String message) {

			if (message.equals("error")) {
				// we now know that the search query the user entered does NOT
				// represent an author
				// the search query entered something that represents a keyword
				// / topic.
				// we can start a new AsyncTask that will run a keyword search
				// on the searchQuery.
				new TagSearch().execute(generateTagUrl());
				

			} else if (message.equals("found initials")) {
				new SearchWithInitials()
						.execute(generateAuthorWithInitialsUrl());

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
							new AboutAuthorSearch()
									.execute(generateTagUrl());
							break;
						case R.id.byAuthor:
							new ByAuthorSearch()
									.execute(generateAuthorUrl());
							break;
						}
						return true;
					}
				});
				popup.show();
			}
		}
	}

	private class TagSearch extends AsyncTask<String, Void, String> {
		// this method will run a Tag search.
		@Override
		protected String doInBackground(String... params) {

			searchType = "tag";
			getDocumentAndModifyElements();
			if (doc == null)
				return "ERROR!  INVALID SEARCH" + "\n\n URL : " + url;
			else
				return quote.get(index).text() + "\n\n--"
						+ author.get(index).text() + "\n\n INDEX : " + index
						+ "\n\n quoteNum : " + quoteNum + "\n\n PAGE : "
						+ pageNum + "\n\n URL : " + url;
		}

		@Override
		protected void onPostExecute(String quote) {
			star.setVisibility(0);
			textView.setText(quote);
			updateFavButton();
			if((pageNum == 1 && index == 0) || (pageNum == 1 && index == 1))
				showCustomToast(index);
		}
	}

	private class ByAuthorSearch extends AsyncTask<String, Void, String> {
		// this method will return a quote BY the author
		@Override
		protected String doInBackground(String... params) {

			searchType = "byAuthor";
			getDocumentAndModifyElements();
			if (doc == null)
				return "ERROR!  INVALID SEARCH" + "\n\n URL : " + url;
			else
				return quote.get(index).text() + "\n\n--" + queryText
						+ "\n\n INDEX : " + index + "\n\n quoteNum : "
						+ quoteNum + "\n\n PAGE : " + pageNum + "\n\n URL : "
						+ url;
		}

		@Override
		protected void onPostExecute(String quote) {
			star.setVisibility(0);
			textView.setText(quote);
			updateFavButton();
			
			if((pageNum == 1 && index == 0) || (pageNum == 1 && index == 1))
				showCustomToast(index);
		}
	}

	private class AboutAuthorSearch extends AsyncTask<String, Void, String> {
		// this method will return a quote ABOUT the author.
		@Override
		protected String doInBackground(String... params) {

			searchType = "aboutAuthor";
			getDocumentAndModifyElements();
			if (doc == null)
				return "There are no quotes about " + queryText + ". Sorry!"
						+ "\n\n URL : " + url;
			else
				return quote.get(index).text() + "\n\n--"
						+ author.get(index).text() + "\n\n INDEX : " + index
						+ "\n\n quoteNum : " + quoteNum + "\n\n URL : " + url;

		}

		@Override
		protected void onPostExecute(String quote) {

			if (quote.equals("error")) {
				textView.setText("No available quotes about " + queryText + "!");
				updateFavButton();
			} else {
				star.setVisibility(0);
				textView.setText(quote);
				updateFavButton();
				if((pageNum == 1 && index == 0) || (pageNum == 1 && index == 1))
					showCustomToast(index);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateFavButton();
	}

	public void getTopics() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open(
					"categories.txt")));
			String word;
			while ((word = br.readLine()) != null) {
				topics.add(word);
			}
		} catch (IOException e) {
		}
	}

	public void checkIfTopic(String str) {
		for (String s : topics) {
			if (s.equalsIgnoreCase(str)) {
				foundTopic = true;
				break;
			}
		}
	}
}