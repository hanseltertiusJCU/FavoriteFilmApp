package com.example.favoritefilmapp.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.favoritefilmapp.BuildConfig;
import com.example.favoritefilmapp.entity.TvShowItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DetailedFavoriteTvShowViewModel extends AndroidViewModel {

    // Gunakan Build Config untuk melindungi credential
    private String apiKey = BuildConfig.API_KEY;
    private String detailedTvShowUrlBase = BuildConfig.BASE_TV_SHOW_DETAILED_URL;
    private String apiKeyFiller = BuildConfig.DETAILED_ITEM_API_KEY_FILLER;

    // Create LiveData object agar dapat di observer melalui observer
    private DetailedFavoriteTvShowLiveData detailedFavoriteTvShowLiveData;

    // Id untuk movie
    private int mDetailedFavoriteTvShowId;

    // Constructor untuk view model yang gunanya untuk membuat LiveData object
    // (since LiveData is a part of ViewModel)
    public DetailedFavoriteTvShowViewModel(@NonNull Application application, int detailedFavoriteTvShowId) {
        super(application);
        this.mDetailedFavoriteTvShowId = detailedFavoriteTvShowId;
        // Buat LiveData object dengan memanggil constructor di class
        detailedFavoriteTvShowLiveData = new DetailedFavoriteTvShowLiveData(application, detailedFavoriteTvShowId);
    }

    // Method ini berguna untuk return live data object yang menampung ArrayList<MovieItem>
    // alias return LiveData class since it extends
    // live data object yang menampung ArrayList<MovieItem>
    public LiveData<ArrayList<TvShowItem>> getDetailedFavoriteTvShow() {
        return detailedFavoriteTvShowLiveData;
    }

    // Class ini berguna untuk membuat datanya
    private class DetailedFavoriteTvShowLiveData extends LiveData<ArrayList<TvShowItem>> {
        private final Context context;
        private final int id;

        // Buat constructor untuk mengakomodasi parameter yang ada di {@link DetailedFavoriteMovieViewModel}
        DetailedFavoriteTvShowLiveData(Context context, int id) {
            this.context = context;
            this.id = id;
            loadDetailedFavoriteTvShowLiveData(); // Call method untuk dapatin array list object
        }

        @SuppressLint("StaticFieldLeak")
        private void loadDetailedFavoriteTvShowLiveData() {
            // Create new AsyncTask object
            new AsyncTask<Void, Void, ArrayList<TvShowItem>>() {

                @Override
                protected ArrayList<TvShowItem> doInBackground(Void... voids) {
                    // Create SyncHttpClient object yg gunanya untuk melakukan network operation
                    SyncHttpClient syncHttpClient = new SyncHttpClient();
                    // Initiate ArrayList that takes up MovieItem and declared final cus of accessing inner class
                    final ArrayList<TvShowItem> favoriteTvShowItems = new ArrayList<>();
                    // URL for detailed favorite movie item
                    String detailedFavoriteTvShowUrl = detailedTvShowUrlBase + mDetailedFavoriteTvShowId + apiKeyFiller + apiKey;
                    // Do the task
                    syncHttpClient.get(detailedFavoriteTvShowUrl, new AsyncHttpResponseHandler() {

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
                                TvShowItem favoriteTvShowItem = new TvShowItem(responseObject);
                                // Add MovieItem ke ArrayList
                                favoriteTvShowItems.add(favoriteTvShowItem);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });

                    return favoriteTvShowItems;
                }

                @Override
                protected void onPostExecute(ArrayList<TvShowItem> tvShowItems) {
                    // Set value dari Observer yang berisi ArrayList yang merupakan
                    // hasil dari doInBackground method
                    setValue(tvShowItems);
                }
            }.execute();
        }
    }
}
