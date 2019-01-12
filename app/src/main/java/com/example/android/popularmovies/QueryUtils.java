package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Helper methods related to requesting and receiving movie data from The MovieDB.
 */
final class QueryUtils {

    /**
     * URL for the review from The MovieDB
     */
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    /**
     * URL for the trailers from The MovieDB
     */
    static final String TRAILER_QUERY = "videos";

    /**
     * URL for the reviews from The MovieDB
     */
    static final String REVIEW_QUERY = "reviews";
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
     * Create a URL for the reviews and trailers
     */
    static URL createReviewTrailerUrl(String movieId, String query) {
        /* API key parameter that will be appended to the URL */
        String API_PARAM = "api_key";

        URL url = null;

        Uri baseUri = Uri.parse(BASE_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(movieId)
                .appendEncodedPath(query)
                .appendQueryParameter(API_PARAM, MainActivity.apiKey)
                .build();
        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Create a URL for the most popular and top rated movies
     */
    static URL createMovieUrl(String selectedOption) {

        /* API key parameter that will be appended to the URL */
        String API_PARAM = "api_key";

        URL url = null;

        Uri baseUri = Uri.parse(BASE_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(selectedOption)
                .appendQueryParameter(API_PARAM, MainActivity.apiKey)
                .build();
        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    static String makeHttpRequest(URL url) throws IOException {

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
            reader.close();
        }
        return output.toString();
    }
}