package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    // Extra for the movie ID to be received in the intent
//    public static final String EXTRA_MOVIE_ID = "extraMovieId";

    // Extra for the movie ID to be received after rotation
//    public static final String INSTANCE_MOVIE_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
//    private static final int DEFAULT_MOVIE_ID = -1;

//    private int movieId = DEFAULT_MOVIE_ID;

    AppDatabase database;

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    boolean isFavorite;

    ImageButton addToFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        addToFavoritesButton = findViewById(R.id.favorites_button);


        database = AppDatabase.getInstance(getApplicationContext());

//        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
//            movieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
//        }


        generateUI();
//        generateUI(movie);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
////        outState.putInt(movie, movieId);
//        super.onSaveInstanceState(outState);
//    }

    /**
     * Populates the UI with details of the selected movie
     */
//    private void generateUI(Movie movie) {
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
            final Movie currentMovie = intent.getParcelableExtra("currentMovie");

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


            // COMPLETED (10) Declare a AddTaskViewModelFactory using mDb and mTaskId
            AddMovieViewModelFactory factory = new AddMovieViewModelFactory(database, id);
            // COMPLETED (11) Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
            // for that use the factory created above AddTaskViewModel
            final AddMovieViewModel viewModel
                    = ViewModelProviders.of(this, factory).get(AddMovieViewModel.class);

            // COMPLETED (12) Observe the LiveData object in the ViewModel. Use it also when
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

            addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            final Movie movie = new Movie(id, title, releaseDate, posterPath, userRating, plotSynopsis);

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
