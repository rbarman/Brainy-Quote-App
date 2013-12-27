package com.example.brainyquote;

import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	Button searchButton;
	Button randomButton;
	SearchView searchView;
	ImageView logo;
	String queryText = null;
	boolean textChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	    setContentView(R.layout.activity_main);
        searchButton = (Button) findViewById(R.id.searchButton);
        randomButton = (Button) findViewById(R.id.randomButton);
        logo = (ImageView)findViewById(R.id.logo);
        
        randomButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//Now a new intent will be created to go to the RandomQuote.java activity! 
				Intent intent = new Intent(getBaseContext(), RandomQuote.class);
				startActivity(intent);				 
			}
		});        
    }

    public void launchSpecificQuoteActivity(String queryText) {
    	
    	queryText = queryText.replaceAll("[^a-zA-Z\\s]","");
    	String [] queryTextSplit = queryText.split(" ");
    	//regex statement gets rid of all non letter characters.
        Toast toast = Toast.makeText(getApplicationContext(), "Searching for " + queryText + "...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        
        //Now a new intent will be created to go to the SpecificQuote.java activity! 
		Intent intent = new Intent(getBaseContext(), SpecificQuote.class);
		
		//we will pass the value of query as a string variable called queryText to the SpecificQuote activity
		//so the SpecificQuote activity can use the queryText as the search parameter. 
		intent.putExtra("queryTextSplit", queryTextSplit);
		intent.putExtra("queryText", queryText);
		startActivity(intent);	
    }


}
