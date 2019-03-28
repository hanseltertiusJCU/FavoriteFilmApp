package com.example.favoritefilmapp.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.favoritefilmapp.BuildConfig;
import com.example.favoritefilmapp.entity.MovieItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DetailedFavoriteMovieViewModel extends AndroidViewModel {

    // Gunakan Build Config untuk melindungi credential
    private String apiKey = BuildConfig.API_KEY;
    private String detailedUrlBase = BuildConfig.BASE_MOVIE_DETAILED_URL;
    private String apiKeyFiller = BuildConfig.DETAILED_ITEM_API_KEY_FILLER;

    // Create LiveData object agar dapat di observe melalui observer
    private DetailedFavoriteMovieLiveData detailedFavoriteMovieLiveData;

    // Id untuk movie
    private int mDetailedFavoriteMovieId;

    // Constructor untuk view model yang gunanya untuk membuat LiveData object (since LiveData is a part of ViewModel)
    public DetailedFavoriteMovieViewModel(@NonNull Application application, int detailedFavoriteMovieId) {
        super(application);
        this.mDetailedFavoriteMovieId = detailedFavoriteMovieId;
        // Buat LiveData object dengan memanggil constructor di class
        detailedFavoriteMovieLiveData = new DetailedFavoriteMovieLiveData(application, detailedFavoriteMovieId);
    }

    // Method ini berguna untuk return live data object yang menampung ArrayList<MovieItem> alias return LiveData class since it extends live data object yang menampung ArrayList<MovieItem>
    public LiveData<ArrayList<MovieItem>> getDetailedFavoriteMovie() {
        return detailedFavoriteMovieLiveData;
    }

    // Class ini berguna untuk membuat datanya
    private class DetailedFavoriteMovieLiveData extends LiveData<ArrayList<MovieItem>> {
        private final Context context;
        private final int id;

        // Buat constructor untuk mengakomodasi parameter yang ada di {@link DetailedFavoriteMovieViewModel}
        DetailedFavoriteMovieLiveData(Context context, int id) {
            this.context = context;
            this.id = id;
            loadDetailedFavoriteMovieLiveData(); // Call method untuk dapatin array list object
        }

        @SuppressLint("StaticFieldLeak")
        private void loadDetailedFavoriteMovieLiveData() {
            // Create new Asynctask object
            new AsyncTask<Void, Void, ArrayList<MovieItem>>() {

                @Override
                protected ArrayList<MovieItem> doInBackground(Void... voids) {
                    // Create SyncHttpClient object yg gunanya untuk melakukan network operation
                    SyncHttpClient syncHttpClient = new SyncHttpClient();
                    // Initiate ArrayList that takes up MovieItem and declared final cus of accessing inner class
                    final ArrayList<MovieItem> favoriteMovieItems = new ArrayList<>();
                    // URL for detailed favorite movie item
                    String detailedFavoriteMovieUrl = detailedUrlBase + mDetailedFavoriteMovieId + apiKeyFiller + apiKey;
                    // Do the task
                    syncHttpClient.get(detailedFavoriteMovieUrl, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            // Set ke synchronous karena kita ingin balikin object
                            setUseSynchronousMode(true);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                // Create JSON Object
                                String result = new String(responseBody);
                                JSONObject responseObject = new JSONObject(result);
                                // Create MovieItem
                                MovieItem favoriteMovieItem = new MovieItem(responseObject);
                                // Add MovieItem ke ArrayList
                                favoriteMovieItems.add(favoriteMovieItem);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });

                    return favoriteMovieItems;
                }

                @Override
                protected void onPostExecute(ArrayList<MovieItem> movieItems) {
                    // Set value dari Observer yang berisi ArrayList yang merupakan
                    // hasil dari doInBackground method
                    setValue(movieItems);
                }
            }.execute();
        }
    }
}
