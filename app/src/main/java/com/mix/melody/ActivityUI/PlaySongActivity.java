package com.mix.melody.ActivityUI;


import static com.mix.melody.ActivityUI.PlaylistDetailsActivity.currentPlaylistPos;
import static com.mix.melody.AdapterClass.AllSongAdapter.formatDuration;
import static com.mix.melody.CustomClass.Music.SetPotionsOfSong;
import static com.mix.melody.CustomClass.Music.favouriteChecker;
import static com.mix.melody.CustomClass.Music.getImgArt;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mix.melody.CustomClass.Music;
import com.mix.melody.CustomClass.MusicService;
import com.mix.melody.MainActivity;
import com.mix.melody.R;
import com.mix.melody.UI.FavouriteFragment;
import com.mix.melody.UI.HomeFragment;
import com.mix.melody.UI.NowPlayingFragment;
import com.mix.melody.UI.PlayListFragment;
import com.mix.melody.UI.PlayNextFragment;
import com.mix.melody.databinding.ActivityPlaySongBinding;


import java.util.ArrayList;
import java.util.Collections;

// This is the PlaySongActivity class which extends AppCompatActivity and implements
// ServiceConnection and MediaPlayer.OnCompletionListener interfaces
public class PlaySongActivity extends AppCompatActivity implements ServiceConnection  ,MediaPlayer.OnCompletionListener  {
    // These are some class-level variables used throughout the activity
    public static int songPosition = 0;   // This is the position of the current song in the musicListPA array
    public static boolean isPlaying = false; // This indicates whether the music is currently playing or not
    public static MusicService musicService; // This is an instance of the MusicService class
    public static boolean isFavourite = false;   // This indicates whether the current song is marked as a favourite or not
    public static int fIndex = -1; // This is the index of the current song in the favourite songs list

    public static boolean repeat = false;
    // This indicates whether the repeat button is currently active or not
    boolean min15 = false;
    boolean min30 = false;
    boolean min60 = false;

    public static ArrayList<Music> musicListPA;   // This is the list of all songs that can be played in the activity
    @SuppressLint("StaticFieldLeak")

    public static ActivityPlaySongBinding binding;
    private GestureDetectorCompat gestureDetector;
    private GestureDetector.SimpleOnGestureListener gestureListener;


    public static String NowPlayingID = "";// This is the ID of the currently playing song
    boolean flagss = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaySongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.songNameMV.setSelected(true);



        if (getIntent().getData() != null && "content".equals(getIntent().getData().getScheme())) {
            songPosition = 0;
            Intent intentService = new Intent(this, MusicService.class);
            bindService(intentService, this, BIND_AUTO_CREATE);
            startService(intentService);
            musicListPA = new ArrayList<>();
            Glide.with(this)
                    .load(getImgArt(musicListPA.get(songPosition).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.baseline_library_music_24).centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            int dominantColor = getDominantColor(bitmap);
                            setFragmentBackground(dominantColor);
                            setStatusBarColor(dominantColor);
                            return false;
                        }
                    })
                    .into(binding.SongImg);

            binding.songNameMV.setText(musicListPA.get(songPosition).getTitle());
            binding.artistName.setText(musicListPA.get(songPosition).getArtist());
        }


        // This sets an OnClickListener for the PlayPauseBtn
        binding.PlayPauseBtn.setOnClickListener(v -> {
            // This checks whether the music is currently playing or not, and calls the corresponding
            // function to either pause or play the music
            if (PlaySongActivity.isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        // This sets an OnClickListener for the equalizerBtn
        binding.equalizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // This creates an intent to launch the audio effects control panel for the current
                    // audio session, and starts the activity
                    Intent EqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    EqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicService.mediaPlayer.getAudioSessionId());
                    EqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                    startActivity(EqIntent);
                } catch (Exception e) {
                    Toast.makeText(PlaySongActivity.this, "Equalizer Feature Not Supported!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // This sets an OnClickListener for the shareBtn
        binding.shareBtn.setOnClickListener(v -> {
            // This creates an intent to share the currently playing song and starts the activity
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("audio/");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA.get(songPosition).getPath()));
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"));
        });


        binding.NextBtn.setOnClickListener(v ->
                prevNextSong(true)
        );

        binding.PreviousBtn.setOnClickListener(v -> prevNextSong(false));
        // Call prevNextSong method with true to play next song


        gestureListener = new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();
                if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (deltaX > 0) {
                        prevNextSong(false);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);// Swipe right to play previous song
                    } else {
                        prevNextSong(true); // Swipe left to play next song
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_in_right);
                    }
                    return true;
                } else if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (deltaY > 0) {
                        // Swipe down to perform an action (e.g., open a new activity)
                        Intent intent = new Intent(PlaySongActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                    }
                    return true;
                }
                return false;
            }
        };

        gestureDetector = new GestureDetectorCompat(this, gestureListener);
        // This sets an OnClickListener for the PreviousBtn

        // Set listener for SeekBar changes
        binding.seekBarPA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // If the progress was changed by user, seek to the selected position
                    MusicService.mediaPlayer.seekTo(progress);
                }
            }
            // Implement other methods of the interface, but do nothing in them

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        binding.repeatBtnPA.setOnClickListener(v -> {
            // If repeat is false, set it to true and change button color to green
            if (!repeat) {
                repeat = true;
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.RED));

            } else {
                repeat = false;
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.white));

            }
        });

        binding.shufflebtn.setOnClickListener(v -> {
            if (!flagss) {
                binding.shufflebtn.setImageResource(R.drawable.baseline_shuffle_24);
                flagss = true;
                musicListPA = new ArrayList<>();
                musicListPA.addAll(HomeFragment.MusicListMV);
                Collections.shuffle(musicListPA);
                songPosition =0;

            } else {
                binding.shufflebtn.setImageResource(R.drawable.shuffle_icon);
                flagss = false;
            }
        });
        binding.timerBtnPA.setOnClickListener(v -> {
            boolean timer = min15 || min30 || min60;
            if (!timer) {
                showBottomSheetDialog();
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PlaySongActivity.this);
                builder.setTitle("Stop Timer")
                        .setMessage("Do you want to stop the timer?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            min15 = false;
                            min30 = false;
                            min60 = false;
                            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.white));
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog customDialog = builder.create();
                customDialog.show();
//                    setDialogBtnBackground(PlaySongActivity.this, customDialog);
            }
        });

        // Set click listener for Favourite button
        binding.FavouriteBtn.setOnClickListener(v -> {
            // Get the index of the song in the Favourite list
            fIndex = favouriteChecker(musicListPA.get(songPosition).getId());
            if (isFavourite) {
                // If the song is already favourite, remove it from the list and change the button icon
                isFavourite = false;
                binding.FavouriteBtn.setImageResource(R.drawable.white_favorite);
                FavouriteFragment.favouriteSongs.remove(fIndex);
            } else {
                // If the song is not favourite, add it to the list and change the button icon
                isFavourite = true;
                binding.FavouriteBtn.setImageResource(R.drawable.favorite);
                FavouriteFragment.favouriteSongs.add(musicListPA.get(songPosition));
            }
            FavouriteFragment.favouritesChanged = true;
        });

// Get the index of the song that was selected to play from the Intent
        songPosition = getIntent().getIntExtra("index", 0);
        if ("NowPlaying".equals(getIntent().getStringExtra("class"))) {
            setLayout();
            binding.SeekBarStart.setText(formatDuration(MusicService.mediaPlayer.getCurrentPosition()));
            binding.SeekBarEnd.setText(formatDuration(MusicService.mediaPlayer.getDuration()));
            binding.seekBarPA.setProgress(MusicService.mediaPlayer.getCurrentPosition());
            binding.seekBarPA.setMax(MusicService.mediaPlayer.getDuration());

            if (isPlaying) {
                binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);

            } else {
                binding.PlayPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);


            }
        }


        if ("MusicAdapterSearch".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(MainActivity.MusicListSearch);
            setLayout();

        } else if ("MusicAdapter".equals(getIntent().getStringExtra("class"))) {

            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(HomeFragment.MusicListMV);
            setLayout();

        } else if ("HomeFragment".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(HomeFragment.MusicListMV);
            setLayout();
        } else if ("FavouriteAdapter".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(FavouriteFragment.favouriteSongs);
            setLayout();
        }  else if ("Shuffle".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(HomeFragment.MusicListMV);
            Collections.shuffle(musicListPA);
            setLayout();
            createMediaPlayer();

        } else if ("PlaylistDetailsAdapter".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist);
            setLayout();

        } else if ("PlayNext".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA= new ArrayList<>();
            musicListPA.addAll(PlayNextFragment.playNextList);
            setLayout();
        } else if ("PlaylistDetailsShuffle".equals(getIntent().getStringExtra("class"))) {
            StartNowIntent();
            musicListPA = new ArrayList<>();
            musicListPA.addAll(PlayListFragment.musicPlaylist.ref.get(currentPlaylistPos).playlist);
            Collections.shuffle(musicListPA);
            setLayout();

        }


    }



    public  void createMediaPlayer() {
        try {
            if (MusicService.mediaPlayer == null) {
                MusicService.mediaPlayer = new MediaPlayer();
            }
            MusicService.mediaPlayer.reset();
            MusicService.mediaPlayer.setDataSource(musicListPA.get(songPosition).getPath());
            MusicService.mediaPlayer.prepare();
            MusicService.mediaPlayer.start();
            isPlaying = true;
            binding.SeekBarStart.setText(formatDuration(MusicService.mediaPlayer.getCurrentPosition()));
            binding.seekBarPA.setProgress(0);
            binding.seekBarPA.setMax(MusicService.mediaPlayer.getDuration());
            binding.SeekBarEnd.setText(formatDuration(MusicService.mediaPlayer.getDuration()));
            musicService.showNotification(R.drawable.baseline_pause_circle_24,1F);
            binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
            MusicService.mediaPlayer.setOnCompletionListener(this);
            NowPlayingID = musicListPA.get(songPosition).getId();
        } catch (Exception e) {
            return;
        }
    }

    private void setLayout() {

        fIndex = favouriteChecker(musicListPA.get(songPosition).getId());
        if (!musicListPA.isEmpty()) {
            Glide.with(this)
                    .load(getImgArt(musicListPA.get(songPosition).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.icon_music).centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            int dominantColor = getDominantColor(bitmap);
                            setFragmentBackground(dominantColor);
                            setStatusBarColor(dominantColor);
                            NowPlayingFragment.binding.getRoot().setBackgroundColor(dominantColor);
                            return false;
                        }
                    })
                    .into(binding.SongImg);
            binding.songNameMV.setText(musicListPA.get(songPosition).getTitle());
            binding.artistName.setText(musicListPA.get(songPosition).getArtist());
        }
        if (repeat) {
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.RED));
        }

        if (isFavourite) {
            binding.FavouriteBtn.setImageResource(R.drawable.favorite);
        } else {
            binding.FavouriteBtn.setImageResource(R.drawable.white_favorite);
        }
        byte[] img = getImgArt(musicListPA.get(songPosition).getPath());
        Bitmap image;
        if (img != null) {
            image = BitmapFactory.decodeByteArray(img, 0, img.length);
        } else {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music);
        }
        int bgColor = image.getPixel(0, 0);
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xFFFFFF, bgColor});
        binding.getRoot().setBackground(gradient);
        Window window = getWindow();
        if (window != null) {
            window.setStatusBarColor(bgColor);
        }
    }



    private void playMusic() {
        //fix
        isPlaying = true;


        musicService.mediaPlayer.start();
        musicService.showNotification(R.drawable.baseline_pause_circle_24,1F);
        binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
    }

    private void pauseMusic() {
        //fix
        isPlaying = false;
        musicService.mediaPlayer.pause();
        musicService.showNotification(R.drawable.baseline_play_circle_24,0F);
        binding.PlayPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
    }

   private void prevNextSong(boolean increment) {

        if (increment) {
            SetPotionsOfSong(true);
            setLayout();
            createMediaPlayer();



} else {
            SetPotionsOfSong(false);
            setLayout();
            createMediaPlayer();

        }


    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.currentService();
        createMediaPlayer();
        musicService.seekBarSetup();
        musicService.audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
        musicService.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        SetPotionsOfSong(true);
        createMediaPlayer();
        try {
            setLayout();
        } catch (Exception e) {
            return;
        }

    }

    private void StartNowIntent() {
        Intent intentService = new Intent(this, MusicService.class);
        bindService(intentService, this, BIND_AUTO_CREATE);
        startService(intentService);
    }



    private void setFragmentBackground(int dominantColor) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{0xFFFFFF, dominantColor});
        binding.getRoot().setBackground(gradient);
    }

    private void setStatusBarColor(int color) {
        Window window = getWindow();
        if (window != null) {
            window.setStatusBarColor(color);
        }
    }

    private int getDominantColor(Bitmap bitmap) {
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(PlaySongActivity.this);
        dialog.setContentView(R.layout.bottom_sheet_dialog);
        dialog.show();

        LinearLayout min15Layout = dialog.findViewById(R.id.min_15);
        if (min15Layout != null) {
            min15Layout.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "Music will stop after 15 minutes", Toast.LENGTH_SHORT).show();
                binding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.RED));
                min15 = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(15 * 60000);
                        if (min15) {
                            finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                dialog.dismiss();
            });
        }

        LinearLayout min30Layout = dialog.findViewById(R.id.min_30);
        if (min30Layout != null) {
            min30Layout.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "Music will stop after 30 minutes", Toast.LENGTH_SHORT).show();
                binding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.RED));
                min30 = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(30 * 60000);
                        if (min30) {
                           finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                dialog.dismiss();
            });
        }

        LinearLayout min60Layout = dialog.findViewById(R.id.min_60);
        if (min60Layout != null) {
            min60Layout.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "Music will stop after 60 minutes", Toast.LENGTH_SHORT).show();

                binding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlaySongActivity.this, R.color.RED));

                min60 = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(60 * 60000);
                        if (min60) {
                           finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                dialog.dismiss();
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicListPA.get(songPosition).getId().equals("Unknown") && !isPlaying) {
            exitApplication();
        }
    }
    private void exitApplication() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

}

