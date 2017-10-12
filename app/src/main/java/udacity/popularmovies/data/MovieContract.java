package udacity.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {
    //keys used to pass values via various intents
    public static final String CURRENT_TABLE_KEY = "current_table";
    public static final String CURRENT_URI_KEY = "current_uri";
    //keys to facilitate construction of uri
    public static final String CONTENT_AUTHORITY = "udacity.popularmovies";// would love to be able to access R.string.content_authority here instead
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";
    private static final String LOG_TAG = MovieContract.class.getSimpleName();
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {

        //uris to cover popular, top rated and favorites
        public static final Uri CONTENT_URI_POPULAR = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR)
                .build();

        public static final Uri CONTENT_URI_TOP_RATED = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED)
                .build();

        public static final Uri CONTENT_URI_FAVORITE = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        //table names for popular, top rated and favorites
        public static final String TABLE_NAME_POPULAR = "movies_popular";
        public static final String TABLE_NAME_TOP_RATED = "movies_top_rated";
        public static final String TABLE_NAME_FAVORITE = "movies_favorite";

        //table field names shared by popular, top rated and favorites
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_IS_ADULT = "adult";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_HAS_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_IS_FAVORITE = "favorite";

        //uris for a single record in popular, top rated and favorites
        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_POPULAR, id);
        }

        public static Uri buildTopRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_TOP_RATED, id);
        }

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_FAVORITE, id);
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI_TRAILER = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILER)
                .build();

        public static final String TABLE_NAME_TRAILER = "movies_trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_ID = "id";
        public static final String COLUMN_ISO_639_1 = "iso_639_1";
        public static final String COLUMN_ISO_3166_1 = "iso_3166_1";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_TRAILER, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI_REVIEW = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW)
                .build();

        public static final String TABLE_NAME_REVIEW = "movies_review";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_REVIEW, id);
        }
    }

}
