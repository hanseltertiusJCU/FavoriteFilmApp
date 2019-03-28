package com.example.favoritefilmapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.favoritefilmapp.BuildConfig;
import com.example.favoritefilmapp.R;
import com.example.favoritefilmapp.entity.TvShowItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TvShowItemAdapter extends RecyclerView.Adapter<TvShowItemAdapter.TvShowItemViewHolder> {
    // Initiate variables
    private ArrayList<TvShowItem> mTvShowItemData = new ArrayList<>();
    private Context context;

    // Constructor that takes up Context
    public TvShowItemAdapter(Context context) {
        this.context = context;
    }

    // Getter untuk dapatin arraylist data
    public ArrayList<TvShowItem> getTvShowItemData() {
        return mTvShowItemData;
    }

    // Get context
    private Context getContext() {
        return context;
    }

    // Set data into arraylist global variable
    public void setTvShowItemData(ArrayList<TvShowItem> mTvShowItemData) {
        // Clear existing array list content
        this.mTvShowItemData.clear();
        // Add everything (parameter) into array list
        this.mTvShowItemData.addAll(mTvShowItemData);
        // Notify that the data in adapter has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TvShowItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Set layout xml yang berisi favorite tv show items ke View
        View favoriteTvShowItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_tv_show_items, viewGroup, false);
        // Return TvShowItemViewHolder object dengan memanggil constructor yg membawa view
        return new TvShowItemViewHolder(favoriteTvShowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowItemViewHolder tvShowItemViewHolder, int position) {
        // Load image jika ada poster path
        String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
        Picasso.get().load(baseImageUrl + mTvShowItemData.get(position).getTvShowPosterPath()).into(tvShowItemViewHolder.imageViewFavoriteTvShowPoster);

        // Set text view
        tvShowItemViewHolder.textViewFavoriteTvShowName.setText(mTvShowItemData.get(position).getTvShowName());

        // Set text view span (bagiannya adalah: title dan content) untuk rating
        Spannable ratingFavoriteTvShowItemWord = new SpannableString(context.getString(R.string.span_tv_show_item_ratings) + " ");
        ratingFavoriteTvShowItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFavoriteTvShowItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowItemViewHolder.textViewFavoriteTvShowRatings.setText(ratingFavoriteTvShowItemWord);
        Spannable ratingFavoriteTvShowItem = new SpannableString(mTvShowItemData.get(position).getTvShowRatings());
        ratingFavoriteTvShowItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, ratingFavoriteTvShowItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowItemViewHolder.textViewFavoriteTvShowRatings.append(ratingFavoriteTvShowItem);

        // Set text view span (bagiannya adalah: title dan content) untuk first air date
        Spannable firstAirDateFavoriteTvShowItemWord = new SpannableString(context.getString(R.string.span_tv_show_item_first_air_date) + " ");
        firstAirDateFavoriteTvShowItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, firstAirDateFavoriteTvShowItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowItemViewHolder.textViewFavoriteTvShowFirstAirDate.setText(firstAirDateFavoriteTvShowItemWord);
        Spannable firstAirDateFavoriteTvShowItem = new SpannableString(mTvShowItemData.get(position).getTvShowFirstAirDate());
        firstAirDateFavoriteTvShowItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, firstAirDateFavoriteTvShowItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowItemViewHolder.textViewFavoriteTvShowFirstAirDate.append(firstAirDateFavoriteTvShowItem);

        // Set text view span (bagiannya adalah: title dan content) untuk first air date
        Spannable originalLanguageFavoriteTvShowItemWord = new SpannableString(context.getString(R.string.span_tv_show_item_language) + " ");
        originalLanguageFavoriteTvShowItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, originalLanguageFavoriteTvShowItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowItemViewHolder.textViewFavoriteTvShowOriginalLanguage.setText(originalLanguageFavoriteTvShowItemWord);
        Spannable originalLanguageFavoriteTvShowItem = new SpannableString(mTvShowItemData.get(position).getTvShowOriginalLanguage());
        originalLanguageFavoriteTvShowItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, originalLanguageFavoriteTvShowItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowItemViewHolder.textViewFavoriteTvShowOriginalLanguage.append(originalLanguageFavoriteTvShowItem);
    }

    // Get array list size
    @Override
    public int getItemCount() {
        return getTvShowItemData().size();
    }

    class TvShowItemViewHolder extends RecyclerView.ViewHolder {
        // Assign view by binding view
        @BindView(R.id.poster_image)
        ImageView imageViewFavoriteTvShowPoster;
        @BindView(R.id.tv_show_name_text)
        TextView textViewFavoriteTvShowName;
        @BindView(R.id.tv_show_ratings_text)
        TextView textViewFavoriteTvShowRatings;
        @BindView(R.id.tv_show_first_air_date_text)
        TextView textViewFavoriteTvShowFirstAirDate;
        @BindView(R.id.tv_show_language_text)
        TextView textViewFavoriteTvShowOriginalLanguage;

        TvShowItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assign view di dalam constructor
            ButterKnife.bind(this, itemView);
        }
    }
}
