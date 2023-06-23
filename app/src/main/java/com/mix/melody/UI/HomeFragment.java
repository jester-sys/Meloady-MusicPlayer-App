package com.mix.melody.UI;





import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mix.melody.ActivityUI.PlaySongActivity;

import com.mix.melody.AdapterClass.AllSongAdapter;

import com.mix.melody.CustomClass.Music;

import com.mix.melody.MainActivity;
import com.mix.melody.databinding.FragmentHomeBinding;

import java.io.File;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class HomeFragment extends Fragment {




    public HomeFragment() {
    }

    private FragmentHomeBinding binding;
    public static ArrayList<Music> MusicListMV = new ArrayList<>();
    public static Music music;

    public static AllSongAdapter songAdapter;

     public static int sortOrder =0 ;
    String[] sortingList = { MediaStore.Audio.Media.DATE_ADDED + " DESC",
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC" };

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);


        RequestPermision();



        FavouriteFragment.favouriteSongs=  new ArrayList<Music>();
        SharedPreferences editor = getActivity().getSharedPreferences("FAVOURITES", MODE_PRIVATE);
        String jsonString = editor.getString("FavouriteSongs", null);
        Type typeToken = new TypeToken<ArrayList<Music>>(){}.getType();
        if (jsonString != null) {
            ArrayList<Music> data = new GsonBuilder().create().fromJson(jsonString, typeToken);
            FavouriteFragment.favouriteSongs.addAll(data);
        }
        PlayListFragment.musicPlaylist =new Music.MusicPlaylist();
        String jsonStringPlaylist = editor.getString("MusicPlaylist", null);
        if (jsonStringPlaylist != null) {
            Music.MusicPlaylist dataPlaylist = new GsonBuilder().create().fromJson(jsonStringPlaylist, Music.MusicPlaylist.class);
            PlayListFragment.musicPlaylist= dataPlaylist;
        }
        MainActivity.search = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MusicListMV = getAllAudio();
        }

        binding.HomeRecycler.setHasFixedSize(true);
        binding.HomeRecycler.setItemViewCacheSize(13);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.HomeRecycler.setLayoutManager(linearLayoutManager);
        songAdapter = new AllSongAdapter(getActivity(), MusicListMV,false,false);
        binding.HomeRecycler.setAdapter(songAdapter);

        binding.AllSongs.setText("Play All : " + songAdapter.getItemCount());

        binding.AllSongs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),PlaySongActivity.class);
            intent.putExtra("index",0);
            intent.putExtra("class","Shuffle");
            startActivity(intent);
        });
        songAdapter.notifyDataSetChanged();
        return binding.getRoot();

    }


    @SuppressLint({"Recycle", "Range"})
    @RequiresApi(api = Build.VERSION_CODES.R)
    private ArrayList<Music> getAllAudio() {
        ArrayList<Music> tempList = new ArrayList<>();
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = requireActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortingList[sortOrder],
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    long durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String albumIdC = String.valueOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    Uri uri = Uri.parse("content://media/external/audio/albumart");
                    String artUriC = Uri.withAppendedPath(uri, albumIdC).toString();
                    Music music = new Music(idC, titleC, albumC, artistC, pathC, durationC, artUriC);
                    File file = new File(music.getPath());
                    if (file.exists()) {
                        tempList.add(music);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return tempList;
    }


    private void RequestPermision() {
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
             updateAdapter();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit();
        String jsonString = new GsonBuilder().create().toJson(FavouriteFragment.favouriteSongs);
        editor.putString("FavouriteSongs", jsonString);
        String jsonStringPlaylist = new GsonBuilder().create().toJson(PlayListFragment.musicPlaylist);
        editor.putString("MusicPlaylist", jsonStringPlaylist);
        editor.apply();

        SharedPreferences sortEditor = requireActivity().getSharedPreferences("SORTING", MODE_PRIVATE);
        int sortValue = sortEditor.getInt("sortOrder", 0);
        if(sortOrder != sortValue) {
            sortOrder = sortValue;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MusicListMV = getAllAudio();
            }
            songAdapter.updateMusicList(MusicListMV);


        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!PlaySongActivity.isPlaying && PlaySongActivity.musicService != null) {
            PlaySongActivity.musicService.stopForeground(true);
            PlaySongActivity.musicService.mediaPlayer.release();
            PlaySongActivity.musicService = null;
            System.exit(1);
            super.onDestroy();


        }


    }

    private void updateAdapter() {
        if (songAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MusicListMV = getAllAudio();
            }
            songAdapter.updateMusicList(MusicListMV);
            binding.AllSongs.setText("Play All: " + songAdapter.getItemCount());
        }
    }


}

