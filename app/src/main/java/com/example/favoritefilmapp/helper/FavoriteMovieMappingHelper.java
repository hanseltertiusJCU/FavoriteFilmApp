package com.example.favoritefilmapp.helper;

import android.database.Cursor;

import com.example.favoritefilmapp.entity.MovieItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FILE_PATH_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RATINGS_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RELEASE_DATE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TITLE_COLUMN;

public class FavoriteMovieMappingHelper {
    // Method ini berguna untuk memindahkan cursor ke ArrayList
    public static ArrayList<MovieItem> mapCursorToFavoriteMovieArrayList(Cursor favoriteMovieItemsCursor) {
        // Array list yang di return
        ArrayList<MovieItem> favoriteMovieItemList = new ArrayList<>();

        if (favoriteMovieItemsCursor != null) {
            // Cek jika masih ada data di cursor
            while (favoriteMovieItemsCursor.moveToNext()) {
                // Get column values
                int movieId = favoriteMovieItemsCursor.getInt(favoriteMovieItemsCursor.getColumnIndexOrThrow(_ID));
                String movieTitle = favoriteMovieItemsCursor.getString(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_TITLE_COLUMN));
                String movieRatings = favoriteMovieItemsCursor.getString(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_RATINGS_COLUMN));
                String movieOriginalLanguage = favoriteMovieItemsCursor.getString(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_ORIGINAL_LANGUAGE_COLUMN));
                String movieReleaseDate = favoriteMovieItemsCursor.getString(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_RELEASE_DATE_COLUMN));
                String moviePosterPath = favoriteMovieItemsCursor.getString(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_FILE_PATH_COLUMN));
                String movieDateAddedFavorite = favoriteMovieItemsCursor.getString(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_DATE_ADDED_FAVORITE_COLUMN));
                int movieBooleanState = favoriteMovieItemsCursor.getInt(favoriteMovieItemsCursor.getColumnIndexOrThrow(MOVIE_FAVORITE_COLUMN));
                // Add MovieItem to arraylist, use constructor that takes variables
                favoriteMovieItemList.add(new MovieItem(movieId, movieTitle, movieRatings, movieOriginalLanguage, movieReleaseDate, moviePosterPath, movieDateAddedFavorite, movieBooleanState));
            }
        }

        // Return arraylist
        return favoriteMovieItemList;
    }
}
