package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    /**
     * Trailer URL path of the movie
     */
    private String trailerUrlPath;

    /**
     * Trailer name
     */
    private String trailerName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(trailerUrlPath);
        parcel.writeString(trailerName);
    }

    private Trailer() {
    }

    Trailer(String trailerUrlPath, String trailerName) {
        this.trailerUrlPath = trailerUrlPath;
        this.trailerName = trailerName;
    }

    private Trailer(Parcel in) {
        trailerUrlPath = in.readString();
        trailerName = in.readString();
    }

    /**
     * Returns the trailer URL path of the movie
     */
    public String getTrailerUrlPath() {
        return trailerUrlPath;
    }

    /**
     * Returns the trailer name of the movie
     */
    public String getTrailerName() {
        return trailerName;
    }

    /* Sets the URL of the movie trailer
     */
    void setTrailerUrlPath(String trailerUrlPath) {
        this.trailerUrlPath = trailerUrlPath;
    }

    /* Sets the name of the movie trailer
     */
    void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }


    static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {

        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };
}
