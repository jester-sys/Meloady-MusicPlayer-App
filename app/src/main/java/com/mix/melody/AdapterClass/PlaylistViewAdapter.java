package com.mix.melody.AdapterClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mix.melody.ActivityUI.PlaylistDetailsActivity;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.R;
import com.mix.melody.UI.PlayListFragment;
import com.mix.melody.databinding.PlaylistViewBinding;

import java.util.ArrayList;

public class PlaylistViewAdapter extends RecyclerView.Adapter<PlaylistViewAdapter.MyHolder> {

    private final Context context;
    private ArrayList<Music.Playlist> playlistList;

    public PlaylistViewAdapter(Context context, ArrayList<Music.Playlist> playlistList) {
        this.context = context;
        this.playlistList = playlistList;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public final PlaylistViewBinding binding;

        public MyHolder(PlaylistViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PlaylistViewBinding binding = PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
//        if(MainActivity.themeIndex == 4){
//            holder.binding.getRoot().setStrokeColor(ContextCompat.getColor(context, R.color.white));
//        }
        holder.binding.playlistName.setText(playlistList.get(position).name);
        holder.binding.playlistName.setSelected(true);
        holder.binding.playlistDeleteBtn.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle(playlistList.get(position).name)
                    .setMessage("Do you want to delete playlist?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        PlayListFragment.musicPlaylist.ref.remove(position);
                        refreshPlaylist();
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog customDialog = builder.create();
            customDialog.show();

//       setDialogBtnBackground(context, customDialog);
        });
        holder.binding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(context, PlaylistDetailsActivity.class);
            intent.putExtra("index", position);
            ContextCompat.startActivity(context, intent, null);
        });
        if(PlayListFragment.musicPlaylist.ref.get(position).playlist.size() > 0){
            Glide.with(context)
                    .load(PlayListFragment.musicPlaylist.ref.get(position).playlist.get(0).getArtUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.icon_music).centerCrop())
                    .into(holder.binding.playlistImg);
        }

    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshPlaylist() {
        playlistList = new ArrayList<>(PlayListFragment.musicPlaylist.ref);
        notifyDataSetChanged();
    }
}