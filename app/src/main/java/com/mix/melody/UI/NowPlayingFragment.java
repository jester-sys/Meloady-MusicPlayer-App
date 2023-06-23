package com.mix.melody.UI;

import static android.view.View.VISIBLE;

import static com.mix.melody.ActivityUI.PlaySongActivity.musicListPA;
import static com.mix.melody.ActivityUI.PlaySongActivity.musicService;
import static com.mix.melody.ActivityUI.PlaySongActivity.songPosition;
import static com.mix.melody.CustomClass.Music.SetPotionsOfSong;
import static com.mix.melody.CustomClass.Music.getImgArt;
import static com.mix.melody.CustomClass.MusicService.mediaPlayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mix.melody.ActivityUI.PlaySongActivity;
import com.mix.melody.CustomClass.MusicService;
import com.mix.melody.MainActivity;
import com.mix.melody.R;
import com.mix.melody.databinding.FragmentNowPlayingBinding;

import java.util.Objects;




public class NowPlayingFragment extends Fragment {

    public NowPlayingFragment() {
        // Required empty public constructor
    }

    public static FragmentNowPlayingBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false);
        binding.SongNameNP.setSelected(true);
        binding.root.setVisibility(View.INVISIBLE);

        binding.root.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PlaySongActivity.class);
            intent.putExtra("index", songPosition);
            intent.putExtra("class", "NowPlaying");
            ContextCompat.startActivity(requireContext(), intent, null);
        });
        binding.NextBtnNp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPotionsOfSong(true);
                MusicService.createMediaPlayer();

                Glide.with(requireActivity())
                        .load(getImgArt(musicListPA.get(songPosition).getPath()))
                        .apply(new RequestOptions().placeholder(R.drawable.baseline_library_music_24).centerCrop())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_library_music_24);
                                int dominantColor = getDominantColor(bitmap);
                                setFragmentBackground(dominantColor);
                                setStatusBarColor(dominantColor);
                                MainActivity.binding.tablayout.setBackgroundColor(dominantColor);
                                MainActivity.binding.toolbar.setBackgroundColor(dominantColor);
                                MainActivity.binding.toolbar.setTitleTextColor(dominantColor);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                int dominantColor = getDominantColor(bitmap);
                                setFragmentBackground(dominantColor);
                                setStatusBarColor(dominantColor);
                                MainActivity.binding.tablayout.setBackgroundColor(dominantColor);
                                MainActivity.binding.toolbar.setBackgroundColor(dominantColor);
                                MainActivity.binding.toolbar.setTitleTextColor(dominantColor);
                                if (getActivity() != null) {
                                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                                    if (activity.getSupportActionBar() != null) {
                                        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(dominantColor));
                                    }
                                }
                                return false;
                            }
                        })
                        .into(binding.SongImgNP);

                binding.SongNameNP.setText(PlaySongActivity.musicListPA.get(songPosition).getTitle());

                musicService.showNotification(R.drawable.baseline_pause_circle_24, 1F);
                playMusic();

                byte[] img = getImgArt(musicListPA.get(songPosition).getPath());
                Bitmap image;
                if (img != null) {
                    image = BitmapFactory.decodeByteArray(img, 0, img.length);
                } else {
                    image = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_library_music_24);
                }

                int bgColor = getDominantColor(image);
                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{0xFFFFFF, bgColor}
                );

                binding.getRoot().setBackground(gradient);
                Window window = getActivity().getWindow();
                if (window != null) {
                    window.setStatusBarColor(bgColor);
                }
            }
        });
        binding.PlayPauseBtn.setOnClickListener(v -> {
            if (PlaySongActivity.isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (musicService != null) {
            binding.getRoot().setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(getImgArt(musicListPA.get(songPosition).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.baseline_library_music_24).centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_library_music_24);
                            int dominantColor = getDominantColor(bitmap);
                            setFragmentBackground(dominantColor);
                            setStatusBarColor(dominantColor);
                            MainActivity.binding.tablayout.setBackgroundColor(dominantColor);
                            MainActivity.binding.toolbar.setBackgroundColor(dominantColor);
                            MainActivity.binding.toolbar.setTitleTextColor(dominantColor);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            int dominantColor = getDominantColor(bitmap);
                            setFragmentBackground(dominantColor);
                            setStatusBarColor(dominantColor);
                            MainActivity.binding.tablayout.setBackgroundColor(dominantColor);
                            MainActivity.binding.toolbar.setBackgroundColor(dominantColor);
                            MainActivity.binding.toolbar.setTitleTextColor(dominantColor);
                            if (getActivity() != null) {
                                AppCompatActivity activity = (AppCompatActivity) getActivity();
                                if (activity.getSupportActionBar() != null) {
                                    activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(dominantColor));
                                }
                            }
                            return false;
                        }
                    })
                    .into(binding.SongImgNP);

            binding.SongNameNP.setText(musicListPA.get(songPosition).getTitle());
            if (PlaySongActivity.isPlaying)
                binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
            else
                binding.PlayPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
        }
    }

    private void playMusic() {
        mediaPlayer.start();
        binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
        musicService.showNotification(R.drawable.baseline_pause_circle_24, 1F);
        PlaySongActivity.binding.NextBtn.setImageResource(R.drawable.baseline_pause_circle_24);
        PlaySongActivity.isPlaying = true;
    }

    private void pauseMusic() {
        mediaPlayer.pause();
        binding.PlayPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
        musicService.showNotification(R.drawable.baseline_play_circle_24, 0F);
        PlaySongActivity.binding.NextBtn.setImageResource(R.drawable.baseline_play_circle_24);
        PlaySongActivity.isPlaying = false;
    }

    private void setFragmentBackground(int dominantColor) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{0xFFFFFF, dominantColor});
        binding.getRoot().setBackground(gradient);
    }

    private void setStatusBarColor(int color) {
        Window window = requireActivity().getWindow();
        if (window != null) {
            window.setStatusBarColor(color);
        }
    }

    private void setRootBackground(int color) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{color, Color.BLACK}
        );
        binding.getRoot().setBackground(drawable);
    }

    private int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            // Return a default color if the bitmap is null
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

        for (int pixel : pixels) {
            red += Color.red(pixel);
            green += Color.green(pixel);
            blue += Color.blue(pixel);
        }

        red /= size;
        green /= size;
        blue /= size;

        return Color.rgb(red, green, blue);
    }
}