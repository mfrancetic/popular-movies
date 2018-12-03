//package com.example.android.popularmovies;
//
//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.Ignore;
//import android.arch.persistence.room.PrimaryKey;
//
//@Entity (tableName = "movie")
//public class MovieEntry {
//
//    @PrimaryKey (autoGenerate = true)
//    private int id;
//    private int movieId;
//    private String title;

//    @Ignore
//    MovieEntry(int movieId, String title) {
//        this.movieId = movieId;
//        this.title = title;
//    }
//
//
//     MovieEntry(int id, int movieId, String title) {
//        this.id = id;
//        this.movieId = movieId;
//        this.title = title;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public int getMovieId() {
//        return movieId;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setMovieId(int movieId) {
//        this.movieId = movieId;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//}
