package com.example.brainyquote;

import java.io.IOException;
import java.io.PrintWriter;
import android.os.AsyncTask;
import java.util.UUID;

public class Favourites {

	//starts StartWriteFavQuoteTask
	//quoteAndDir array will contain the quote at index 0, and the app dir to save at index 2
	public void StartWriteFavQuoteTask(String[] quoteAndDir) {
		WriteFavQuoteTask quoteTask = new WriteFavQuoteTask();
		quoteTask.execute(quoteAndDir);
	}
	
	//stores the given quote (which includes author) in app's install directory as a txt file
	//using given array. For some reason, using getFilesDir() does not work, so array must
	//also contain app directory for saving.
	private class WriteFavQuoteTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... quoteAndDir) {
	
			try {
				//generate a unique ID for quote file and write quote to it
				String id = UUID.randomUUID().toString();
				PrintWriter out = new PrintWriter(quoteAndDir[1] + "/" + id + ".txt");
				out.print(quoteAndDir[0]);
				out.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	//returns a string array of fav quotes to be used with a listview
	public String[] getFavQuote() {
		
		return null;
	}

}
