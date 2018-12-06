package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

class ReviewAdapter extends ArrayAdapter<Movie> {

    /* COMPLETED Change to ArrayAdapter<Movie> */
    private List<Movie> movies;

    ReviewAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        TextView reviewAuthorTextView;
        TextView reviewTextView;
        Button fullReviewButton;

          /* Check if there is an existing list item view (convertView) that we can reuse.
         Otherwise if convertView is null, then inflate a new listItem layout. */
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent,
//                    false);
//        }

        /* Find the review at the given position in the list of movies */
        final Movie currentMovie = getItem(position);

        /* Get context and find the listItemView */
        final Context context = getContext();
        reviewAuthorTextView = convertView.findViewById(R.id.review_author_text_view);
        reviewTextView = convertView.findViewById(R.id.review_text_view);
        fullReviewButton = convertView.findViewById(R.id.full_review_button);

        /* COMPLETED add binding views . author.settext - value of movies na positionu
        * + onClickListener --> getReviewUrl and ACTION_VIEW  */
        String reviewAuthor = currentMovie.getReviewAuthor();
        reviewAuthorTextView.setText(reviewAuthor);

        String reviewText = currentMovie.getReviewText();
        reviewTextView.setText(reviewText);

        final String reviewUrlString = currentMovie.getReviewUrl();
        final Uri reviewUri = Uri.parse(reviewUrlString);

        fullReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openFullReviewIntent = new Intent(Intent.ACTION_VIEW);
                openFullReviewIntent.setData(reviewUri);
                context.startActivity(openFullReviewIntent);
            }
        });


        return convertView;
    }

//    public void setReviews(List<Review> reviews) {
//        movies = reviews;
//        notifyDataSetChanged();
//    }
}

