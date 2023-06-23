package com.mix.melody.AdapterClass;

import static androidx.core.util.TimeUtils.formatDuration;

import static com.mix.melody.ActivityUI.PlaylistDetailsActivity.currentPlaylistPos;
import static com.mix.melody.ActivityUI.selectionActivity.binding;
import static com.mix.melody.UI.HomeFragment.music;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.mix.melody.ActivityUI.PlaySongActivity;

import com.mix.melody.ActivityUI.PlaylistDetailsActivity;
import com.mix.melody.ActivityUI.selectionActivity;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.MainActivity;
import com.mix.melody.R;
import com.mix.melody.UI.PlayListFragment;
import com.mix.melody.UI.PlayNextFragment;
import com.mix.melody.databinding.AllsongHomeLayoutBinding;
import com.mix.melody.databinding.DetailsViewBinding;
import com.mix.melody.databinding.MoreFeaturesBinding;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class AllSongAdapter extends RecyclerView.Adapter<AllSongAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Music> musicList;
    Boolean PlaylistDetials;
    Boolean SlectionActivity;
    public AllSongAdapter(Context context, ArrayList<Music> musicList, Boolean PlaylistDetials, Boolean SlectionActivity) {
        this.SlectionActivity = SlectionActivity;
        this.PlaylistDetials = PlaylistDetials;
        this.context = context;
        this.musicList = musicList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(AllsongHomeLayoutBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(musicList.get(position).getTitle());
        holder.album.setText(musicList.get(position).getAlbum());
        holder.duration.setText(formatDuration(musicList.get(position).getDuration()));


        Glide.with(context)
                .load(musicList.get(position).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_library_music_24))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Enable disk caching
                .into(holder.image);

        if (!SlectionActivity) {
            holder.binding.root.setOnLongClickListener(view -> {
                View customDialog = LayoutInflater.from(context).inflate(R.layout.more_features, holder.binding.root, false);
                MoreFeaturesBinding bindingMF = MoreFeaturesBinding.bind(customDialog);

                AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                        .setView(customDialog)
                        .create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x99000000));

                bindingMF.AddToPNBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (PlayNextFragment.playNextList.isEmpty()) {
                                PlayNextFragment.playNextList.add(PlaySongActivity.musicListPA.get(PlaySongActivity.songPosition));
                                PlaySongActivity.songPosition = 0;
                            }

                            PlayNextFragment.playNextList.add(musicList.get(position));
                            PlaySongActivity.musicListPA = new ArrayList<>();
                            PlaySongActivity.musicListPA.addAll(PlayNextFragment.playNextList);
                        } catch (Exception e) {
                            Snackbar.make(context, holder.binding.root, "Play A Song First!!", 3000).show();
                        }
                        dialog.dismiss();
                    }
                });

                bindingMF.infoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        View detailsDialog = LayoutInflater.from(context).inflate(R.layout.details_view, bindingMF.getRoot(), false);
                        DetailsViewBinding binder = DetailsViewBinding.bind(detailsDialog);
                        binder.detailsTV.setTextColor(Color.WHITE);
                        binder.getRoot().setBackgroundColor(Color.TRANSPARENT);

                        MaterialAlertDialogBuilder dDialog = new MaterialAlertDialogBuilder(context)
                                .setView(detailsDialog)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false);
                        AlertDialog alertDialog = dDialog.create();
                        alertDialog.show();

                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(Color.RED);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x99000000));

                        SpannableStringBuilder str = new SpannableStringBuilder()
                                .append("DETAILS\n\nName: ")
                                .append(musicList.get(position).getTitle(), new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                .append("\n\nDuration: ")
                                .append(DateUtils.formatElapsedTime(musicList.get(position).getDuration() / 1000))
                                .append("\n\nLocation: ")
                                .append(musicList.get(position).getPath(), new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        binder.detailsTV.setText(str);
                    }
                });




                return true;
            });
        }

        if (PlaylistDetials) {
            holder.binding.root.setOnClickListener(view -> sendIntent("PlaylistDetailsAdapter", position));
        } else if (SlectionActivity) {
            holder.binding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addSong(musicList.get(position))) {
                        holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.RED));
                        binding.check.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                    }
                }
            });
        } else {
            holder.root.setOnClickListener(v -> {
                if (MainActivity.search) {
                    sendIntent("MusicAdapterSearch", position);
                } else if (musicList.get(position).getId().equals(PlaySongActivity.NowPlayingID)) {
                    sendIntent("NowPlaying", PlaySongActivity.songPosition);
                } else {
                    sendIntent("MusicAdapter", position);
                }
            });
        }

    }


    public static String formatDuration(long duration) {
        long minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        long seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)) - minutes *
                TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);

        return String.format("%02d:%02d", minutes, seconds);
    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        AllsongHomeLayoutBinding binding;
        TextView title;
        TextView album;
        ImageView image;
        TextView duration;
        RelativeLayout root;

        public MyViewHolder(AllsongHomeLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.title = binding.songNameMV;
            this.album = binding.songAlbumMV;
            this.image = binding.imageMV;
            this.duration = binding.songDuration;
            this.root = binding.getRoot();

        }
    }


    private void   sendIntent(String Ref, int Pos) {
        Intent intent = new Intent(context, PlaySongActivity.class);
        intent.putExtra("index", Pos);
        intent.putExtra("class", Ref);
        ContextCompat.startActivity(context, intent, null);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateMusicList(ArrayList<Music> searchList) {
        musicList = new ArrayList<>();
        musicList.addAll(searchList);
        notifyDataSetChanged();

    }
    private boolean addSong(Music song) {
        for (int i = 0; i < PlayListFragment.musicPlaylist.ref.get(PlaylistDetailsActivity.currentPlaylistPos).playlist.size(); i++) {
            Music music = PlayListFragment.musicPlaylist.ref.get(PlaylistDetailsActivity.currentPlaylistPos).playlist.get(i);
            if (song.getId() == music.getId()) {
                PlayListFragment.musicPlaylist.ref.get(PlaylistDetailsActivity.currentPlaylistPos).playlist.remove(i);
                return false;
            }
        }
        PlayListFragment.musicPlaylist.ref.get(PlaylistDetailsActivity.currentPlaylistPos).playlist.add(song);
        return true;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void refreshPlaylist() {
        musicList = new ArrayList<>();
        musicList = PlayListFragment.musicPlaylist.ref.get(PlaylistDetailsActivity.currentPlaylistPos).playlist;
        notifyDataSetChanged();
    }





}



