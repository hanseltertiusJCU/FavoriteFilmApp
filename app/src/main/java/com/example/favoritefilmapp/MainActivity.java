package com.example.favoritefilmapp;

import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements LoadFavoriteMoviesCallback, LoadFavoriteTvShowCallback{

    // Bind bottom navigation view
    @BindView(R.id.favorite_film_navigation)
    BottomNavigationView favoriteFilmBottomNavigationView;

    // Initiate ArrayList that takes up MovieItem and TvShowItem
    public static ArrayList<MovieItem> favoriteMovieItemArray;
    public static ArrayList<TvShowItem> favoriteTvShowItemArray;

    // Create fragment object
    Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            // Cek menu item id yang akan dipilih
            switch (menuItem.getItemId()){
                case R.id.navigation_favorite_movie: // Line code jika menu item id adalah navigation_favorite_movie
                    fragment = new FavoriteMovieFragment(); // Set fragment value into certain fragment
                    getSupportFragmentManager().beginTransaction() // Get into Fragment Transaction to access its method
                            .replace(R.id.favorite_film_container_layout, fragment, fragment.getClass().getSimpleName()) // If there is existing fragment, replace selected fragment into container to make container to fill with fragment
                            .commit(); // commit transaction
                    // Cek jika action bar exist
                    if(getSupportActionBar() != null){
                        getSupportActionBar().setTitle(getString(R.string.favorite_movie)); // Set action bar title sesuai dengan fragment tertentu
                    }
                    return true;
                case R.id.navigation_favorite_tv_show: // Line code jika menu item id adalah navigation_favorite_tv_show
                    fragment = new FavoriteTvShowFragment(); // Set fragment value into certain fragment
                    getSupportFragmentManager().beginTransaction() // Get into Fragment Transaction to access its method
                            .replace(R.id.favorite_film_container_layout, fragment, fragment.getClass().getSimpleName()) // If there is existing fragment, replace selected fragment into container to make container to fill with fragment
                            .commit(); // commit transaction
                    // Cek jika action bar exist
                    if(getSupportActionBar() != null){
                        getSupportActionBar().setTitle(getString(R.string.favorite_tv_show)); // Set action bar title sesuai dengan fragment tertentu
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

        // Cek jika bundle tidak ada alias saat pertama kali activity tercipta
        if(savedInstanceState == null){
            favoriteFilmBottomNavigationView.setSelectedItemId(R.id.navigation_favorite_movie); // Set default navigation
            // Load async task for getting the Cursor in favorite Movies and TV Show
            new LoadFavoriteMoviesAsync(this, this).execute();
            new LoadFavoriteTvShowAsync(this, this).execute();
        }

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


    }

    // Implement method dari interface load favorite movie
    @Override
    public void favoriteMoviePostExecute(Cursor favoriteMovieItems) {
        favoriteMovieItemArray = mapCursorToFavoriteMovieArrayList(favoriteMovieItems);
    }

    // Implement method dari interface load favorite tv show
    @Override
    public void favoriteTvShowPostExecute(Cursor favoriteTvShowItems) {
        favoriteTvShowItemArray = mapCursorToFavoriteTvShowArrayList(favoriteTvShowItems);
    }
}
