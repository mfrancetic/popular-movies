package com.example.android.popularmovies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity (tableName = "movie")
public class MovieEntry {

    @PrimaryKey (autoGenerate = true)
    private int id;
    private String title;


    public MovieEntry(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
