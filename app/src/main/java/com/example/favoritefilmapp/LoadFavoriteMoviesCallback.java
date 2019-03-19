package com.example.favoritefilmapp;

import android.database.Cursor;

public interface LoadFavoriteMoviesCallback {
    void favoriteMoviePostExecute(Cursor favoriteMovieItems);
}
