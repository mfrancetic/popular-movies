package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * A MovieAdapter creates a list item layout for each movie in the data source
 * (a list of Movie objects).
 * These list item layouts will be provided to the GridView to be displayed to the user.
 */
class MovieAdapter extends ArrayAdapter<Movie> {

    /**
     * URL for the movie poster from The MovieDB
     */
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";

    /**
     * Size of the movie poster
     */
    private static final String posterSize = "w185";

    /**
     * Current movie object
     */
    private static Movie currentMovie;

    /**
     * Constructs a new MovieAdapter.
     *
     * @param context is the context of the app.
     * @param movies  is the list of all movies, which is the date source of the adapter.
     */
    MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ImageView gridItemImageView;

        /* Check if there is an existing grid item view (convertView) that we can reuse.
         Otherwise if convertView is null, then inflate a new gridItem layout. */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent,
                    false);
        }

        /* Find the movie at the given position in the list of movies */
        currentMovie = getItem(position);

        /* Get context and find the gridItemImageView */
        Context context = getContext();
        gridItemImageView = convertView.findViewById(R.id.movie_poster_image_view);

        /* Get the fullPosterPathUri from the formatPosterPath() method */
        Uri fullPosterPathUri = formatPosterPath();

        /* Get the width and height pixels and store them in integers width and height*/
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        /* Using the Picasso library load the fullPosterPathUri into the gridItemView,
         * resize and center it. */
        com.squareup.picasso.Picasso
                .with(context)
                .load(fullPosterPathUri)
                .centerInside().resize(width / 2, height / 2)
                .into(gridItemImageView);

        /* Return the gridItemView that is now showing the appropriate data */
        return convertView;
    }

    /**
     * Return the formatted poster path Uri
     */
    static Uri formatPosterPath() {
        String posterPath = currentMovie.getUrlPoster();
        String fullPosterPath = BASE_POSTER_URL + posterSize + posterPath;
        return Uri.parse(fullPosterPath);
    }
}