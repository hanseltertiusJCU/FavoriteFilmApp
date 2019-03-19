package com.example.favoritefilmapp.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.favoritefilmapp.db.FavoriteDatabaseContract;

import org.json.JSONObject;

import static android.provider.BaseColumns._ID;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.getColumnInt;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.getColumnString;

public class MovieItem implements Parcelable {
    // Nilai dari value untuk MovieItem
    private int movieId;
    private String movieTitle;
    private String movieRuntime;
    private String movieRatings;
    private String movieOriginalLanguage;
    private String movieReleaseDate;
    private String movieOverview;
    private String moviePosterPath;
    // Nilai untuk mengetahui waktu dimana sebuah data di add menjadi favorite
    private String dateAddedFavorite;
    // Nilai untuk tahu bahwa movie item itu termasuk dalam kategori favorit ato tidak
    private int favoriteBooleanState;

    public MovieItem(JSONObject jsonObject){
        try{
            int dataId = jsonObject.getInt("id");
            String dataTitle = jsonObject.getString("title");
            String dataRuntime = jsonObject.getString("runtime");
            String dataRatings = jsonObject.getString("vote_average");
            String dataOriginalLanguage = jsonObject.getString("original_language");
            String dataOriginalDisplayLanguage = dataOriginalLanguage.toUpperCase();
            String dataReleaseDate = jsonObject.getString("release_date");
            String dataOverview = jsonObject.getString("overview");
            String dataPosterPath = jsonObject.getString("poster_path");

            // Set values bedasarkan variable-variable yang merepresentasikan field dari sebuah JSON
            // object
            this.movieId = dataId;
            this.movieTitle = dataTitle;
            this.movieRuntime = dataRuntime;
            this.movieRatings = dataRatings;
            this.movieOriginalLanguage = dataOriginalDisplayLanguage;
            this.movieReleaseDate = dataReleaseDate;
            this.movieOverview = dataOverview;
            this.moviePosterPath = dataPosterPath;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Empty constructor
    public MovieItem(){

    }

    // Constructor untuk menampung value columns yang ada
    public MovieItem(int movieId, String movieTitle, String movieRatings, String movieOriginalLanguage, String movieReleaseDate, String moviePosterPath, String dateAddedFavorite, int favoriteBooleanState) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieRatings = movieRatings;
        this.movieOriginalLanguage = movieOriginalLanguage;
        this.movieReleaseDate = movieReleaseDate;
        this.moviePosterPath = moviePosterPath;
        this.dateAddedFavorite = dateAddedFavorite;
        this.favoriteBooleanState = favoriteBooleanState;
    }

    // Constructor untuk menampung Cursor
    public MovieItem(Cursor cursor){
        // Method ini berguna untuk set variable values yang ada di column values
        this.movieId = getColumnInt(cursor, _ID);
        this.movieTitle = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TITLE_COLUMN);
        this.movieRatings = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RATINGS_COLUMN);
        this.movieOriginalLanguage = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_ORIGINAL_LANGUAGE_COLUMN);
        this.movieReleaseDate = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RELEASE_DATE_COLUMN);
        this.moviePosterPath = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FILE_PATH_COLUMN);
        this.dateAddedFavorite = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN);
        this.favoriteBooleanState = getColumnInt(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_COLUMN);
    }

    protected MovieItem(Parcel in) {
        movieId = in.readInt();
        movieTitle = in.readString();
        movieRuntime = in.readString();
        movieRatings = in.readString();
        movieOriginalLanguage = in.readString();
        movieReleaseDate = in.readString();
        movieOverview = in.readString();
        moviePosterPath = in.readString();
        dateAddedFavorite = in.readString();
        favoriteBooleanState = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeString(movieRuntime);
        dest.writeString(movieRatings);
        dest.writeString(movieOriginalLanguage);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieOverview);
        dest.writeString(moviePosterPath);
        dest.writeString(dateAddedFavorite);
        dest.writeInt(favoriteBooleanState);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    // Getter untuk semua attribute
    public int getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieRuntime() {
        return movieRuntime;
    }

    public String getMovieRatings() {
        return movieRatings;
    }

    public String getMovieOriginalLanguage() {
        return movieOriginalLanguage;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public String getDateAddedFavorite() {
        return dateAddedFavorite;
    }

    public int getFavoriteBooleanState() {
        return favoriteBooleanState;
    }

    // Setter untuk favorite date added di movie
    public void setDateAddedFavorite(String dateAddedFavorite) {
        this.dateAddedFavorite = dateAddedFavorite;
    }

    // Setter untuk favorite boolean state di movie
    public void setFavoriteBooleanState(int favoriteBooleanState) {
        this.favoriteBooleanState = favoriteBooleanState;
    }
}
