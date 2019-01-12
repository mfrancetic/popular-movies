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
import android.os.Parcel;
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
    public static final String apiKey = "f23c3ad9ff6e93efec4c6716b2d44d35";

    private static final String LIST_STATE = "LIST_STATE";

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
    public static int selectedPosition;

    /**
     * Selected position of the spinner
     */
    private static int spinnerSelectedPosition;

    /**
     * Key of the spinner selected position
     */
    public static final String SPINNER_SELECTED_POSITION = "spinnerSelectedPosition";

    /**
     * database AppDatabase object
     */
    private AppDatabase appDatabase;

    /**
     * movieList List<Movie> object
     */
    public List<Movie> movieList;

    /**
     * Key of the movie list
     */
    private final String MOVIE_LIST = "movieList";

    private int spanCount;

    /**
     * Current scrolling position
     */
    private Parcelable savedRecyclerViewState;
    private Parcelable savedRecyclerViewState1;

    /**
     * GridView
     */
    public RecyclerView movieRecyclerView;

    /**
     * ScrollView of the DetailActivity
     */
    private NestedScrollView scrollView;

    private GridLayoutManager mLayoutManager;

    private boolean changeSpinner = true;
    private Spinner spinner;
    private int mScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieList = new ArrayList<>();

        emptyTextView = findViewById(R.id.empty_text_view);
        movieRecyclerView = findViewById(R.id.movies_recycler_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        scrollView = findViewById(R.id.main_scroll_view);

        movieRecyclerView.setFocusable(false);
        scrollView.requestFocus();

        /* Create a new MovieAdapter */
        movieAdapter = new MovieAdapter();
        movieAdapter.setMovieList(movieList);

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
        if (getIntent() != null) {
            selectedPosition = getIntent().getIntExtra(SPINNER_SELECTED_POSITION, 0);

        }

        /* Check if the savedInstanceState exists, and contains the key "movieList".
         * If so, get the values under their keys from the savedInstanceState,
         * if not, create a new ArrayList and initialize the loader. */
        generateRecyclerView();
        if (savedInstanceState == null) {
            generateSpinner(0);
            if (movieList == null) {
                Log.i("TAG", " onCreate IF " + (savedRecyclerViewState1 != null) + " " + (savedInstanceState == null));
                new MovieAsyncTask().execute(selectedOption);
            }
        } else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
            generateSpinner(spinnerSelectedPosition);
            savedRecyclerViewState1 = savedInstanceState.getParcelable(LIST_STATE);
            movieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState1);
            Log.i("TAG", " onCreate changeSpinner" + changeSpinner + " " + savedInstanceState.getInt(SPINNER_SELECTED_POSITION));
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager layoutManager = movieRecyclerView.getLayoutManager();
        savedRecyclerViewState1 = movieRecyclerView.getLayoutManager().onSaveInstanceState();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            mScrollPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        outState.putParcelable(LIST_STATE, savedRecyclerViewState1);
        outState.putInt(SPINNER_SELECTED_POSITION, selectedPosition);
        outState.putInt("ItemSelect", spinner.getSelectedItemPosition());
        outState.putParcelableArrayList(MOVIE_LIST, (ArrayList<? extends Parcelable>) movieList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            Log.i("TAG", " onRestoreInstanceState " + (savedRecyclerViewState1 != null));
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
            savedRecyclerViewState1 = savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    private void restorePosition() {
        Log.i("TAG", " restorePosition " + (savedRecyclerViewState1 != null));

        if (savedRecyclerViewState1 != null) {
            Log.i("TAG", " savedRecyclerViewState " + (savedRecyclerViewState1 != null));
            movieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState1);
            savedRecyclerViewState = null;
        }
    }

    /**
     * Populate the trailers of the current movie
     */
    private void populateMovies(List<Movie> movies) {
        Log.i("TAG", " populateMovies " + (savedRecyclerViewState1 != null));
        this.movieList = movies;
        movieAdapter.setMovieList(movieList);
        restorePosition();
        movieRecyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    /**
     * Generate and populate the GridView
     */
    private void generateRecyclerView() {
        /* Find a reference to the GridView in the layout, create a new adapter that takes
         * an empty list of movies an input and set the adapter on the GridView,
         * so the grid can be populated in the user interface. */
        Log.i("TAG", " generateRecyclerView " + (savedRecyclerViewState1 != null));

        if (movieList == null) {
            Log.i("TAG", " generateRecyclerView movieList " + (movieList == null));
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
    private void generateSpinner(int selected) {
        Log.i("TAG", " generateSpinner " + (savedRecyclerViewState1 != null));
        spinner = findViewById(R.id.spinner);

        /* Create a new ArrayAdapter, set the DropDownViewResource and set the adapter to the spinner */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, R.layout.support_simple_spinner_dropdown_item);

        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(selected);

        /* Set the OnItemSelectedListener on the Spinner */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* Depending on the selected item, update the selectedOption value with the value
                 * of the popular or top_rated key, or load the Favorites from the database */

                selectedPosition = parent.getSelectedItemPosition();
                Log.i("TAG", " selectedOption " + position + " " + selectedPosition + "   " + (view != null));

                 if (!movieList.isEmpty() && selectedPosition == MainActivity.spinnerSelectedPosition) {
                     loadingIndicator.setVisibility(View.GONE);
                     return;
                 }
                if (view == null) {
                    populateMovies(movieList);
                } else {
                    if (selectedPosition == 0) {
                        selectedOption = getString(R.string.settings_sort_by_most_popular_value);
                        new MovieAsyncTask().execute(selectedOption);
                    } else if (selectedPosition == 1) {
                        Log.i("TAG", " selectedOption " + selectedOption);
                        selectedOption = getString(R.string.settings_sort_by_top_rated_value);
                        new MovieAsyncTask().execute(selectedOption);
                    } else if (selectedPosition == 2) {
                        /* if the selectedPosition is 2, call the method loadFavorites to load all the
                         * favorite movies from the local database */
                        loadFavorites();
                    }
                    MainActivity.spinnerSelectedPosition = selectedPosition;
                }
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
            List<Movie> movieList1 = new ArrayList<>();

            try {
                URL url = QueryUtils.createMovieUrl(selectedOption);
                String movieJson = QueryUtils.makeHttpRequest(url);

                /* Create a JSONObject from the JSON response string */
                JSONObject baseJsonResponse = new JSONObject(movieJson);

                /* Extract the JSONArray with the key "results" **/
                JSONArray movieArray = baseJsonResponse.getJSONArray("results");

                if (movieArray.length() == 0) {
                    return movieList1;
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

                    movieList1.add(i, movie);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            /* Return a list of reviews */
            return movieList1;
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
                Log.i("TAG", " onPostExecute " + (savedRecyclerViewState1 != null));
                populateMovies(movies);
            }
        }
    }

    /**
     * Load the favorite movies saved in the local database, using the ViewModel and Observer
     */
    private void loadFavorites() {

//        if (spinnerSelectedPosition != 2) {
//            return;
//        }


//        if (selectedPosition != 2) {
//            return;
//        }

            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {

            @Override
            public void onChanged(@Nullable final List<Movie> movies) {
                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");

                if (selectedPosition != 2) {
                    return;
                }

                /* Load all favorite movies from the database */
                appDatabase.movieDao().loadAllFavoriteMovies();

                /* Get the AppExecutors and check if there are movies saved in the Favorites */
                AppExecutors.getExecutors().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int numberOfMovies = appDatabase.movieDao().getMovieCount();
                        /* In case the numberOfMovies is smaller than 1, inform the user that there
                         * are no movies in the Favorites list */

                        if (numberOfMovies == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    movieList = movies;
                                    movieRecyclerView.setVisibility(View.GONE);
                                    loadingIndicator.setVisibility(View.GONE);
                                    emptyTextView.setVisibility(View.VISIBLE);
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
                                    Log.i("TAG", " runOnUiThread " + (savedRecyclerViewState1 != null));
                                    populateMovies(movieList);
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