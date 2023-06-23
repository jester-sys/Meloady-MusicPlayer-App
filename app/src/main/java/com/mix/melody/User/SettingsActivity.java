package com.mix.melody.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.mix.melody.UI.HomeFragment;
import com.mix.melody.databinding.ActivitySettingsBinding;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        binding.sortBy.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String[] menuList = {"Recently Added", "Song Title", "File Size"};
                final int[] currentSort = {HomeFragment.sortOrder};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Sorting")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit();
                                editor.putInt("sortOrder", currentSort[0]);
                                editor.apply();
                            }
                        })
                        .setSingleChoiceItems(menuList, currentSort[0], new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentSort[0] = which;
                            }
                        });
                AlertDialog customDialog = builder.create();
                customDialog.show();


                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);

            }
        });


binding.termAndCondition.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SettingsActivity.this,TermAndCondionActivity.class);
        startActivity(intent);
    }
});


    }
}