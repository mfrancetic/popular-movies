package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    // Extra for the movie ID to be received in the intent
    public static final String EXTRA_MOVIE_ID = "extraMovieId";

    // Extra for the movie ID to be received after rotation
    public static final String INSTANCE_MOVIE_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_MOVIE_ID = -1;

    private int movieId = DEFAULT_MOVIE_ID;

    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        database = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            movieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }


        generateUI();
//        generateUI(movie);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_MOVIE_ID, movieId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Populates the UI with details of the selected movie
     */
//    private void generateUI(MovieEntry movie) {
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
            /* If the intent exists, get the currentMovie object from the parcelableExtra */
            Movie currentMovie = intent.getParcelableExtra("currentMovie");

            /* Get the title of the current movie and set it to the titleTextView */
            final String title = currentMovie.getTitle();
            titleTextView.setText(title);

            /* Get the release date of the current movie and set it to the releaseDateTextView */
            String releaseDate = currentMovie.getReleaseDate();
            releaseDateTextView.setText(releaseDate);

            /* Get the user rating of the current movie and set it to the userRatingTextView */
            String userRating = currentMovie.getUserRating();
            userRatingTextView.setText(userRating);

            /* Get the plot synopsis of the current movie and set it to the plotSynopsisTextView */
            String plotSynopsis = currentMovie.getPlotSynopsis();
            plotSynopsisTextView.setText(plotSynopsis);


            final int id = currentMovie.getId();


            /* Get the full poster path Uri of the current movie and using the Picasso library
              load it into the moviePosterImageView */
            Uri fullPosterPathUri = MovieAdapter.formatPosterPath(currentMovie);
            com.squareup.picasso.Picasso
                    .with(context)
                    .load(fullPosterPathUri)
                    .into(moviePosterImageView);

            final ImageButton addToFavoritesButton = findViewById(R.id.favorites_button);
            addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MovieEntry movie = new MovieEntry(id, title);
                    AppExecutors.getExecutors().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            database.movieDao().insertMovie(movie);
                            Toast.makeText(context, R.string.toast_added_to_favorites, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                }

            );
        }
    }

//
//    private void onFavoritesButtonClicked() {
//
//
//        final MovieEntry movie = new MovieEntry()
//    }
}