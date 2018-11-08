package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        implements LoaderManager.LoaderCallbacks<List<Movie>>


//        AdapterView.OnItemSelectedListener
//        SharedPreferences.OnSharedPreferenceChangeListener
//
{

    /**
     * URL for the movie data from The MovieDB database
     */
    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";

    /**
     * Generated value of the API key
     */
    private static final String apiKey = "cf57b652542b1bf6395086b6ae46c100";

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


    String selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Find a reference to the GridView in the layout, create a new adapter that takes
         * an empty list of movies an input and set the adapter on the GridView,
         * so the grid can be populated in the user interface. */
        GridView movieGridView = findViewById(R.id.grid_view);

        emptyTextView = findViewById(R.id.empty_text_view);
        movieGridView.setEmptyView(emptyTextView);

        loadingIndicator = findViewById(R.id.loading_indicator);

        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        movieGridView.setAdapter(movieAdapter);


        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, R.layout.support_simple_spinner_dropdown_item);

        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedPosition = parent.getSelectedItemPosition();
                if (selectedPosition == R.id.popular) {
                    selectedOption = getString(R.string.settings_sort_by_most_popular_value);
                } else {
                    selectedOption = getString(R.string.settings_sort_by_top_rated_value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        spinner.setOnItemClickListener();


        /* Obtain a reference to the SharedPreferences file for this app
         * and register OnSharedPreferenceChangeListener */
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        preferences.registerOnSharedPreferenceChangeListener(this);

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
                intent.putExtra("currentMovie", currentMovie);
                startActivity(intent);
            }
        });

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

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(getString(R.string.settings_sort_by_key))) {
//            /* Clear the GridView as a new query will be kicked off */
//            movieAdapter.clear();
//
//            /* Hide the empty state text view as the loading indicator will be displayed */
//            emptyTextView.setVisibility(View.GONE);
//
//            /* Show the loading indicator while new date is being fetched */
//            loadingIndicator.setVisibility(View.VISIBLE);
//
//            /* Restart the loader to query again The MovieDB as the query settings have been updated */
//            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
//        }
//    }

    /**
     * Create a new loader for the given URL
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle bundle) {

        /* API key parameter that will be appended to the URL */
        String API_PARAM = "api_key";

//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(this);
//
//        /* GetString retrieves a String value from the sort-by preferences. The second parameter is
//        the default value for the preference. */
//        String sortBy = sharedPreferences.getString(getString(R.string.settings_sort_by_key),
//                getString(R.string.settings_sort_by_default));

        /* Uri.parse breaks apart the URI string that's passed into its parameter */
        Uri baseUri = Uri.parse(MOVIES_BASE_URL);

        /* buildUpon prepares the baseUri that we just parsed so we can add query parameters to it */
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* Append query parameter and its value. */
//        uriBuilder.appendQueryParameter(getString(R.string.settings_sort_by_key), sortBy);
        String sortBy = "";

        uriBuilder.appendEncodedPath(getString(R.string.settings_sort_by_most_popular_value));
        uriBuilder.appendQueryParameter(API_PARAM, apiKey);

        /* Return the completed uri */
        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        /* Hide loading indicator because the data has been loaded */
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        /* Set empty state text to display "No movies found." */
        emptyTextView.setText(R.string.no_movies_found);

        /* Clear the adapter of previous movie data */
        movieAdapter.clear();

        /* If there is a valid list of movies, then add them to the adapter's data set.
         This will trigger the GridView to update */
        if (movies != null && !movies.isEmpty()) {
            movieAdapter.addAll(movies);
        }
    }

    /**
     * Loader reset, so we can clear out our existing data
     */
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        movieAdapter.clear();
    }


    //    /**
//     * Initialize the contents of the Activity's options menu.
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        /* Inflate the Options Menu as specified in the XML */
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    /**
//     * Create a new intent to open the SettingsActivity
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.action_settings) {
//            Intent settingsIntent = new Intent(this, SettingsActivity.class);
//            startActivity(settingsIntent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}