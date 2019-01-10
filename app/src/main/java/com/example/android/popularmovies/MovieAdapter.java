package com.example.android.popularmovies;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.List;

/**
 * A MovieAdapter creates a grid item layout for each movie in the data source
 * (a list of Movie objects).
 * These list item layouts will be provided to the RecyclerView to be displayed to the user.
 */
class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView moviePosterImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.movie_poster_image_view);
        }
    }

    /**
     * List of trailers
     */
    private  List<Movie> movies;

    /**
     * URL for the movie poster from The MovieDB
     */
    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";


    private static final String CURRENT_MOVIE = "currentMovie";

    /**
     * Size of the movie poster
     */
    private static final String posterSize = "w185";


  /*  public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }*/

  public void setMovieList(List<Movie> movies){
      this.movies = movies;
      notifyDataSetChanged();

  }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Movie movie = movies.get(position);
        ImageView moviePosterImageView = viewHolder.moviePosterImageView;

        final Context context = moviePosterImageView.getContext();

        /* Get the fullPosterPathUri from the formatPosterPath() method */
        assert movie != null;
        Uri fullPosterPathUri = formatPosterPath(movie);

        /* Get the width and height pixels and store them in integers width and height*/
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        /* Using the Picasso library load the fullPosterPathUri into the gridItemView,
         * resize and center it. */

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            com.squareup.picasso.Picasso
                    .get()
                    .load(fullPosterPathUri)
                    .centerInside().resize(width / 3, height)
                    .into(moviePosterImageView);
        } else {
            com.squareup.picasso.Picasso
                    .get()
                    .load(fullPosterPathUri)
                    .centerInside().resize(width / 2, height / 2)
                    .into(moviePosterImageView);
        }

           /* Set an item click listener on the GridView, which sends an intent to the DetailActivity
         to open the details of the selected movie. */
        moviePosterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(CURRENT_MOVIE, movie);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        } else {
            return movies.size();
        }
    }

    /**
     * Return the formatted poster path Uri
     */
    static Uri formatPosterPath(Movie selectedMovie) {
        String posterPath = selectedMovie.getMovieUrlPoster();
        String fullPosterPath = BASE_POSTER_URL + posterSize + posterPath;
        return Uri.parse(fullPosterPath);
    }
}