package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private final String reviewAuthor;

    private final String reviewText;

    private final String reviewUrl;

    Review(String reviewAuthor, String reviewText, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewText = reviewText;
        this.reviewUrl = reviewUrl;
    }

    private Review(Parcel in) {
        reviewAuthor = in.readString();
        reviewText = in.readString();
        reviewUrl = in.readString();
    }

    String getReviewAuthor() {
        return reviewAuthor;
    }

    String getReviewText() {
        return reviewText;
    }

    String getReviewUrl() {
        return reviewUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(reviewAuthor);
        parcel.writeString(reviewText);
        parcel.writeString(reviewUrl);
    }

    static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };

}
