package com.mix.melody.ActivityUI;

import static com.mix.melody.CustomClass.Music.checkPlaylist;
import static com.mix.melody.CustomClass.Music.getImgArt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.GsonBuilder;
import com.mix.melody.AdapterClass.AllSongAdapter;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.MainActivity;
import com.mix.melody.R;
import com.mix.melody.UI.PlayListFragment;
import com.mix.melody.databinding.ActivityPlaylistDetailsBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;


public class PlaylistDetailsActivity extends AppCompatActivity {

    ActivityPlaylistDetailsBinding binding; // Binding class for the activity layout
    private AllSongAdapter adapter; // Adapter for the RecyclerView
    public static int currentPlaylistPos = -1; // Variable to store the current playlist position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the current playlist position from the intent's extras
        currentPlaylistPos = Objects.requireNonNull(getIntent().getExtras()).getInt("index", -1);

        try {
            // Check and remove duplicate songs from the playlist
            PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist = checkPlaylist(PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist);
        } catch (Exception ignored) {
        }

        // Set up the RecyclerView
        binding.playlistDetailsRV.setItemViewCacheSize(10);
        binding.playlistDetailsRV.setHasFixedSize(true);
        binding.playlistDetailsRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AllSongAdapter(this, PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist, true, false);
        binding.playlistDetailsRV.setAdapter(adapter);

        // Shuffle button click listener
        binding.shuffleBtnPD.setOnClickListener(view -> {
            // Start the PlaySongActivity in shuffle mode
            Intent intent = new Intent(this, PlaySongActivity.class);
            intent.putExtra("index", 0);
            intent.putExtra("class", "PlaylistDetailsShuffle");
            startActivity(intent);
        });

        // Add button click listener
        binding.addBtnPD.setOnClickListener(view -> startActivity(new Intent(this, selectionActivity.class)));

        // Remove all button click listener
        binding.removeAllPD.setOnClickListener(view -> {
            // Show a confirmation dialog to remove all songs from the playlist
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Remove")
                    .setMessage("Do you want to remove all songs from the playlist?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear the playlist and refresh the adapter
                        PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist.clear();
                        adapter.refreshPlaylist();
                        dialog.dismiss();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog customDialog = builder.create();
            customDialog.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if the required data is null or empty
        if (PlayListFragment.musicPlaylist == null || PlayListFragment.musicPlaylist.ref == null || PlayListFragment.musicPlaylist.ref.isEmpty()) {
            return; // Exit early if the required data is null or empty
        }

        // Update the playlist details based on the current playlist position
        if (currentPlaylistPos >= 0 && currentPlaylistPos < PlayListFragment.musicPlaylist.ref.size()) {
            // Set the playlist name and information text
            binding.moreInfoPD.setText(PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).name);
            binding.moreInfoPD.setText("Total " + adapter.getItemCount() + " Songs.\n\n" +
                    "Created On:\n" + PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).createdOn + "\n\n" +
                    "  -- " + PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).createdBy);

            if (adapter.getItemCount() > 0) {
                // Load the image for the first song in the playlist
                Glide.with(this)
                        .load(PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist.get(0).getArtUri())
                        .apply(new RequestOptions().placeholder(R.drawable.icon_music).centerCrop())
                        .into(binding.playlistImgPD);

                // Get the dominant color from the image and set the background and status bar colors
                int position = 0; // Set the desired position to 1 (assuming it's a valid index)
                byte[] img = getImgArt(PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist.get(position).getPath());
                Bitmap image;
                if (img != null) {
                    image = BitmapFactory.decodeByteArray(img, 0, img.length);
                } else {
                    image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music);
                }

                int dominantColor = getDominantColor(image);
                setFragmentBackground(dominantColor);
                setStatusBarColor(dominantColor);
            }
        }
        binding.shuffleBtnPD.setVisibility(View.VISIBLE);

        adapter.notifyDataSetChanged();

        // Save the updated music playlist to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit();
        String jsonStringPlaylist = new GsonBuilder().create().toJson(PlayListFragment.musicPlaylist);
        editor.putString("MusicPlaylist", jsonStringPlaylist);
        editor.apply();
    }

    // Set the background gradient color
    private Drawable setFragmentBackground(int dominantColor) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{0xFFFFFF, dominantColor});
        binding.getRoot().setBackground(gradient);
        return null;
    }

    // Set the status bar color
    private void setStatusBarColor(int color) {
        Window window = getWindow();
        if (window != null) {
            window.setStatusBarColor(color);
        }
    }

    // Get the dominant color from a bitmap image
    private int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            return Color.RED;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int red = 0;
        int green = 0;
        int blue = 0;

        // Calculate the total red, green, and blue values of all pixels
        for (int pixel : pixels) {
            red += Color.red(pixel);
            green += Color.green(pixel);
            blue += Color.blue(pixel);
        }

        // Calculate the average red, green, and blue values
        red /= size;
        green /= size;
        blue /= size;

        // Return the RGB color value
        return Color.rgb(red, green, blue);
    }

    // Remove duplicate songs from the playlist
    private ArrayList<Music> checkPlaylist(ArrayList<Music> playlist) {
        HashSet<String> songIds = new HashSet<>();
        ArrayList<Music> newPlaylist = new ArrayList<>();

        for (Music song : playlist) {
            if (!songIds.contains(song.getId())) {
                songIds.add(song.getId());
                newPlaylist.add(song);
            }
        }

        return newPlaylist;
    }
}
