package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailer> trailers;

    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * ImageButton for playing the trailer
         */
        ImageButton playTrailerButton;

        /**
         * TextView for displaying the name of the trailer
         */
        TextView trailerNameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            playTrailerButton = itemView.findViewById(R.id.play_trailer_button);
            trailerNameTextView = itemView.findViewById(R.id.trailer_name);
        }
    }


    public TrailerAdapter(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View trailerView = inflater.inflate(R.layout.trailer_list_item, parent, false);

        return new ViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.ViewHolder viewHolder, int position) {

        Trailer trailer = trailers.get(position);
        TextView trailerNameTextView = viewHolder.trailerNameTextView;
        trailerNameTextView.setText(trailer.getTrailerName());

        ImageButton playTrailerButton = viewHolder.playTrailerButton;

        final Context context = playTrailerButton.getContext();

        String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";

        String trailerUrlPath = trailer.getTrailerUrlPath();
        final Uri trailerUri = Uri.parse(TRAILER_BASE_URL + trailerUrlPath);

        /* Set an onClickListener to the playTrailerButton.*/
        playTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* When the user clicks on the button create and start an intent to
                 play the trailer */
                Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
                playTrailerIntent.setData(trailerUri);
                context.startActivity(playTrailerIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        } else {
            return trailers.size();
        }
    }
}
