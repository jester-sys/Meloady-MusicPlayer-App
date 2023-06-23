package com.mix.melody.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mix.melody.databinding.ActivityTermAndCondionBinding;

import java.util.Objects;

public class TermAndCondionActivity extends AppCompatActivity {
    ActivityTermAndCondionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermAndCondionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Privacy Policy");

    }
}

