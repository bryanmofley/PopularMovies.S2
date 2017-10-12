package udacity.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.sync.MoviesSyncUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler {
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
    };
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_POSTER_PATH = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int ID_MOVIE_LOADER = 42;

    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private MovieAdapter mMovieAdapter;
    private String mCurrentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "***** onCreate starting");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        //spanCount enhancement based on Stage 1 Project Review suggestion
        final int spanCount = getResources().getInteger(R.integer.grid_columns);

        GridLayoutManager layoutManager =
                new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        showProgressBar();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MovieContract.CURRENT_TABLE_KEY)) {
                mCurrentTable = savedInstanceState.getString(MovieContract.CURRENT_TABLE_KEY);
            }
        } else {
            mCurrentTable = MovieContract.MovieEntry.TABLE_NAME_POPULAR;
        }

        setActivityTitle();

        final Bundle tableToDisplay = new Bundle();
        tableToDisplay.putString(MovieContract.CURRENT_TABLE_KEY, mCurrentTable);
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, tableToDisplay, this);

        MoviesSyncUtils.initialize(this, mCurrentTable);

        mSwipeContainer = findViewById(R.id.swipe_container);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MoviesSyncUtils.startSync(MainActivity.this, mCurrentTable);
                refreshLoaderGrid(ID_MOVIE_LOADER);
                mSwipeContainer.setRefreshing(false);
            }
        });
        Log.d(LOG_TAG, "***** onCreate finished");
    }


    private void showMovieDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void refreshLoaderGrid(int loaderId) {
        Log.d(LOG_TAG, "***** refreshLoaderGrid starting");
        mMovieAdapter.swapCursor(null);
        Bundle tableToQuery = new Bundle();
        tableToQuery.putString(MovieContract.CURRENT_TABLE_KEY, mCurrentTable);
        getSupportLoaderManager().restartLoader(loaderId, tableToQuery, this);
        MoviesSyncUtils.initialize(this, mCurrentTable);
        Log.d(LOG_TAG, "***** refreshLoaderGrid finished");
    }

    private void setActivityTitle() {
        switch (mCurrentTable) {
            case MovieContract.MovieEntry.TABLE_NAME_POPULAR:
                setTitle(R.string.activity_popular_title);
                break;
            case MovieContract.MovieEntry.TABLE_NAME_TOP_RATED:
                setTitle(R.string.activity_top_rated_title);
                break;
            case MovieContract.MovieEntry.TABLE_NAME_FAVORITE:
                setTitle(R.string.activity_favorites_title);
                break;
        }
    }

    @Override
    public void onClick(int movieId) {
        Log.d(LOG_TAG, "***** onClick starting");
        Class movieDetailActivity = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(this, movieDetailActivity);

        Uri uriForMovieClicked;
        switch (mCurrentTable) {
            case MovieContract.MovieEntry.TABLE_NAME_POPULAR:
                uriForMovieClicked = MovieContract.MovieEntry.buildPopularUri(movieId);
                break;
            case MovieContract.MovieEntry.TABLE_NAME_TOP_RATED:
                uriForMovieClicked = MovieContract.MovieEntry.buildTopRatedUri(movieId);
                break;
            case MovieContract.MovieEntry.TABLE_NAME_FAVORITE:
                uriForMovieClicked = MovieContract.MovieEntry.buildFavoriteUri(movieId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown table: " + mCurrentTable);
        }

        if (!(uriForMovieClicked == null)) {
            movieDetailIntent.putExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieDetailIntent.setData(uriForMovieClicked);
            startActivity(movieDetailIntent);
        }
        Log.d(LOG_TAG, "***** onClick finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "***** onOptionsItemSelected: " + item.toString());
        int id = item.getItemId();
        boolean validItem;

        switch (id) {
            case R.id.action_sort_popular:
                mCurrentTable = MovieContract.MovieEntry.TABLE_NAME_POPULAR;
                setTitle(R.string.activity_popular_title);
                validItem = true;
                break;
            case R.id.action_sort_top_rated:
                mCurrentTable = MovieContract.MovieEntry.TABLE_NAME_TOP_RATED;
                setTitle(R.string.activity_top_rated_title);
                validItem = true;
                break;
            case R.id.action_sort_favorite:
                mCurrentTable = MovieContract.MovieEntry.TABLE_NAME_FAVORITE;
                setTitle(R.string.activity_favorites_title);
                validItem = true;
                break;
            default:
                validItem = false;
        }

        if (validItem) {
            refreshLoaderGrid(ID_MOVIE_LOADER);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        Log.d(LOG_TAG, "***** Loader<Cursor> onCreateLoader starting");
        switch (loaderId) {
            case ID_MOVIE_LOADER:

                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI_POPULAR;
                String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                if (null != bundle) {
                    switch (bundle.getString(MovieContract.CURRENT_TABLE_KEY)) {
                        case MovieContract.MovieEntry.TABLE_NAME_POPULAR:
                            movieQueryUri = MovieContract.MovieEntry.CONTENT_URI_POPULAR;
                            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                            break;
                        case MovieContract.MovieEntry.TABLE_NAME_TOP_RATED:
                            movieQueryUri = MovieContract.MovieEntry.CONTENT_URI_TOP_RATED;
                            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                            break;
                        case MovieContract.MovieEntry.TABLE_NAME_FAVORITE:
                            movieQueryUri = MovieContract.MovieEntry.CONTENT_URI_FAVORITE;
                            sortOrder = MovieContract.MovieEntry._ID;
                            break;
                        default:
                            movieQueryUri = MovieContract.MovieEntry.CONTENT_URI_POPULAR;
                            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                            break;
                    }
                }

                Log.d(LOG_TAG, "***** Loader<Cursor> onCreateLoader finishing");
                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "***** Loader<Cursor> onLoadFinished starting");
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
//        mRecyclerView.smoothScrollToPosition(mPosition); //this line was causing recyclerview to lose position after rotating once on details screen
        if (data.getCount() != 0) {
            showMovieDataView();
        }
        Log.d(LOG_TAG, "***** Loader<Cursor> onLoadFinished finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "***** Loader<Cursor> onLoaderReset starting");
        mMovieAdapter.swapCursor(null);
        Log.d(LOG_TAG, "***** Loader<Cursor> onLoaderReset finished");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "***** onSaveInstanceState starting");
        outState.putString(MovieContract.CURRENT_TABLE_KEY, mCurrentTable);
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "***** onSaveInstanceState finished");
    }
}
