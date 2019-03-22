package com.example.favoritefilmapp.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.example.favoritefilmapp.LoadFavoriteMoviesCallback;
import com.example.favoritefilmapp.async.LoadFavoriteMoviesAsync;

// Class ini berguna untuk memantau ada perubahan data di ContentResolver
public class FavoriteMovieDataObserver extends ContentObserver {
    final Context context;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public FavoriteMovieDataObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // Load AsyncTask yang menampung content dari ContentObserver class beserta callback nya
        new LoadFavoriteMoviesAsync(context, (LoadFavoriteMoviesCallback) context).execute();
    }
}
