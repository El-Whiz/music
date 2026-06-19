package com.toluwalase.musicapp0.Customs;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.toluwalase.musicapp0.Fragments.NowPlayingFragment;
import com.toluwalase.musicapp0.MainActivity;

public class ApplicationClass extends Application {
    private MainActivity mainActivity;

    public static final String CHANNEL_ID_1 = "channel_1";
    public static final String CHANNEL_ID_2 = "channel_2";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_CANCEL = "action_cancel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc...");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 2 Desc...");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }

    public MainActivity getMainActivity(){
        return mainActivity;
    }

    public void setMainActivity(MainActivity activity) {
        this.mainActivity = activity;
    }
}






































