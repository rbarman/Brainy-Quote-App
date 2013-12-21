package com.example.brainyquote;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.AsyncTask;

/**Houses many AsyncTask classes with common methods
 *used by other activities for background work.
 *Most background methods take an array of 2 strings.
 *Index 0 stores text of textview which contains quote.
 *Next index stores a location of where to write the file.
 *In this case, its the app's installation directory.
 */
public class Tools {
	
	//checks if a given quote exists
	protected static class CheckQuoteTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... quoteAndDir) {
			//create a test quote file to compare with other files in dir
			String filePath;
			if (quoteAndDir[0].length() < 33) {
				filePath = quoteAndDir[1] + "/" + quoteAndDir[0] + ".txt";
			} else {
				filePath = quoteAndDir[1] + "/" + quoteAndDir[0].substring(0, 33) + ".txt";
			}
			
			//get directory of fav files and list the interior files ending with ".txt"
			File dir = new File(quoteAndDir[1]);
			File[] dirFiles = dir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".txt");
			    }
			});
			
			//
			for (File item : dirFiles) {
				if (filePath.equals(item.getAbsolutePath().toString())) {
					return true;
				}
			}
			return false;
		}
	}
	
	//Writes the quote currently on screen to a file.
	protected static class WriteFavQuoteTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... quoteAndDir) {
			// File will be named using part of or all quote.
			// If quote is longer than 32 characters, then take a substring
			// up to 32 characters. Else, fileName = quote
			// This also prevents duplicate favorites by simply overwriting
			// them.
			String filePath;
			if (quoteAndDir[0].length() < 33) {
				filePath = quoteAndDir[1] + "/" + quoteAndDir[0] + ".txt";
			} else {
				filePath = quoteAndDir[1] + "/" + quoteAndDir[0].substring(0, 33) + ".txt";
			}
			// Create file name and write out contents
			try {
				PrintWriter out = new PrintWriter(filePath);
				out.print(quoteAndDir[0]);
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	//deletes all favorited quotes
	protected static class DeleteAllFavsTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			//get directory of fav files and list the interior files ending with ".txt"
			File dir = new File(params[0]);
			File[] dirFiles = dir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".txt");
			    }
			});
			
			for (File item : dirFiles) {
				//delete each quote file
				item.delete();
			}
			return null;
		}
	}
	
	//takes a single string of the directory to delete all
	//favorite files
	protected static class DeleteFavTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... quoteAndDir) {
			//create a test quote file to compare with other files in dir
			String filePath;
			if (quoteAndDir[0].length() < 33) {
				filePath = quoteAndDir[1] + "/" + quoteAndDir[0] + ".txt";
			} else {
				filePath = quoteAndDir[1] + "/" + quoteAndDir[0].substring(0, 33) + ".txt";
			}
			
			File file = new File(filePath);
			file.delete();
			
			return null;
		}
	}
	
	public static void setShareQuote(String quote) {
		BaseActivity.sharingQuote = quote;
	}
}
