package com.example.favoritefilmapp.entity;

import android.database.Cursor;

import com.example.favoritefilmapp.db.FavoriteDatabaseContract;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.provider.BaseColumns._ID;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.getColumnInt;
import static com.example.favoritefilmapp.db.FavoriteDatabaseContract.getColumnString;

public class TvShowItem {
    // Nilai dari value untuk TvShowItem
    private int tvShowId;
    private String tvShowName;
    private String tvShowRuntimeEpisodes;
    private String tvShowRatings;
    private String tvShowOriginalLanguage;
    private String tvShowFirstAirDate;
    private String tvShowOverview;
    private String tvShowPosterPath;
    // Nilai untuk mengetahui waktu dimana sebuah data di add menjadi favorite
    private String dateAddedFavorite;
    // Nilai untuk tahu bahwa tv show item itu termasuk dalam kategori favorit ato tidak
    private int favoriteBooleanState;

    // Constrcutor untuk menampung JSONObject then set data
    public TvShowItem(JSONObject jsonObject){
        try {
            // Get JSON object fields
            int dataId = jsonObject.getInt("id");
            String dataName = jsonObject.getString("name");
            JSONArray dataEpisodesRuntimeArray = jsonObject.getJSONArray("episode_run_time");
            String dataRuntimeEpisodes = null;
            if(dataEpisodesRuntimeArray.length() > 0){
                dataRuntimeEpisodes = dataEpisodesRuntimeArray.getString(0); // retrieve value from episode_run_time JSON Array
            }
            String dataRatings = jsonObject.getString("vote_average");
            String dataOriginalLanguage = jsonObject.getString("original_language");
            // Ubah original language menjadi upper case
            String displayedOriginalLanguage = dataOriginalLanguage.toUpperCase();
            String dataFirstAirDate = jsonObject.getString("first_air_date");
            String dataOverview = jsonObject.getString("overview");
            String dataPosterPath = jsonObject.getString("poster_path");

            // Set values bedasarkan variable-variable yang merepresentasikan object
            this.tvShowId = dataId;
            this.tvShowName = dataName;
            this.tvShowRuntimeEpisodes = dataRuntimeEpisodes;
            this.tvShowRatings = dataRatings;
            this.tvShowOriginalLanguage = displayedOriginalLanguage;
            this.tvShowFirstAirDate = dataFirstAirDate;
            this.tvShowOverview = dataOverview;
            this.tvShowPosterPath = dataPosterPath;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Empty constructor
    public TvShowItem(){

    }

    // Constructor untuk menampung value columns yang ada
    public TvShowItem(int tvShowId, String tvShowName, String tvShowRatings, String tvShowOriginalLanguage, String tvShowFirstAirDate, String tvShowPosterPath, String dateAddedFavorite, int favoriteBooleanState) {
        this.tvShowId = tvShowId;
        this.tvShowName = tvShowName;
        this.tvShowRatings = tvShowRatings;
        this.tvShowOriginalLanguage = tvShowOriginalLanguage;
        this.tvShowFirstAirDate = tvShowFirstAirDate;
        this.tvShowPosterPath = tvShowPosterPath;
        this.dateAddedFavorite = dateAddedFavorite;
        this.favoriteBooleanState = favoriteBooleanState;
    }

    // Constructor untuk menampung Cursor
    public TvShowItem(Cursor cursor){
        // Method ini berguna untuk set variable values yang ada di column values
        this.tvShowId = getColumnInt(cursor, _ID);
        this.tvShowName = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME);
        this.tvShowRatings = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN);
        this.tvShowOriginalLanguage = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN);
        this.tvShowFirstAirDate = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN);
        this.tvShowPosterPath = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN);
        this.dateAddedFavorite = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN);
        this.favoriteBooleanState = getColumnInt(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN);
    }

    // Getter untuk semua attribute
    public int getTvShowId() {
        return tvShowId;
    }

    public String getTvShowName() {
        return tvShowName;
    }

    public String getTvShowRuntimeEpisodes() {
        return tvShowRuntimeEpisodes;
    }

    public String getTvShowRatings() {
        return tvShowRatings;
    }

    public String getTvShowOriginalLanguage() {
        return tvShowOriginalLanguage;
    }

    public String getTvShowFirstAirDate() {
        return tvShowFirstAirDate;
    }

    public String getTvShowOverview() {
        return tvShowOverview;
    }

    public String getTvShowPosterPath() {
        return tvShowPosterPath;
    }

    public String getDateAddedFavorite() {
        return dateAddedFavorite;
    }

    public int getFavoriteBooleanState() {
        return favoriteBooleanState;
    }

    // Setter untuk favorite boolean state di tv show
    public void setFavoriteBooleanState(int favoriteBooleanState) {
        this.favoriteBooleanState = favoriteBooleanState;
    }
}
