package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * URL for the movie data from The MovieDB database
     */
    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";

    /**
     * Generated value of the API key
     */
    public static final String apiKey = "cf57b652542b1bf6395086b6ae46c100";

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

    /**
     * Current scrolling position
     */
    private int scrollIndex;

    /**
     * Key of the scrollIndex
     */
    private static final String SCROLL_INDEX = "scrollIndex";

    /**
     * GridView
     */
    private GridView movieGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieGridView = findViewById(R.id.grid_view);

        /* Check if the savedInstanceState exists, and contains the key "movieList".
         * If so, get the values under their keys from the savedInstanceState,
         * if not, create a new ArrayList and initialize the loader. */
        if (savedInstanceState == null) {
            if (movieList == null) {
                movieList = new ArrayList<>();
                initializeLoader();
            }
        } else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
            scrollIndex = savedInstanceState.getInt(SCROLL_INDEX);
        }
        generateSpinner();
        generateGridView();
    }

    /**
     * Restores the saved values from the savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
        spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
        scrollIndex = savedInstanceState.getInt(SCROLL_INDEX);
        super.onRestoreInstanceState(savedInstanceState);
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
        } else {
            if (movieList == null) {
                movieList = new ArrayList<>();
                initializeLoader();
            }
        }
    }

    /**
     * Store the values under their keys to the savedInstanceState bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SPINNER_SELECTED_POSITION, selectedPosition);
        savedInstanceState.putParcelableArrayList(MOVIE_LIST, (ArrayList<? extends Parcelable>) movieList);
        scrollIndex = movieGridView.getFirstVisiblePosition();
        savedInstanceState.putInt(SCROLL_INDEX, scrollIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generate and populate the GridView
     */
    private void generateGridView() {
        /* Find a reference to the GridView in the layout, create a new adapter that takes
         * an empty list of movies an input and set the adapter on the GridView,
         * so the grid can be populated in the user interface. */
        emptyTextView = findViewById(R.id.empty_text_view);
        movieGridView.setEmptyView(emptyTextView);
        loadingIndicator = findViewById(R.id.loading_indicator);

        movieAdapter = new MovieAdapter(this, movieList);
        movieGridView.setAdapter(movieAdapter);
        movieGridView.setSelection(scrollIndex);

          /* Set an item click listener on the GridView, which sends an intent to the DetailActivity
         to open the details of the selected movie. */
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Find the movie that was clicked on, create a new intent to open the DetailActivity,
                 * add the details of the movie as an Extra and launch the activity */
                Movie currentMovie = movieAdapter.getItem(position);
                Context context = MainActivity.this;
                Class destinationClass = DetailActivity.class;
                Intent intent = new Intent(context, destinationClass);
                intent.putExtra(CURRENT_MOVIE, currentMovie);
                startActivity(intent);
            }
        });
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
                    movieGridView.setSelection(scrollIndex);
                    return;
                }
                if (selectedPosition == 0) {
                    selectedOption = getString(R.string.settings_sort_by_most_popular_value);

                    /* Clear the GridView as a new query will be kicked off */
                    movieAdapter.clear();

                    /* Hide the empty state text view as the loading indicator will be displayed */
                    emptyTextView.setVisibility(View.GONE);

                    /* Show the loading indicator while new date is being fetched */
                    loadingIndicator.setVisibility(View.VISIBLE);

                    /* Restart the loader to query again The MovieDB as the query settings have been updated */
                    getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                } else if (selectedPosition == 1) {
                    selectedOption = getString(R.string.settings_sort_by_top_rated_value);

                    /* Clear the GridView as a new query will be kicked off */
                    movieAdapter.clear();

                    /* Hide the empty state text view as the loading indicator will be displayed */
                    emptyTextView.setVisibility(View.GONE);

                    /* Show the loading indicator while new date is being fetched */
                    loadingIndicator.setVisibility(View.VISIBLE);

                    /* Restart the loader to query again The MovieDB as the query settings have been updated */
                    getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                } else if (selectedPosition == 2) {
                    /* if the selectedPosition is 2, call the method loadFavorites to load all the
                     * favorite movies from the local database */
                    loadFavorites();
                }
                MainActivity.spinnerSelectedPosition = selectedPosition;
                movieGridView.smoothScrollToPosition(scrollIndex);
                movieGridView.setSelection(scrollIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                appDatabase.movieDao().loadAllFavoriteMovies();

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
                                    movieAdapter.clear();
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
                                    /* Clear the GridView as a new query will be kicked off */
                                    movieAdapter.clear();

                                    /* Hide the empty state text view as the loading indicator will
                                    be displayed */
                                    emptyTextView.setVisibility(View.GONE);

                                    /* Show the loading indicator while new date is being fetched,
                                    notify the movieAdapter that the DataSet has been change, and
                                    after adding all the movies to the adapter, hide the loading
                                    indicator */
                                    loadingIndicator.setVisibility(View.VISIBLE);
                                    movieAdapter.notifyDataSetChanged();
                                    if (movies != null) {
                                        movieAdapter.addAll(movies);
                                    }
                                    loadingIndicator.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    /**
     * Initialize the loader
     */
    private void initializeLoader() {
        /* Get a reference to the ConnectivityManager to check state of network connectivity
         * and get details on the currently active default data network*/
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService
                (Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        /* If there is a network connection, fetch data */
        if (networkInfo != null && networkInfo.isConnected()) {

            /* Get a reference to the LoaderManager, in order to interact with loaders. */
            LoaderManager loaderManager = getLoaderManager();

            /* Initialize the loader. Pass in the int ID constant defined above and pass in null for
             the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
             because this activity implements the LoaderCallbacks interface). */
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            /* Otherwise, display error; hide loading indicator so the error message will be visible */
            loadingIndicator.setVisibility(View.GONE);

            /* Update empty state with no connection error message */
            emptyTextView.setText(R.string.no_internet_connection);
        }
    }

    /**
     * Create a new loader for the given URL
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle bundle) {

        Uri baseUri = Uri.parse(MOVIES_BASE_URL);

        /* buildUpon prepares the baseUri that we just parsed so we can add query parameters to it */
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* Append the encoded path with the selected sorting option and the API key as a
        query parameter */
        uriBuilder.appendEncodedPath(selectedOption);
        uriBuilder.appendQueryParameter(API_KEY, apiKey);

        /* Return the completed uri */
        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        if (spinnerSelectedPosition == 2) {

            movieGridView.setSelection(scrollIndex);
            return;
        } else if (movies.size()== 0 && movieList.size()!= 0) {
            return;
        } else {
            /* Hide loading indicator because the data has been loaded */
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            /* If there is a valid list of movies, then add them to the adapter's data set.
            This will trigger the GridView to update */
            if (!movies.isEmpty()) {
                movieAdapter.addAll(movies);
            } else {
                /* Set empty state text to display "No movies found." */
                emptyTextView.setText(R.string.no_movies_found);
                /* Clear the adapter of previous movie data */
                movieAdapter.clear();
            }
        }
        movieGridView.setSelection(scrollIndex);
    }

    /**
     * Loader reset, so we can clear out our existing data
     */
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        movieAdapter.clear();
    }
}