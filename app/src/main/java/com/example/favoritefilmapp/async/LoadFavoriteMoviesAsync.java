package com.example.favoritefilmapp.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.favoritefilmapp.LoadFavoriteMoviesCallback;

import java.lang.ref.WeakReference;

import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;

public class LoadFavoriteMoviesAsync extends AsyncTask<Void, Void, Cursor> {

    // WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
    // Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
    // dapat mencegah memory leak
    private final WeakReference<Context> weakContext;
    private final WeakReference<LoadFavoriteMoviesCallback> weakCallback;

    //
    public LoadFavoriteMoviesAsync(Context weakContext, LoadFavoriteMoviesCallback weakCallback) {
        this.weakContext = new WeakReference<>(weakContext);
        this.weakCallback = new WeakReference<>(weakCallback);
    }

    @Override
    protected Cursor doInBackground(Void... voids) {
        Context context = weakContext.get();
        return context.getContentResolver().query(MOVIE_FAVORITE_CONTENT_URI, null, null, null, null); // Mengakses content resolver dari context yang menggunakan asynctask agar URI dapat dioper ke ContentProvider
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        weakCallback.get().favoriteMoviePostExecute(cursor); // memanggil method post execute di interface (eksekusi dari method berada di antara activity/fragment terkait)
    }
}
