package udacity.popularmovies.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import udacity.popularmovies.R;
import udacity.popularmovies.data.MovieContract;

public class MoviesSyncIntentService extends IntentService {

    private static final String LOG_TAG = MoviesSyncIntentService.class.getSimpleName();

    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(LOG_TAG, "***** onHandleIntent invoked");
        if (!isNetworkAvailableAndConnected()) {
            Log.d(LOG_TAG, "***** Network Not Connected and/or Not Available");

            // https://stackoverflow.com/questions/35011011/toast-doesnt-disapear
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_LONG).show();
                }
            });

            return;
        }

        String tableToSync = intent.getStringExtra(MovieContract.CURRENT_TABLE_KEY);
        int movieId = intent.getIntExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 0);
        switch (tableToSync) {
            case MovieContract.MovieEntry.TABLE_NAME_POPULAR:
            case MovieContract.MovieEntry.TABLE_NAME_TOP_RATED:
                MoviesSyncTask.syncMovies(this, tableToSync);
                break;
            case MovieContract.TrailerEntry.TABLE_NAME_TRAILER:
                MoviesSyncTask.syncExtras(this, tableToSync, movieId);
                break;
            case MovieContract.ReviewEntry.TABLE_NAME_REVIEW:
                MoviesSyncTask.syncExtras(this, tableToSync, movieId);
                break;
        }
    }

    // Android Programming: The Big Nerd Ranch Guide (3rd Edition) - Section 28.4
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;

        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }
}
