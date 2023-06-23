package com.mix.melody.AdapterClass;

import static com.mix.melody.AdapterClass.AllSongAdapter.formatDuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mix.melody.ActivityUI.PlaySongActivity;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.R;
import com.mix.melody.databinding.FavouriteViewBinding;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyHolder> {

    private Context context;
    private ArrayList<Music> musicList;
    private boolean playNext;

    public FavouriteAdapter(Context context, ArrayList<Music> musicList, boolean playNext) {
        this.context = context;
        this.musicList = musicList;
        this.playNext = playNext;
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView Name,SongAlbum,SongDuration;
        private View root;

        public MyHolder(FavouriteViewBinding binding) {
            super(binding.getRoot());
            image = binding.imageMV;
            Name = binding.songNameFV;
            root = binding.getRoot();
            SongAlbum =binding.songAlbumMV;
            SongDuration =binding.songDuration;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.Name.setText(musicList.get(position).getTitle());
        holder.SongAlbum.setText(musicList.get(position).getAlbum());
        holder.SongDuration.setText(formatDuration(musicList.get(position).getDuration()));
        Glide.with(context)
                .load(musicList.get(position).getArtUri())
                .apply(new RequestOptions().placeholder(R.drawable.baseline_library_music_24).centerCrop())
                .into(holder.image);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlaySongActivity.class);
                    intent.putExtra("index", position);
                    intent.putExtra("class", "FavouriteAdapter");
                    ContextCompat.startActivity(context, intent, null);
                }
            });
        //when play next music is clicked
        if (playNext) {
            holder.root.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaySongActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("class", "PlayNext");
                ContextCompat.startActivity(context, intent, null);
            });
        }

    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFavourites(ArrayList<Music> newList) {
        musicList = new ArrayList<>();
        musicList.addAll(newList);
        notifyDataSetChanged();
    }

}