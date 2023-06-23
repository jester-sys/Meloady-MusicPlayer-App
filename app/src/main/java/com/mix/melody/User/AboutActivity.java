package com.mix.melody.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mix.melody.R;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).setTitle("About");

    }
}