package com.toluwalase.musicapp0.Customs;

import static com.toluwalase.musicapp0.Customs.ApplicationClass.ACTION_CANCEL;
import static com.toluwalase.musicapp0.Customs.ApplicationClass.ACTION_NEXT;
import static com.toluwalase.musicapp0.Customs.ApplicationClass.ACTION_PLAY;
import static com.toluwalase.musicapp0.Customs.ApplicationClass.ACTION_PREVIOUS;
import static com.toluwalase.musicapp0.Customs.ApplicationClass.CHANNEL_ID_2;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.playLists;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.artist_name;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.cancel_button;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.convertTo_textView;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.cover_art;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.current_position;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.duration_played;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.edit_button;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.edit_complete_button;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.handler;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.lyrics_options_panel;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.lyrics_search_txt;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.lyrics_textView;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.musicService;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.playpause_button;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.search_refresh_button;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.search_webView;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.seekBar;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song_album;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song_title;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.songsList;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.MainActivity.repeatCLicked;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.toluwalase.musicapp0.Fragments.HomeFragment;
import com.toluwalase.musicapp0.Fragments.NowPlayingFragment;
import com.toluwalase.musicapp0.Fragments.QueueFragment;
import com.toluwalase.musicapp0.Interfaces.PlayActionListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    public static final String CURRENT_MUSIC = "CurrentMusic";
    public static final String SONG = "STORED_MUSIC";
    public static final String CURRENT_POS = "CurrentPos";
    public static final String CURRENT_LIST = "CurrentList";
    public static final String RECENT_LIST = "RecentList";
    public static final String PLAYLISTS = "PlayLists";
    public static final String SONG_STATUS = "SongStatus";
    public static final String SONG_POS = "SeekBarPos";
    public static final String CURRENT_QUEUES = "AllQueues";
    public static final String HISTORY = "PlaybackHistory";
    public static boolean NOTIFICATION_VISIBLE = false;
    public static final String LIST_TITLE = "ListTitle";
    public static final String ACTION_FINISH_MAIN_ACTIVITY = "Finish";
    int seekPos;

    public IBinder mBinder = new MyBinder();
    public static MediaPlayer servicesMediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ApplicationClass applicationClass;
    MainActivity activity;
    TabLayout tabLayout;
    public static PlayActionListener playActionListener;
    RemoteViews customView;
    GradientDrawable gradientDrawableBG;
    Palette.Swatch swatch;
    public static SharedPreferences.Editor editor;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationClass = (ApplicationClass) getApplication();
        activity = applicationClass.getMainActivity();
        tabLayout = activity.findViewById(R.id.tab_layout);

        musicFiles = songsList;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPos = intent.getIntExtra("ServicePos", -1);
        String actionName = intent.getStringExtra("ActionName");

        if(myPos != -1){
            playMedia(myPos);
        }

        if (actionName != null){
            switch (actionName){
                case "PlayPause":
                    if(playActionListener != null){
                        playActionListener.playpausebuttonClicked();
                    }
                    break;
                case "Next":
                    if(playActionListener != null){
                        playActionListener.nextbuttonClicked();
                    }
                    break;
                case "Previous":
                    if(playActionListener != null){
                        playActionListener.previousbuttonClicked();
                    }
                    break;
                case "Cancel":
                    NOTIFICATION_VISIBLE = false;
                    Gson gson = new Gson();
                    String queues = gson.toJson(myQueues);
                    String recents = gson.toJson(recentList);
                    String playlists = gson.toJson(playLists);
                    String history = gson.toJson(historySongs);

                    editor.putString(CURRENT_QUEUES, queues);
                    editor.putString(RECENT_LIST, recents);
                    editor.putString(PLAYLISTS, playlists);
                    editor.putString(HISTORY, history);
                    editor.putInt(SONG_POS, servicesMediaPlayer.getCurrentPosition());
                    editor.putString(LIST_TITLE, QueueFragment.LIST_TITLE);
                    editor.apply();
                    musicService = null;
                    stopForeground(true);
                    stopSelf();

                    if(servicesMediaPlayer != null){
                        servicesMediaPlayer.stop();
                        servicesMediaPlayer.release();
                    }
                    servicesMediaPlayer = null;

                    if(loopMP != null){
                        loopMP.stop();
                        loopMP.release();
                    }
                    loopMP = null;

                    Intent finishIntent = new Intent(MusicService.ACTION_FINISH_MAIN_ACTIVITY);
                    sendBroadcast(finishIntent);
                    break;
            }
        }

        int storedPos = intent.getIntExtra("StoredPos", -1);
        seekPos = intent.getIntExtra("SeekPos", -1);
        if (storedPos != -1){
            resumeMediaPlayer(storedPos);
//            showNotification(R.id.play, uri);
        }
        return START_STICKY;
    }

    private void resumeMediaPlayer(int start_position) {
        musicFiles = songsList;
        position = start_position;

        if (servicesMediaPlayer != null) {
            servicesMediaPlayer.stop();
            servicesMediaPlayer.release();
            if(musicFiles != null) {
                createMediaPlayer(position);
            }
        } else {
            createMediaPlayer(position);
        }

        try {
            seekBar.setMax(servicesMediaPlayer.getDuration() / 1000);
            if(seekPos != -1) {
                servicesMediaPlayer.seekTo(seekPos);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            return;
        }


        try {
            onCompleted();
        } catch(NullPointerException e){
            e.printStackTrace();
            return;
        }
    }

    private void playMedia(int start_position) {
        musicFiles = songsList;
        position = start_position;

        if (servicesMediaPlayer != null) {
            servicesMediaPlayer.stop();
            servicesMediaPlayer.release();
            if(musicFiles != null) {
                createMediaPlayer(position);
                try {
                    servicesMediaPlayer.start();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        } else {
            createMediaPlayer(position);
            try {
                servicesMediaPlayer.start();
            } catch(NullPointerException e){
                e.printStackTrace();
                return;
            }
        }

        try {
            seekBar.setMax(servicesMediaPlayer.getDuration() / 1000);
        } catch (NullPointerException e){
            e.printStackTrace();
            return;
        }


        try {
            onCompleted();
        } catch(NullPointerException e){
            e.printStackTrace();
            return;
        }
    }

    public class MyBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public void start(){
        servicesMediaPlayer.start();
    }

    public void pause(){
        servicesMediaPlayer.pause();
    }

    public boolean isPlaying(){
        return servicesMediaPlayer.isPlaying();
    }

    public void stop(){
        servicesMediaPlayer.stop();
    }

    public void release(){
        servicesMediaPlayer.release();
    }

    public int getDuration(){
        return servicesMediaPlayer.getDuration();
    }

    public void seekTo(int position){
        servicesMediaPlayer.seekTo(position);
    }

    public void createMediaPlayer(int position){
        NOTIFICATION_VISIBLE = true;
        uri = Uri.parse(musicFiles.get(position).getPath());
        Gson gson = new Gson();

        servicesMediaPlayer = MediaPlayer.create(getBaseContext(), uri);
        String json = gson.toJson(songsList);
        String queues = gson.toJson(myQueues);

        editor = getSharedPreferences(CURRENT_MUSIC, MODE_PRIVATE).edit();
        editor.putString(SONG, uri.toString());
        editor.putInt(CURRENT_POS, current_position);
        editor.putString(CURRENT_LIST, json);
        editor.putString(CURRENT_QUEUES, queues);
//        editor.apply();
    }

    public int getCurrentPosition(){
        return servicesMediaPlayer.getCurrentPosition();
    }

    public void onCompleted(){
        servicesMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(playActionListener != null){
            playActionListener.completed();
            playActionListener.load_search();
        }
    }

    public void setCallBack(PlayActionListener actionListener){
//        this.playActionListener = actionListener;
        playActionListener = actionListener;
    }

    public void showNotification(int play_pause_btn, Uri uri){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent resumePending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent previousPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPending = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_CANCEL);
        PendingIntent cancelPending = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] art = null;
        art = getAlbumArt(songsList.get(current_position).getPath());
        Bitmap thumb = null;
        if(art != null){
            thumb = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            thumb = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_art);
        }

        customView = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
        customView.setTextViewText(R.id.song_name_1, songsList.get(current_position).getTitle());
        customView.setTextViewText(R.id.song_artist_1, songsList.get(current_position).getArtist());
        customView.setTextViewText(R.id.album_name_1, songsList.get(current_position).getAlbum());
        customView.setImageViewBitmap(R.id.album_cover, thumb);
        customView.setImageViewResource(R.id.play_pause_btn_1, play_pause_btn);
        customView.setInt(R.id.notification, "setBackgroundColor", getGradient(uri));

        customView.setOnClickPendingIntent(R.id.play_pause_btn_1, playPending);
        customView.setOnClickPendingIntent(R.id.previous_btn_1, previousPending);
        customView.setOnClickPendingIntent(R.id.next_btn_1, nextPending);
        customView.setOnClickPendingIntent(R.id.cancel_button, cancelPending);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(play_pause_btn)
                .setCustomContentView(customView)
                .setContentIntent(resumePending)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSilent(true)
                .setOnlyAlertOnce(true);

        startForeground(-1, builder.build());
        NOTIFICATION_VISIBLE = true;
    }

    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(path);
            return retriever.getEmbeddedPicture();
        } catch (RuntimeException e){
            e.printStackTrace();
            return null;
        }
    }

    public int getGradient(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri.toString());
        } catch (RuntimeException e){
            e.printStackTrace();
        }

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;

        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@org.jetbrains.annotations.Nullable @androidx.annotation.Nullable Palette palette) {
                    swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        gradientDrawableBG = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff000000, swatch.getRgb()});
                    }else{
                        gradientDrawableBG = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                    }
                }
            });

            return Color.WHITE;
        }else{
            gradientDrawableBG = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
            return 0xff000000;
        }
    }
}






















