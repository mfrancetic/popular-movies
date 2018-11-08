package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of movies by using an AsyncTask to perform the network request to the given URL.
 */
class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    /**
     * Query URL
     */
    private final String urlAddress;

    /**
     * Constructs a new MovieLoader.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    MovieLoader(Context context, String url) {
        super(context);
        urlAddress = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Performed on the background thread
     */
    @Override
    public List<Movie> loadInBackground() {
        if (urlAddress == null) {
            return null;
        }

        /* Perform a network request, parse the response and extract a list of movies*/
        return QueryUtils.fetchMovieData(urlAddress);
    }
}