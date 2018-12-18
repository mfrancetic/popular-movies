package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    /**
     * Author of the movie review
     */
    private String reviewAuthor;

    /**
     * Text of the movie review
     */
    private String reviewText;

    /**
     * URL of the movie review
     */
    private String reviewUrl;

    private Review() {
    }

    Review (String reviewAuthor, String reviewText, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewText = reviewText;
        this.reviewUrl = reviewUrl;
    }

    private Review(Parcel in) {
        reviewAuthor = in.readString();
        reviewText = in.readString();
        reviewUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(reviewAuthor);
        parcel.writeString(reviewText);
        parcel.writeString(reviewUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns the author of the movie review
     */
    String getReviewAuthor() {
        return reviewAuthor;
    }

    /**
     * Returns the text of the movie review
     */
    String getReviewText() {
        return reviewText;
    }

    /**
     * Returns the URL of the movie review
     */
    String getReviewUrl() {
        return reviewUrl;
    }

    /**
     * Sets the author of the movie review
     */
    void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    /**
     * Sets the text of the movie review
     */
    void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    /**
     * Sets the URL of the movie review
     */
    void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }


    public static final Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
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
