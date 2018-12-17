package com.example.android.popularmovies;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favoriteMovies";
    private static AppDatabase databaseInstance;

    /* Gets the instance of the AppDatabase and builds the database using the
    * Room.databaseBuilder method */
     static AppDatabase getInstance(Context context) {
        if (databaseInstance== null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating a new database instance");
                databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return databaseInstance;
    }

    public abstract MovieDao movieDao();
}
