package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class ReviewAdapter extends ArrayAdapter<Review> {

    private List<Review> reviewList;

    ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        TextView reviewAuthorTextView;
        TextView reviewTextView;

          /* Check if there is an existing list item view (convertView) that we can reuse.
         Otherwise if convertView is null, then inflate a new listItem layout. */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent,
                    false);
        }

        /* Find the review at the given position in the list of movies */
        Review currentReview = getItem(position);

        /* Get context and find the listItemView */
        Context context = getContext();
        reviewAuthorTextView = convertView.findViewById(R.id.review_author_text_view);
        reviewTextView = convertView.findViewById(R.id.review_text_view);





        return convertView;
    }

    public void setReviews(List<Review> reviews) {
        reviewList = reviews;
        notifyDataSetChanged();
    }
}

