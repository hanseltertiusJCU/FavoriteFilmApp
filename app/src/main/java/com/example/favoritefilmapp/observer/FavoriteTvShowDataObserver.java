package com.example.favoritefilmapp.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.example.favoritefilmapp.LoadFavoriteTvShowCallback;
import com.example.favoritefilmapp.async.LoadFavoriteTvShowAsync;

public class FavoriteTvShowDataObserver extends ContentObserver {
    private Context context;


    public FavoriteTvShowDataObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        new LoadFavoriteTvShowAsync(context, (LoadFavoriteTvShowCallback) context).execute();
    }
}
