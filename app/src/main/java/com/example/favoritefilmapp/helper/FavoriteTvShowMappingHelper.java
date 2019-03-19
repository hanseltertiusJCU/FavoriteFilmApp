package com.example.favoritefilmapp.helper;

import android.database.Cursor;

import com.example.favoritefilmapp.entity.TvShowItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_NAME_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN;

public class FavoriteTvShowMappingHelper {
    // Method ini berguna untuk memindahkan cursor ke ArrayList
    public static ArrayList<TvShowItem> mapCursorToFavoriteTvShowArrayList(Cursor favoriteTvShowItemsCursor){
        // Array list yang di return
        ArrayList<TvShowItem> favoriteTvShowItemList = new ArrayList<>();

        // Cek jika masih ada data di cursor
        while (favoriteTvShowItemsCursor.moveToNext()){
            // Get column values
            int tvShowId = favoriteTvShowItemsCursor.getInt(favoriteTvShowItemsCursor.getColumnIndexOrThrow(_ID));
            String tvShowName = favoriteTvShowItemsCursor.getString(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_NAME_COLUMN));
            String tvShowRatings = favoriteTvShowItemsCursor.getString(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_RATINGS_COLUMN));
            String tvShowOriginalLanguage = favoriteTvShowItemsCursor.getString(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_ORIGINAL_LANGUAGE_COLUMN));
            String tvShowFirstAirDate = favoriteTvShowItemsCursor.getString(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_FIRST_AIR_DATE_COLUMN));
            String tvShowPosterPath = favoriteTvShowItemsCursor.getString(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_FILE_PATH_COLUMN));
            String tvShowDateAddedFavorite = favoriteTvShowItemsCursor.getString(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_DATE_ADDED_COLUMN));
            int tvShowBooleanState = favoriteTvShowItemsCursor.getInt(favoriteTvShowItemsCursor.getColumnIndexOrThrow(TV_SHOW_FAVORITE_COLUMN));
            // Add TvShowItem to arraylist, use constructor that takes variables
            favoriteTvShowItemList.add(new TvShowItem(tvShowId, tvShowName, tvShowRatings, tvShowOriginalLanguage, tvShowFirstAirDate, tvShowPosterPath, tvShowDateAddedFavorite, tvShowBooleanState));
        }
        
        // Return arraylist
        return favoriteTvShowItemList;
    }
}
