package com.example.favoritefilmapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.favoritefilmapp.entity.MovieItem;
import com.example.favoritefilmapp.entity.TvShowItem;
import com.example.favoritefilmapp.factory.DetailedFavoriteTvShowViewModelFactory;
import com.example.favoritefilmapp.factory.DetailedFavoriteViewModelFactory;
import com.example.favoritefilmapp.model.DetailedFavoriteMovieViewModel;
import com.example.favoritefilmapp.model.DetailedFavoriteTvShowViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // line tsb guna untuk bind view (anggapnya pengganti findViewById)
    @BindView(R.id.detailed_item_poster_image)
    ImageView detailedItemPosterImage;
    @BindView(R.id.detailed_first_info_text)
    TextView detailedFirstInfoTextView;
    @BindView(R.id.detailed_second_info_text)
    TextView detailedSecondInfoTextView;
    @BindView(R.id.detailed_third_info_text)
    TextView detailedThirdInfoTextView;
    @BindView(R.id.detailed_fourth_info_title)
    TextView detailedFourthInfoTitleTextView;
    @BindView(R.id.detailed_fourth_info_text)
    TextView detailedFourthInfoTextView;
    @BindView(R.id.detailed_fifth_info_title)
    TextView detailedFifthInfoTitleTextView;
    @BindView(R.id.detailed_fifth_info_text)
    TextView detailedFifthInfoTextView;
    @BindView(R.id.detailed_sixth_info_title)
    TextView detailedSixthInfoTitleTextView;
    @BindView(R.id.detailed_sixth_info_text)
    TextView detailedSixthInfoTextView;
    // View untuk empty text
    @BindView(R.id.empty_detailed_info_text)
    TextView detailedEmptyInfoTextView;
    // View untuk menentukan visibility ketika loading
    @BindView(R.id.detailed_progress_bar)
    ProgressBar detailedProgressBar;
    @BindView(R.id.detailed_info_content)
    LinearLayout detailedInfoContent;

    // Request code
    public static final int REQUEST_CHANGE = 100;
    // Result code
    public static final int RESULT_CHANGE = 200;

    /*
    * Line tsb berguna untuk setup variable
    * */
    // Setup intent value untuk movie items
    private int detailedMovieId;
    private String detailedMovieTitle;
    private int detailedMovieFavoriteStateValue;
    private int detailedMovieFavoriteStateValueComparison;
    // Setup intent value untuk tv show items
    private int detailedTvShowId;
    private String detailedTvShowName;
    private int detailedTvShowFavoriteStateValue;
    private int detailedTvShowFavoriteStateValueComparison;
    // Setup boolean menu clickable state
    private boolean menuClickable = false;
    // Gunakan BuildConfig untuk menjaga credential
    private String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
    // Drawable Global variable to handle orientation changes
    private int drawableMenuMarkedAsFavouriteResourceId;
    // Initiate MovieItem class untuk mengotak-atik value dr sebuah item di MovieItem class
    private MovieItem detailedMovieItem;
    // Initiate TvShowItem class untuk mengotak-atik value dr sebuah item di TvShowItem class
    private TvShowItem detailedTvShowItem;
    // String value untuk mengetahui mode data yg dibuka
    private String accessItemMode;
    // Boolean value untuk extra di Intent
    private boolean changedState;
    // Uri value untuk membaca data (jika data ada di favorite) ataupun insert data
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        // Setup intent values
        accessItemMode = getIntent().getStringExtra(BuildConfig.FAVORITE_MODE_INTENT);

        // Cek untuk mode yg tepat
        if(accessItemMode.equals("open_movie_detail")){
            // Get intent untuk mendapatkan id, title serta favorite movie state dari {@link MainActivity}
            detailedMovieId = getIntent().getIntExtra(BuildConfig.FAVORITE_MOVIE_ID_DATA, 0);
            detailedMovieTitle = getIntent().getStringExtra(BuildConfig.FAVORITE_MOVIE_TITLE_DATA);
            detailedMovieFavoriteStateValueComparison = getIntent().getIntExtra(BuildConfig.FAVORITE_MOTIE_BOOLEAN_STATE_DATA, 0);
        } else if(accessItemMode.equals("open_tv_show_detail")){
            // Get intent untuk mendapatkan id, title serta favorite tv show state dari {@link MainActivity}
            detailedTvShowId = getIntent().getIntExtra(BuildConfig.FAVORITE_TV_SHOW_ID_DATA, 0);
            detailedTvShowName = getIntent().getStringExtra(BuildConfig.FAVORITE_TV_SHOW_TITLE_DATA);
            detailedTvShowFavoriteStateValueComparison = getIntent().getIntExtra(BuildConfig.FAVORITE_TV_SHOW_BOOLEAN_STATE, 0);
        }

        // Uri bedasarkan intent (selected favorite item)
        uri = getIntent().getData();

        // Cek jika uri itu exist
        if(uri != null){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            // Cek jika cursor exist
            if(cursor != null){
                if(cursor.moveToFirst()){
                    // If condition to accomodate which custom class object to create and takes up cursor as input parameter
                    if(accessItemMode.equals("open_movie_detail")){
                        detailedMovieItem = new MovieItem(cursor);
                        cursor.close();
                    } else if(accessItemMode.equals("open_tv_show_detail")) {
                        detailedTvShowItem = new TvShowItem(cursor);
                        cursor.close();
                    }
                }
            }
        }

        // Cek jika savedInstanceState itu ada, jika iya, restore drawable marked as favorite icon state
        if(savedInstanceState != null){
            if(accessItemMode.equals("open_movie_detail")){
                detailedMovieFavoriteStateValue = savedInstanceState.getInt(BuildConfig.KEY_DRAWABLE_MARKED_AS_FAVORITE);
                changedState = savedInstanceState.getBoolean(BuildConfig.FAVORITE_EXTRA_CHANGED_STATE);
                // Bawa object Uri agar Uri tidak null pada saat rotate, sehingga mencegah run time error
                uri = savedInstanceState.getParcelable(BuildConfig.FAVORITE_EXTRA_URI);
                // Bawa object MovieItem agar MovieItem tidak null pada saat rotate, sehingga mencegah run time error (NullPointerException)
                detailedMovieItem = savedInstanceState.getParcelable(BuildConfig.FAVORITE_EXTRA_MOVIE_ITEM);
                // Tujuan dari line code ini adalah agar bs bawa ke result serta handle comparison value
                // dimana kedua hal tsb dapat menghandle situasi orientation changes
                if(changedState){ // Cek jika value dari changedState itu true, alias data state ud tergantikan
                    // Cek jika value dari state value itu 1 = state marked as fav
                    if(detailedMovieFavoriteStateValue == 1){
                        detailedMovieFavoriteStateValueComparison = 1; // Update comparison value
                    } else {
                        detailedMovieFavoriteStateValueComparison = 0;
                    }
                    Intent resultIntent = new Intent();
                    // Put extra ke intent
                    resultIntent.putExtra(BuildConfig.FAVORITE_EXTRA_CHANGED_STATE, changedState);
                    // Set result untuk menghandle onActivityResult
                    setResult(RESULT_CHANGE, resultIntent);
                }
            } else if(accessItemMode.equals("open_tv_show_detail")) {
                detailedTvShowFavoriteStateValue = savedInstanceState.getInt(BuildConfig.KEY_DRAWABLE_MARKED_AS_FAVORITE);
                changedState = savedInstanceState.getBoolean(BuildConfig.FAVORITE_EXTRA_CHANGED_STATE);
                // Bawa object Uri agar Uri tidak null pada saat rotate, sehingga mencegah run time error
                uri = savedInstanceState.getParcelable(BuildConfig.FAVORITE_EXTRA_URI);
                // Bawa object TvShowItem agar TvShowItem tidak null pada saat rotate, sehingga mencegah run time error (NullPointerException)
                detailedTvShowItem = savedInstanceState.getParcelable(BuildConfig.FAVORITE_EXTRA_TV_SHOW_ITEM);
                // Tujuan dari line code ini adalah agar bs bawa ke result serta handle comparison value
                // dimana kedua hal tsb dapat menghandle situasi orientation changes
                if(changedState) { // Cek jika value dari changedState itu true, alias data state ud tergantikan
                    // Cek jika value dari state value itu 1 = state marked as fav
                    if (detailedTvShowFavoriteStateValue == 1) {
                        detailedTvShowFavoriteStateValueComparison = 1; // Update comparison value
                    } else {
                        detailedTvShowFavoriteStateValueComparison = 0;
                    }
                    Intent resultIntent = new Intent();
                    // Put extra ke intent
                    resultIntent.putExtra(BuildConfig.FAVORITE_EXTRA_CHANGED_STATE, changedState);
                    // Set result untuk menghandle onActivityResult
                    setResult(RESULT_CHANGE, resultIntent);
                }
            }
        } else { // Jika tidak ada bundle savedinstance state
            if(accessItemMode.equals("open_movie_detail")){
                // Valuenya dr MovieFavoriteState d samain sm comparison
                detailedMovieFavoriteStateValue = detailedMovieFavoriteStateValueComparison;
            } else if(accessItemMode.equals("open_tv_show_detail")){
                // Valuenya dr TvShowFavoriteState d samain sm comparison
                detailedTvShowFavoriteStateValue = detailedTvShowFavoriteStateValueComparison;
            }
        }

        // Cek kalo ada action bar
        if(getSupportActionBar() != null){
            // Set action bar title untuk DetailActivity
            if(accessItemMode.equals("open_movie_detail")){
                getSupportActionBar().setTitle(detailedMovieTitle); // Set title based on movie title in intent
            } else if(accessItemMode.equals("open_tv_show_detail")){
                getSupportActionBar().setTitle(detailedTvShowName); // Set title based on tv show name in intent
            }
            // Set up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set visibility of views ketika sedang dalam meretrieve data
        detailedInfoContent.setVisibility(View.INVISIBLE);
        detailedProgressBar.setVisibility(View.VISIBLE);
        detailedEmptyInfoTextView.setVisibility(View.GONE);


        // Mode untuk menangani ViewModel yg berbeda
        if(accessItemMode.equals("open_movie_detail")) {
            // Connectivity manager untuk mengecek state dari network connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            // Network Info object untuk melihat ada data network yang aktif
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
                // Buat ViewModel untuk detailedMovieInfo
                DetailedFavoriteMovieViewModel detailedFavoriteMovieViewModel = ViewModelProviders.of(this, new DetailedFavoriteViewModelFactory(this.getApplication(), detailedMovieId)).get(DetailedFavoriteMovieViewModel.class);
                // Buat observer object untuk mendisplay data ke UI
                // Buat Observer untuk detailedMovieInfo
                Observer<ArrayList<MovieItem>> detailedFavoriteMovieObserver = createDetailedFavoriteMovieObserver(); // todo: make one
                // Tempelkan Observer ke LiveData object
                detailedFavoriteMovieViewModel.getDetailedFavoriteMovie().observe(this, detailedFavoriteMovieObserver);
            } else {
                // Progress bar into gone and recycler view into invisible as the data finished on loading
                detailedProgressBar.setVisibility(View.GONE);
                detailedInfoContent.setVisibility(View.VISIBLE);
                // Set empty view visibility into visible
                detailedEmptyInfoTextView.setVisibility(View.VISIBLE);
                // Empty text view yang menunjukkan bahwa tidak ada internet yang sedang terhubung
                detailedEmptyInfoTextView.setText(getString(R.string.no_internet_connection));
            }
        } else if(accessItemMode.equals("open_tv_show_detail")) {
            // Connectivity manager untuk mengecek state dari network connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            // Network Info object untuk melihat ada data network yang aktif
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            // Cek jika ada network connection
            if(networkInfo != null && networkInfo.isConnected()){
                // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
                // Buat ViewModel untuk detailedTvShowInfo
                DetailedFavoriteTvShowViewModel detailedFavoriteTvShowViewModel = ViewModelProviders.of(this, new DetailedFavoriteTvShowViewModelFactory(this.getApplication(), detailedTvShowId)).get(DetailedFavoriteTvShowViewModel.class);
                // Buat observer object untuk mendisplay data ke UI
                // Buat Observer untuk detailedTvShowInfo
                Observer<ArrayList<TvShowItem>> detailedFavoriteTvShowObserver = createDetailedFavoriteTvShowObserver(); // todo: make one
                // Tempelkan Observer ke LiveData object
                detailedFavoriteTvShowViewModel.getDetailedFavoriteTvShow().observe(this, detailedFavoriteTvShowObserver);
            } else { // Kondisi jika tidak connected ke network
                // Progress bar into gone and recycler view into invisible as the data finished on loading
                detailedProgressBar.setVisibility(View.GONE);
                detailedInfoContent.setVisibility(View.VISIBLE);
                // Set empty view visibility into visible
                detailedEmptyInfoTextView.setVisibility(View.VISIBLE);
                // Empty text view yang menunjukkan bahwa tidak ada internet yang sedang terhubung
                detailedEmptyInfoTextView.setText(getString(R.string.no_internet_connection));
            }
        }
    }

    // Buat observer method untuk favorite Movie yang gunanya untuk return observer object
    private Observer<ArrayList<MovieItem>> createDetailedFavoriteMovieObserver(){
        return new Observer<ArrayList<MovieItem>>() {
            // Method tsb triggered ketika data changed
            @Override
            public void onChanged(@Nullable ArrayList<MovieItem> movieItems) {
                // todo: execute the code
            }
        };
    }

    // Buat observer method untuk favorite TV Show yang gunanya untuk return observer object
    private Observer<ArrayList<TvShowItem>> createDetailedFavoriteTvShowObserver(){
        return new Observer<ArrayList<TvShowItem>>() {
            // Method tsb triggered ketika data changed
            @Override
            public void onChanged(@Nullable ArrayList<TvShowItem> tvShowItems) {
                // todo: execute the code
            }
        };
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Cek jika menu itu bisa di click
        if(menuClickable){
            menu.findItem(R.id.action_marked_as_favorite).setEnabled(true); // Set menu item to be clickable, particularly when the data finished on loading
        } else {
            menu.findItem(R.id.action_marked_as_favorite).setEnabled(false); // Set menu item not to be clickable, particularly when the data is on loading
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_mark_as_favorite_item, menu);
        // Cek untuk mode intent yang tepat
        if(accessItemMode.equals("open_movie_detail")){
            // Cek jika value booleannya itu adalah true, yang berarti menandakan movie favorite
            if(detailedMovieFavoriteStateValue == 1){
                // Set drawable resource
                drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_on;
            } else {
                drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_off;
            }
        } else if(accessItemMode.equals("open_tv_show_detail")){
            // Cek jika value booleannya itu adalah true, yang berarti menandakan movie favorite
            if(detailedMovieFavoriteStateValue == 1){
                // Set drawable resource
                drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_on;
            } else {
                drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_off;
            }
        }
        // Set inflated menu icon
        menu.findItem(R.id.action_marked_as_favorite).setIcon(drawableMenuMarkedAsFavouriteResourceId);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // todo: execute code
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finish method untuk membawa Intent ke MainActivity
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // cek mode intent
        if(accessItemMode.equals("open_movie_detail")){
            // Save drawable marked as favorite state for movie as well as boolean changed state + comparisons
            outState.putInt(BuildConfig.KEY_DRAWABLE_MARKED_AS_FAVORITE, detailedMovieFavoriteStateValue);
            outState.putBoolean(BuildConfig.FAVORITE_EXTRA_CHANGED_STATE, changedState);
            outState.putParcelable(BuildConfig.FAVORITE_EXTRA_URI, uri);
            outState.putParcelable(BuildConfig.FAVORITE_EXTRA_MOVIE_ITEM, detailedMovieItem);
        } else if(accessItemMode.equals("open_tv_show_detail")){
            // Save drawable marked as favorite state for tv show as well as boolean changed state + comparisons
            outState.putInt(BuildConfig.KEY_DRAWABLE_MARKED_AS_FAVORITE, detailedTvShowFavoriteStateValue);
            outState.putBoolean(BuildConfig.FAVORITE_EXTRA_CHANGED_STATE, changedState);
            outState.putParcelable(BuildConfig.FAVORITE_EXTRA_URI, uri);
            outState.putParcelable(BuildConfig.FAVORITE_EXTRA_TV_SHOW_ITEM, detailedTvShowItem);
        }
        super.onSaveInstanceState(outState);
    }

    // Method tsb berguna untuk mendapatkan waktu dimana sebuah item di tambahkan
    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }
}
