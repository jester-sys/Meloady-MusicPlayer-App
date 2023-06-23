package com.mix.melody.CustomClass;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID = "channel1";
    public static final String PLAY = "play";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String EXIT = "exit";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Now Playing Song", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This is a important channel for showing song!!");
            //for lockscreen -> test this and let me know.
//            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
