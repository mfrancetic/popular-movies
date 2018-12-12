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
     * List of Movie objects
     */
    private List<Movie> movieData;

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
        super.onStartLoading();

        movieData = MainActivity.movieList;

        /* If there is movieData available, deliver the results.
         * If not, forceLoad() */
        if (movieData.size() != 0) {
            deliverResult(movieData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Movie> data) {
        /* Deliver the results set in the onStartLoading method */
        movieData = data;
        super.deliverResult(data);
    }

    /**
     * Performed on the background thread
     */
    @Override
    public List<Movie> loadInBackground() {
        if (urlAddress == null) {
            return null;
        }

        /* If there is movieData available, return it. If not, fetch the movie data */
        if (movieData.size() != 0) {
            return movieData;
        } else {
            /* Perform a network request, parse the response and extract a list of movies*/
            return QueryUtils.fetchMovieData(urlAddress);
        }
    }
}