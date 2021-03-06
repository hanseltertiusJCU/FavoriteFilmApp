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
import com.example.favoritefilmapp.LoadFavoriteTvShowCallback;
import com.example.favoritefilmapp.MainActivity;
import com.example.favoritefilmapp.R;
import com.example.favoritefilmapp.adapter.TvShowItemAdapter;
import com.example.favoritefilmapp.async.LoadFavoriteTvShowAsync;
import com.example.favoritefilmapp.entity.TvShowItem;
import com.example.favoritefilmapp.support.RecyclerViewItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;

public class FavoriteTvShowFragment extends Fragment implements LoadFavoriteTvShowCallback {

    // Initiate view yang diperlukan di fragment favorite
    @BindView(R.id.rv_favorite_tv_show_item_list)
    RecyclerView recyclerViewFavoriteTvShowItems;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.favorite_tv_show_empty_state_data_text)
    TextView textViewFavoriteTvShowEmptyState;
    // Set tv show adapter class
    TvShowItemAdapter tvShowItemAdapter;
    // Initiate Swipe to refresh layout
    @BindView(R.id.fragment_tv_show_swipe_refresh_layout)
    SwipeRefreshLayout fragmentTvShowSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_tv_show, container, false);
        ButterKnife.bind(this, view); // Bind view dari fragment layout ke fragment class
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize tv show adapter
        tvShowItemAdapter = new TvShowItemAdapter(getContext());
        // Notify when the data in adapter changed
        tvShowItemAdapter.notifyDataSetChanged();

        // Set layout manager into RecyclerView
        recyclerViewFavoriteTvShowItems.setLayoutManager(new LinearLayoutManager(getContext()));
        // Buat ukuaran dari masing-masing item di RecyclerView menjadi sama
        recyclerViewFavoriteTvShowItems.setHasFixedSize(true);

        // Attach adapter ke RecyclerView agar bisa menghandle orientation changes
        recyclerViewFavoriteTvShowItems.setAdapter(tvShowItemAdapter);

        // Set background color untuk recycler view
        recyclerViewFavoriteTvShowItems.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Cek jika context itu ada
        if (getContext() != null) {
            // Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.item_divider)));
            // Set divider untuk RecyclerView items
            recyclerViewFavoriteTvShowItems.addItemDecoration(itemDecorator);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Cek jika bundle savedInstanceState itu ada
        if (savedInstanceState != null) {
            // Retrieve array list parcelable untuk retrieve scroll position
            final ArrayList<TvShowItem> tvShowItemsList = savedInstanceState.getParcelableArrayList(BuildConfig.FAVORITE_TV_SHOW_LIST_STATE);
            // Cek jika array list exist
            if (tvShowItemsList != null) {
                if (tvShowItemsList.size() > 0) {
                    // Hilangkan progress bar agar tidak ada progress bar lagi setelah d rotate
                    progressBar.setVisibility(View.GONE);
                    recyclerViewFavoriteTvShowItems.setVisibility(View.VISIBLE);
                    // Set empty view visibility into gone
                    textViewFavoriteTvShowEmptyState.setVisibility(View.GONE);
                    // Set data ke adapter
                    tvShowItemAdapter.setTvShowItemData(tvShowItemsList);
                    // Set item click listener di dalam recycler view agar item tsb dapat di click
                    RecyclerViewItemClickSupport.addClickSupportToView(recyclerViewFavoriteTvShowItems).setOnRecyclerViewItemClickListener(new RecyclerViewItemClickSupport.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                            // Panggil method showSelectedTvShowItems untuk mengakses DetailActivity bedasarkan data yang ada
                            showSelectedFavoriteTvShowItems(tvShowItemsList.get(position));
                        }
                    });
                } else {
                    // Ketika tidak ada data untuk display, set RecyclerView ke
                    // invisible dan progress bar menjadi tidak ada
                    tvShowItemAdapter.setTvShowItemData(tvShowItemsList);
                    progressBar.setVisibility(View.GONE);
                    recyclerViewFavoriteTvShowItems.setVisibility(View.INVISIBLE);
                    // Set empty view visibility into visible
                    textViewFavoriteTvShowEmptyState.setVisibility(View.VISIBLE);
                    // Set empty text
                    textViewFavoriteTvShowEmptyState.setText(getString(R.string.no_favorite_tv_show_data_shown));
                }
            }
        }

        // Cek jika ada activity
        if (getActivity() != null) {
            // Connectivity manager untuk mengecek state dari network connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            // Network Info object untuk melihat ada data network yang aktif
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            // Cek jika ada network connection
            if (networkInfo != null && networkInfo.isConnected()) {
                // Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
                new LoadFavoriteTvShowAsync(getActivity(), this).execute();
            } else {
                // Progress bar into gone and recycler view into invisible as the data finished on loading
                progressBar.setVisibility(View.GONE);
                recyclerViewFavoriteTvShowItems.setVisibility(View.INVISIBLE);
                // Set empty text view visibility into visible
                textViewFavoriteTvShowEmptyState.setVisibility(View.VISIBLE);
                // Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
                textViewFavoriteTvShowEmptyState.setText(getString(R.string.no_internet_connection));
            }
        }

        // Set on refresh listener on fragment tv show
        fragmentTvShowSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Cek jika ada activity
                if (getActivity() != null) {
                    // Connectivity manager untuk mengecek state dari network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    // Network Info object untuk melihat ada data network yang aktif
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    // Cek jika ada network connection
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
                        new LoadFavoriteTvShowAsync(getActivity(), FavoriteTvShowFragment.this).execute();
                    } else {
                        // Progress bar into gone and recycler view into invisible as the data finished on loading
                        progressBar.setVisibility(View.GONE);
                        recyclerViewFavoriteTvShowItems.setVisibility(View.INVISIBLE);
                        // Set empty text view visibility into visible
                        textViewFavoriteTvShowEmptyState.setVisibility(View.VISIBLE);
                        // Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
                        textViewFavoriteTvShowEmptyState.setText(getString(R.string.no_internet_connection));
                    }
                }
                // Set refresh jadi false menandakan bahwa datanya sudah di load
                fragmentTvShowSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Method tsb berguna untuk membawa value dari Intent ke Activity tujuan serta memanggil Activity tujuan
    private void showSelectedFavoriteTvShowItems(TvShowItem favoriteTvShowItem) {
        // Dapatkan id dan title bedasarkan item di ArrayList
        int favoriteTvShowIdItem = favoriteTvShowItem.getTvShowId();
        String favoriteTvShowNameItem = favoriteTvShowItem.getTvShowName();
        int favoriteTvShowBooleanStateItem = favoriteTvShowItem.getFavoriteBooleanState();
        // Tentukan bahwa kita ingin membuka data Tv Show
        String favoriteModeItem = "open_tv_show_detail";
        // Create URI untuk bawa URI ke data di intent dengan row id value
        // content://com.example.cataloguemoviefinal/favorite_tv_shows/id
        Uri favoriteTvShowUriItem = Uri.parse(TV_SHOW_FAVORITE_CONTENT_URI + "/" + favoriteTvShowIdItem);
        // Initiate intent
        Intent intentWithFavoriteTvShowIdData = new Intent(getContext(), DetailActivity.class);
        // Bawa data untuk disampaikan ke {@link DetailActivity}
        intentWithFavoriteTvShowIdData.putExtra(BuildConfig.FAVORITE_TV_SHOW_ID_DATA, favoriteTvShowIdItem);
        intentWithFavoriteTvShowIdData.putExtra(BuildConfig.FAVORITE_TV_SHOW_TITLE_DATA, favoriteTvShowNameItem);
        intentWithFavoriteTvShowIdData.putExtra(BuildConfig.FAVORITE_TV_SHOW_BOOLEAN_STATE, favoriteTvShowBooleanStateItem);
        intentWithFavoriteTvShowIdData.putExtra(BuildConfig.FAVORITE_MODE_INTENT, favoriteModeItem);
        // Set URI into intent
        intentWithFavoriteTvShowIdData.setData(favoriteTvShowUriItem);
        // Start activity to detail activity
        startActivity(intentWithFavoriteTvShowIdData);
    }

    @Override
    public void favoriteTvShowPreExecute() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Set visibility of views ketika sedang dalam memuat data
                    recyclerViewFavoriteTvShowItems.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    textViewFavoriteTvShowEmptyState.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void favoriteTvShowPostExecute(Cursor favoriteTvShowItems) {
        // Cek jika array list ada data
        if (MainActivity.favoriteTvShowItemArray.size() > 0) {
            // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
            // yang menandakan bahwa loadingnya sudah selesai
            progressBar.setVisibility(View.GONE);
            recyclerViewFavoriteTvShowItems.setVisibility(View.VISIBLE);
            // Set recycler view scroll position ke paling awal
            recyclerViewFavoriteTvShowItems.smoothScrollToPosition(0);
            // Set empty view visibility into gone
            textViewFavoriteTvShowEmptyState.setVisibility(View.GONE);
            // Set data into adapter
            tvShowItemAdapter.setTvShowItemData(MainActivity.favoriteTvShowItemArray);
            // Set item click listener di dalam recycler view
            RecyclerViewItemClickSupport.addClickSupportToView(recyclerViewFavoriteTvShowItems).setOnRecyclerViewItemClickListener(new RecyclerViewItemClickSupport.OnRecyclerViewItemClickListener() {
                // Implement interface method
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                    showSelectedFavoriteTvShowItems(MainActivity.favoriteTvShowItemArray.get(position)); // Call method untuk pindah ke detail activity bedasarkan MovieItem object di certain position yang ada di ArrayList
                }
            });
        } else {
            // Ketika tidak ada data untuk display, set RecyclerView ke
            // invisible dan progress bar menjadi tidak ada
            tvShowItemAdapter.setTvShowItemData(MainActivity.favoriteTvShowItemArray);
            progressBar.setVisibility(View.GONE);
            recyclerViewFavoriteTvShowItems.setVisibility(View.INVISIBLE);
            // Set empty view visibility into visible, krn merepresentasikan empty data
            textViewFavoriteTvShowEmptyState.setVisibility(View.VISIBLE);
            // Set empty view text
            textViewFavoriteTvShowEmptyState.setText(getString(R.string.no_favorite_tv_show_data_shown));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Put ArrayList into Bundle for handling orientation change
        outState.putParcelableArrayList(BuildConfig.FAVORITE_TV_SHOW_LIST_STATE, tvShowItemAdapter.getTvShowItemData());
    }

}
