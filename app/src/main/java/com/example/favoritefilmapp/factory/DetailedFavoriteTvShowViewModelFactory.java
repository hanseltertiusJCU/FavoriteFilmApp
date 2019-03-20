package com.example.favoritefilmapp.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.favoritefilmapp.model.DetailedFavoriteTvShowViewModel;

// Class ini berguna untuk membuat ViewModel yang menampung lebih dari 1 parameter
public class DetailedFavoriteTvShowViewModelFactory implements ViewModelProvider.Factory {

    // Initiate application and tv show id variable
    private Application mApplication;
    private int mFavoriteTvShowId;

    public DetailedFavoriteTvShowViewModelFactory(Application application, int favoriteTvShowId) {
        this.mApplication = application;
        this.mFavoriteTvShowId = favoriteTvShowId;
    }

    // Create ViewModel object bedasarkan value yg ad di constructors sebagai parameter
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailedFavoriteTvShowViewModel(mApplication, mFavoriteTvShowId);
    }
}
