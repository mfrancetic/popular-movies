package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private String trailerName;
    private String trailerUrl;

    Trailer(String trailerName, String trailerUrl) {
        this.trailerName = trailerName;
        this.trailerUrl = trailerUrl;
    }

    private Trailer(Parcel in) {
        trailerName = in.readString();
        trailerUrl = in.readString();
    }

    String getTrailerName() {
        return trailerName;
    }

    String getTrailerUrl() {
        return trailerUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trailerName);
        parcel.writeString(trailerUrl);
    }

    Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
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
