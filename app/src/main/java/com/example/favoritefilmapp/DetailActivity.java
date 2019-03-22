package com.example.favoritefilmapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.example.favoritefilmapp.factory.DetailedFavoriteMovieViewModelFactory;
import com.example.favoritefilmapp.model.DetailedFavoriteMovieViewModel;
import com.example.favoritefilmapp.model.DetailedFavoriteTvShowViewModel;
import com.example.favoritefilmapp.observer.FavoriteMovieDataObserver;
import com.example.favoritefilmapp.observer.FavoriteTvShowDataObserver;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.BaseColumns._ID;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FILE_PATH_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RATINGS_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RELEASE_DATE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TITLE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_NAME_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN;

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
    // Register content resolver
    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        // Setup intent values
        accessItemMode = getIntent().getStringExtra(BuildConfig.FAVORITE_MODE_INTENT);

        // Get content resolver
        contentResolver = getContentResolver();

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

        // Mode untuk menangani ViewModel yg berbeda
        if(accessItemMode.equals("open_movie_detail")) {
            // Set visibility of views ketika sedang dalam meretrieve data
            detailedInfoContent.setVisibility(View.INVISIBLE);
            detailedProgressBar.setVisibility(View.VISIBLE);
            detailedEmptyInfoTextView.setVisibility(View.GONE);
            // Connectivity manager untuk mengecek state dari network connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            // Network Info object untuk melihat ada data network yang aktif
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
                // Buat ViewModel untuk detailedMovieInfo
                DetailedFavoriteMovieViewModel detailedFavoriteMovieViewModel = ViewModelProviders.of(this, new DetailedFavoriteMovieViewModelFactory(this.getApplication(), detailedMovieId)).get(DetailedFavoriteMovieViewModel.class);
                // Buat observer object untuk mendisplay data ke UI
                // Buat Observer untuk detailedMovieInfo
                Observer<ArrayList<MovieItem>> detailedFavoriteMovieObserver = createDetailedFavoriteMovieObserver();
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
            // Set visibility of views ketika sedang dalam meretrieve data
            detailedInfoContent.setVisibility(View.INVISIBLE);
            detailedProgressBar.setVisibility(View.VISIBLE);
            detailedEmptyInfoTextView.setVisibility(View.GONE);
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
                Observer<ArrayList<TvShowItem>> detailedFavoriteTvShowObserver = createDetailedFavoriteTvShowObserver();
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
            // Method tsb triggered ketika data changed dan gunanya itu untuk display data
            @Override
            public void onChanged(@Nullable ArrayList<MovieItem> movieItems) {
                // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
                // yang menandakan bahwa loadingnya sudah selesai
                detailedInfoContent.setVisibility(View.VISIBLE);
                detailedProgressBar.setVisibility(View.GONE);

                // Cek jika array list tidak null
                if(movieItems != null){
                    // Load image jika ada poster path ke dalam image view
                    Picasso.get().load(baseImageUrl + movieItems.get(0).getMoviePosterPath()).into(detailedItemPosterImage);

                    // Cek jika ada value dari variable movie title
                    if(movieItems.get(0).getMovieTitle() != null && !movieItems.get(0).getMovieTitle().isEmpty()){
                        detailedFirstInfoTextView.setText(movieItems.get(0).getMovieTitle());
                    } else {
                        detailedFirstInfoTextView.setText(getString(R.string.detailed_movie_unknown_title)); // Set unknown placeholder value jika tidak ada value dari variable
                    }

                    // Set textview content in detailed movie rating to contain a variety of different colors
                    Spannable ratingWord = new SpannableString(getString(R.string.span_movie_item_ratings) + " ");
                    ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    detailedSecondInfoTextView.setText(ratingWord);

                    // Cek jika ada value dari variable movie ratings
                    if(movieItems.get(0).getMovieRatings() != null && !movieItems.get(0).getMovieRatings().isEmpty()){
                        Spannable ratingDetailedMovie = new SpannableString(movieItems.get(0).getMovieRatings());
                        ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedSecondInfoTextView.append(ratingDetailedMovie);
                    } else {
                        Spannable ratingDetailedMovie = new SpannableString(getString(R.string.detailed_movie_default_value_ratings)); // Set default value menjadi 0
                        ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedSecondInfoTextView.append(ratingDetailedMovie);
                    }

                    // Set textview content in detailed movie runtime to contain a variety of different colors
                    Spannable runtimeWord = new SpannableString(getString(R.string.span_movie_detail_runtime) + " ");
                    runtimeWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, runtimeWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    detailedThirdInfoTextView.setText(runtimeWord);

                    // Cek jika ada value dari variable movie runtime
                    if(movieItems.get(0).getMovieRuntime() != null && !movieItems.get(0).getMovieRuntime().isEmpty()){
                        Spannable runtimeDetailedMovie = new SpannableString(movieItems.get(0).getMovieRuntime() + " ");
                        runtimeDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, runtimeDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedThirdInfoTextView.append(runtimeDetailedMovie);

                        Spannable runtimeDetailedMovieMinutes = new SpannableString(getString(R.string.span_movie_detail_runtime_minutes));
                        runtimeDetailedMovieMinutes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, runtimeDetailedMovieMinutes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedThirdInfoTextView.append(runtimeDetailedMovieMinutes);
                    } else {
                        Spannable runtimeDetailedMovie = new SpannableString(getString(R.string.detailed_movie_unknown_runtime));
                        runtimeDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, runtimeDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedThirdInfoTextView.append(runtimeDetailedMovie);
                    }

                    // Set title yg isinya original language
                    detailedFourthInfoTitleTextView.setText(getString(R.string.detailed_movie_original_language_title));

                    // Cek jika ada value dari variable movie original language
                    if(movieItems.get(0).getMovieOriginalLanguage() != null && !movieItems.get(0).getMovieOriginalLanguage().isEmpty()){
                        detailedFourthInfoTextView.setText(movieItems.get(0).getMovieOriginalLanguage());
                    } else {
                        detailedFourthInfoTextView.setText(getString(R.string.detailed_movie_unknown_original_language));
                    }

                    // Set title yg isinya release date
                    detailedFifthInfoTitleTextView.setText(getString(R.string.detailed_movie_release_date_title));

                    // Cek jika ada value dari variable movie release date
                    if(movieItems.get(0).getMovieReleaseDate() != null && !movieItems.get(0).getMovieReleaseDate().isEmpty()){
                        detailedFifthInfoTextView.setText(movieItems.get(0).getMovieReleaseDate());
                    } else {
                        detailedFifthInfoTextView.setText(getString(R.string.detailed_movie_unknown_release_date));
                    }

                    // Set title yg isinya movie overview
                    detailedSixthInfoTitleTextView.setText(getString(R.string.detailed_movie_overview_title));

                    // Cek jika ada value dari variable movie overview
                    if(movieItems.get(0).getMovieOverview() != null && !movieItems.get(0).getMovieOverview().isEmpty()){
                        detailedSixthInfoTextView.setText(movieItems.get(0).getMovieOverview());
                    } else {
                        detailedSixthInfoTextView.setText(getString(R.string.detailed_movie_unknown_overview));
                    }

                    // Cek jika ga ada uri
                    if(uri == null){
                        // Set custom class value bedasarkan object pertama di ArrayList
                        detailedMovieItem = movieItems.get(0);
                    }

                    // Set menu clickable into true, literally setelah asynctask kelar,
                    // maka menu bs d click
                    menuClickable = true;
                    // Update option menu to recall onPrepareOptionMenu method
                    invalidateOptionsMenu();
                }
            }
        };
    }

    // Buat observer method untuk favorite TV Show yang gunanya untuk return observer object
    private Observer<ArrayList<TvShowItem>> createDetailedFavoriteTvShowObserver(){
        return new Observer<ArrayList<TvShowItem>>() {
            // Method tsb triggered ketika data changed dan gunanya itu untuk display data
            @Override
            public void onChanged(@Nullable ArrayList<TvShowItem> tvShowItems) {
                // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
                // yang menandakan bahwa loadingnya sudah selesai
                detailedInfoContent.setVisibility(View.VISIBLE);
                detailedProgressBar.setVisibility(View.GONE);

                // Cek jika array list exist, ini gunanya untuk mencegah null pointer exception
                if(tvShowItems != null){
                    // Load image jika ada poster path ke dalam image view
                    Picasso.get().load(baseImageUrl + tvShowItems.get(0).getTvShowPosterPath()).into(detailedItemPosterImage);

                    // Cek jika ada value dari variable tv show name
                    if(tvShowItems.get(0).getTvShowName() != null && !tvShowItems.get(0).getTvShowName().isEmpty()){
                        detailedFirstInfoTextView.setText(tvShowItems.get(0).getTvShowName());
                    } else {
                        detailedFirstInfoTextView.setText(getString(R.string.detailed_tv_show_unknown_name)); // Set unknown placeholder value jika tidak ada value dari variable
                    }

                    // Set textview content in detailed tv show rating to contain a variety of different colors
                    Spannable ratingWord = new SpannableString(getString(R.string.span_tv_show_item_ratings) + " ");
                    ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    detailedSecondInfoTextView.setText(ratingWord);

                    // Cek jika ada value dari variable tv show ratings
                    if(tvShowItems.get(0).getTvShowRatings() != null && !tvShowItems.get(0).getTvShowRatings().isEmpty()){
                        Spannable ratingDetailedTvShow = new SpannableString(tvShowItems.get(0).getTvShowRatings());
                        ratingDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedSecondInfoTextView.append(ratingDetailedTvShow);
                    } else {
                        Spannable ratingDetailedTvShow = new SpannableString(getString(R.string.detailed_tv_show_default_value_ratings)); // Set default value menjadi 0
                        ratingDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedSecondInfoTextView.append(ratingDetailedTvShow);
                    }

                    // Set textview content in detailed movie runtime to contain a variety of different colors
                    Spannable runtimeWord = new SpannableString(getString(R.string.span_tv_show_detail_runtime_episodes) + " ");
                    runtimeWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, runtimeWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    detailedThirdInfoTextView.setText(runtimeWord);

                    // Cek jika ada value dari variable tv show runtime episodes
                    if(tvShowItems.get(0).getTvShowRuntimeEpisodes() != null && !tvShowItems.get(0).getTvShowRuntimeEpisodes().isEmpty()){
                        Spannable runtimeDetailedTvShow = new SpannableString(tvShowItems.get(0).getTvShowRuntimeEpisodes() + " ");
                        runtimeDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, runtimeDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedThirdInfoTextView.append(runtimeDetailedTvShow);

                        Spannable runtimeDetailedTvShowMinutes = new SpannableString(getString(R.string.span_tv_show_detail_runtime_episodes_minutes));
                        runtimeDetailedTvShowMinutes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, runtimeDetailedTvShowMinutes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedThirdInfoTextView.append(runtimeDetailedTvShowMinutes);
                    } else {
                        Spannable runtimeDetailedTvShow = new SpannableString(getString(R.string.detailed_tv_show_unknown_episodes_runtime));
                        runtimeDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, runtimeDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        detailedThirdInfoTextView.append(runtimeDetailedTvShow);
                    }

                    // Set title yg isinya original language
                    detailedFourthInfoTitleTextView.setText(getString(R.string.detailed_tv_show_original_language_title));

                    // Cek jika ada value dari variable tv show original language
                    if(tvShowItems.get(0).getTvShowOriginalLanguage() != null && !tvShowItems.get(0).getTvShowOriginalLanguage().isEmpty()){
                        detailedFourthInfoTextView.setText(tvShowItems.get(0).getTvShowOriginalLanguage());
                    } else {
                        detailedFourthInfoTextView.setText(getString(R.string.detailed_tv_show_unknown_original_language));
                    }

                    // Set title yg isinya first air date
                    detailedFifthInfoTitleTextView.setText(getString(R.string.detailed_tv_show_first_air_date_title));

                    // Cek jika ada value dari variable tv show first air date
                    if(tvShowItems.get(0).getTvShowFirstAirDate() != null && !tvShowItems.get(0).getTvShowFirstAirDate().isEmpty()){
                        detailedFifthInfoTextView.setText(tvShowItems.get(0).getTvShowFirstAirDate());
                    } else {
                        detailedFifthInfoTextView.setText(getString(R.string.detailed_tv_show_unknown_first_air_date));
                    }

                    // Set title yg isinya tv show overview
                    detailedSixthInfoTitleTextView.setText(getString(R.string.detailed_tv_show_overview_title));

                    // Cek jika ada value dari variable tv show overview
                    if(tvShowItems.get(0).getTvShowOverview() != null && !tvShowItems.get(0).getTvShowOverview().isEmpty()){
                        detailedSixthInfoTextView.setText(tvShowItems.get(0).getTvShowOverview());
                    } else {
                        detailedSixthInfoTextView.setText(getString(R.string.detailed_tv_show_unknown_overview));
                    }

                    // Cek jika ga ada uri yang exist
                    if(uri == null){
                        // Set custom class value bedasarkan object pertama di ArrayList
                        detailedTvShowItem = tvShowItems.get(0);
                    }

                    menuClickable = true;

                    invalidateOptionsMenu();
                }

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
            if(detailedTvShowFavoriteStateValue == 1){
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
        // Cek untuk menu item id yang tepat
        switch (item.getItemId()){
            case R.id.action_marked_as_favorite:
                if(accessItemMode.equals("open_movie_detail")){
                    // Check for current state for drawable menu icon
                    if(detailedMovieFavoriteStateValue != 1){
                        // Change icon into marked as favorite
                        drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_on;
                        // Set value state into marked as favorite
                        detailedMovieFavoriteStateValue = 1;
                        // Set current date value into MovieItem, where MovieItem added into Favorite
                        detailedMovieItem.setDateAddedFavorite(getCurrentDate());
                        // Set boolean state value into MovieItem
                        detailedMovieItem.setFavoriteBooleanState(detailedMovieFavoriteStateValue);

                        // Cek jika value dari detailedMovieFavoriteStateValue sama dengan value bawaan intent dengan key FAVORITE_MOVIE_BOOLEAN_STATE_DATA
                        changedState = detailedMovieFavoriteStateValue != detailedMovieFavoriteStateValueComparison; // is changed true jika value favorite state bawaan intent itu tidak sama dengan value favorite state

                        // Initiate ContentValues object
                        ContentValues favoriteMovieColumnValues = new ContentValues();
                        // Put column values in content values
                        favoriteMovieColumnValues.put(_ID, detailedMovieItem.getMovieId());
                        favoriteMovieColumnValues.put(MOVIE_TITLE_COLUMN, detailedMovieItem.getMovieTitle());
                        favoriteMovieColumnValues.put(MOVIE_RATINGS_COLUMN, detailedMovieItem.getMovieRatings());
                        favoriteMovieColumnValues.put(MOVIE_ORIGINAL_LANGUAGE_COLUMN, detailedMovieItem.getMovieOriginalLanguage());
                        favoriteMovieColumnValues.put(MOVIE_RELEASE_DATE_COLUMN, detailedMovieItem.getMovieReleaseDate());
                        favoriteMovieColumnValues.put(MOVIE_FILE_PATH_COLUMN, detailedMovieItem.getMoviePosterPath());
                        favoriteMovieColumnValues.put(MOVIE_DATE_ADDED_FAVORITE_COLUMN, detailedMovieItem.getDateAddedFavorite());
                        favoriteMovieColumnValues.put(MOVIE_FAVORITE_COLUMN, detailedMovieItem.getFavoriteBooleanState());

                        // Cek jika ada pergantian state dari sebuah data
                        if(changedState){
                            uri = contentResolver.insert(MOVIE_FAVORITE_CONTENT_URI, favoriteMovieColumnValues); // Insert column into content provider through content resolver and return uri
                            Log.d("movie uri", String.valueOf(uri));
                            Log.d("insert movie item", "inserted movie item");
                            detailedMovieFavoriteStateValueComparison = 1; // Ganti value untuk mengupdate comparison
                            // Notify change when data is inserted
                            contentResolver.notifyChange(MOVIE_FAVORITE_CONTENT_URI, new FavoriteMovieDataObserver(new Handler(), DetailActivity.this)); // Notify change on content resolver based on database URI, which is then go to content provider
                        }

                        // Update option menu
                        invalidateOptionsMenu();
                    } else { // Code ini dieksekusi jika value statenya itu sama dengan 1
                        // Change icon into unmarked as favorite
                        drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_off;
                        // Set value state into unmarked as favorite
                        detailedMovieFavoriteStateValue = 0;
                        // Set boolean state value into MovieItem
                        detailedMovieItem.setFavoriteBooleanState(detailedMovieFavoriteStateValue);

                        // Cek jika value dari detailedMovieFavoriteStateValue sama dengan value bawaan intent dengan key FAVORITE_MOVIE_BOOLEAN_STATE_DATA
                        changedState = detailedMovieFavoriteStateValue != detailedMovieFavoriteStateValueComparison; // is changed true jika value favorite state bawaan intent itu tidak sama dengan value favorite state
                        // Cek jika ada pergantian state dari sebuah data
                        if(changedState){
                            Log.d("movie uri", String.valueOf(uri));
                            // Cek jika ada uri
                            if(uri != null){
                                contentResolver.delete(uri, null, null); // Delete column based on inserted URI item into content provider through content resolver
                                Log.d("delete movie item", "deleted movie item");
                                detailedMovieFavoriteStateValueComparison = 0; // Ganti value untuk comparison value, agar dapat menghandle changed state boolean
                                contentResolver.notifyChange(MOVIE_FAVORITE_CONTENT_URI, new FavoriteMovieDataObserver(new Handler(), DetailActivity.this)); // Notify change on content resolver based on database URI, which is then go to content provider
                            }
                        }

                        // Update option menu
                        invalidateOptionsMenu();
                    }
                } else if(accessItemMode.equals("open_tv_show_detail")){ // Statement code jika mode intentnya berada di TV Show
                    if(detailedTvShowFavoriteStateValue != 1){
                        // Change icon into marked as favorite
                        drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_on;
                        // Set value state into marked as favorite
                        detailedTvShowFavoriteStateValue = 1;
                        // Set current date value into TvShowItem, where TvShowItem added into Favorite
                        detailedTvShowItem.setDateAddedFavorite(getCurrentDate());
                        // Set boolean state value into TvShowItem
                        detailedTvShowItem.setFavoriteBooleanState(detailedTvShowFavoriteStateValue);

                        // Cek jika value dari detailedMovieFavoriteStateValue sama dengan value bawaan intent dengan key FAVORITE_TV_SHOW_BOOLEAN_STATE_DATA
                        changedState = detailedTvShowFavoriteStateValue != detailedTvShowFavoriteStateValueComparison; // is changed true jika value favorite state bawaan intent itu tidak sama dengan value favorite state

                        // Initiate ContentValues object
                        ContentValues favoriteTvShowColumnValues = new ContentValues();
                        // Put column values in content values
                        favoriteTvShowColumnValues.put(_ID, detailedTvShowItem.getTvShowId());
                        favoriteTvShowColumnValues.put(TV_SHOW_NAME_COLUMN, detailedTvShowItem.getTvShowName());
                        favoriteTvShowColumnValues.put(TV_SHOW_RATINGS_COLUMN, detailedTvShowItem.getTvShowRatings());
                        favoriteTvShowColumnValues.put(TV_SHOW_ORIGINAL_LANGUAGE_COLUMN, detailedTvShowItem.getTvShowOriginalLanguage());
                        favoriteTvShowColumnValues.put(TV_SHOW_FIRST_AIR_DATE_COLUMN, detailedTvShowItem.getTvShowFirstAirDate());
                        favoriteTvShowColumnValues.put(TV_SHOW_FILE_PATH_COLUMN, detailedTvShowItem.getTvShowPosterPath());
                        favoriteTvShowColumnValues.put(TV_SHOW_DATE_ADDED_COLUMN, detailedTvShowItem.getDateAddedFavorite());
                        favoriteTvShowColumnValues.put(TV_SHOW_FAVORITE_COLUMN, detailedTvShowItem.getFavoriteBooleanState());

                        // Cek jika ada pergantian state dari sebuah data
                        if(changedState){
                            uri = contentResolver.insert(TV_SHOW_FAVORITE_CONTENT_URI, favoriteTvShowColumnValues);
                            Log.d("tv show uri", String.valueOf(uri));
                            Log.d("insert tv show item", "inserted tv show item");
                            detailedTvShowFavoriteStateValueComparison = 1; // Ganti value untuk mengupdate comparison
                            contentResolver.notifyChange(TV_SHOW_FAVORITE_CONTENT_URI, new FavoriteTvShowDataObserver(new Handler(), DetailActivity.this)); // Notify change on content resolver based on database URI, which is then go to content provider
                        }

                        // Update option menu
                        invalidateOptionsMenu();
                    } else {
                        // Change icon into unmarked as favorite
                        drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favorite_toggle_off;
                        // Set value state into unmarked as favorite
                        detailedTvShowFavoriteStateValue = 0;
                        // Set boolean state value into TvShowItem
                        detailedTvShowItem.setFavoriteBooleanState(detailedTvShowFavoriteStateValue);
                        // Cek jika value dari detailedTvShowFavoriteStateValue sama dengan value bawaan intent dengan key TV_SHOW_BOOLEAN_STATE_EXTRA
                        changedState = detailedTvShowFavoriteStateValue != detailedTvShowFavoriteStateValueComparison; // is changed true jika value favorite state bawaan intent itu tidak sama dengan value favorite state
                        // Cek jika ada pergantian state dari sebuah data
                        if(changedState){
                            Log.d("tv show uri", String.valueOf(uri));
                            // Cek jika urinya itu ada
                            if(uri != null){
                                contentResolver.delete(uri, null, null);
                                Log.d("delete tv show item", "deleted tv show item");
                                detailedTvShowFavoriteStateValueComparison = 0; // Ganti value untuk mengupdate comparison
                                contentResolver.notifyChange(TV_SHOW_FAVORITE_CONTENT_URI, new FavoriteTvShowDataObserver(new Handler(), DetailActivity.this)); // Notify change on content resolver based on database URI, which is then go to content provider
                            }
                        }

                        // Update option menu
                        invalidateOptionsMenu();
                    }
                }
                break;
            case R.id.home:
                // Finish method untuk membawa Intent ke MainActivity
                finish();
                break;
            default:
                break;
        }
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
