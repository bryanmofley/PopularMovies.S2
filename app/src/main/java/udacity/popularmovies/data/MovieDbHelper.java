package udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "***** onCreate executed");
        addTable(sqLiteDatabase, MovieContract.MovieEntry.TABLE_NAME_POPULAR);
        addTable(sqLiteDatabase, MovieContract.MovieEntry.TABLE_NAME_TOP_RATED);
        addTable(sqLiteDatabase, MovieContract.MovieEntry.TABLE_NAME_FAVORITE);
        addTrailerTable(sqLiteDatabase);
        addReviewTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "***** onUpgrade executed");
        String dropTableSql = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(dropTableSql + MovieContract.MovieEntry.TABLE_NAME_POPULAR);
        sqLiteDatabase.execSQL(dropTableSql + MovieContract.MovieEntry.TABLE_NAME_TOP_RATED);
        sqLiteDatabase.execSQL(dropTableSql + MovieContract.MovieEntry.TABLE_NAME_FAVORITE);
        sqLiteDatabase.execSQL(dropTableSql + MovieContract.TrailerEntry.TABLE_NAME_TRAILER);
        sqLiteDatabase.execSQL(dropTableSql + MovieContract.ReviewEntry.TABLE_NAME_REVIEW);
        onCreate(sqLiteDatabase);
    }

    private void addTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + tableName + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_IS_ADULT + " BOOLEAN NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_GENRE_IDS + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL, " +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                        MovieContract.MovieEntry.COLUMN_HAS_VIDEO + " BOOLEAN NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                        MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        Log.d(LOG_TAG, "***** Table " + tableName + " created in " + DATABASE_NAME);
    }

    private void addTrailerTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME_TRAILER + " (" +
                        MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                        MovieContract.TrailerEntry.COLUMN_ISO_639_1 + " TEXT, " +
                        MovieContract.TrailerEntry.COLUMN_ISO_3166_1 + " TEXT, " +
                        MovieContract.TrailerEntry.COLUMN_KEY + " TEXT, " +
                        MovieContract.TrailerEntry.COLUMN_NAME + " TEXT, " +
                        MovieContract.TrailerEntry.COLUMN_SITE + " TEXT, " +
                        MovieContract.TrailerEntry.COLUMN_SIZE + " INT, " +
                        MovieContract.TrailerEntry.COLUMN_TYPE + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        Log.d(LOG_TAG, "***** Table " + MovieContract.TrailerEntry.TABLE_NAME_TRAILER + " created in " + DATABASE_NAME);
    }

    private void addReviewTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME_REVIEW + " (" +
                        MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                        MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                        MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT, " +
                        MovieContract.ReviewEntry.COLUMN_URL + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        Log.d(LOG_TAG, "***** Table " + MovieContract.ReviewEntry.TABLE_NAME_REVIEW + " created in " + DATABASE_NAME);
    }
}
