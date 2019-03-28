package com.example.favoritefilmapp.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.favoritefilmapp.model.DetailedFavoriteMovieViewModel;

// Class ini berguna untuk membuat ViewModel yang menampung lebih dari 1 parameter
public class DetailedFavoriteMovieViewModelFactory implements ViewModelProvider.Factory {

    // Initiate application and movie id variable
    private Application mApplication;
    private int mFavoriteMovieId;

    // Constructor untuk membawa application context beserta id ke ViewModel constructor
    public DetailedFavoriteMovieViewModelFactory(Application application, int favoriteMovieId) {
        mApplication = application;
        mFavoriteMovieId = favoriteMovieId;
    }

    // Create ViewModel object bedasarkan value yg ad di constructors sebagai parameter
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailedFavoriteMovieViewModel(mApplication, mFavoriteMovieId);
    }
}
