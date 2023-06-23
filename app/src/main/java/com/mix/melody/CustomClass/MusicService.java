package com.mix.melody.CustomClass;


import static com.mix.melody.ActivityUI.PlaySongActivity.binding;
import static com.mix.melody.ActivityUI.PlaySongActivity.musicService;
import static com.mix.melody.AdapterClass.AllSongAdapter.formatDuration;
import static com.mix.melody.CustomClass.Music.getImgArt;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mix.melody.ActivityUI.PlaySongActivity;
import com.mix.melody.R;
import com.mix.melody.UI.NowPlayingFragment;

import java.io.IOException;
import java.util.Locale;


public class MusicService extends Service  implements   AudioManager.OnAudioFocusChangeListener {
    private MyBinder myBinder = new MyBinder();


    public  static MediaPlayer mediaPlayer;
    private Runnable runnable;
    MediaSessionCompat _mediaSession;

    public  AudioManager audioManager;


    @Override
    public IBinder onBind(Intent intent) {
        _mediaSession = new MediaSessionCompat(this, "MyMusic");

        return myBinder;
    }

    public class MyBinder extends Binder {
        public MusicService currentService() {

            return MusicService.this;
        }
    }

        public void showNotification(int PlayPauseBtn,float playBackSpeed) {
            Intent intent = new Intent(getApplicationContext(), PlaySongActivity.class);
            intent.putExtra("index", PlaySongActivity.songPosition);
            intent.putExtra("class", "NowPlaying");
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contextIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent prevIntent = new Intent(getApplicationContext(), NotificationReceiver.class).setAction(ApplicationClass.PREVIOUS);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 8, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent playIntent = new Intent(getApplicationContext(), NotificationReceiver.class).setAction(ApplicationClass.PLAY);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent playPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 8, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent nextIntent = new Intent(getApplicationContext(), NotificationReceiver.class).setAction(ApplicationClass.NEXT);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 8, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent exitIntent = new Intent(getApplicationContext(), NotificationReceiver.class).setAction(ApplicationClass.EXIT);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent exitPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 8, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            byte[] imgArt = getImgArt(PlaySongActivity.musicListPA.get(PlaySongActivity.songPosition).getPath());
            Bitmap image;
            if (imgArt != null) {
                image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
            } else {
                image = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_music);
            }

            Notification notification ;
                notification = new NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID)
                        .setContentIntent(contextIntent)
                        .setContentTitle(PlaySongActivity.musicListPA.get(PlaySongActivity.songPosition).getTitle())
                        .setContentText(PlaySongActivity.musicListPA.get(PlaySongActivity.songPosition).getArtist())
                        .setSmallIcon(R.drawable.baseline_library_music_24)
                        .setLargeIcon(image)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(_mediaSession.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true)
                        .addAction(R.drawable.skip_previous, "Previous", prevPendingIntent)
                        .addAction(PlayPauseBtn, "Play", playPendingIntent)
                        .addAction(R.drawable.kip_next, "Next", nextPendingIntent)
                        .addAction(R.drawable.baseline_exit_to_app_24, "Exit", exitPendingIntent)
                        .build();

                startForeground(1, notification);





                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    float playbackSpeed = PlaySongActivity.isPlaying ? 1F : 0F;
                    _mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration())
                            .build());
                    PlaybackStateCompat playBackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), playbackSpeed)
                            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                            .build();
                }
            }



        public static Exception createMediaPlayer() {
            try {
                if (musicService.mediaPlayer == null) {
                    musicService.mediaPlayer = new MediaPlayer();
                }
                musicService.mediaPlayer.reset();
                musicService.mediaPlayer.setDataSource(PlaySongActivity.musicListPA.get(PlaySongActivity.songPosition).getPath());
                musicService.mediaPlayer.prepare();
                musicService.showNotification(R.drawable.baseline_pause_circle_24,0F);
                binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
                binding.SeekBarStart.setText(formatDuration(musicService.mediaPlayer.getCurrentPosition()));
                binding.seekBarPA.setProgress(0);
                binding.seekBarPA.setMax(musicService.mediaPlayer.getDuration());
                binding.SeekBarEnd.setText(formatDuration(musicService.mediaPlayer.getDuration()));
                PlaySongActivity.NowPlayingID = PlaySongActivity.musicListPA.get(PlaySongActivity.songPosition).getId();
            } catch (Exception e) {
                return e;
            }
            return null;
        }

    public void seekBarSetup() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    binding.SeekBarStart.setText(formatDuration(currentPosition));
                    binding.seekBarPA.setProgress(currentPosition);

                    // Update the SeekBar in the notification layout
                    updateNotificationSeekBar(currentPosition);
                }
                new Handler(Looper.getMainLooper()).postDelayed(runnable, 200);
            }
        };
        new Handler(Looper.getMainLooper()).postDelayed(runnable, 0);
    }

    private void updateNotificationSeekBar(int currentPosition) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && _mediaSession != null) {
            PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, currentPosition, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO);

            _mediaSession.setPlaybackState(playbackStateBuilder.build());
        }
    }
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) {
            PlaySongActivity.isPlaying = true;
            mediaPlayer.start();
            showNotification(R.drawable.baseline_pause_circle_24,0F);
            binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
            NowPlayingFragment.binding.PlayPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);

        } else {
            PlaySongActivity.isPlaying = false;
            mediaPlayer.pause();
            showNotification(R.drawable.baseline_play_circle_24,0F);
            binding.PlayPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
            NowPlayingFragment.binding.PlayPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);

        }

    }

}

