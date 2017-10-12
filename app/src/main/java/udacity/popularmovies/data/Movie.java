package udacity.popularmovies.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import udacity.popularmovies.data.MovieContract;

/**
 * Created by Mofley on 8/25/17.
 * <p>
 * Most of this class was created via CMD-N and choosing Constructor... override methods... Implements methods, etc.
 */

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private static final String LOG_TAG = Movie.class.getSimpleName();
    int id;
    boolean adult;
    String backdrop_path;
    String genre_ids;
    String original_language;
    String original_title;
    String overview;
    double popularity;
    String poster_path;
    String release_date;
    String title;
    boolean video;
    double vote_average;
    int vote_count;
    boolean favorite;

    public Movie(int id, boolean adult, String backdrop_path, String genre_ids, String original_language, String original_title, String overview, double popularity, String poster_path, String release_date, String title, boolean video, double vote_average, int vote_count, boolean favorite) {
        this.id = id;
        this.adult = adult;
        this.backdrop_path = backdrop_path;
        this.genre_ids = genre_ids;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.title = title;
        this.video = video;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.favorite = favorite;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        adult = in.readByte() != 0;
        backdrop_path = in.readString();
        genre_ids = in.readString();
        original_language = in.readString();
        original_title = in.readString();
        overview = in.readString();
        popularity = in.readDouble();
        poster_path = in.readString();
        release_date = in.readString();
        title = in.readString();
        video = in.readByte() != 0;
        vote_average = in.readDouble();
        vote_count = in.readInt();
        favorite = in.readByte() != 0;
    }

    public Movie(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)); // int id;
        this.adult = (cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_ADULT)) == 1); //boolean adult;
        this.backdrop_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)); //String backdrop_path;
        this.genre_ids = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_GENRE_IDS));// List<Genre> genre_ids;
        this.original_language = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE)); //String original_language;
        this.original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)); //String original_title;
        this.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)); //String overview;
        this.popularity = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)); //double popularity;
        this.poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)); //String poster_path;
        this.release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)); //String release_date;
        this.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)); //String title;
        this.video = (cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_HAS_VIDEO)) == 1); //boolean video;
        this.vote_average = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)); //double vote_average;
        this.vote_count = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)); //int vote_count;
        this.favorite = (cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE)) == 1); //boolean favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(backdrop_path);
        parcel.writeString(genre_ids);
        parcel.writeString(original_language);
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeDouble(popularity);
        parcel.writeString(poster_path);
        parcel.writeString(release_date);
        parcel.writeString(title);
        parcel.writeByte((byte) (video ? 1 : 0));
        parcel.writeDouble(vote_average);
        parcel.writeInt(vote_count);
        parcel.writeByte((byte) (favorite ? 1 : 0));
    }

    @Override
    public String toString() {
        return "MovieP{" +
                "id=" + id +
                ", adult=" + adult +
                ", backdrop_path='" + backdrop_path + '\'' +
                ", genre_ids=" + genre_ids +
                ", original_language='" + original_language + '\'' +
                ", original_title='" + original_title + '\'' +
                ", overview='" + overview + '\'' +
                ", popularity=" + popularity +
                ", poster_path='" + poster_path + '\'' +
                ", release_date='" + release_date + '\'' +
                ", title='" + title + '\'' +
                ", video=" + video +
                ", vote_average=" + vote_average +
                ", vote_count=" + vote_count +
                ", favorite=" + favorite +
                '}';
    }
}
