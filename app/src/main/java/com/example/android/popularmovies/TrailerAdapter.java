package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

class TrailerAdapter extends ArrayAdapter<Movie> {

    /* TODO izbrisat klasu TrailerAdapter i ReveiwAdapter? (to je za ListView)  */

    private List<Movie> movies;

    ImageButton trailerImageButton;

    String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=\"";

    Uri trailerUri;


    TrailerAdapter(Context context, List<Movie> movies) {

        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


          /* Check if there is an existing list item view (convertView) that we can reuse.
         Otherwise if convertView is null, then inflate a new listItem layout. */
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent,
//                    false);
//        }

        /* Find the trailer at the given position in the list of movies */
        Movie currentMovie = getItem(position);

        /* Get context and find the listItemView */
        final Context context = getContext();
        trailerImageButton = convertView.findViewById(R.id.play_trailer_button);

        String trailerUrlPath = currentMovie.getTrailerUrlPath();
          trailerUri = Uri.parse(TRAILER_BASE_URL + trailerUrlPath);




        playMovieTrailer(trailerUri);

        return convertView;
    }

    void playMovieTrailer(final Uri trailerUri) {
        trailerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* COMPLETED ACTION_VIEW */
                Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
                playTrailerIntent.setData(trailerUri);
                getContext().startActivity(playTrailerIntent);
            }
        });
    }

//    public void setTrailers (List<Trailer> trailers) {
//        trailersList = trailers;
//        notifyDataSetChanged();
//    }
}