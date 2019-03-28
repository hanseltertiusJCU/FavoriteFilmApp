package com.example.favoritefilmapp;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.favoritefilmapp.async.LoadFavoriteMoviesAsync;
import com.example.favoritefilmapp.async.LoadFavoriteTvShowAsync;
import com.example.favoritefilmapp.entity.MovieItem;
import com.example.favoritefilmapp.entity.TvShowItem;
import com.example.favoritefilmapp.fragment.FavoriteMovieFragment;
import com.example.favoritefilmapp.fragment.FavoriteTvShowFragment;
import com.example.favoritefilmapp.observer.FavoriteMovieDataObserver;
import com.example.favoritefilmapp.observer.FavoriteTvShowDataObserver;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;
import static com.example.favoritefilmapp.helper.FavoriteMovieMappingHelper.mapCursorToFavoriteMovieArrayList;
import static com.example.favoritefilmapp.helper.FavoriteTvShowMappingHelper.mapCursorToFavoriteTvShowArrayList;

public class MainActivity extends AppCompatActivity implements LoadFavoriteMoviesCallback, LoadFavoriteTvShowCallback {

    // Bind bottom navigation view
    @BindView(R.id.favorite_film_navigation)
    BottomNavigationView favoriteFilmBottomNavigationView;

    // Initiate ArrayList that takes up MovieItem and TvShowItem
    public static ArrayList<MovieItem> favoriteMovieItemArray; // Initiate array list object that shows favorite movie item
    public static ArrayList<TvShowItem> favoriteTvShowItemArray; // Initiate array list object that shows favorite tv show item

    // Create fragment object
    Fragment fragment;

    // String untuk menampung action bar title
    String actionBarTitle;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            // Cek menu item id yang akan dipilih
            switch (menuItem.getItemId()) {
                case R.id.navigation_favorite_movie: // Line code jika menu item id adalah navigation_favorite_movie
                    fragment = new FavoriteMovieFragment(); // Set fragment value into certain fragment
                    getSupportFragmentManager().beginTransaction() // Get into Fragment Transaction to access its method
                            .replace(R.id.favorite_film_container_layout, fragment, fragment.getClass().getSimpleName()) // If there is existing fragment, replace selected fragment into container to make container to fill with fragment
                            .commit(); // commit transaction
                    // Cek jika action bar exist
                    if (getSupportActionBar() != null) {
                        actionBarTitle = getString(R.string.favorite_movie); // Set action bar value
                        getSupportActionBar().setTitle(actionBarTitle); // Set action bar title sesuai dengan fragment tertentu
                    }
                    return true;
                case R.id.navigation_favorite_tv_show: // Line code jika menu item id adalah navigation_favorite_tv_show
                    fragment = new FavoriteTvShowFragment(); // Set fragment value into certain fragment
                    getSupportFragmentManager().beginTransaction() // Get into Fragment Transaction to access its method
                            .replace(R.id.favorite_film_container_layout, fragment, fragment.getClass().getSimpleName()) // If there is existing fragment, replace selected fragment into container to make container to fill with fragment
                            .commit(); // commit transaction
                    // Cek jika action bar exist
                    if (getSupportActionBar() != null) {
                        actionBarTitle = getString(R.string.favorite_tv_show); // Set action bar value
                        getSupportActionBar().setTitle(actionBarTitle); // Set action bar title sesuai dengan fragment tertentu
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind view into Activity
        ButterKnife.bind(this);

        favoriteFilmBottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener); // Set event listener ke BottomNavigationView

        // Initiate handler thread operation in Movie
        HandlerThread movieHandlerThread = new HandlerThread("FavoriteMovieDataObserver"); // Initiate HandlerThread
        movieHandlerThread.start();

        Handler movieHandler = new Handler(movieHandlerThread.getLooper()); // Initiate Handler
        FavoriteMovieDataObserver myFavoriteMovieObserver = new FavoriteMovieDataObserver(movieHandler, this); // Initiate ContentObserver
        getContentResolver().registerContentObserver(MOVIE_FAVORITE_CONTENT_URI, true, myFavoriteMovieObserver); // Register ContentResolver ke ContentObserver bedasarkan URI

        // Initiate handler thread operation in TV Show
        HandlerThread tvShowHandlerThread = new HandlerThread("FavoriteTvShowDataObserver"); // Initiate HandlerThread
        tvShowHandlerThread.start();

        Handler tvShowHandler = new Handler(tvShowHandlerThread.getLooper()); // Initiate Handler
        FavoriteTvShowDataObserver myFavoriteTvShowObserver = new FavoriteTvShowDataObserver(tvShowHandler, this); // Initiate ContentObserver

        getContentResolver().registerContentObserver(TV_SHOW_FAVORITE_CONTENT_URI, true, myFavoriteTvShowObserver); // Register ContentResolver ke ContentObserver bedasarkan URI

        // Cek jika bundle tidak ada alias saat pertama kali activity tercipta
        if (savedInstanceState == null) {
            favoriteFilmBottomNavigationView.setSelectedItemId(R.id.navigation_favorite_movie); // Set default navigation
        } else {
            if (getSupportActionBar() != null) {
                actionBarTitle = savedInstanceState.getString(BuildConfig.ACTION_BAR_TITLE); // Get value from savedinstancestate
                getSupportActionBar().setTitle(actionBarTitle); // Set action bar title
            }
        }

        // Load async task for getting the Cursor in favorite Movies and TV Show,
        // taruh code disini supaya load data scr lbh konsisten
        new LoadFavoriteMoviesAsync(this, this).execute();
        new LoadFavoriteTvShowAsync(this, this).execute();
    }

    // Implement method dari interface load favorite movie
    @Override
    public void favoriteMoviePreExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                favoriteMovieItemArray = new ArrayList<>(); // Initiate array list object when preparing data to load
            }
        });
    }

    @Override
    public void favoriteMoviePostExecute(Cursor favoriteMovieItems) {
        // make array list value based on cursor
        favoriteMovieItemArray = mapCursorToFavoriteMovieArrayList(favoriteMovieItems);
        // Make favorite movie fragment value to be the same fragment as fragment instantiated above
        Fragment favoriteMovieFragment = getSupportFragmentManager().findFragmentByTag(FavoriteMovieFragment.class.getSimpleName());
        // Initiate fragment transaction untuk melakukan fragment operation
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // cek jika fragment nya itu ada
        if (favoriteMovieFragment != null) {
            // cek jika fragment di attach ke activity
            if (favoriteMovieFragment.isAdded()) {
                fragmentTransaction.detach(favoriteMovieFragment); // detatch fragment existing
                fragmentTransaction.attach(favoriteMovieFragment); // attatch fragment baru
                fragmentTransaction.commitAllowingStateLoss(); // commit fragment transaction untuk display dan gunakan method tsb biar mencegah illegal state exception
            }
        }

        // Intent for broadcast receiver
        Intent broadcastIntent = new Intent();
        // Add flags to intent which can be communicated with closed app package (idk if its true)
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // Set component package
        broadcastIntent.setComponent(new ComponentName("com.example.cataloguemoviefinal.receiver", "com.example.cataloguemoviefinal.receiver.UpdateWidgetDataReceiver"));
        // Set action
        broadcastIntent.setAction("com.example.cataloguemoviefinal.widget.ACTION_UPDATE_WIDGET_DATA");
        // Sent broadcast to receiver
        sendBroadcast(broadcastIntent);

    }

    // Implement method dari interface load favorite tv show
    @Override
    public void favoriteTvShowPreExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                favoriteTvShowItemArray = new ArrayList<>(); // Initiate array list object when preparing data to load
            }
        });
    }

    @Override
    public void favoriteTvShowPostExecute(Cursor favoriteTvShowItems) {
        // make array list value based on cursor
        favoriteTvShowItemArray = mapCursorToFavoriteTvShowArrayList(favoriteTvShowItems);
        // Make favorite tv show fragment value to be the same fragment as fragment instantiated above
        Fragment favoriteTvShowFragment = getSupportFragmentManager().findFragmentByTag(FavoriteTvShowFragment.class.getSimpleName());
        // Initiate fragment transaction untuk melakukan fragment operation
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Cek jika fragmentnya itu ada
        if (favoriteTvShowFragment != null) {
            // Cek jika fragment di attach ke activity
            if (favoriteTvShowFragment.isAdded()) {
                fragmentTransaction.detach(favoriteTvShowFragment); // detatch fragment lama
                fragmentTransaction.attach(favoriteTvShowFragment); // attach fragment baru
                fragmentTransaction.commitAllowingStateLoss(); // commit fragment transaction untuk display dan gunakan method tsb biar mencegah illegal state exception
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BuildConfig.ACTION_BAR_TITLE, actionBarTitle);
    }
}
