package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Database;

public class AddMovieViewModel extends ViewModel {

    private LiveData<MovieEntry> movie;

    public AddMovieViewModel(AppDatabase database, int movieId) {
        movie = database.movieDao().loadMovieById(movieId);
    }

    public LiveData<MovieEntry> getMovie() {
        return movie;
    }
}
