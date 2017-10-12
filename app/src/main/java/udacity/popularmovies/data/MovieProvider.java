package udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final int POPULAR = 100;
    private static final int POPULAR_MOVIE_ID = 101;
    private static final int TOP_RATED = 200;
    private static final int TOP_RATED_MOVIE_ID = 201;
    private static final int FAVORITE = 300;
    private static final int FAVORITE_MOVIE_ID = 301;

    private static final int TRAILER = 400;
    private static final int TRAILER_ID = 401;
    private static final int REVIEW = 500;
    private static final int REVIEW_MOVIE_ID = 501;


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    public static UriMatcher buildUriMatcher() {

        final String authority = MovieContract.CONTENT_AUTHORITY;

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MovieContract.PATH_POPULAR + "/#", POPULAR_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, TOP_RATED);
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED + "/#", TOP_RATED_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", FAVORITE_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("Not implemented in Popular Movies.");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "***** insert about to execute for " + uri.toString());

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case POPULAR:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_POPULAR, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildPopularUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case TOP_RATED:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_TOP_RATED, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildTopRatedUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case FAVORITE:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_FAVORITE, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildFavoriteUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case TRAILER:
                _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME_TRAILER, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case REVIEW:
                _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME_REVIEW, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "***** insert finished: returnUri = " + returnUri.toString());
        return returnUri;

    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        Log.d(LOG_TAG, "***** bulkInsert about to execute for " + uri.toString());

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        String tableName = "";
        int rowsInserted = 0;

        switch (sUriMatcher.match(uri)) {
            case POPULAR:
                tableName = MovieContract.MovieEntry.TABLE_NAME_POPULAR;
                break;
            case TOP_RATED:
                tableName = MovieContract.MovieEntry.TABLE_NAME_TOP_RATED;
                break;
            case FAVORITE:
                tableName = MovieContract.MovieEntry.TABLE_NAME_FAVORITE;
                break;
            case TRAILER:
                tableName = MovieContract.TrailerEntry.TABLE_NAME_TRAILER;
                break;
            case REVIEW:
                tableName = MovieContract.ReviewEntry.TABLE_NAME_REVIEW;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                // why is the for loop adding an extra row of null values?????
                if (value != null) {
                    long _id = db.insert(tableName, null, value);
                    if (_id != -1) {
                        rowsInserted++;
                    } else {
                        throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(LOG_TAG, "***** bulkInsert finished: inserted " + rowsInserted + " into " + tableName);
        return rowsInserted;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "***** query about to execute for " + uri.toString());

        final String popularTable = MovieContract.MovieEntry.TABLE_NAME_POPULAR;
        final String topRatedTable = MovieContract.MovieEntry.TABLE_NAME_TOP_RATED;
        final String favoriteTable = MovieContract.MovieEntry.TABLE_NAME_FAVORITE;

        final String movieIdCol = MovieContract.MovieEntry.COLUMN_MOVIE_ID;

        // we will use a one-to-many join to favorites when grabbing popular and top_rated tables.
        // this will allow for an easy flag to indicated whether or not the movie is also marked as a favorite
        final String tableJoinPopular = popularTable + " LEFT JOIN " + favoriteTable + " ON " + popularTable + "." + movieIdCol + " = " + favoriteTable + "." + movieIdCol + " ";
        final String tableJoinTopRated = topRatedTable + " LEFT JOIN " + favoriteTable + " ON " + topRatedTable + "." + movieIdCol + " = " + favoriteTable + "." + movieIdCol + " ";

        final String popularSelection = popularTable + "." + movieIdCol + " = ? ";
        final String topRatedSelection = topRatedTable + "." + movieIdCol + " = ? ";

        final String favoriteFlag = " (case when (" + favoriteTable + "." + movieIdCol + ") > 0 then 1 else 0 end) as favorite ";

        //initialize an string array - to be overwritten within one of the switch statements below
        String[] modifiedProjection = new String[0];

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor retCursor;
        long _id;
        switch (sUriMatcher.match(uri)) {
            case POPULAR:
                retCursor = db.query(
                        popularTable,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case POPULAR_MOVIE_ID:
                if (projection != null) {
                    //Projection is passed in by reference, not by value.
                    //As such, to prevent modifying this and subsequent projections, we make
                    //a copy, modify it by adding an additional element and use the copy this go around.
                    //This allows us to utilize a one-to-many join by adding the table names to all of the
                    //projected columns
                    modifiedProjection = new String[projection.length + 1];
                    for (int i = 0; i < projection.length; i++) {
                        modifiedProjection[i] = popularTable + "." + projection[i];
                    }
                    modifiedProjection[projection.length] = favoriteFlag;
                }
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        projection != null ? tableJoinPopular : popularTable,
                        projection != null ? modifiedProjection : projection,
                        projection != null ? popularSelection : movieIdCol + " = ? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case TOP_RATED:
                retCursor = db.query(
                        topRatedTable,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TOP_RATED_MOVIE_ID:
                if (projection != null) {
                    modifiedProjection = new String[projection.length + 1];
                    for (int i = 0; i < projection.length; i++) {
                        modifiedProjection[i] = topRatedTable + "." + projection[i];
                    }
                    modifiedProjection[projection.length] = favoriteFlag;
                }
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        projection != null ? tableJoinTopRated : topRatedTable,
                        projection != null ? modifiedProjection : projection,
                        projection != null ? topRatedSelection : movieIdCol + " = ? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE:
                retCursor = db.query(
                        favoriteTable,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_MOVIE_ID:
                if (projection != null) {
                    modifiedProjection = new String[projection.length + 1];
                    for (int i = 0; i < projection.length; i++) {
                        modifiedProjection[i] = favoriteTable + "." + projection[i];
                    }
                    modifiedProjection[projection.length] = favoriteFlag;
                }
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        favoriteTable,
                        projection != null ? modifiedProjection : projection,
                        movieIdCol + " = ? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILER:
                retCursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME_TRAILER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILER_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME_TRAILER,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW:
                retCursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME_REVIEW,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW_MOVIE_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME_REVIEW,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(LOG_TAG, "***** query returned " + retCursor.getCount() + " records for " + uri.toString());
        return retCursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "***** update about to execute for " + uri.toString());
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "***** delete about to execute for " + uri.toString());
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case POPULAR:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME_POPULAR,
                        selection,
                        selectionArgs
                );
                break;
            case TOP_RATED:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME_TOP_RATED,
                        selection,
                        selectionArgs
                );
                break;
            case FAVORITE:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME_FAVORITE,
                        selection,
                        selectionArgs
                );
                break;
            case TRAILER:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.TrailerEntry.TABLE_NAME_TRAILER,
                        selection,
                        selectionArgs
                );
                break;
            case REVIEW:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.ReviewEntry.TABLE_NAME_REVIEW,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(LOG_TAG, "***** delete finished: deleted " + numRowsDeleted + " from " + uri.toString());
        return numRowsDeleted;
    }
}
