package udacity.popularmovies.data;

import android.net.Uri;

import udacity.popularmovies.data.MovieContract;

/**
 * Created by Bryan K Mofley on October 07, 2017.
 */

public final class HelperUtils {
    public static Uri getUriFromTableName(String tableName) {
        switch (tableName) {
            case MovieContract.MovieEntry.TABLE_NAME_POPULAR:
                return MovieContract.MovieEntry.CONTENT_URI_POPULAR;
            case MovieContract.MovieEntry.TABLE_NAME_TOP_RATED:
                return MovieContract.MovieEntry.CONTENT_URI_TOP_RATED;
            case MovieContract.MovieEntry.TABLE_NAME_FAVORITE:
                return MovieContract.MovieEntry.CONTENT_URI_FAVORITE;
            case MovieContract.TrailerEntry.TABLE_NAME_TRAILER:
                return MovieContract.TrailerEntry.CONTENT_URI_TRAILER;
            case MovieContract.ReviewEntry.TABLE_NAME_REVIEW:
                return MovieContract.ReviewEntry.CONTENT_URI_REVIEW;
            default:
                throw new UnsupportedOperationException("Unknown table: " + tableName);
        }
    }
}
