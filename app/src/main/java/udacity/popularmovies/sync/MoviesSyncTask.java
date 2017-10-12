package udacity.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.URL;

import udacity.popularmovies.data.HelperUtils;
import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.utilities.NetworkUtils;
import udacity.popularmovies.utilities.TheMovieDbJsonUtils;

public class MoviesSyncTask {

    private static final String LOG_TAG = MoviesSyncTask.class.getSimpleName();
    private static final int pagesToLoad = 5;

    synchronized public static void syncMovies(Context context, @NonNull String tableToSync) {
        Log.d(LOG_TAG, "***** syncMovies started");

        // force load 5 pages (100 movies) worth of data every time the user refreshes the app
        // (via either a 'pull down to refresh' or uninstall/install).  It was either this
        // or implement OnScrollListener().  I chose the brute force to allow for an instant cache
        // of data rather than a possible delay per 'End of list'
        for (int pageNumber = 1; pageNumber <= pagesToLoad; pageNumber++) {
            try {
                URL movieRequestUrl = NetworkUtils.buildUrlForMovies(tableToSync, pageNumber);
                Log.d(LOG_TAG, "***** " + movieRequestUrl.toString());


                String jsonMovieResponse = NetworkUtils.getResponseFromHttpsUrl(movieRequestUrl);
                Log.d(LOG_TAG, "***** " + jsonMovieResponse);

                ContentValues[] movieValues = TheMovieDbJsonUtils
                        .getMovieContentValuesFromJson(context, jsonMovieResponse);

                if (movieValues != null && movieValues.length != 0) {

                    ContentResolver movieContentResolver = context.getContentResolver();
                    if (pageNumber == 1) { //only delete the local data if this is the first page
                        movieContentResolver.delete(
                                HelperUtils.getUriFromTableName(tableToSync),
                                null,
                                null);
                        Log.d(LOG_TAG, "***** movieContentResolver.delete executed");
                    }
                    movieContentResolver.bulkInsert(
                            HelperUtils.getUriFromTableName(tableToSync),
                            movieValues);
                    Log.d(LOG_TAG, "***** movieContentResolver.bulkInsert executed");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized public static void syncExtras(Context context, @NonNull String tableToSync, @NonNull int movieId) {
        Log.d(LOG_TAG, "***** syncExtras started for " + tableToSync);

        try {
            URL extraRequestUrl = NetworkUtils.buildUrlForExtras(tableToSync, movieId);
            Log.d(LOG_TAG, "***** " + extraRequestUrl.toString());

            String jsonExtraResponse = NetworkUtils.getResponseFromHttpsUrl(extraRequestUrl);
            Log.d(LOG_TAG, "***** " + jsonExtraResponse);

            String columnMovieId = "";
            ContentValues[] extraValues;
            switch (tableToSync) {
                case MovieContract.TrailerEntry.TABLE_NAME_TRAILER:
                    columnMovieId = MovieContract.TrailerEntry.COLUMN_MOVIE_ID;
                    extraValues = TheMovieDbJsonUtils
                            .getTrailerContentValuesFromJson(context, jsonExtraResponse);
                    break;
                case MovieContract.ReviewEntry.TABLE_NAME_REVIEW:
                    columnMovieId = MovieContract.ReviewEntry.COLUMN_MOVIE_ID;
                    extraValues = TheMovieDbJsonUtils
                            .getReviewContentValuesFromJson(context, jsonExtraResponse);
                    break;
                default:
                    extraValues = null;
            }

            if (extraValues != null && extraValues.length != 0) {

                ContentResolver movieContentResolver = context.getContentResolver();
                movieContentResolver.delete(
                        HelperUtils.getUriFromTableName(tableToSync),
                        columnMovieId + " = ? ",
                        new String[]{String.valueOf(movieId)});
                Log.d(LOG_TAG, "***** movieContentResolver.delete executed for table " + tableToSync);

                movieContentResolver.bulkInsert(
                        HelperUtils.getUriFromTableName(tableToSync),
                        extraValues);
                Log.d(LOG_TAG, "***** movieContentResolver.bulkInsert executed for table " + tableToSync);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    synchronized public static void syncFavorite(Context context, @NonNull Uri movieUri, @NonNull boolean addToFavorites) {
        Log.d(LOG_TAG, "***** syncFavorite started");

        Uri favoriteUri = MovieContract.MovieEntry.CONTENT_URI_FAVORITE;
        ContentResolver movieContentResolver = context.getContentResolver();
        long _id;

        if (addToFavorites) {
            //add the movie to the favorites table
            Cursor cursor = movieContentResolver.query(
                    movieUri,
                    null,
                    null,
                    null,
                    null);
            ContentValues sourceValues = new ContentValues();
            cursor.moveToFirst();
            DatabaseUtils.cursorRowToContentValues(cursor, sourceValues);
            sourceValues.remove(MovieContract.MovieEntry._ID); //remove key or unique constraint amy be violated
            movieContentResolver.insert(favoriteUri, sourceValues);
        } else {
            //remove the movie record from the favorites table
            _id = ContentUris.parseId(movieUri);
            movieContentResolver.delete(
                    favoriteUri,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{String.valueOf(_id)});
        }
    }
}