package com.example.android.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class AddMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase movieDatabase;
    private final int movieId;

    /* Initializes the member variables database and id in the constructor
     * with the parameters received */
    AddMovieViewModelFactory(AppDatabase database, int id) {
        movieDatabase = database;
        movieId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMovieViewModel(movieDatabase, movieId);
    }
}