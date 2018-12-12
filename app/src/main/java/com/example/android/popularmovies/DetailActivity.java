package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    /**
     * The base URL for watching the trailers
     */
    String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";

    /**
     * CurrentMovie Movie object
     */
    Movie currentMovie;

    /**
     * database AppDatabase object
     */
    AppDatabase database;

    /**
     * Boolean used for checking if the movie is added to the Favorites list
     */
    boolean isFavorite;

    /**
     * ImageButton for playing the trailer
     */
    ImageButton playTrailerButton;

    /**
     * ImageButton for adding the movie to the Favorites list
     */
    ImageButton addToFavoritesButton;

    /**
     * TextView displaying the label for the review
     */
    TextView reviewLabelTextView;

    /**
     * Button for viewing the complete review
     */
    Button fullReviewButton;

    /**
     * TextView displaying the the author of the review
     */
    TextView reviewAuthorTextView;

    /**
     * TextView displaying the the text of the review
     */
    TextView reviewTextView;

    /**
     * TextView displaying the the name of the trailer
     */
    TextView trailerTextView;

    /**
     * TextView displaying the the label for the trailer
     */
    TextView trailerLabelTextView;

    /**
     * viewModel AddMovieViewModel object
     */
    AddMovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /* Find references to all the UI components */
        addToFavoritesButton = findViewById(R.id.favorites_button);
        playTrailerButton = findViewById(R.id.play_trailer_button);
        fullReviewButton = findViewById(R.id.full_review_button);
        reviewAuthorTextView = findViewById(R.id.review_author_text_view);
        reviewTextView = findViewById(R.id.review_text_view);
        trailerTextView = findViewById(R.id.trailer_text_view);
        trailerLabelTextView = findViewById(R.id.trailer_label);
        reviewLabelTextView = findViewById(R.id.review_label);

        /* Get instance of the AppDatabase using the app context */
        database = AppDatabase.getInstance(getApplicationContext());

        /* Check if the savedInstanceState exists, and contains the key "currentMovie".
         * If so, get the parcelable under that key value from the savedInstanceState,
         * if not, get the parcelable from the intent. */
        if (savedInstanceState == null || !savedInstanceState.containsKey("currentMovie")) {
            currentMovie = getIntent().getParcelableExtra("currentMovie");
        } else {
            currentMovie = savedInstanceState.getParcelable("currentMovie");
        }
        generateUI();
    }

    /**
     * Store the currentMovie object under the key "currentMovie" to the savedInstanceState
     * bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("currentMovie", currentMovie);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Restore the currentMovie object under the key "currentMovie" from the savedInstanceState
     * bundle
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.getParcelable("currentMovie");
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * ReviewAsyncTask class that uses the movie ID to create the reviewUrl String of that
     * movie, makes the HTTP request and parses the JSON String in order to set the review author,
     * text and URL values to the currentMovie object.
     */
    private class ReviewAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            List<Movie> movies = new ArrayList<>();

            String reviewUrl = null;
            int id = currentMovie.getMovieId();

            try {
                URL url = QueryUtils.createReviewTrailerUrl(String.valueOf(id), QueryUtils.REVIEW_QUERY);
                String reviewJson = QueryUtils.makeHttpRequest(url);

                /* Create a JSONObject from the JSON response string */
                JSONObject baseJsonResponse = new JSONObject(reviewJson);

                /* Extract the JSONArray with the key "results" **/
                JSONArray reviewArray = baseJsonResponse.getJSONArray("results");

                /* For each movie in the movieArray, create a Movie object */
                for (int i = 0; i < reviewArray.length(); i++) {

                    /* Get a single movie at position i within the list of movies */
                    JSONObject movieObject = reviewArray.getJSONObject(i);

                    /* Extract the value for the required keys */
                    String reviewAuthor = movieObject.getString("author");
                    String reviewText = movieObject.getString("content");
                    reviewUrl = movieObject.getString("url");

                    /* Set the values of the author, text and url of the review to the
                     * currentMovie object */
                    currentMovie.setReviewAuthor(reviewAuthor);
                    currentMovie.setReviewText(reviewText);
                    currentMovie.setReviewUrl(reviewUrl);

                    /* Add the currentMovie values to the movies list */
                    movies.add(0, currentMovie);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            return reviewUrl;
        }

        /**
         * Use the review author, text and URL values of the currentMovie to populate the UI
         */
        @Override
        protected void onPostExecute(String reviewUrl) {

            String reviewAuthor = currentMovie.getReviewAuthor();
            String reviewText = currentMovie.getReviewText();

            /* If there are no review values, set the visibility of their TextViews to GONE*/
            if (reviewUrl == null || reviewAuthor == null || reviewText == null) {
                reviewAuthorTextView.setVisibility(View.GONE);
                reviewTextView.setVisibility(View.GONE);
                fullReviewButton.setVisibility(View.GONE);
                reviewLabelTextView.setVisibility(View.GONE);
                /* If there are review values, populate the UI with them */
            } else {
                reviewAuthorTextView.setText(reviewAuthor);
                reviewTextView.setText(reviewText);
                reviewUrl = currentMovie.getReviewUrl();
                final Uri reviewUri = Uri.parse(reviewUrl);
                /* Set the onClickListener to the fullReviewButton.
                 * Using the OnClick method, create and start the intent that opens the full
                 * review using the review URl */
                fullReviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openFullReviewIntent = new Intent(Intent.ACTION_VIEW);
                        openFullReviewIntent.setData(reviewUri);
                        getApplicationContext().startActivity(openFullReviewIntent);
                    }
                });
            }
        }
    }

    /**
     * TrailerAsyncTask class that uses the movie ID to create the trailerUrlPath String of that
     * movie, makes the HTTP request and parses the JSON String in order to set the trailer
     * URL value to the currentMovie object.
     */
    private class TrailerAsyncTask extends AsyncTask<String, Void, String> {

        String trailerUrlPath = null;

        @Override
        protected String doInBackground(String... strings) {
            List<Movie> movies = new ArrayList<>();

            int id = currentMovie.getMovieId();
            try {
                URL url = QueryUtils.createReviewTrailerUrl(String.valueOf(id), QueryUtils.TRAILER_QUERY);
                String movieString = QueryUtils.makeHttpRequest(url);

                /* Create a JSONObject from the JSON response string */
                JSONObject baseJsonResponse = new JSONObject(movieString);

                /* Extract the JSONArray with the key "results" */
                JSONArray movieTrailerArray = baseJsonResponse.getJSONArray("results");

                if (movieTrailerArray.length() == 0) {
                    trailerUrlPath = null;
                }
                /* Create a Movie object for the first movie in the movieArray, */
                JSONObject movieObject = movieTrailerArray.getJSONObject(0);

                /* Extract the value for the required keys */
                trailerUrlPath = movieObject.getString("key");

                /* Set the trailer URL path value to the currentMovie object */
                currentMovie.setTrailerUrlPath(trailerUrlPath);

                /* Add the currentMovie values to the movies list */
                movies.add(0, currentMovie);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            return trailerUrlPath;
        }

        /**
         * Use the trailer URL value of the currentMovie to populate the UI
         */
        @Override
        protected void onPostExecute(String trailerUrlPath) {
            /* If there is no trailer URL path, set the visibility of the corresponding views to
             * GONE */
            if (trailerUrlPath == null) {
                playTrailerButton.setVisibility(View.GONE);
                trailerTextView.setVisibility(View.GONE);
                trailerLabelTextView.setVisibility(View.GONE);
                /* If there are trailer URL path values, populate the UI with it */
            } else {
                trailerUrlPath = currentMovie.getTrailerUrlPath();
                final Uri trailerUri = Uri.parse(TRAILER_BASE_URL + trailerUrlPath);
                /* Set the onClickListener to the playTrailerButton.
                 * Using the OnClick method, create and start the intent that opens the
                 * trailer in the web browser using the trailer URl */
                playTrailerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
                        playTrailerIntent.setData(trailerUri);
                        getApplicationContext().startActivity(playTrailerIntent);
                    }
                });
            }
        }
    }

    /**
     * Populates the UI with details of the selected movie
     */
    private void generateUI() {

        /* Get BaseContext and store it a Context variable */
        final Context context = getBaseContext();

        /* Find the TextViews of the title, plot_synopsis, user_rating and release_date,
         * as well as the ImageView of the movie_poster_thumbnail. */
        TextView titleTextView = findViewById(R.id.title_text_view);
        ImageView moviePosterImageView = findViewById(R.id.movie_poster_thumbnail_image_view);
        TextView plotSynopsisTextView = findViewById(R.id.plot_synopsis_text_view);
        TextView userRatingTextView = findViewById(R.id.user_rating_text_view);
        TextView releaseDateTextView = findViewById(R.id.release_date_text_view);

        /* Get the Intent and check if it is null. */
        Intent intent = getIntent();
        /* If the intent exists, get the currentMovie object from the parcelableExtra */
        if (intent != null) {

            /* Get the id of the current movie */
            final int id = currentMovie.getMovieId();

            /* Load the movie by the id of the current movie from the database */
            database.movieDao().loadMovieById(id);

            /* Get the title of the current movie and set it to the titleTextView */
            final String title = currentMovie.getMovieTitle();
            titleTextView.setText(title);

            /* Get the release date of the current movie and set it to the releaseDateTextView */
            final String releaseDate = currentMovie.getMovieReleaseDate();
            releaseDateTextView.setText(releaseDate);

            /* Get the user rating of the current movie and set it to the userRatingTextView */
            final String userRating = currentMovie.getMovieUserRating();
            userRatingTextView.setText(userRating);

            /* Get the plot synopsis of the current movie and set it to the plotSynopsisTextView */
            final String plotSynopsis = currentMovie.getMoviePlotSynopsis();
            plotSynopsisTextView.setText(plotSynopsis);

            /* Declare the AddTaskViewModelFactory using the database and id parameters */
            AddMovieViewModelFactory factory = new AddMovieViewModelFactory(database, id);
            /* Initialize the viewModel variable by calling ViewModelProviders.of
            /* for that use the factory created */
            viewModel = ViewModelProviders.of(this, factory).get(AddMovieViewModel.class);

            /* Observe the LiveData object in the ViewModel. Use it also when
             * removing the observer */
            viewModel.getMovie().observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movieInDatabase) {
                    viewModel.getMovie().removeObserver(this);
                    /* If the movieInDatabase isn't null, set the isFavorite boolean to true,
                     * and set the image resource of the button the ic_star_rate */
                    if (movieInDatabase != null) {
                        isFavorite = true;
                        addToFavoritesButton.setImageResource(R.drawable.ic_star_rate);
                        /* If the movieInDatabase is null, set the isFavorite boolean to false,
                         * and set the image resource of the button the ic_star_empty */
                    } else {
                        isFavorite = false;
                        addToFavoritesButton.setImageResource(R.drawable.ic_star_empty);
                    }
                }
            });

            /* Get the full poster path Uri of the current movie and using the Picasso library
              load it into the moviePosterImageView */
            final String posterPath = currentMovie.getMovieUrlPoster();

            Uri fullPosterPathUri = MovieAdapter.formatPosterPath(currentMovie);
            com.squareup.picasso.Picasso
                    .with(context)
                    .load(fullPosterPathUri)
                    .into(moviePosterImageView);

            /* Execute the TrailerAsyncTask and ReviewAsyncTask, using the trailer and review query
             * String */
            new TrailerAsyncTask().execute(String.valueOf(id), QueryUtils.TRAILER_QUERY);
            new ReviewAsyncTask().execute(String.valueOf(id), String.valueOf(QueryUtils.REVIEW_QUERY));

            /* Set the onClickListener to tha addToFavorites button */
            addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                                                        /* Using the onClick method, create a new movie with the values provided and check
                                                         * if the movie is added to the Favorites list or not.  */
                                                        @Override
                                                        public void onClick(View v) {
                                                            final Movie movie = new Movie(id, title, releaseDate, posterPath, userRating, plotSynopsis, null, null, null, null);
                                                            AppExecutors.getExecutors().diskIO().execute(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    /* If the movie is not the Favorites list, insert the movie and change the isFavorite boolean value to true.*/
                                                                    if (!isFavorite) {
                                                                        database.movieDao().insertMovie(movie);
                                                                        isFavorite = true;
                                                                        /* If the movie is in the Favorites list, delete the movie and change the isFavorite boolean value to false.*/
                                                                    } else {
                                                                        database.movieDao().deleteMovie(movie);
                                                                        isFavorite = false;
                                                                    }
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            /* If the movie is not the Favorites list, change the image resource and create a Toast to inform the user
                                                                            that the movie has been added to the Favorites list.*/
                                                                            if (isFavorite) {
                                                                                addToFavoritesButton.setImageResource(R.drawable.ic_star_rate);
                                                                                Toast.makeText(context, getString(R.string.toast_added_to_favorites), Toast.LENGTH_SHORT).show();
                                                                                                   /* If the movie is in the Favorites list, change the image resource and create a Toast to inform the user
                                                                            that the movie has been deleted from the Favorites list.*/
                                                                            } else {
                                                                                addToFavoritesButton.setImageResource(R.drawable.ic_star_empty);
                                                                                Toast.makeText(getApplicationContext(), getString(R.string.toast_deleted_from_favorites), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
            );
        }
    }
}
