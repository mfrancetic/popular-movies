package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * TextView displaying the label for the review
         */
        TextView reviewLabelTextView;


        /**
         * TextView displaying the the text of the review
         */
        private TextView reviewTextView;

        /**
         * TextView displaying the the author of the review
         */
        TextView reviewAuthorTextView;

        /**
         * Button for viewing the complete review
         */
        Button fullReviewButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewLabelTextView = itemView.findViewById(R.id.review_label);
            reviewTextView = itemView.findViewById(R.id.review_text_view);
            reviewAuthorTextView = itemView.findViewById(R.id.review_author_text_view);
            fullReviewButton = itemView.findViewById(R.id.full_review_button);
        }
    }

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reviewView = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder viewHolder, int position) {
        Review review = reviews.get(position);
        TextView reviewLabelTextView = viewHolder.reviewLabelTextView;
        TextView reviewTextView = viewHolder.reviewTextView;
        TextView reviewAuthorTextView = viewHolder.reviewAuthorTextView;
        Button fullReviewButton = viewHolder.fullReviewButton;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
