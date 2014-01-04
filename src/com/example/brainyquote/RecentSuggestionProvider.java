package com.example.brainyquote;

import android.content.SearchRecentSuggestionsProvider;

public class RecentSuggestionProvider extends SearchRecentSuggestionsProvider {
	public static final String AUTHORITY =

	RecentSuggestionProvider.class.getName();

	public static final int MODE = DATABASE_MODE_QUERIES;

	public RecentSuggestionProvider() {

		setupSuggestions(AUTHORITY, MODE);

	}
}
