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

import com.mix.melody.databinding.ActivityProfileBinding;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=  ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");

        SharedPreferences preferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        preferences.getString("UserName" , binding.ProfileName.toString());
        String userName = preferences.getString("UserName", "");
        binding.ProfileName.setText(userName);
        editor.apply();


        binding.FeedbackAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,FeedbackActivity.class);
                startActivity(intent);
            }
        });
        binding.AboutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        binding.Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
//                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor1 = sharedPreferences.edit();
//                editor.putBoolean("flag",false);
//                editor.apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Remove")
                        .setMessage("Do you want to Logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences sharedPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
                               @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                 editor.putBoolean("flag",false);
                                 Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                 startActivity(intent);
                                   editor.apply();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog customDialog = builder.create();
                customDialog.show();
                            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);



            }
        });
        binding.Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }
}