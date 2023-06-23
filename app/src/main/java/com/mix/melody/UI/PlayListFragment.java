package com.mix.melody.UI;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mix.melody.AdapterClass.PlaylistViewAdapter;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.R;
import com.mix.melody.databinding.AddPlaylistDialogBinding;
import com.mix.melody.databinding.FragmentPlayListBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class PlayListFragment extends Fragment {
    public PlayListFragment() {

    }
    FragmentPlayListBinding binding;
    public static Music.MusicPlaylist musicPlaylist ;
    private   PlaylistViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPlayListBinding.inflate(inflater, container, false);

        binding.playlistRV.setHasFixedSize(true);
        binding.playlistRV.setItemViewCacheSize(13);
        binding.playlistRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        if (musicPlaylist == null) {
            musicPlaylist = new Music.MusicPlaylist();
        }
        adapter = new PlaylistViewAdapter(getActivity(), musicPlaylist.ref);
        binding.playlistRV.setAdapter(adapter);
        binding.AllSongs.setText("All Playlist : " + adapter.getItemCount());

         binding.addPlaylistBtn.setOnClickListener(v -> customAlertDialog());
        return binding.getRoot();

    }
        private void customAlertDialog() {
            View customDialog = LayoutInflater.from(getActivity()).inflate(R.layout.add_playlist_dialog, binding.getRoot(), false);
            AddPlaylistDialogBinding binder = AddPlaylistDialogBinding.bind(customDialog);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
            AlertDialog dialog = builder.setView(customDialog)
                    .setTitle("Playlist Details")
                    .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Editable playlistName = binder.playlistName.getText();
                            Editable createdBy = binder.yourName.getText();
                            if (playlistName != null && createdBy != null) {
                                if (playlistName.toString().length() > 0 && createdBy.toString().length() > 0) {
                                    addPlaylist(playlistName.toString(), createdBy.toString());
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    }).create();
            dialog.show();


        }


    private void addPlaylist(String name, String createdBy) {
        boolean playlistExists = false;
        for (Music.Playlist i : musicPlaylist.ref) {
            if (name.equals(i.name)) {
                playlistExists = true;
                break;
            }
        }
        if (playlistExists) {
            Toast.makeText(getActivity(), "Playlist Exist!!", Toast.LENGTH_SHORT).show();
        } else {
            Music.Playlist tempPlaylist = new Music.Playlist();
            tempPlaylist.name = name;
            tempPlaylist.playlist = new ArrayList<>();
            tempPlaylist.createdBy = createdBy;
            Date calendar = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            tempPlaylist.createdOn = sdf.format(calendar);
            musicPlaylist.ref.add(tempPlaylist);
            adapter.refreshPlaylist();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}