package com.example.favoritefilmapp;

import android.database.Cursor;

public interface LoadFavoriteMoviesCallback {
    void favoriteMoviePreExecute();

    void favoriteMoviePostExecute(Cursor favoriteMovieItems);
}
