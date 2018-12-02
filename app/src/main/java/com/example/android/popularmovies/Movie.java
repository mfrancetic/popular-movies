package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Movie object contains information related to a single movie
 */
public class Movie implements Parcelable {

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
     * ID of the movie
     */
    private final int movieId;


    /**
     * Constructs a new Movie object.
     *
     * @param title        is the title of the movie
     * @param releaseDate  is the release date of the movie
     * @param urlPoster    is the url of the movie poster
     * @param userRating   is the user rating of the movie
     * @param plotSynopsis is the plot synopsis of the movie
     * @param
     */
    Movie(String title, String releaseDate, String urlPoster, String userRating, String plotSynopsis, int id) {
        this.movieTitle = title;
        this.movieReleaseDate = releaseDate;
        this.movieUrlPoster = urlPoster;
        this.movieUserRating = userRating;
        this.moviePlotSynopsis = plotSynopsis;
        this.movieId = id;
    }

    private Movie(Parcel in) {
        movieTitle = in.readString();
        movieReleaseDate = in.readString();
        movieUrlPoster = in.readString();
        movieUserRating = in.readString();
        moviePlotSynopsis = in.readString();
        movieId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns the title of the movie
     */
    public String getTitle() {
        return movieTitle;
    }

    /**
     * Returns the release date of the movie
     */
    String getReleaseDate() {
        return movieReleaseDate;
    }

    /**
     * Returns the url of the movie poster
     */
    String getUrlPoster() {
        return movieUrlPoster;
    }

    /**
     * Returns the user rating of the movie
     */
     String getUserRating() {
        return movieUserRating;
    }

    /**
     * Returns the plot synopsis of the movie
     */
     String getPlotSynopsis() {
        return moviePlotSynopsis;
    }

    /**
     * Returns the ID of the movie
     */
    int getId() {
        return movieId;
    }


    /**
     * Writes the movie title, release date, url of the movie poster, user rating
     * and plot synopsis to the parcel
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieTitle);
        parcel.writeString(movieReleaseDate);
        parcel.writeString(movieUrlPoster);
        parcel.writeString(movieUserRating);
        parcel.writeString(moviePlotSynopsis);
        parcel.writeInt(movieId);
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