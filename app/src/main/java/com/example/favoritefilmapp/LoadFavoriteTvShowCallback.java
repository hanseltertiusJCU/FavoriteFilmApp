package com.example.favoritefilmapp;

import android.database.Cursor;

public interface LoadFavoriteTvShowCallback {
    void favoriteTvShowPreExecute();
    void favoriteTvShowPostExecute(Cursor favoriteTvShowItems);
}
