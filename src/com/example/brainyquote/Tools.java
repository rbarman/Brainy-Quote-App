package com.example.brainyquote;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import android.os.AsyncTask;

//Helper class with useful methods to reduce redundancy
//Must be instantiated to function properly (can't use static methods).
public class Tools {
	
	public void startDeleteFavsTask(String dir) {
		DeleteFavsTask deleteFavs = new DeleteFavsTask();
		deleteFavs.execute(dir);
	}
	
	//deletes all favorited quotes
	private class DeleteFavsTask extends AsyncTask<String, Void, Void> {

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
	
	//will add IO write methods and remove the other
	//write methods from Random and Specific quote activities
}
