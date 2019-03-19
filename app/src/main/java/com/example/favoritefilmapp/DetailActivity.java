package com.example.favoritefilmapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    // Request code
    public static final int REQUEST_CHANGE = 100;
    // Result code
    public static final int RESULT_CHANGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}
