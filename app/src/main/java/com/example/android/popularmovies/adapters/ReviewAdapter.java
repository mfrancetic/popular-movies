package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
         * TextView displaying the the text of the review
         */
        private final TextView reviewTextView;

        /**
         * TextView displaying the the author of the review
         */
        final TextView reviewAuthorTextView;

        /**
         * Button for viewing the complete review
         */
        final Button fullReviewButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTextView = itemView.findViewById(R.id.review_text_view);
            reviewAuthorTextView = itemView.findViewById(R.id.review_author_text_view);
            fullReviewButton = itemView.findViewById(R.id.full_review_button);
        }
    }

    /**
     * List of reviews
     */
    private final List<Review> reviews;

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
        TextView reviewTextView = viewHolder.reviewTextView;
        TextView reviewAuthorTextView = viewHolder.reviewAuthorTextView;
        Button fullReviewButton = viewHolder.fullReviewButton;

        final Context context = fullReviewButton.getContext();

        reviewTextView.setText(review.getReviewText());
        reviewAuthorTextView.setText(review.getReviewAuthor());

        String reviewUrl = review.getReviewUrl();
        final Uri reviewUri = Uri.parse(reviewUrl);

        /* Set an onClickListener to the fullReviewButton.*/
        fullReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
             /* When the user clicks on the fullReviewButton, a new intent is created and started to
                 open the full review */
            public void onClick(View v) {
                Intent openFullReviewIntent = new Intent(Intent.ACTION_VIEW);
                openFullReviewIntent.setData(reviewUri);
                context.startActivity(openFullReviewIntent);
            }
        });
    }

    /**
     * Returns the number of items in the review list
     */
    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        } else {
            return reviews.size();
        }
    }
}