package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
    public static final String apiKey = Secret.api_key;

    /**
     * Key of the movie list state
     */
    private static final String LIST_STATE = "LIST_STATE";

    /**
     * Adapter for the grid of movies
     */
    private MovieAdapter movieAdapter;

    /**
     * TextView that is displayed when the recyclerView is empty
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
    private static int selectedPosition;

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
    private List<Movie> movieList;

    /**
     * Key of the movie list
     */
    private final String MOVIE_LIST = "movieList";

    /**
     * Saved recyclerViewState
     */
    private Parcelable savedRecyclerViewState;

    /**
     * Movie RecyclerView
     */
    private RecyclerView movieRecyclerView;

    /**
     * Number of columns in the RecyclerView
     */
    private int spanCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieList = new ArrayList<>();

        emptyTextView = findViewById(R.id.empty_text_view);
        movieRecyclerView = findViewById(R.id.movies_recycler_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        NestedScrollView scrollView = findViewById(R.id.main_scroll_view);
        movieRecyclerView.setFocusable(false);
        scrollView.requestFocus();

        /* Create a new MovieAdapter */
        movieAdapter = new MovieAdapter();
        movieAdapter.setMovieList(movieList);

        /* The number of columns in the recyclerView */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ||
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 2;
        }

        /* Set a new LinearLayoutManager to the movieRecyclerView */
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, spanCount);
        movieRecyclerView.setLayoutManager(mLayoutManager);

        /* Set the adapters to the RecyclerViews */
        movieRecyclerView.setAdapter(movieAdapter);

        /* Get instance of the AppDatabase using the app context */
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        if (selectedOption == null) {
            selectedOption = getString(R.string.settings_sort_by_most_popular_value);
        }

        /* Check if the savedInstanceState exists.
         * If so, get the values under their keys from the savedInstanceState,
         * if not, generate the spinner and if the movieList is null, execute the MovieAsyncTask. */
        generateRecyclerView();
        if (savedInstanceState == null) {
            generateSpinner(0);
            if (movieList == null) {
                new MovieAsyncTask().execute(selectedOption);
            }
        } else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
            generateSpinner(spinnerSelectedPosition);
            savedRecyclerViewState = savedInstanceState.getParcelable(LIST_STATE);
            if (movieRecyclerView.getLayoutManager() != null) {
                movieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
            }
        }
    }

    /**
     * In OnResume the selected spinner position is checked - and the loadFavorites (for favorite movies)
     * method is called
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
        if (movieRecyclerView.getLayoutManager() != null) {
            savedRecyclerViewState = movieRecyclerView.getLayoutManager().onSaveInstanceState();
        }
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        outState.putParcelable(LIST_STATE, savedRecyclerViewState);
        outState.putInt(SPINNER_SELECTED_POSITION, selectedPosition);
        outState.putParcelableArrayList(MOVIE_LIST, (ArrayList<? extends Parcelable>) movieList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /* If there is a savedInstanceState, get the movieList, spinnerSelectedPosition and
        savedRecyclerViewState*/
        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            spinnerSelectedPosition = savedInstanceState.getInt(SPINNER_SELECTED_POSITION);
            savedRecyclerViewState = savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    /**
     * Restores the position of the savedRecyclerViewState
     */
    private void restorePosition() {
        if (savedRecyclerViewState != null && movieRecyclerView.getLayoutManager() != null) {
            movieRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
        }
    }

    /**
     * Populate the movies in the recyclerView
     */
    private void populateMovies(List<Movie> movies) {
        this.movieList = movies;
        movieAdapter.setMovieList(movieList);
        restorePosition();
        movieRecyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    /**
     * Generate and populate the RecyclerView
     */
    private void generateRecyclerView() {
        /* If the movieList is null, execute the MovieAsyncTask. Otherwise set the text to the
         * emptyTextView and hide the loading indicator */
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
     * Generate the Spinner, which enables sorting between most popular, top rated and favorite movies
     */
    private void generateSpinner(int selected) {
        Spinner spinner = findViewById(R.id.spinner);

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

                /* If there are movies in the movieList and the spinnerSelectedPosition is not changed,
                 * hide the loading indicator and return */
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
     * MovieAsyncTask class that creates the URL for loading the movies, makes the HTTP request and
     * parses the JSON String in order to create a new Movie object.
     * Returns a list of movies.
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

                /* Extract the JSONArray with the key "results" */
                JSONArray movieArray = baseJsonResponse.getJSONArray("results");

                if (movieArray.length() == 0) {
                    return movieList1;
                }

                /* For each movie in the movieArray, create a Movie object */
                for (int i = 0; i < movieArray.length(); i++) {

                    /* Get a single movie at position i within the list of movies */
                    JSONObject movieObject = movieArray.getJSONObject(i);

                    /* Extract the value for the required keys */
                    String title = movieObject.getString("title");
                    String releaseDate = movieObject.getString("release_date");
                    String posterUrl = movieObject.getString("poster_path");
                    String userRating = movieObject.getString("vote_average");
                    String plotSynopsis = movieObject.getString("overview");
                    int id = movieObject.getInt("id");

                    /* Create a new Movie object with the id, title, releaseDate, posterUrl, userRating
                    and plotSynopsis from the JSON response. */
                    Movie movie = new Movie(id, title, releaseDate, posterUrl, userRating, plotSynopsis);

                    movie.setMovieId(id);
                    movie.setMovieTitle(title);
                    movie.setMovieReleaseDate(releaseDate);
                    movie.setMovieUrlPoster(posterUrl);
                    movie.setMovieUserRating(userRating);
                    movie.setMoviePlotSynopsis(plotSynopsis);

                    /* Add the movie to the movieList */
                    movieList1.add(i, movie);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            /* Return a list of movies */
            return movieList1;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            /* If there are no movies, hide the movieRecyclerView and loading indicator and inform
            the user there are no movies found */
            if (movies.size() == 0) {
                movieRecyclerView.setVisibility(View.GONE);
                emptyTextView.setText(getString(R.string.no_movies_found));
                loadingIndicator.setVisibility(View.GONE);
            } else {
                populateMovies(movies);
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
                        /* In case the numberOfMovies is 0, inform the user that there
                         * are no movies in the Favorites list */
                        if (numberOfMovies == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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