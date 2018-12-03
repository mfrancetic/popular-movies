package com.example.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving movie data from The MovieDB.
 */
final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor QueryUtils.
     */
    private QueryUtils() {
    }

    /**
     * Query The MovieDB data set and return a list of Movie objects.
     */
    static List<Movie> fetchMovieData(String requestUrl) {

        /* Create an URL object */
        URL url = createUrl(requestUrl);

        /* Perform an HTTP request to the URL and receive a JSON response back */
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request", e);
        }
        /* Extract relevant fields from the JSON response and create a list of Movies */
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        /* Define the read time out, connect time out, success response code and request method */
        int READ_TIME_OUT = 10000;
        int CONNECT_TIME_OUT = 15000;
        int SUCCESS_RESPONSE_CODE = 200;

        String REQUEST_METHOD = "GET";
        String jsonResponse = "";

        /* If the URL is null, then return early. */
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            /* If the request was successful (response code 200),
             then read the input stream and parse the response. */
            if (urlConnection.getResponseCode() == SUCCESS_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                /* Closing the input stream could throw an IOException, which is why
                 the makeHttpRequest(URL url) method signature specifies than an IOException
                 could be thrown. */
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of Movie objects that has been built up from parsing a JSON response
     */
    private static List<Movie> extractFeatureFromJson(String movieJSON) {

        /* If the JSON string is empty or null, then return early. */
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        /* Create an empty ArrayList that we can start adding movies to */
        List<Movie> movies = new ArrayList<>();

        /* Try to parse the JSON response string. If there's a problem with the way the JSON
         is formatted, a JSONException exception object will be thrown.
         Catch the exception so the app doesn't crash, and print the error message to the logs. */
        try {
            /* Create a JSONObject from the JSON response string */
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            /* Extract the JSONArray with the key "results" **/
            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            /* For each movie in the movieArray, create a Movie object */
            for (int i = 0; i < movieArray.length(); i++) {

                /* Get a single movie at position i within the list of movies */
                JSONObject currentMovie = movieArray.getJSONObject(i);

                /* Extract the value for the required keys */
                String title = currentMovie.getString("title");
                String releaseDate = currentMovie.getString("release_date");
                String posterUrl = currentMovie.getString("poster_path");
                String userRating = currentMovie.getString("vote_average");
                String plotSynopsis = currentMovie.getString("overview");
                int id = currentMovie.getInt("id");

                /* Create a new Movie object with the title, releaseDate, posterUrl, userRating,
               plotSynopsis and ID from the JSON response. */
                Movie movie = new Movie(id, title, releaseDate, posterUrl, userRating, plotSynopsis);

                /* Add the new movie to the list of movies. */
                movies.add(movie);
            }
        } catch (JSONException e) {
            /* If an error is thrown when executing any of the above statements in the "try" block,
             catch the exception here, so the app doesn't crash. Print a log message
             with the message from the exception. */
            Log.e(TAG, "Problem parsing the movie JSON response results", e);
        }

        /* Return the list of movies. */
        return movies;
    }
}