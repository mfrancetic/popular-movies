package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Trailer;

import org.w3c.dom.Text;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

     class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * ImageButton for playing the trailer
         */
        ImageButton playTrailerButton;
        /**
         * TextView displaying the the label for the trailer
         */
        TextView trailerLabelTextView;

        /**
         * TextView for displaying the name of the trailer
         */
        TextView trailerNameTextView;

         ViewHolder(@NonNull View itemView) {
            super(itemView);

            playTrailerButton = (ImageButton) itemView.findViewById(R.id.play_trailer_button);
            trailerLabelTextView = (TextView) itemView.findViewById(R.id.trailer_label);
            trailerNameTextView = (TextView) itemView.findViewById(R.id.trailer_name);
        }
    }

    private List<Trailer> trailers;



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


    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }
}
