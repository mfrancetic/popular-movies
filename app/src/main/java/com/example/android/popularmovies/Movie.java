package com.example.android.popularmovies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Movie object contains information related to a single movie
 */
@Entity(tableName = "movie")
public class Movie implements Parcelable {


    /** Database generated ID of the movie */
//    private int id;


    /**
     * ID of the movie
     */
    @PrimaryKey
    private final int movieId;

    /**
     * Title of the movie
     */
    private final String movieTitle;

    /**
     * Release date of the movie
     */
    private final String movieReleaseDate;

    /**
     * URL of the movie poster
     */
    private final String movieUrlPoster;

    /**
     * User rating of the movie
     */
    private final String movieUserRating;

    /**
     * Plot synopsis of the movie
     */
    private final String moviePlotSynopsis;


    /**
     * Constructs a new Movie object.
     *
     * @param movieId           is the ID of the movie
     * @param movieTitle        is the title of the movie
     * @param movieReleaseDate  is the release date of the movie
     * @param movieUrlPoster    is the url of the movie poster
     * @param movieUserRating   is the user rating of the movie
     * @param moviePlotSynopsis is the plot synopsis of the movie
     * @param
     */

    Movie(int movieId, String movieTitle, String movieReleaseDate, String movieUrlPoster, String movieUserRating, String moviePlotSynopsis) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieUrlPoster = movieUrlPoster;
        this.movieUserRating = movieUserRating;
        this.moviePlotSynopsis = moviePlotSynopsis;
    }

//    Movie(int id, int movieId, String movieTitle) {
//        this.id = id;
//        this.movieId = movieId;
//        this.movieTitle = movieTitle;
//    }

    private Movie(Parcel in) {
        movieId = in.readInt();
        movieTitle = in.readString();
        movieReleaseDate = in.readString();
        movieUrlPoster = in.readString();
        movieUserRating = in.readString();
        moviePlotSynopsis = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns the title of the movie
     */
    public String getMovieTitle() {
        return movieTitle;
    }

    /**
     * Returns the release date of the movie
     */
    String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    /**
     * Returns the url of the movie poster
     */
    String getMovieUrlPoster() {
        return movieUrlPoster;
    }

    /**
     * Returns the user rating of the movie
     */
    String getMovieUserRating() {
        return movieUserRating;
    }

    /**
     * Returns the plot synopsis of the movie
     */
    String getMoviePlotSynopsis() {
        return moviePlotSynopsis;
    }

//    /**
//     * Returns the databse ID of the movie
//     */
//    int getId() {
//        return id;
//    }

    /**
     * Returns the ID of the movie
     */
    int getMovieId() {
        return movieId;
    }


    /**
     * Writes the movie title, release date, url of the movie poster, user rating
     * and plot synopsis to the parcel
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieId);
        parcel.writeString(movieTitle);
        parcel.writeString(movieReleaseDate);
        parcel.writeString(movieUrlPoster);
        parcel.writeString(movieUserRating);
        parcel.writeString(moviePlotSynopsis);
    }

    /**
     * Creates and returns a new Movie object, as well as a new Movie Array
     */
    static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}