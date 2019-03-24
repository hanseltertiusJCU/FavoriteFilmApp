package com.example.favoritefilmapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.favoritefilmapp.BuildConfig;
import com.example.favoritefilmapp.DetailActivity;
import com.example.favoritefilmapp.LoadFavoriteMoviesCallback;
import com.example.favoritefilmapp.MainActivity;
import com.example.favoritefilmapp.R;
import com.example.favoritefilmapp.adapter.MovieItemAdapter;
import com.example.favoritefilmapp.async.LoadFavoriteMoviesAsync;
import com.example.favoritefilmapp.entity.MovieItem;
import com.example.favoritefilmapp.support.RecyclerViewItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;

public class FavoriteMovieFragment extends Fragment implements LoadFavoriteMoviesCallback {

    // Initiate view yang diperlukan di fragment favorite
    @BindView(R.id.rv_favorite_movie_item_list)
    RecyclerView recyclerViewFavoriteMovieItems;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.favorite_movie_empty_state_data_text)
    TextView textViewFavoriteMovieEmptyState;
    // Set movie adapter class
    MovieItemAdapter movieItemAdapter;
    // Initiate Swipe to refresh layout
    @BindView(R.id.fragment_movie_swipe_refresh_layout)
    SwipeRefreshLayout fragmentMovieSwipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_movie, container, false);
        ButterKnife.bind(this, view); // Bind view dari fragment layout ke fragment class
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set layout manager into RecyclerView
        recyclerViewFavoriteMovieItems.setLayoutManager(new LinearLayoutManager(getContext()));
        // Buat ukuaran dari masing-masing item di RecyclerView menjadi sama
        recyclerViewFavoriteMovieItems.setHasFixedSize(true);

        // Initialize movie adapter
        movieItemAdapter = new MovieItemAdapter(getContext());
        // Notify when the data in adapter changed
        movieItemAdapter.notifyDataSetChanged();

        // Attach adapter ke RecyclerView agar bisa menghandle orientation changes
        recyclerViewFavoriteMovieItems.setAdapter(movieItemAdapter);

        // Set background color untuk recycler view
        recyclerViewFavoriteMovieItems.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Cek jika context itu ada
        if(getContext() != null){
            // Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.item_divider)));
            // Set divider untuk RecyclerView items
            recyclerViewFavoriteMovieItems.addItemDecoration(itemDecorator);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Cek jika bundle savedInstanceState itu ada
        if(savedInstanceState != null) {
            // Retrieve array list parcelable
            final ArrayList<MovieItem> movieItemList = savedInstanceState.getParcelableArrayList(BuildConfig.FAVORITE_MOVIE_LIST_STATE);
            // Cek jika array list itu ada
            if (movieItemList != null) {
                // Cek jika array list itu ada datanya
                if (movieItemList.size() > 0) {
                    // Hilangkan progress bar agar tidak ada progress bar lagi setelah d rotate
                    progressBar.setVisibility(View.GONE);
                    recyclerViewFavoriteMovieItems.setVisibility(View.VISIBLE);
                    // Set data ke adapter
                    movieItemAdapter.setMovieItemData(movieItemList);
                    // Set item click listener di dalam recycler view agar item tsb dapat di click
                    RecyclerViewItemClickSupport.addClickSupportToView(recyclerViewFavoriteMovieItems).setOnRecyclerViewItemClickListener(new RecyclerViewItemClickSupport.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                            // Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
                            showSelectedFavoriteMovieItems(movieItemList.get(position));
                        }
                    });
                } else {
                    // Ketika tidak ada data untuk display, set RecyclerView ke
                    // invisible dan progress bar menjadi tidak ada
                    movieItemAdapter.setMovieItemData(movieItemList);
                    progressBar.setVisibility(View.GONE);
                    recyclerViewFavoriteMovieItems.setVisibility(View.INVISIBLE);
                    // Set empty view visibility into visible
                    textViewFavoriteMovieEmptyState.setVisibility(View.VISIBLE);
                    // Set empty view text
                    textViewFavoriteMovieEmptyState.setText(getString(R.string.no_favorite_movie_data_shown));
                }
            }
        }

        // Cek jika ada activity
        if(getActivity() != null){
            // Connectivity manager untuk mengecek state dari network connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            // Network Info object untuk melihat ada data network yang aktif
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            // Cek jika ada network connection
            if(networkInfo != null && networkInfo.isConnected()){
                // Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
                new LoadFavoriteMoviesAsync(getActivity(), this).execute();
            } else {
                // Progress bar into gone and recycler view into invisible as the data finished on loading
                progressBar.setVisibility(View.GONE);
                recyclerViewFavoriteMovieItems.setVisibility(View.INVISIBLE);
                // Set empty view visibility into visible
                textViewFavoriteMovieEmptyState.setVisibility(View.VISIBLE);
                // Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
                textViewFavoriteMovieEmptyState.setText(getString(R.string.no_internet_connection));
            }
        }

        // Set refresh listener untuk menghandle refresh event
        fragmentMovieSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Line ini berguna ketika fragment sedang di refresh
            @Override
            public void onRefresh() {
                // Cek jika ada activity
                if(getActivity() != null){
                    // Connectivity manager untuk mengecek state dari network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    // Network Info object untuk melihat ada data network yang aktif
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    // Cek jika ada network connection
                    if(networkInfo != null && networkInfo.isConnected()){
                        // Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
                        new LoadFavoriteMoviesAsync(getActivity(), FavoriteMovieFragment.this).execute();
                    } else {
                        // Progress bar into gone and recycler view into invisible as the data finished on loading
                        progressBar.setVisibility(View.GONE);
                        recyclerViewFavoriteMovieItems.setVisibility(View.INVISIBLE);
                        // Set empty view visibility into visible
                        textViewFavoriteMovieEmptyState.setVisibility(View.VISIBLE);
                        // Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
                        textViewFavoriteMovieEmptyState.setText(getString(R.string.no_internet_connection));
                    }
                }
                // Set refresh jadi false menandakan bahwa datanya sudah di load
                fragmentMovieSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Method tsb berguna untuk membawa value dari Intent ke Activity tujuan serta memanggil Activity tujuan
    private void showSelectedFavoriteMovieItems(MovieItem favoriteMovieItem){
        // Dapatkan id dan title bedasarkan item di ArrayList
        int favoriteMovieIdItem = favoriteMovieItem.getMovieId();
        String favoriteMovieTitleItem = favoriteMovieItem.getMovieTitle();
        int favoriteMovieBooleanStateItem = favoriteMovieItem.getFavoriteBooleanState();
        // Tentukan bahwa kita ingin membuka data Movie
        String favoriteModeItem = "open_movie_detail";
        // Create URI untuk bawa URI ke data di intent dengan row id value
        // content://com.example.cataloguemoviefinal/favorite_movies/id
        Uri favoriteMovieUriItem = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/" + favoriteMovieIdItem);
        // Initiate intent
        Intent intentWithFavoriteMovieIdData = new Intent(getContext(), DetailActivity.class);
        // Bawa data untuk disampaikan ke {@link DetailActivity}
        intentWithFavoriteMovieIdData.putExtra(BuildConfig.FAVORITE_MOVIE_ID_DATA, favoriteMovieIdItem);
        intentWithFavoriteMovieIdData.putExtra(BuildConfig.FAVORITE_MOVIE_TITLE_DATA, favoriteMovieTitleItem);
        intentWithFavoriteMovieIdData.putExtra(BuildConfig.FAVORITE_MOTIE_BOOLEAN_STATE_DATA, favoriteMovieBooleanStateItem);
        intentWithFavoriteMovieIdData.putExtra(BuildConfig.FAVORITE_MODE_INTENT, favoriteModeItem);
        // Set URI into intent
        intentWithFavoriteMovieIdData.setData(favoriteMovieUriItem);
        startActivity(intentWithFavoriteMovieIdData);
    }

    @Override
    public void favoriteMoviePreExecute() {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Set visibility of views ketika sedang dalam memuat data
                    recyclerViewFavoriteMovieItems.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    textViewFavoriteMovieEmptyState.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void favoriteMoviePostExecute(Cursor favoriteMovieItems) {
        // Cek jika array list ada data
        if(MainActivity.favoriteMovieItemArray.size() > 0){
            // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
            // yang menandakan bahwa loadingnya sudah selesai
            progressBar.setVisibility(View.GONE);
            recyclerViewFavoriteMovieItems.setVisibility(View.VISIBLE);
            // Set empty view visibility into gone
            textViewFavoriteMovieEmptyState.setVisibility(View.GONE);
            // Set data into adapter
            movieItemAdapter.setMovieItemData(MainActivity.favoriteMovieItemArray);
            // Set item click listener di dalam recycler view
            RecyclerViewItemClickSupport.addClickSupportToView(recyclerViewFavoriteMovieItems).setOnRecyclerViewItemClickListener(new RecyclerViewItemClickSupport.OnRecyclerViewItemClickListener() {
                // Implement interface method
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                    showSelectedFavoriteMovieItems(MainActivity.favoriteMovieItemArray.get(position)); // Call method untuk pindah ke detail activity bedasarkan MovieItem object di certain position yang ada di ArrayList
                }
            });
        } else {
            // Ketika tidak ada data untuk display, set RecyclerView ke
            // invisible dan progress bar menjadi tidak ada
            movieItemAdapter.setMovieItemData(MainActivity.favoriteMovieItemArray); // Set empty data ke adapter
            progressBar.setVisibility(View.GONE);
            recyclerViewFavoriteMovieItems.setVisibility(View.INVISIBLE);
            // Set empty view visibility into visible, krn merepresentasikan empty data
            textViewFavoriteMovieEmptyState.setVisibility(View.VISIBLE);
            // Set empty view text
            textViewFavoriteMovieEmptyState.setText(getString(R.string.no_favorite_movie_data_shown));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Put ArrayList into Bundle for handling orientation change
        outState.putParcelableArrayList(BuildConfig.FAVORITE_MOVIE_LIST_STATE, movieItemAdapter.getMovieItemData());
    }

}
