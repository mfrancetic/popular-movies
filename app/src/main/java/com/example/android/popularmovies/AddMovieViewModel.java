package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Database;

public class AddMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public AddMovieViewModel(AppDatabase database, int movieId) {
        movie = database.movieDao().loadMovieById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
