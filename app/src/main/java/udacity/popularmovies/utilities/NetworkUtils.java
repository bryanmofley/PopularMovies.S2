package udacity.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import udacity.popularmovies.BuildConfig;
import udacity.popularmovies.data.MovieContract;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * These utilities will be used to communicate with themoviedb.org servers
 */

public final class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /*
     * The following constants are specific to The MovieDB.  Note that
     * TMDB_API_KEY gets its value from gradle.properties
     */
    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String TMDB_API_KEY_PARAM = "api_key";

    private static final String TMDB_MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TMDB_URL_POPULAR = "popular";
    private static final String TMDB_URL_TOP_RATED = "top_rated";
    private static final String TMDB_URL_TRAILERS = "videos";
    private static final String TMDB_URL_REVIEWS = "reviews";

    private static final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";
    // poster sizes: w92, w154, w185, w342, w500, w780, original
    private static final String TMDB_POSTER_SIZE = "w342";
    public static final String TMDB_POSTER_URL = TMDB_POSTER_BASE_URL + TMDB_POSTER_SIZE;
    private static final String TMDB_BACKDROP_SIZE = "w780";
    public static final String TMDB_BACKDROP_URL = TMDB_POSTER_BASE_URL + TMDB_BACKDROP_SIZE;

    private static final String TMDB_PAGE = "page";

    /*
     The Movie DB API Documentation:
        https://developers.themoviedb.org/3/movies/get-popular-movies
        https://developers.themoviedb.org/3/movies/get-top-rated-movies
        https://developers.themoviedb.org/3/movies/get-movie-videos
        https://developers.themoviedb.org/3/movies/get-movie-reviews

     */

    public static URL buildUrlForMovies(@NonNull String tableToSync, int pageNumber) {
        String sortBy;
        switch (tableToSync) {
            case MovieContract.MovieEntry.TABLE_NAME_POPULAR:
                sortBy = TMDB_URL_POPULAR;
                break;
            case MovieContract.MovieEntry.TABLE_NAME_TOP_RATED:
                sortBy = TMDB_URL_TOP_RATED;
                break;
            default:
                throw new UnsupportedOperationException("Unknown table: " + tableToSync);
        }

        Uri builtUri = Uri.parse(TMDB_MOVIE_BASE_URL + sortBy).buildUpon()
                .appendQueryParameter(TMDB_API_KEY_PARAM, TMDB_API_KEY)
                .appendQueryParameter(TMDB_PAGE, String.valueOf(pageNumber))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlForExtras(@NonNull String extraTable, @NonNull int movieId) {
        String extraUrl;
        switch (extraTable) {
            case MovieContract.TrailerEntry.TABLE_NAME_TRAILER:
                extraUrl = String.valueOf(movieId) + "/" + TMDB_URL_TRAILERS;
                break;
            case MovieContract.ReviewEntry.TABLE_NAME_REVIEW:
                extraUrl = String.valueOf(movieId) + "/" + TMDB_URL_REVIEWS;
                break;
            default:
                return null;
        }
        Uri builtUri = Uri.parse(TMDB_MOVIE_BASE_URL + extraUrl).buildUpon()
                .appendQueryParameter(TMDB_API_KEY_PARAM, TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpsUrl(URL url) throws IOException {

        int responseCode = 0;
        InputStream in = null;

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            responseCode = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "***** api.themoviedb.org HTTP Response Code: " + Integer.toString(responseCode));
            if (responseCode == 200) {
                in = urlConnection.getInputStream();
            } else {
                //likely a 401 or 404 error... go ahead and grab the contents to create an informed
                //error message later
                in = urlConnection.getErrorStream();
            }
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
