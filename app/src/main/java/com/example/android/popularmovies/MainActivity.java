package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.TrailerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    /**
     * Generated value of the API key
     */
    public static final String apiKey = "";

    /**
     * Constant value for the movie loader ID
     */
    private static final int MOVIE_LOADER_ID = 1;

    /**
     * Adapter for the grid of movies
     */
    private MovieAdapter movieAdapter;

    /**
     * TextView that is displayed when the grid is empty
     */
    private TextView emptyTextView;

    /**
     * Progress bar displayed while loading the movie data
     */
    private ProgressBar loadingIndicator;

    /**
     * Selected option String, between most popular and top rated
     */
    private String selectedOption;

    /**
     * SelectedPosition integer
     */
    private int selectedPosition;

    /**
     * Selected position of the spinner
     */
    private static int spinnerSelectedPosition;

    /**
     * Key of the spinner selected position
     */
    private static final String SPINNER_SELECTED_POSITION = "spinnerSelectedPosition";

    /**
     * database AppDatabase object
     */
    private AppDatabase appDatabase;

    /**
     * movieList List<Movie> object
     */
    public static List<Movie> movieList;

    /**
     * Key of the movie list
     */
    private static final String MOVIE_LIST = "movieList";

    /**
     * Key of the current movie
     */
    private static final String CURRENT_MOVIE = "currentMovie";

    /**
     * Api-key string
     */
    private static final String API_KEY = "api_key";

    private static final String SPAN_COUNT = "spanCount";

    private int spanCount;

    /**
     * Current scrolling position
     */
    private Parcelable savedRecyclerViewState;

    /**
     * Key of the scrollIndex
     */
    private static final String RECYCLER_VIEW_BUNDLE = "recyclerViewBundle";

    /**
     * GridView
     */
    private RecyclerView movieRecyclerView;

    /**
     * ScrollView of the DetailActivity
     */
    private NestedScrollView scrollView;



    private static final String SCROLL_POSITION_Y = "scrollPositionY";

    private int scrollY;

    private GridLayoutManager mLayoutManager;



    private int scrollIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTextView = findViewById(R.id.empty_text_view);
        movieRecyclerView = findViewById(R.id.movies_recycler_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        scrollView = findViewById(R.id.main_scroll_view);

//        movieRecyclerView.setNestedScrollingEnabled(false);
        movieRecyclerView.setFocusable(false);
        scrollView.requestFocus();

        /* Create a new TrailerAdapter and ReviewAdapter */
        movieAdapter = new MovieAdapter(movieList);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3;
        } else {
            spanCount = 2;
        }
        /* Set a new LinearLayoutManager to the trailerRecyclerView and reviewRecyclerView*/
        movieRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        mLayoutManager = new GridLayoutManager(this, spanCount);
        movieRecyclerView.setLayoutManager(mLayoutManager);

        /* Set the adapters to the RecyclerViews */
        movieRecyclerView.setAdapter(movieAdapter);

        /* Get instance of the AppDatabase using the app context */
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        if (selectedOption == null) {
            selectedOption = getString(R.string.settings_sort_by_most_popular_value);
        }

        /* Check if the savedInstanceState exists, and contains the key "movieList".
         * If so, get the values under their keys from the savedInstanceState,
         * if not, create a new ArrayList and initialize the loader. */
        if (savedInstanceState == null) {
            if (movieList == null) {
                movieList = new ArrayList<>();
//                initializeLoader();
                new MovieAsyncTask().execute(selectedOption);
            }
        } else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
            scrollY = savedInstanceState.getInt(SCROLL_POSITION_Y);
            savedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_BUNDLE);

//            savedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_BUNDLE);
//            movieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
        }
        generateSpinner();
        generateRecyclerView();
    }

    /**
     * OnResume the selected spinner position is checked - and the initializeLoader method (for
     * popular or top rated movies) or loadFavorites (for favorite movies) method is called respectively
     */
    @Override
    protected void onResume() {
        super.onResume();
        selectedPosition = spinnerSelectedPosition;

        if (spinnerSelectedPosition == 2) {
            loadFavorites();
        }
    }

    /**
     * Store the values under their keys to the savedInstanceState bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SPINNER_SELECTED_POSITION, selectedPosition);
        savedInstanceState.putParcelableArrayList(MOVIE_LIST, (ArrayList<? extends Parcelable>) movieList);
//        savedInstanceState.putParcelable(RECYCLER_VIEW_BUNDLE, movieRecyclerView.getLayoutManager().onSaveInstanceState());
        scrollY = scrollView.getScrollY();
        savedInstanceState.putInt(SCROLL_POSITION_Y, scrollY);

        savedRecyclerViewState = mLayoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable(RECYCLER_VIEW_BUNDLE, savedRecyclerViewState);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Populate the trailers of the current movie
     */
    private void populateMovies() {
        movieAdapter = new MovieAdapter(movieList);
        movieRecyclerView.setAdapter(movieAdapter);
        if(savedRecyclerViewState != null){
            mLayoutManager.onRestoreInstanceState(savedRecyclerViewState);
        }
        movieRecyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
//        scrollView.scrollTo(0,scrollY);
    }

    /**
     * Generate and populate the GridView
     */
    private void generateRecyclerView() {
        /* Find a reference to the GridView in the layout, create a new adapter that takes
         * an empty list of movies an input and set the adapter on the GridView,
         * so the grid can be populated in the user interface. */

        final Context context = getBaseContext();

//        if (savedRecyclerViewState != null) {
//            movieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
//        }

        if (movieList == null) {
            new MovieAsyncTask().execute(selectedOption);
        } else if (movieList.size() == 0) {
            emptyTextView.setText(R.string.no_movies_found);
            loadingIndicator.setVisibility(View.GONE);
        }

        /* Get the instance of the AppDatabase using the ApplicationContext */
        appDatabase = AppDatabase.getInstance(getApplicationContext());
    }

    /**
     * Generate the Spinner, which enables sorting between most popular and top rated movies
     */
    private void generateSpinner() {

        Spinner spinner = findViewById(R.id.spinner);

        /* Create a new ArrayAdapter, set the DropDownViewResource and set the adapter to the spinner */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, R.layout.support_simple_spinner_dropdown_item);

        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerSelectedPosition);

        /* Set the OnItemSelectedListener on the Spinner */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* Depending on the selected item, update the selectedOption value with the value
                 * of the popular or top_rated key, or load the Favorites from the database */

                selectedPosition = parent.getSelectedItemPosition();

                if (!MainActivity.movieList.isEmpty() && selectedPosition == MainActivity.spinnerSelectedPosition) {
                    loadingIndicator.setVisibility(View.GONE);
//                    populateMovies();

                    return;
                }
                if (selectedPosition == 0) {

                    selectedOption = getString(R.string.settings_sort_by_most_popular_value);
                    new MovieAsyncTask().execute(selectedOption);

                } else if (selectedPosition == 1) {

                    selectedOption = getString(R.string.settings_sort_by_top_rated_value);
                    new MovieAsyncTask().execute(selectedOption);

                } else if (selectedPosition == 2) {
                    /* if the selectedPosition is 2, call the method loadFavorites to load all the
                     * favorite movies from the local database */
                    loadFavorites();
                }

                MainActivity.spinnerSelectedPosition = selectedPosition;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * ReviewAsyncTask class that uses the movie ID to create the reviewUrl String of that
     * movie, makes the HTTP request and parses the JSON String in order to create a new Review object.
     * Returns a list of reviews.
     */
    private class MovieAsyncTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {

//            int id = currentMovie.getMovieId();
            movieList = new ArrayList<>();

            try {
                URL url = QueryUtils.createMovieUrl(selectedOption);
                String movieJson = QueryUtils.makeHttpRequest(url);

                /* Create a JSONObject from the JSON response string */
                JSONObject baseJsonResponse = new JSONObject(movieJson);

                /* Extract the JSONArray with the key "results" **/
                JSONArray movieArray = baseJsonResponse.getJSONArray("results");

                if (movieArray.length() == 0) {
                    return movieList;
                }

                /* For each review in the reviewArray, create a Review object */
                for (int i = 0; i < movieArray.length(); i++) {

                    /* Get a single review at position i within the list of reviews */
                    JSONObject movieObject = movieArray.getJSONObject(i);

                    /* Extract the value for the required keys */
                    String title = movieObject.getString("title");
                    String releaseDate = movieObject.getString("release_date");
                    String posterUrl = movieObject.getString("poster_path");
                    String userRating = movieObject.getString("vote_average");
                    String plotSynopsis = movieObject.getString("overview");
                    int id = movieObject.getInt("id");

                    /* Create a new Movie object with the title, releaseDate, posterUrl, userRating,
                    plotSynopsis and ID from the JSON response. */
                    Movie movie = new Movie(id, title, releaseDate, posterUrl, userRating, plotSynopsis);

                    movie.setMovieId(id);
                    movie.setMovieTitle(title);
                    movie.setMovieReleaseDate(releaseDate);
                    movie.setMovieUrlPoster(posterUrl);
                    movie.setMovieUserRating(userRating);
                    movie.setMoviePlotSynopsis(plotSynopsis);

                    movieList.add(i, movie);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            /* Return a list of reviews */
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            /* If there are no trailers, hide the trailerRecyclerView and inform the user there
             * are no trailers found */
            if (movies.size() == 0) {
                movieRecyclerView.setVisibility(View.GONE);
                emptyTextView.setText(getString(R.string.no_movies_found));
                loadingIndicator.setVisibility(View.GONE);
            } else {
                populateMovies();
            }
        }
    }

    /**
     * Load the favorite movies saved in the local database, using the ViewModel and Observer
     */
    private void loadFavorites() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movies) {
                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");

                /* Load all favorite movies from the database */
                LiveData<List<Movie>> favoriteMovies = appDatabase.movieDao().loadAllFavoriteMovies();

                /* Get the AppExecutors and check if there are movies saved in the Favorites */
                AppExecutors.getExecutors().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int numberOfMovies = appDatabase.movieDao().getMovieCount();
                        /* In case the numberOfMovies is smaller than 1, inform the user that there
                         * are no movies in the Favorites list */
                        if (numberOfMovies < 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingIndicator.setVisibility(View.GONE);
                                    emptyTextView.setText(R.string.no_favorite_movies);
                                }
                            });
                        } else {
                            /* If there are movies in the Favorites list, populate the UI with their
                            movie posters */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    /* Hide the empty state text view as the loading indicator will
                                    be displayed */
                                    movieList = movies;
                                    movieAdapter.notifyDataSetChanged();
                                    populateMovies();
                                    emptyTextView.setVisibility(View.GONE);

                                    /* Show the loading indicator while new date is being fetched,
                                    notify the movieAdapter that the DataSet has been change, and
                                    after adding all the movies to the adapter, hide the loading
                                    indicator */
                                    loadingIndicator.setVisibility(View.VISIBLE);
                                    loadingIndicator.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}