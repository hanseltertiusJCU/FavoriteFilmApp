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
import com.example.favoritefilmapp.entity.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.MovieItemViewHolder> {

    private ArrayList<MovieItem> mMovieItemData = new ArrayList<>();
    private Context context;

    // Constructor that takes up Context
    public MovieItemAdapter(Context context){
        this.context = context;
    }

    // Getter untuk dapatin arraylist data
    public ArrayList<MovieItem> getMovieItemData() {
        return mMovieItemData;
    }

    // Get context
    private Context getContext(){
        return context;
    }

    // Set data into arraylist global variable
    public void setMovieItemData(ArrayList<MovieItem> mMovieItemData){
        // Clear existing array list content
        this.mMovieItemData.clear();
        // Add everything (parameter) into array list
        this.mMovieItemData.addAll(mMovieItemData);
        // Notify that the data in adapter has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Set layout xml yang berisi favorite movie items ke View
        View favoriteMovieItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_movie_items, viewGroup, false);
        // Return MovieItemViewHolder object dengan memanggil constructor yg membawa view
        return new MovieItemViewHolder(favoriteMovieItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieItemViewHolder movieItemViewHolder, int position) {
        // Load image jika ada poster path
        String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
        Picasso.get().load(baseImageUrl + mMovieItemData.get(position).getMoviePosterPath()).into(movieItemViewHolder.imageViewFavoriteMoviePoster);

        // Set text view
        movieItemViewHolder.textViewFavoriteMovieTitle.setText(mMovieItemData.get(position).getMovieTitle());

        // Set text view span (bagiannya adalah: title dan content) untuk rating
        Spannable ratingFavoriteMovieItemWord = new SpannableString(context.getString(R.string.span_movie_item_ratings) + " ");
        ratingFavoriteMovieItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFavoriteMovieItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        movieItemViewHolder.textViewFavoriteMovieRatings.setText(ratingFavoriteMovieItemWord);
        Spannable ratingFavoriteMovieItem = new SpannableString(mMovieItemData.get(position).getMovieRatings());
        ratingFavoriteMovieItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, ratingFavoriteMovieItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        movieItemViewHolder.textViewFavoriteMovieRatings.append(ratingFavoriteMovieItem);

        // Set text view span (bagiannya adalah: title dan content) untuk release date
        Spannable releaseDateFavoriteMovieItemWord = new SpannableString(context.getString(R.string.span_movie_item_release_date) + " ");
        releaseDateFavoriteMovieItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, releaseDateFavoriteMovieItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        movieItemViewHolder.textViewFavoriteMovieReleaseDate.setText(releaseDateFavoriteMovieItemWord);
        Spannable releaseDateFavoriteMovieItem = new SpannableString(mMovieItemData.get(position).getMovieReleaseDate());
        releaseDateFavoriteMovieItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, releaseDateFavoriteMovieItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        movieItemViewHolder.textViewFavoriteMovieReleaseDate.append(releaseDateFavoriteMovieItem);

        // Set text view span (bagiannya adalah: title dan content) untuk original language
        Spannable originalLanguageFavoriteMovieItemWord = new SpannableString(context.getString(R.string.span_movie_item_language) + " ");
        originalLanguageFavoriteMovieItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, originalLanguageFavoriteMovieItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        movieItemViewHolder.textViewFavoriteMovieOriginalLanguage.setText(originalLanguageFavoriteMovieItemWord);
        Spannable originalLanguageFavoriteMovieItem = new SpannableString(mMovieItemData.get(position).getMovieOriginalLanguage());
        originalLanguageFavoriteMovieItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, originalLanguageFavoriteMovieItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        movieItemViewHolder.textViewFavoriteMovieOriginalLanguage.append(originalLanguageFavoriteMovieItem);

    }

    // Get array list size
    @Override
    public int getItemCount() {
        return getMovieItemData().size();
    }

    // Kelas ini berguna untuk menampung view yang ada tanpa mendeclare view di adapter
    class MovieItemViewHolder extends RecyclerView.ViewHolder{
        // Assign view by binding view
        @BindView(R.id.poster_image)
        ImageView imageViewFavoriteMoviePoster;
        @BindView(R.id.movie_title_text)
        TextView textViewFavoriteMovieTitle;
        @BindView(R.id.movie_ratings_text)
        TextView textViewFavoriteMovieRatings;
        @BindView(R.id.movie_release_date_text)
        TextView textViewFavoriteMovieReleaseDate;
        @BindView(R.id.movie_language_text)
        TextView textViewFavoriteMovieOriginalLanguage;

        MovieItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assign view di dalam constructor
            ButterKnife.bind(this, itemView);
        }
    }
}
