package com.mix.melody.ActivityUI;

import static com.mix.melody.ActivityUI.PlaylistDetailsActivity.currentPlaylistPos;
import static com.mix.melody.MainActivity.MusicListSearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mix.melody.AdapterClass.AllSongAdapter;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.MainActivity;
import com.mix.melody.databinding.ActivitySelectionBinding;
import com.mix.melody.UI.HomeFragment;

import java.util.ArrayList;
import java.util.Objects;

public class selectionActivity extends AppCompatActivity {

    public static ActivitySelectionBinding binding;
    AllSongAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.selectionRV.setItemViewCacheSize(30);
        binding.selectionRV.setHasFixedSize(true);
        binding.selectionRV.setLayoutManager(new LinearLayoutManager(this));



        adapter = new AllSongAdapter(selectionActivity.this, HomeFragment.MusicListMV, false, true);
        binding.selectionRV.setAdapter(adapter);
        binding.searchViewSA.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MusicListSearch = new ArrayList<>();
                if (newText != null) {
                    String userInput = newText.toLowerCase();
                    for (Music song : HomeFragment.MusicListMV) {
                        if (song.getTitle().toLowerCase().contains(userInput)) {
                            MusicListSearch.add(song);
                        }
                    }
                    MainActivity.search = true;
                    adapter.updateMusicList(MusicListSearch);
                }
                return true;
            }
        });
        binding.check.setOnClickListener(v -> {
            Intent intent = new Intent(selectionActivity.this, PlaylistDetailsActivity.class);
            intent.putExtra("index", currentPlaylistPos);
            startActivity(intent);
            finish();

        });


}}