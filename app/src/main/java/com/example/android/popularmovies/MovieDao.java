package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY movieId")
    LiveData<List<Movie>> loadAllFavoriteMovies();

    @Insert
    void insertMovie(Movie movieEntry);

    @Delete
    void deleteMovie(Movie movieEntry);

    @Query("SELECT * FROM movie WHERE movieId = :movieId")
    LiveData<Movie> loadMovieById(int movieId);
}
