package com.toluwalase.musicapp0;

import static com.toluwalase.musicapp0.Customs.MusicService.HISTORY;
import static com.toluwalase.musicapp0.Customs.MusicService.PLAYLISTS;
import static com.toluwalase.musicapp0.Customs.MusicService.RECENT_LIST;
import static com.toluwalase.musicapp0.Customs.MusicService.SONG_STATUS;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.backToHome;
import static com.toluwalase.musicapp0.Customs.MusicService.CURRENT_LIST;
import static com.toluwalase.musicapp0.Customs.MusicService.CURRENT_MUSIC;
import static com.toluwalase.musicapp0.Customs.MusicService.CURRENT_POS;
import static com.toluwalase.musicapp0.Customs.MusicService.CURRENT_QUEUES;
import static com.toluwalase.musicapp0.Customs.MusicService.LIST_TITLE;
import static com.toluwalase.musicapp0.Customs.MusicService.NOTIFICATION_VISIBLE;
import static com.toluwalase.musicapp0.Customs.MusicService.SONG;
import static com.toluwalase.musicapp0.Customs.MusicService.SONG_POS;
import static com.toluwalase.musicapp0.Customs.MusicService.editor;
import static com.toluwalase.musicapp0.Fragments.AlbumFragment.inItems;
import static com.toluwalase.musicapp0.Fragments.AlbumItemFragment.recyclerView_album_item;
import static com.toluwalase.musicapp0.Fragments.ArtistFragment.inArtistItems;
import static com.toluwalase.musicapp0.Fragments.ArtistItemFragment.artist_recyclerView;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inFaves;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHistory;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHome;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inPlayList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.current_position;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.defaultArtPresent;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.getAlbumPos;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.getArtistPos;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.swatch;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.toluwalase.musicapp0.Adapters.ViewPagerAdapter;
import com.toluwalase.musicapp0.Customs.ApplicationClass;
import com.toluwalase.musicapp0.Customs.MusicService;
import com.toluwalase.musicapp0.Database.MusicDB;
import com.toluwalase.musicapp0.Fragments.AlbumFragment;
import com.toluwalase.musicapp0.Fragments.AlbumItemFragment;
import com.toluwalase.musicapp0.Fragments.ArtistFragment;
import com.toluwalase.musicapp0.Fragments.ArtistItemFragment;
import com.toluwalase.musicapp0.Fragments.HomeFragment;
import com.toluwalase.musicapp0.Fragments.MainFragment;
import com.toluwalase.musicapp0.Fragments.NowPlayingFragment;
import com.toluwalase.musicapp0.Fragments.QueueFragment;
import com.toluwalase.musicapp0.Fragments.SongFragment;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.ArtistItems;
import com.toluwalase.musicapp0.Models.MusicFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static String SHARED_FILE_PATH = "";
    public static String PLAY_HISTORY = null;
    public static String RECENT_SONGS = null;
    public static String PLAY_LISTS = null;
    public static boolean GET_CURRENT_SONG = false;
    public static String PATH = null;
    public static String LIST = null;
    public static String QUEUES = null;
    public static String STATUS = "-1";
    public static int POS = -1;
    public static String TITLE_LIST = null;
    public static int SEEKBAR_POS = -1;
    public static final int REQUEST_CODE = 1;
    public static boolean alive = false;
    public static ArrayList<MusicFiles> musicFiles;
    public static ArrayList<AlbumItems> albumFiles = new ArrayList<>();
    public static ArrayList<ArtistItems> artistFiles = new ArrayList<>();
    public static boolean shuffleClicked = false;
    public static boolean repeatCLicked = false;
    public static boolean fromMainSearch;

    public static ViewPager viewPager;
    TabLayout tabLayout;
    static MediaPlayer mediaPlayer;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();

        permission();

        ApplicationClass applicationClass = (ApplicationClass) getApplication();
        applicationClass.setMainActivity(this);

        IntentFilter filter = new IntentFilter(MusicService.ACTION_FINISH_MAIN_ACTIVITY);
        registerReceiver(finishReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(SHARED_FILE_PATH != null){
            File sharedFile = new File(SHARED_FILE_PATH);
            if(sharedFile.exists()){
                sharedFile.delete();
            }
        }

        if(NOTIFICATION_VISIBLE){
            Gson gson = new Gson();
            String queues = gson.toJson(myQueues);
            editor.putString(CURRENT_QUEUES, queues);
            editor.putString(LIST_TITLE, QueueFragment.LIST_TITLE);
        }
        String status = song.getFaveStatus();
        editor.putString(SONG_STATUS, status);
        editor.apply();
        unregisterReceiver(finishReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(CURRENT_MUSIC, MODE_PRIVATE);
        String value = preferences.getString(SONG, null);
        int pos = preferences.getInt(CURRENT_POS, -1);
        String list = preferences.getString(CURRENT_LIST, null);
        int seekBarPos = preferences.getInt(SONG_POS, -1);
        String queues = preferences.getString(CURRENT_QUEUES, null);
        String list_title = preferences.getString(LIST_TITLE, null);
        String status = preferences.getString(SONG_STATUS, "-1");
        String recentList = preferences.getString(RECENT_LIST, null);
        String playLists = preferences.getString(PLAYLISTS, null);
        String history = preferences.getString(HISTORY, null);

        if(NOTIFICATION_VISIBLE){
            QUEUES = queues;
            TITLE_LIST = list_title;
            LIST = list;
            STATUS = status;
            alive = true;
        }else {
            alive = false;

            if (value != null) {
                GET_CURRENT_SONG = true;
                PATH = value;
                POS = pos;
                LIST = list;
                SEEKBAR_POS = seekBarPos;
                QUEUES = queues;
                STATUS = status;
                TITLE_LIST = list_title;
                RECENT_SONGS = recentList;
                PLAY_LISTS = playLists;
                PLAY_HISTORY = history;
            } else {
                GET_CURRENT_SONG = false;
                PATH = null;
            }
        }

        if(SHARED_FILE_PATH != null){
            File sharedFile = new File(SHARED_FILE_PATH);
            if(sharedFile.exists()){
                sharedFile.delete();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());

        if (currentFragment instanceof AlbumFragment){
            if(!inItems) {
                loadAnAlbumFragment(new AlbumItemFragment());
                inItems = true;
            }
        }
        else if (currentFragment instanceof ArtistFragment){
            if(!inArtistItems) {
                loadAnArtistFragment(new ArtistItemFragment());
                inArtistItems = true;
            }
        } else if (currentFragment instanceof MainFragment) {
            if(inPlayList){
                loadAnHomeFragment(new HomeFragment());
                inHome = true;
                backToHome = true;
                inPlayList = false;
            } else if (inFaves) {
                loadAnHomeFragment(new HomeFragment());
                inHome = true;
                backToHome = true;
                inFaves = false;
            } else if (inHistory) {
                loadAnHomeFragment(new HomeFragment());
                inHome = true;
                backToHome = true;
                inHistory = false;
            }
        } else {
            super.onBackPressed();
        }
    }

    public TabLayout getTabLayout(){
        return tabLayout;
    }

    public void setStatusBarColor(int color){
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(color);
        }
    }

    private void permission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else{
            musicFiles = getAllAudio(this);
            albumFiles = getAllAlbums(this);
            artistFiles = getArtist(this);

            initViewPager();
            tabLayout.getTabAt(0).select();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Perform Action
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                musicFiles = getAllAudio(this);
                albumFiles = getAllAlbums(this);
                artistFiles = getArtist(this);

                initViewPager();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.bg_white));

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new MainFragment());
        viewPagerAdapter.addFragments(new NowPlayingFragment());
        viewPagerAdapter.addFragments(new SongFragment());
        viewPagerAdapter.addFragments(new ArtistFragment());
        viewPagerAdapter.addFragments(new AlbumFragment());
        viewPagerAdapter.addFragments(new QueueFragment());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_now_playing);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_songs);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_artist);
        tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_album_icon_1);
        tabLayout.getTabAt(4).getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(5).setIcon(R.drawable.ic_queue);
        tabLayout.getTabAt(5).getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);

        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.bg_white), PorterDuff.Mode.SRC_IN);

                if(song != null) {
                    recyclerView_album_item.scrollToPosition(getAlbumPos(song.getAlbum()));
                    artist_recyclerView.scrollToPosition(getArtistPos(song.getArtist()));
                }

                if(tabLayout.getSelectedTabPosition() == 1){
                    if (swatch != null){
                        if(!defaultArtPresent) {
                            tabLayout.setBackgroundColor(swatch.getRgb());
                            setStatusBarColor(swatch.getRgb());
                        }
                    }
                }
                else{
                    if(song != null) {
                        QueueFragment.queue_recyclerView_songs.scrollToPosition(current_position);
                        QueueFragment.queueAdapter.changeOutlineColor(current_position, Color.GREEN);
                    }
                    tabLayout.setBackgroundResource(R.drawable.main_bg);
                    setStatusBarColor(getResources().getColor(R.color.bg_black));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.GENRE,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.CD_TRACK_NUMBER,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.BITRATE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                String genre = cursor.getString(6);
                String composer = cursor.getString(7);
                String number = cursor.getString(8);
                String year = cursor.getString(9);
                String size = cursor.getString(10);
                String bitrate = cursor.getString(11);
                String date = cursor.getString(12);
                String file_name = cursor.getString(13);
                String art = cursor.getString(14);

//                Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(art));

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id, genre, composer, number, year, size, bitrate, date, file_name, art);

                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }

    public ArrayList<AlbumItems> getAllAlbums(Context context){
        Set<String> albumSet = new HashSet<>();
        ArrayList<AlbumItems> tempAlbumList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);

                if(!albumSet.contains(album)){
                    String album_artist = cursor.getString(1);
                    String album_id = cursor.getString(2);
                    String album_path = cursor.getString(3);

                    Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(album_id));
                    AlbumItems albumItem = new AlbumItems(album, album_artist, album_id, album_path, artUri);

                    int j = 0;
                    for (int i = 0 ; i < musicFiles.size() ; i++){
                        if(album.equalsIgnoreCase(musicFiles.get(i).getAlbum())){
                            j++;
                        }
                    }
                    albumItem.setSongCount(j);

                    tempAlbumList.add(albumItem);

                    albumSet.add(album);
                }
            }
            cursor.close();
        }
        return tempAlbumList;
    }

    public ArrayList<ArtistItems> getArtist(Context context){
        Set<String> artistSet = new HashSet<>();
        ArrayList<ArtistItems> tempArtistList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ArrayList<MusicFiles> artistSongs = new ArrayList<>();
        ArrayList<AlbumItems> albumItems = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media._ID
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                String artist = cursor.getString(0);

                if(!artistSet.contains(artist)){
                    String id = cursor.getString(1);
                    String path = cursor.getString(2);

                    if(artistSongs != null){
                        artistSongs.clear();
                    }
                    if(albumItems != null){
                        albumItems.clear();
                    }

                    int j = 0;
                    for (int i = 0 ; i < musicFiles.size() ; i++){
                        if(artist.equalsIgnoreCase(musicFiles.get(i).getArtist())){
                            artistSongs.add(j, musicFiles.get(i));
                            j++;
                        }
                    }
                    int k = 0;
                    for(int i = 0; i < albumFiles.size(); i++){
                        if(artist.equalsIgnoreCase(albumFiles.get(i).getArtist())){
                            albumItems.add(k, albumFiles.get(i));
                            k++;
                        }
                    }

                    ArtistItems artistItem = new ArtistItems(artist, id, path);
                    artistItem.setSongsCount(j);
                    artistItem.setArtistSongs(artistSongs);
                    artistItem.setAlbumsCount(k);
                    tempArtistList.add(artistItem);

                    artistSet.add(artist);
                }
            }
            cursor.close();
        }

        return tempArtistList;
    }

    public void loadAnArtistFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.artist_container_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadAnAlbumFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.album_container_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadAnHomeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                Snackbar.make(viewPager, "File deleted", Snackbar.LENGTH_LONG).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(viewPager, "Cancelled", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MusicService.ACTION_FINISH_MAIN_ACTIVITY)){
                finish();
            }
        }
    };
}
























