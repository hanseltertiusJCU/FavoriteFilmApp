package com.example.favoritefilmapp;

import android.database.Cursor;

public interface LoadFavoriteTvShowCallback {
    void favoriteTvShowPostExecute(Cursor favoriteTvShowItems);
}
