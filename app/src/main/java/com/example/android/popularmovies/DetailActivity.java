package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
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


    String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";


    Movie currentMovie;

    // Extra for the movie ID to be received in the intent
//    public static final String EXTRA_MOVIE_ID = "extraMovieId";

    // Extra for the movie ID to be received after rotation
//    public static final String INSTANCE_MOVIE_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
//    private static final int DEFAULT_MOVIE_ID = -1;

//    private int movieId = DEFAULT_MOVIE_ID;

    AppDatabase database;

//    List<Movie> movies;

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    boolean isFavorite;

    ImageButton playTrailerButton;

    ImageButton addToFavoritesButton;

    TextView reviewLabelTextView;

    Button fullReviewButton;

    TextView reviewAuthorTextView;

    TextView reviewTextView;

    TextView trailerTextView;

    TextView trailerLabelTextView;

    AddMovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        addToFavoritesButton = findViewById(R.id.favorites_button);
        playTrailerButton = findViewById(R.id.play_trailer_button);
        database = AppDatabase.getInstance(getApplicationContext());
        fullReviewButton = findViewById(R.id.full_review_button);
        reviewAuthorTextView = findViewById(R.id.review_author_text_view);
        reviewTextView = findViewById(R.id.review_text_view);
        trailerTextView = findViewById(R.id.trailer_text_view);
        trailerLabelTextView = findViewById(R.id.trailer_label);
        reviewLabelTextView = findViewById(R.id.review_label);

//        viewModel = ViewModelProviders.of(this).get(AddMovieViewModel.class);


        generateUI();
    }

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

                    currentMovie.setReviewAuthor(reviewAuthor);
                    currentMovie.setReviewText(reviewText);
                    currentMovie.setReviewUrl(reviewUrl);

                    movies.add(0, currentMovie);

                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            return reviewUrl;
        }

        @Override
        protected void onPostExecute(String reviewUrl) {

            String reviewAuthor = currentMovie.getReviewAuthor();
            String reviewText = currentMovie.getReviewText();

            if (reviewUrl == null || reviewAuthor == null || reviewText == null) {
                reviewAuthorTextView.setVisibility(View.GONE);
                reviewTextView.setVisibility(View.GONE);
                fullReviewButton.setVisibility(View.GONE);
                reviewLabelTextView.setVisibility(View.GONE);
            } else {
                reviewAuthorTextView.setText(reviewAuthor);
                reviewTextView.setText(reviewText);
                reviewUrl = currentMovie.getReviewUrl();
                final Uri reviewUri = Uri.parse(reviewUrl);
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

                /* Extract the JSONArray with the key "results" **/
                JSONArray movieTrailerArray = baseJsonResponse.getJSONArray("results");

                if (movieTrailerArray.length() == 0) {
                    trailerUrlPath = null;
                }
                /* Create a Movie object for the first movie in the movieArray, */

                /* Get a single movie at position i within the list of movies */
                JSONObject movieObject = movieTrailerArray.getJSONObject(0);

                /* Extract the value for the required keys */

                trailerUrlPath = movieObject.getString("key");

                currentMovie.setTrailerUrlPath(trailerUrlPath);

                movies.add(0, currentMovie);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON response results", e);
            }
            return trailerUrlPath;
        }

        @Override
        protected void onPostExecute(String trailerUrlPath) {
            if (trailerUrlPath == null) {
                playTrailerButton.setVisibility(View.GONE);
                trailerTextView.setVisibility(View.GONE);
                trailerLabelTextView.setVisibility(View.GONE);

            } else {
                trailerUrlPath = currentMovie.getTrailerUrlPath();
                final Uri trailerUri = Uri.parse(TRAILER_BASE_URL + trailerUrlPath);
                playTrailerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* COMPLETED ACTION_VIEW */
                        Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
                        playTrailerIntent.setData(trailerUri);
                        getApplicationContext().startActivity(playTrailerIntent);
                    }
                });

            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt(currentMovie);
        outState.putParcelable();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
        if (intent != null) {

//            if

            /* If the intent exists, get the currentMovie object from the parcelableExtra */
            currentMovie = intent.getParcelableExtra("currentMovie");

            /* Get the id of the current movie */
            final int id = currentMovie.getMovieId();

            LiveData<Movie> movieInDatabase = database.movieDao().loadMovieById(id);

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


            // Declared a AddTaskViewModelFactory using mDb and mTaskId
            AddMovieViewModelFactory factory = new AddMovieViewModelFactory(database, id);
            // Declared a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
            // for that use the factory created above AddTaskViewModel
            viewModel
                    = ViewModelProviders.of(this, factory).get(AddMovieViewModel.class);

            // Observe the LiveData object in the ViewModel. Use it also when
            // removing the observer
            viewModel.getMovie().observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movieInDatabase) {
                    viewModel.getMovie().removeObserver(this);
                    if (movieInDatabase != null) {
                        isFavorite = true;
                        addToFavoritesButton.setImageResource(R.drawable.ic_star_rate);
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

            new TrailerAsyncTask().execute(String.valueOf(id), QueryUtils.TRAILER_QUERY);

            new ReviewAsyncTask().execute(String.valueOf(id), String.valueOf(QueryUtils.REVIEW_QUERY));


            addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final Movie movie = new Movie(id, title, releaseDate, posterPath, userRating, plotSynopsis, null, null, null, null);

                                                            AppExecutors.getExecutors().diskIO().execute(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (!isFavorite) {
                                                                        database.movieDao().insertMovie(movie);
                                                                        isFavorite = true;

                                                                    } else {
                                                                        database.movieDao().deleteMovie(movie);
                                                                        isFavorite = false;
                                                                    }
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (isFavorite) {
                                                                                addToFavoritesButton.setImageResource(R.drawable.ic_star_rate);
                                                                                Toast.makeText(context, getString(R.string.toast_added_to_favorites), Toast.LENGTH_SHORT).show();
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
