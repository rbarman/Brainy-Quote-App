package com.example.brainyquote;

import java.io.IOException;
import java.io.PrintWriter;
import android.os.AsyncTask;

//helper class with methods used by fav buttons from specific
//and random quote activity screens
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
	
			//File will be named using part of or all quote.
			//If quote is longer than 32 characters, then take a substring
			//up to 32 characters, and append 3 dots. Else, fileName = quote
			//This also prevents duplicate favourites by simply overwriting them.
			String fileName;
			if (quoteAndDir[1].length() < 33) {
				fileName = quoteAndDir[1];
			} else {
				fileName = quoteAndDir[0].substring(0, 33) + "...";
			}
			//Create file name and write out contents
			try {
				PrintWriter out = new PrintWriter(quoteAndDir[1] + "/" + fileName + ".txt");
				out.print(quoteAndDir[0]);
				out.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
