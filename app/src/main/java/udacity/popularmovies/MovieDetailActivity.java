package udacity.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.databinding.ActivityMovieDetailBinding;
import udacity.popularmovies.sync.MoviesSyncTask;
import udacity.popularmovies.sync.MoviesSyncUtils;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        TrailerAdapter.TrailerAdapterOnClickHandler {
    public static final String YOUTUBE_VND = "vnd.youtube:";
    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    // Movie Cursor
    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH
    };
    public static final int INDEX_MOVIE_TITLE = 0;
    public static final int INDEX_MOVIE_OVERVIEW = 1;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_POSTER_PATH = 4;
    public static final int INDEX_MOVIE_BACKDROP_PATH = 5;
    public static final int INDEX_MOVIE_IS_FAVORITE = 6; //one-to-many for favorites
    // Trailer Cursor
    public static final String[] TRAILER_PROJECTION = {
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_KEY
    };
    public static final int INDEX_TRAILER_NAME = 0;
    public static final int INDEX_TRAILER_KEY = 1;
    // Review Cursor
    public static final String[] REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };
    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_CONTENT = 1;
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final int ID_DETAIL_LOADER = 84;
    private static final int ID_TRAILER_LOADER = 86;
    private static final int ID_REVIEW_LOADER = 88;
    private RecyclerView mTrailerRecyclerView;
    private int mTrailerPosition = RecyclerView.NO_POSITION;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mReviewRecyclerView;
    private int mReviewPosition = RecyclerView.NO_POSITION;
    private ReviewAdapter mReviewAdapter;

    private Uri mUri;
    private int mMovieId;

    private ActivityMovieDetailBinding mActivityMovieDetailBinding;

    private boolean mIsFavorite = false;
    private FloatingActionButton mFavoriteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovieId = getIntent().getIntExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 0);
        mUri = getIntent().getData();
        if (mUri == null) {
            throw new NullPointerException("URI for MovieDetailActivity cannot be null.");
        }

        //allow use of SVG when swapping images
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mActivityMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        mFavoriteFab = findViewById(R.id.fab_favorite);
        mTrailerRecyclerView = findViewById(R.id.rv_trailers);
        mReviewRecyclerView = findViewById(R.id.rv_reviews);

        LinearLayoutManager trailerLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        LinearLayoutManager reviewLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        DividerItemDecoration reviewDecoration = new DividerItemDecoration(mReviewRecyclerView.getContext(),
                reviewLayoutManager.getOrientation());
        mReviewRecyclerView.addItemDecoration(reviewDecoration);

        MoviesSyncUtils.startSyncExtras(MovieDetailActivity.this, MovieContract.TrailerEntry.TABLE_NAME_TRAILER, mMovieId);
        MoviesSyncUtils.startSyncExtras(MovieDetailActivity.this, MovieContract.ReviewEntry.TABLE_NAME_REVIEW, mMovieId);

        setupSupportManager(ID_DETAIL_LOADER);
        setupSupportManager(ID_TRAILER_LOADER);
        setupSupportManager(ID_REVIEW_LOADER);

    }

    void setupSupportManager(int loaderId) {
        Bundle tableToQuery = new Bundle();
        switch (loaderId) {
            case ID_DETAIL_LOADER:
//                tableToQuery.putString("table", MovieContract.MovieEntry.TABLE_NAME_POPULAR);
                break;
            case ID_TRAILER_LOADER:
                tableToQuery.putString("table", MovieContract.TrailerEntry.TABLE_NAME_TRAILER);
                break;
            case ID_REVIEW_LOADER:
                tableToQuery.putString("table", MovieContract.ReviewEntry.TABLE_NAME_REVIEW);
                break;
            default:
                break;
        }
        getSupportLoaderManager().initLoader(loaderId, tableToQuery, this);
    }

    @Override
    public void onTrailerClick(String youTubeKey) {

        // http://tech.favoritemedium.com/2012/01/scrapping-youtube-video-for-android.html
        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_VND + youTubeKey));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + youTubeKey));

        //if devices does not have youtube app, then default to browser
        try {
            this.startActivity(youTubeIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(browserIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        MOVIE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            case ID_TRAILER_LOADER:
                return new CursorLoader(this,
                        MovieContract.TrailerEntry.CONTENT_URI_TRAILER,
                        TRAILER_PROJECTION,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(mMovieId)},
                        null);
            case ID_REVIEW_LOADER:
                return new CursorLoader(this,
                        MovieContract.ReviewEntry.CONTENT_URI_REVIEW,
                        REVIEW_PROJECTION,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(mMovieId)},
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        int loaderId = loader.getId();
        Log.d(LOG_TAG, "***** Loader<Cursor> onLoadFinished starting for LoaderId " + loaderId);

        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }
        switch (loaderId) {
            case ID_DETAIL_LOADER:
                mActivityMovieDetailBinding.tvMovieTitle
                        .setText(data.getString(INDEX_MOVIE_TITLE));
                mActivityMovieDetailBinding.tvMovieOverview
                        .setText(data.getString(INDEX_MOVIE_OVERVIEW));
                mActivityMovieDetailBinding.tvUserRating
                        .setText(Double.toString(data.getDouble(INDEX_MOVIE_VOTE_AVERAGE)) + getString(R.string.activity_detail_ratings_max_scale));
                mActivityMovieDetailBinding.tvReleaseDate
                        .setText(data.getString(INDEX_MOVIE_RELEASE_DATE).substring(0, 4));

                if (data.getInt(INDEX_MOVIE_IS_FAVORITE) > 0) {
                    mIsFavorite = true;
                    mFavoriteFab.setImageResource(R.drawable.ic_star_black_24dp);
//                    Toast.makeText(this, "Yep, it's a favorite", Toast.LENGTH_SHORT).show();
                }

                final Context context = this;
                final String moviePosterPath = data.getString(INDEX_MOVIE_POSTER_PATH);
                Picasso.with(this)
                        .load(moviePosterPath)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mActivityMovieDetailBinding.ivMoviePoster, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context)
                                        .load(moviePosterPath)
                                        .placeholder(R.drawable.placeholder) //https://www.browncountypubliclibrary.org/sites/default/files/images/download.jpg
                                        .error(R.drawable.error) //https://i.ytimg.com/vi/CEGSitBJ7Gw/hqdefault.jpg
                                        .into(mActivityMovieDetailBinding.ivMoviePoster);
                            }
                        });
                Log.d(LOG_TAG, "***** Loader<Cursor> onLoadFinished finished for LoaderId " + loaderId);
                break;


            case ID_TRAILER_LOADER:
                Log.d(LOG_TAG, "***** Trailer Loader<Cursor> onLoadFinished starting");
                mTrailerAdapter.swapCursor(data);
                if (mTrailerPosition == RecyclerView.NO_POSITION) {
                    mTrailerPosition = 0;
                }
                mTrailerRecyclerView.smoothScrollToPosition(mTrailerPosition);
                if (data.getCount() != 0) {
//                    showMovieDataView();
                }
                Log.d(LOG_TAG, "***** Trailer Loader<Cursor> onLoadFinished finished for LoaderId " + loaderId);
                break;
            case ID_REVIEW_LOADER:
                Log.d(LOG_TAG, "***** Review Loader<Cursor> onLoadFinished starting");
                mReviewAdapter.swapCursor(data);
                if (mReviewPosition == RecyclerView.NO_POSITION) {
                    mReviewPosition = 0;
                }
                mReviewRecyclerView.smoothScrollToPosition(mReviewPosition);
                if (data.getCount() != 0) {
//                    showMovieDataView();
                }
                Log.d(LOG_TAG, "***** Review Loader<Cursor> onLoadFinished finished for LoaderId " + loaderId);
                break;
            default:
                return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void toggleFavorite(View view) {
        if (mIsFavorite) {
            mIsFavorite = false;
            mFavoriteFab.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else {
            mIsFavorite = true;
            mFavoriteFab.setImageResource(R.drawable.ic_star_black_24dp);
        }
        MoviesSyncTask.syncFavorite(this, mUri, mIsFavorite);
    }

}
