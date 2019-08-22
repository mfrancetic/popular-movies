package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

 public class AddMovieViewModel extends ViewModel {

    private final LiveData<Movie> movie;

    /* Constructor which initializes the Movie variable and receives the database
     * and the movieId */
   public AddMovieViewModel(AppDatabase database, int movieId) {
        movie = database.movieDao().loadMovieById(movieId);
    }

    /* Getter for the movie variable, returns a LiveData<Movie> object */
    public LiveData<Movie> getMovie() {
        return movie;
    }
}