package com.example.brainyquote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.AsyncTask;

/**Houses many AsyncTask classes with common methods
 *used by other activities for background work. Also
 *contains methods for retrieving data.
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
	
	//takes a single string of the directory to delete a given
	//favorite file
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
	
	//sets the quote to be shared when the user
	//selects the sharing button
	public static void setShareQuote(String quote) {
		BaseActivity.sharingQuote = quote;
	}

	//TODO 
	//Updates a settings file for user settings such as fonts, colors, etc.
	//Takes a String array of length three. 1. File setting name
	//2. File setting value 3. Settings file path
	protected static class updateSettingsTask extends AsyncTask<String[], Void, Void> {

		@Override
		protected Void doInBackground(String[]... filePath) {
						
			
			/*try {
				PrintWriter out = new PrintWriter(filePath);
				out.print(configInfo[0]);
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}*/
			return null;
	}
	}
	
	//TODO
	//returns config file content for user settings such as fonts, colors, etc.
	protected static String[] readSettings(String Dir) {

		//Currently there are 2 settings, each on its own line
		int numLines = 2;
		String filePath = Dir + "/Settings.cfg";
		String[] content = new String[2];
			
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader textReader = new BufferedReader(fr);
			
			for (int i = 0; i < numLines; i++) {
				content[i] = textReader.readLine();	
			}
			
			textReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return content;
	}
	
	//Checks if there is settings file. If not, make one
	//with default values. Takes a String dir of where to write
	//and check for settings file.
	public static void initSettings(String dir) {
		
		String filePath = dir + "/Settings.cfg";
		
		try {
			File file = new File(filePath);
			if (file.exists()) {
				//No need to make a file :D
			} else {
				PrintWriter out = new PrintWriter(filePath);
				out.print("QuoteFontSize = 20;\n");
				out.print("QuoteFontColor = #000000;\n");
				//more setting configs may be added later
				out.close();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
