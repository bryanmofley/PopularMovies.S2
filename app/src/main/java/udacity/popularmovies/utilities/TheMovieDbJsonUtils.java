package udacity.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import udacity.popularmovies.data.MovieContract;

public class TheMovieDbJsonUtils {

    private static final String LOG_TAG = TheMovieDbJsonUtils.class.getSimpleName();

    // The Movie Database response codes
    private static final String TMDB_STATUS_CODE = "status_code";
    private static final String TMDB_STATUS_MESSAGE = "status_message";
    private static final int TMDB_INVALID_API = 7;
    private static final int TMDB_RESOURCE_NOT_FOUND = 34;

    private static final String TMDB_RESULTS = "results";

    // The Movie Database Results object fields
    private static final String TMDB_ADULT = "adult";
    private static final String TMDB_BACKDROP_PATH = "backdrop_path";
    private static final String TMDB_GENRE_IDS = "genre_ids";
    private static final String TMDB_ID = "id";
    private static final String TMDB_ORIGINAL_LANGUAGE = "original_language";
    private static final String TMDB_ORIGINAL_TITLE = "original_title";
    private static final String TMDB_OVERVIEW = "overview";
    private static final String TMDB_POPULARITY = "popularity";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_RELEASE_DATE = "release_date";
    private static final String TMDB_TITLE = "title";
    private static final String TMDB_VIDEO = "video";
    private static final String TMDB_VOTE_AVERAGE = "vote_average";
    private static final String TMDB_VOTE_COUNT = "vote_count";

    private static final String TMDB_TRAILER_ISO_639_1 = "iso_639_1";
    private static final String TMDB_TRAILER_ISO_3166_1 = "iso_3166_1";
    private static final String TMDB_TRAILER_KEY = "key";
    private static final String TMDB_TRAILER_NAME = "name";
    private static final String TMDB_TRAILER_SITE = "site";
    private static final String TMDB_TRAILER_SIZE = "size";
    private static final String TMDB_TRAILER_TYPE = "type";

    private static final String TMDB_REVIEW_AUTHOR = "author";
    private static final String TMDB_REVIEW_CONTENT = "content";
    private static final String TMDB_REVIEW_URL = "url";

    public static ContentValues[] getMovieContentValuesFromJson(Context context, String jsonMovieResponse)
            throws JSONException {
     /*
      The Movie DB API Documentation:
        https://developers.themoviedb.org/3/movies/get-popular-movies
        https://developers.themoviedb.org/3/movies/get-top-rated-movies
     */

        JSONObject movieJson = new JSONObject(jsonMovieResponse);

        if (movieJson.has(TMDB_STATUS_CODE)) {
            int errorCode = movieJson.getInt(TMDB_STATUS_CODE);

            switch (errorCode) {
                case TMDB_INVALID_API:
                case TMDB_RESOURCE_NOT_FOUND:
                    Log.e(LOG_TAG, "***** api.themoviedb.org: " + movieJson.getString(TMDB_STATUS_MESSAGE));
                    return null;
                default:
                    Log.e(LOG_TAG, "***** api.themoviedb.org: Unknown error");
                    return null;
            }
        }

        JSONArray movieDataArray = movieJson.getJSONArray(TMDB_RESULTS);

        ContentValues[] movieContentValues = new ContentValues[movieDataArray.length()];

        for (int i = 0; i < movieDataArray.length(); i++) {
            JSONObject movie = movieDataArray.getJSONObject(i);

            /**
             * The genre data is one-to-many so we pull it out here and prep it for inclusion
             * as a comma delimited string
             */
            String genreData = "";
            JSONArray genreDataArray = movie.optJSONArray(TMDB_GENRE_IDS);
            for (int j = 0; j < genreDataArray.length(); j++) {
                genreData += genreDataArray.getInt(j) + ",";
            }
            if (genreData != null && genreData.length() > 0 && genreData.charAt(genreData.length() - 1) == ',') {
                genreData = genreData.substring(0, genreData.length() - 1);
            }

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getInt(TMDB_ID));
            movieValues.put(MovieContract.MovieEntry.COLUMN_IS_ADULT, movie.getBoolean(TMDB_ADULT));
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, NetworkUtils.TMDB_BACKDROP_URL + movie.getString(TMDB_BACKDROP_PATH));
            movieValues.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, genreData);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getString(TMDB_ORIGINAL_LANGUAGE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getString(TMDB_ORIGINAL_TITLE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getString(TMDB_OVERVIEW));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getDouble(TMDB_POPULARITY));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, NetworkUtils.TMDB_POSTER_URL + movie.getString(TMDB_POSTER_PATH));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getString(TMDB_RELEASE_DATE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getString(TMDB_TITLE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_HAS_VIDEO, movie.getBoolean(TMDB_VIDEO));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getDouble(TMDB_VOTE_AVERAGE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getInt(TMDB_VOTE_COUNT));

            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }

    public static ContentValues[] getTrailerContentValuesFromJson(Context context, String jsonMovieResponse)
            throws JSONException {

        /*
        * The Movie DB API Documentation:
        * https://developers.themoviedb.org/3/movies/get-movie-videos
        */

        JSONObject movieJson = new JSONObject(jsonMovieResponse);

        if (movieJson.has(TMDB_STATUS_CODE)) {
            int errorCode = movieJson.getInt(TMDB_STATUS_CODE);

            switch (errorCode) {
                case TMDB_INVALID_API:
                case TMDB_RESOURCE_NOT_FOUND:
                    Log.e(LOG_TAG, "***** api.themoviedb.org: " + movieJson.getString(TMDB_STATUS_MESSAGE));
                    return null;
                default:
                    Log.e(LOG_TAG, "***** api.themoviedb.org: Unknown error");
                    return null;
            }
        }

        JSONArray trailerDataArray = movieJson.getJSONArray(TMDB_RESULTS);

        ContentValues[] trailerContentValues = new ContentValues[trailerDataArray.length() + 1];

        for (int i = 0; i < trailerDataArray.length(); i++) {
            JSONObject trailer = trailerDataArray.getJSONObject(i);

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieJson.getInt(TMDB_ID));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getString(TMDB_ID));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_ISO_639_1, trailer.getString(TMDB_TRAILER_ISO_639_1));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_ISO_3166_1, trailer.getString(TMDB_TRAILER_ISO_3166_1));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, trailer.getString(TMDB_TRAILER_KEY));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getString(TMDB_TRAILER_NAME));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE, trailer.getString(TMDB_TRAILER_SITE));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, trailer.getInt(TMDB_TRAILER_SIZE));
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, trailer.getString(TMDB_TRAILER_TYPE));

            trailerContentValues[i] = trailerValues;
        }

        return trailerContentValues;
    }

    public static ContentValues[] getReviewContentValuesFromJson(Context context, String jsonMovieResponse)
            throws JSONException {

        /*
        * The Movie DB API Documentation:
        * https://developers.themoviedb.org/3/movies/get-movie-reviews
        */

        JSONObject movieJson = new JSONObject(jsonMovieResponse);

        if (movieJson.has(TMDB_STATUS_CODE)) {
            int errorCode = movieJson.getInt(TMDB_STATUS_CODE);

            switch (errorCode) {
                case TMDB_INVALID_API:
                case TMDB_RESOURCE_NOT_FOUND:
                    Log.e(LOG_TAG, "***** api.themoviedb.org: " + movieJson.getString(TMDB_STATUS_MESSAGE));
                    return null;
                default:
                    Log.e(LOG_TAG, "***** api.themoviedb.org: Unknown error");
                    return null;
            }
        }

        JSONArray reviewDataArray = movieJson.getJSONArray(TMDB_RESULTS);

        ContentValues[] reviewContentValues = new ContentValues[reviewDataArray.length() + 1];

        for (int i = 0; i < reviewDataArray.length(); i++) {
            JSONObject review = reviewDataArray.getJSONObject(i);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieJson.getInt(TMDB_ID));
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getString(TMDB_ID));
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getString(TMDB_REVIEW_AUTHOR));
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getString(TMDB_REVIEW_CONTENT));
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, review.getString(TMDB_REVIEW_URL));

            reviewContentValues[i] = reviewValues;
        }

        return reviewContentValues;
    }
}
