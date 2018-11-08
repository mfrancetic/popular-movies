package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        populateUI();
    }

    /**
     * Populates the UI with details of the selected movie
     */
    private void populateUI() {
        /* Get BaseContext and store it a Context variable */
        Context context = getBaseContext();

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
            String title = currentMovie.getTitle();
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

            /* Get the full poster path Uri of the current movie and using the Picasso library
              load it into the moviePosterImageView */
            Uri fullPosterPathUri = MovieAdapter.formatPosterPath();
            com.squareup.picasso.Picasso
                    .with(context)
                    .load(fullPosterPathUri)
                    .into(moviePosterImageView);
        }
    }
}