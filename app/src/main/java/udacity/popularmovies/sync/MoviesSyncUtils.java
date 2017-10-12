package udacity.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import udacity.popularmovies.data.HelperUtils;
import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.data.MovieContract.MovieEntry;

public class MoviesSyncUtils {

    private static final String LOG_TAG = MoviesSyncUtils.class.getSimpleName();

    private static String sInitialized;

    synchronized public static void initialize(@NonNull final Context context, @NonNull final String tableToSync) {

        Log.d(LOG_TAG, "***** initialized called for table " + tableToSync);

        if (tableToSync != MovieEntry.TABLE_NAME_FAVORITE) {
            if (sInitialized == tableToSync) return;

            sInitialized = tableToSync;


            // use a background thread to query the database to determine if a sync is needed.
            Thread checkForEmpty = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "***** Runnable Thread started for table " + tableToSync);

                    String[] projectionColumns = {MovieContract.MovieEntry._ID};

                    Cursor cursor = context.getContentResolver().query(
                            HelperUtils.getUriFromTableName(tableToSync),
                            projectionColumns,
                            null,
                            null,
                            null);

                    Log.d(LOG_TAG, "***** Runnable Thread Executed Cursor for table " + tableToSync);
                    if (null == cursor || cursor.getCount() == 0) {
                        Log.d(LOG_TAG, "***** ... about to call startSync for table " + tableToSync);
                        startSync(context, tableToSync);
                    }

                    cursor.close();
                }
            });

            checkForEmpty.start();
        }
    }

    public static void startSync(@NonNull final Context context, @NonNull final String tableToSync) {
        Log.d(LOG_TAG, "***** startSync started for table " + tableToSync);
        if (tableToSync != MovieEntry.TABLE_NAME_FAVORITE) {
            Intent intentToSync = new Intent(context, MoviesSyncIntentService.class);
            intentToSync.putExtra(MovieContract.CURRENT_TABLE_KEY, tableToSync);
            context.startService(intentToSync);
            Log.d(LOG_TAG, "***** startSync finished for table " + tableToSync);
        }
    }

    public static void startSyncExtras(@NonNull final Context context, @NonNull final String tableToSync, @NonNull final int movieId) {
        Log.d(LOG_TAG, "***** startSyncExtras started for table " + tableToSync);
        Intent intentToSync = new Intent(context, MoviesSyncIntentService.class);
        intentToSync.putExtra(MovieContract.CURRENT_TABLE_KEY, tableToSync);
        intentToSync.putExtra(MovieEntry.COLUMN_MOVIE_ID, movieId);

        context.startService(intentToSync);
        Log.d(LOG_TAG, "***** startSyncExtras finished for table " + tableToSync);

    }
}
