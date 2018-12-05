package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

class TrailerAdapter extends ArrayAdapter<Trailer> {

    private List<Trailer> trailersList;

    TrailerAdapter(Context context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        TextView trailerNameTextView;
        ImageButton trailerImageButton;

          /* Check if there is an existing list item view (convertView) that we can reuse.
         Otherwise if convertView is null, then inflate a new listItem layout. */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent,
                    false);
        }

        /* Find the trailer at the given position in the list of movies */
        Trailer currentTrailer = getItem(position);

        /* Get context and find the listItemView */
        Context context = getContext();
        trailerNameTextView = convertView.findViewById(R.id.trailer_text_view);
        trailerImageButton = convertView.findViewById(R.id.play_trailer_button);

//        trailerImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        return convertView;
    }

    public void setTrailers (List<Trailer> trailers) {
        trailersList = trailers;
        notifyDataSetChanged();
    }
}