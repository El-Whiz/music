package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.toluwalase.musicapp0.Customs.MusicService.servicesMediaPlayer;
import static com.toluwalase.musicapp0.Fragments.FavouritesFragment.faveSongs;
import static com.toluwalase.musicapp0.Fragments.FavouritesFragment.favesAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.playLists;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.playlistAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.isCurrentList;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.play_pause;
import static com.toluwalase.musicapp0.MainActivity.STATUS;
import static com.toluwalase.musicapp0.MainActivity.albumFiles;
import static com.toluwalase.musicapp0.MainActivity.alive;
import static com.toluwalase.musicapp0.MainActivity.artistFiles;
import static com.toluwalase.musicapp0.MainActivity.repeatCLicked;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.AddPlaylistAdapter;
import com.toluwalase.musicapp0.Customs.InvalidNumberPairException;
import com.toluwalase.musicapp0.Customs.MusicService;
import com.toluwalase.musicapp0.Customs.MyWebViewClient;
import com.toluwalase.musicapp0.Database.FaveDB;
import com.toluwalase.musicapp0.Interfaces.PlayActionListener;
import com.toluwalase.musicapp0.Interfaces.PlaylistClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class NowPlayingFragment extends Fragment implements PlayActionListener, ServiceConnection {
    View view;
    public static TextView song_title, artist_name, song_album, duration_played, duration_total, start_text, end_text;
    public static ImageView cover_art, next_button, previous_button, shuffle_button, repeat_button, add_lyrics_btn, search_lyrics_btn, edit_button,
            cancel_button, edit_complete_button, search_cancel_button, search_refresh_button, fast_forward, fast_rewind, loop_btn, favourite_btn, playlist_btn;
    public static FloatingActionButton playpause_button;
    public static SeekBar seekBar, seekBarLoop;
    public static TextView lyrics_textView, lyrics_search_txt;
    public static EditText lyrics_editTextView;
    public static RelativeLayout cover_art_n_lyrics, lyrics_options_panel, main_container, loop_panel;
    public static WebView search_webView;
    ProgressBar progressBar;
    public MainActivity my_activity;
    public static MusicService musicService;
    static AddPlaylistAdapter adapterPL;
    public static Palette.Swatch swatch;
    private TabLayout tabLayout;
    public static boolean defaultArtPresent = false;
    private boolean loopPaused, inLyricsOptions, inLyricsMode, activated;
    public static boolean inEditLyrics, inLoop;
    int start_loop_pos, end_loop_pos;
    AlertDialog alertDialog, dialog, playlistDialog, addPlaylistDialog, confirmDialog, testDialog;
    public static int current_position = -1;
    public int seekPos = -1;
    String title, artist, album;
    public static ArrayList<MusicFiles> songsList = new ArrayList<>();
    static Uri uri;
    public static MediaPlayer loopMP;
    public static Handler handler = new Handler();
    public static MusicFiles song;
    private Thread play_thread, next_thread, previous_thread, loop_thread;
    FaveDB favDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favDB = new FaveDB(requireContext());
        my_activity = (MainActivity) getActivity();

        initViews();

        lyrics_editTextView = new EditText(getContext());

        lyrics_textView.setVisibility(View.INVISIBLE);
        search_webView.setVisibility(View.INVISIBLE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(inLoop){
//                    Toast.makeText(getContext(), "Please exit loop mode", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (musicService != null && fromUser && song != null){
                        musicService.seekTo(progress * 1000);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarLoop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(loopMP != null && fromUser) {
                    if(loopPaused){
                        Toast.makeText(getContext(), "Loop paused", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        loopMP.seekTo(progress * 1000);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (servicesMediaPlayer != null) {
                    int mCurrentPosition = servicesMediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }

                handler.postDelayed(this, 1000);
            }
        });

        shuffle_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if (inLoop) {
                        Toast.makeText(getContext(), "Please exist loop mode", Toast.LENGTH_SHORT).show();
                    } else {
                        if (shuffleClicked) {
                            //Deactivate
                            shuffleClicked = false;
                            shuffle_button.setImageResource(R.drawable.ic_next_prev);
                            current_position = getQueuePos(song.getId());
                            QueueFragment.queue_recyclerView_songs.scrollToPosition(current_position);
                            QueueFragment.queueAdapter.notifyDataSetChanged();
                        } else {
                            //Activate
                            shuffleClicked = true;
                            shuffle_button.setImageResource(R.drawable.ic_shuffle_on);

                            Collections.shuffle(songsList);
                            songsList.remove(song);
                            songsList.add(0, song);
                            current_position = 0;
                            QueueFragment.queue_recyclerView_songs.scrollToPosition(current_position);
                            QueueFragment.queueAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        repeat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if (inLoop) {
                        Toast.makeText(getContext(), "Please exist loop mode", Toast.LENGTH_SHORT).show();
                    } else {
                        if (repeatCLicked) {
                            repeatCLicked = false;
                            repeat_button.setImageResource(R.drawable.ic_repeat_off);
                        } else {
                            repeatCLicked = true;
                            repeat_button.setImageResource(R.drawable.ic_repeat_on);
                        }
                    }
                }
            }
        });

        fast_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if (inLoop) {
                        if (loopMP != null) {
                            loopMP.seekTo(loopMP.getCurrentPosition() + 10000);
                        }
                    } else {
                        if (musicService != null) {
                            musicService.seekTo(musicService.getCurrentPosition() + 10000);
                        }
                    }
                }
            }
        });

        fast_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if (inLoop) {
                        Toast.makeText(getContext(), "Please exist loop mode", Toast.LENGTH_SHORT).show();
                    } else {
                        if (musicService != null) {
                            musicService.seekTo(musicService.getCurrentPosition() - 10000);
                        }
                    }
                }
            }
        });

        loop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if (inLoop) {
                        inLoop = false;
                        loop_btn.setImageResource(R.drawable.ic_loop_off);
                        loop_panel.setVisibility(View.INVISIBLE);
                        loopMP.release();
                        loopMP = null;
                        seekBarLoop.setProgress(0);

                        seekBar.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
                        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        seekBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
                        next_button.setColorFilter(Color.WHITE);
                        previous_button.setColorFilter(Color.WHITE);
                        duration_played.setTextColor(Color.WHITE);
                        duration_total.setTextColor(Color.WHITE);
                        song_title.setTextColor(Color.WHITE);
                        song_album.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.WHITE);
                        fast_rewind.setColorFilter(Color.WHITE);
                        fast_forward.setColorFilter(Color.WHITE);
                        shuffle_button.setColorFilter(Color.WHITE);
                        favourite_btn.setColorFilter(Color.WHITE);
                        playlist_btn.setColorFilter(Color.WHITE);

                        if (!musicService.isPlaying()) {
                            musicService.start();
                            playpause_button.setImageResource(R.drawable.ic_pause);
                        }
                    } else {
                        Toast.makeText(getContext(), "Hold down to set loop", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        main_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(inLyricsOptions && !inLyricsMode){
                    int[] location = new int[2];
                    lyrics_options_panel.getLocationOnScreen(location);

                    int viewX = location[0];
                    int viewY = location[1];
                    int touchX = (int) event.getRawX();
                    int touchY = (int) event.getRawY();

                    if (event.getAction() == MotionEvent.ACTION_DOWN && (touchX < viewX || touchX > viewX + lyrics_options_panel.getWidth() || touchY < viewY || touchY > viewY + lyrics_options_panel.getHeight())){
                        lyrics_options_panel.setVisibility(View.INVISIBLE);
                        cover_art.setVisibility(View.VISIBLE);
                        inLyricsOptions = false;
                        return true;
                    }
                }

                return false;
            }
        });

        add_lyrics_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    inLyricsMode = true;
                    inLyricsOptions = false;
                    cancel_button.setVisibility(View.VISIBLE);
                    edit_button.setVisibility(View.VISIBLE);
                    lyrics_textView.setText(song.getLyrics());
                    lyrics_textView.setVisibility(View.VISIBLE);
//                    lyrics_editText.setVisibility(View.GONE);
                    lyrics_textView.setMovementMethod(new ScrollingMovementMethod());
                    lyrics_options_panel.setVisibility(View.INVISIBLE);
                }
            }
        });

        search_lyrics_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    inLyricsOptions = false;
                    search_webView.setVisibility(View.VISIBLE);
                    search_cancel_button.setVisibility(View.VISIBLE);
                    search_refresh_button.setVisibility(View.VISIBLE);
                    lyrics_search_txt.setVisibility(View.VISIBLE);
                    lyrics_options_panel.setVisibility(View.INVISIBLE);
                }
            }
        });

        song_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    Bundle data = new Bundle();
                    data.putInt("AlbumPos", getAlbumPos(song.getAlbum()));
                    data.putParcelable("AlbumItem", albumFiles.get(getAlbumPos(song.getAlbum())));
                    AlbumItemFragment albumItemFragment = new AlbumItemFragment();
                    albumItemFragment.setArguments(data);
                    my_activity.loadAnAlbumFragment(albumItemFragment);
                    tabLayout.getTabAt(4).select();
                }
            }
        });

        artist_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    Bundle data = new Bundle();
                    data.putInt("ArtistPos", getArtistPos(song.getArtist()));
                    data.putParcelable("ArtistItem", artistFiles.get(getArtistPos(song.getArtist())));
                    ArtistItemFragment artistItemFragment = new ArtistItemFragment();
                    artistItemFragment.setArguments(data);
                    my_activity.loadAnArtistFragment(artistItemFragment);
                    tabLayout.getTabAt(3).select();
                }
            }
        });

        song_title.setSelected(true);
        song_title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (song != null) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    View dialogView = getLayoutInflater().inflate(R.layout.info_layout, null);
                    dialogBuilder.setView(dialogView);
                    dialog = dialogBuilder.create();
                    dialogView.setBackgroundResource(R.drawable.main_bg);

                    TextView file_name, title, album, artist, genre, composer, track_number, duration, year, size, bitrate, format, date, location;
                    ImageView cover_art = dialogView.findViewById(R.id.album_cover);

                    if(song.getArtUri() != null){
                        Glide.with(requireContext()).load(song.getArtUri()).placeholder(R.drawable.default_art).error(R.drawable.default_art).into(cover_art);
                    }else{
                        Glide.with(requireContext()).load(R.drawable.default_art).into(cover_art);
                    }

                    file_name = dialogView.findViewById(R.id.file_name);
                    file_name.setText(song.getFileName());

                    title = dialogView.findViewById(R.id.title);
                    title.setText(song.getTitle());

                    artist = dialogView.findViewById(R.id.artist);
                    artist.setText(song.getArtist());

                    album = dialogView.findViewById(R.id.album);
                    album.setText(song.getAlbum());

                    genre = dialogView.findViewById(R.id.genre);
                    genre.setText(song.getGenre());

                    composer = dialogView.findViewById(R.id.composer);
                    composer.setText(song.getComposer());

                    track_number = dialogView.findViewById(R.id.track_number);
                    track_number.setText(song.getNumber());

                    duration = dialogView.findViewById(R.id.duration);
                    try {
                        int durationTotal = Integer.parseInt(song.getDuration()) / 1000;
                        duration.setText(formattedTime(durationTotal));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    year = dialogView.findViewById(R.id.year);
                    year.setText(song.getYear());

                    size = dialogView.findViewById(R.id.size);
                    long i = Long.valueOf(song.getSize()) / (1024 * 1024);
                    size.setText(String.valueOf(i) + "mb");

                    bitrate = dialogView.findViewById(R.id.bitrate);
                    bitrate.setText(song.getBitrate());

                    format = dialogView.findViewById(R.id.format);
                    format.setText(MimeTypeMap.getFileExtensionFromUrl(song.getPath()));

                    date = dialogView.findViewById(R.id.date);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mmaa", Locale.getDefault());
                    Date x = new Date(Long.valueOf(song.getDate()));
                    date.setText(dateFormat.format(x));

                    location = dialogView.findViewById(R.id.location);
                    location.setText(song.getPath());

                    ImageView cancel = dialogView.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });

                    dialog.show();
                }
            }
        });

        playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    addSongToPlayList();
                }
            }
        });

        favourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if(song.getFaveStatus().equals("0")){
                        favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                        song.setFaveStatus("1");
                        favDB.insertIntoDB(song.getTitle(), song.getArtist(), song.getAlbum(), song.getId(), song.getGenre(), song.getPath(), song.getDuration(), song.getComposer(), song.getNumber(),
                                song.getDate(), song.getYear(), song.getSize(), song.getBitrate(), song.getFileName(), song.getArtUri(), song.getFaveStatus());
                        faveSongs.add(song);
                    }
                    else{
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                        song.setFaveStatus("0");
                        favDB.removeFromFave(song.getId());
                        for(MusicFiles item : faveSongs){
                            if(item.getId().equals(song.getId())){
                                faveSongs.remove(item);
                                break;
                            }
                        }
                    }
                    if(favesAdapter != null) {
                        favesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

//        menu_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(getContext(), v);
//                popupMenu.getMenuInflater().inflate(R.menu.player_activity_menu, popupMenu.getMenu());
//                popupMenu.setGravity(Gravity.END);
//                popupMenu.show();
//                popupMenu.setOnMenuItemClickListener(item -> {
//                    switch (item.getItemId()){
//                        case R.id.edit_tags_menu:
//                            Intent intent = new Intent(getActivity(), EditTagsActivity.class);
//                            intent.putExtra("title", song_title.getText().toString());
//                            intent.putExtra("artist", artist_name.getText().toString());
//                            intent.putExtra("path", song.getPath());
//                            intent.putExtra("album", song_album.getText().toString());
////                            intent.putExtra("genre", song_genre.getText().toString());
////                            intent.putExtra("year", song_year.getText().toString());
//
//                            intent.putExtra("Uri", uri.toString());
//                            startActivity(intent);
//                            break;
//                    }
//                    return true;
//                });
//            }
//        });

        lyricsHandler();
        searchLyrics();

        if(my_activity != null) {
            tabLayout = my_activity.findViewById(R.id.tab_layout);
        }

        if(alive){
            if (STATUS.equals("1")){
                favourite_btn.setImageResource(R.drawable.ic_fave_filled);
            }
            if(servicesMediaPlayer != null) {
                if(servicesMediaPlayer.isPlaying()) {
                    song_title.setText(song.getTitle());
                    artist_name.setText(song.getArtist());
                    song_album.setText(song.getAlbum());
                    playpause_button.setImageResource(R.drawable.ic_pause);
                }else{
                    song_title.setText(song.getTitle());
                    artist_name.setText(song.getArtist());
                    song_album.setText(song.getAlbum());
                    playpause_button.setImageResource(R.drawable.ic_play);
                }
            }else{
                song_title.setText(song.getTitle());
                artist_name.setText(song.getArtist());
                song_album.setText(song.getAlbum());
                playpause_button.setImageResource(R.drawable.ic_pause);
            }

            try {
                seekBar.setMax(servicesMediaPlayer.getDuration() / 1000);
            } catch (NullPointerException e){
                e.printStackTrace();
                return;
            }

            metaData(uri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        getParentFragmentManager().setFragmentResultListener("PlayData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                song = result.getParcelable("PlaySong");
                current_position = result.getInt("PlaySongPos", -1);
                songsList = result.getParcelableArrayList("CurrentQueue");

                song_title.setText(song.getTitle());
                artist_name.setText(song.getArtist());
                song_album.setText(song.getAlbum());

                if(song.getFaveStatus().equals("0")){
                    favourite_btn.setImageResource(R.drawable.ic_favorite);
                }else if(song.getFaveStatus().equals("1")){
                    favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                }

                getIntentExtras();
                load_search();
            }
        });

        getParentFragmentManager().setFragmentResultListener("StoredData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                song = result.getParcelable("PlaySong");
                current_position = result.getInt("PlaySongPos", -1);
                songsList = result.getParcelableArrayList("CurrentQueue");
                seekPos = result.getInt("SeekbarPos", -1);

                song_title.setText(song.getTitle());
                artist_name.setText(song.getArtist());
                song_album.setText(song.getAlbum());

                if(song.getFaveStatus().equals("0")){
                    favourite_btn.setImageResource(R.drawable.ic_favorite);
                }else if(song.getFaveStatus().equals("1")){
                    favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                }

                getStoredData();
                load_search();
            }
        });

        return view;
    }

    private void getStoredData() {
        if (songsList != null) {
            playpause_button.setImageResource(R.drawable.ic_play);
            uri = Uri.parse(song.getPath());
        }
        metaData(uri);

        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("StoredPos", current_position);
        intent.putExtra("SeekPos", seekPos);
        getActivity().startService(intent);
    }

    private void initViews() {
        song_title = view.findViewById(R.id.song_name_1);
        song_album = view.findViewById(R.id.album_name_1);
        artist_name = view.findViewById(R.id.song_artist_1);
        duration_played = view.findViewById(R.id.played_duration_1);
        duration_total = view.findViewById(R.id.total_duration_1);
        cover_art = view.findViewById(R.id.cover_art_1);
        next_button = view.findViewById(R.id.next_btn_1);
        previous_button = view.findViewById(R.id.previous_btn_1);
        shuffle_button = view.findViewById(R.id.shuffle_btn_1);
        repeat_button = view.findViewById(R.id.repeat_btn_1);
        playpause_button = view.findViewById(R.id.play_pause_btn_1);
        seekBar = view.findViewById(R.id.seek_bar_1);
        cover_art_n_lyrics = view.findViewById(R.id.song_art_1);
        lyrics_textView = view.findViewById(R.id.lyrics_textView);
        edit_button = view.findViewById(R.id.edit_lyrics_button_1);
        cancel_button = view.findViewById(R.id.cancel_button_1);
        edit_complete_button = view.findViewById(R.id.edit_complete_1);
        search_webView = view.findViewById(R.id.search_webView_1);
        search_cancel_button = view.findViewById(R.id.genius_cancel_button_1);
        search_refresh_button = view.findViewById(R.id.genius_refresh_button_1);
        progressBar = view.findViewById(R.id.geniusProgressBar_1);
        fast_forward = view.findViewById(R.id.fast_forward);
        fast_rewind = view.findViewById(R.id.fast_rewind);
        loop_btn = view.findViewById(R.id.loop_btn);
        seekBarLoop = view.findViewById(R.id.seek_bar_loop);
        start_text = view.findViewById(R.id.start_loop);
        end_text = view.findViewById(R.id.end_loop);
        loop_panel = view.findViewById(R.id.loop_holder);
        lyrics_options_panel = view.findViewById(R.id.lyrics_options);
        main_container = view.findViewById(R.id.mContainer_1);
        add_lyrics_btn = view.findViewById(R.id.add_lyrics_icon);
        search_lyrics_btn = view.findViewById(R.id.search_lyrics_icon);
        lyrics_search_txt = view.findViewById(R.id.lyrics_search_text);
        favourite_btn = view.findViewById(R.id.favourite);
        playlist_btn = view.findViewById(R.id.playlist);
    }

    private void getIntentExtras() {
        if (songsList != null) {
            playpause_button.setImageResource(R.drawable.ic_pause);
            //uri = Uri.parse(songsList.get(position).getPath());
            uri = Uri.parse(song.getPath());
        }
        metaData(uri);

        musicService.showNotification(R.drawable.ic_pause_white, uri);
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("ServicePos", current_position);
        getActivity().startService(intent);
    }

    public void metaData(Uri uri) throws NullPointerException, RuntimeException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri.toString());
        } catch (RuntimeException e){
            e.printStackTrace();
        }

        try {
            int durationTotal = Integer.parseInt(song.getDuration()) / 1000;
            duration_total.setText(formattedTime(durationTotal));
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

//        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(song.getArtUri()));
//        Glide.with(this).load(artUri).error(R.drawable.default_art).into(cover_art);

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null){
            defaultArtPresent = false;
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this.getContext(), cover_art, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@org.jetbrains.annotations.Nullable @androidx.annotation.Nullable Palette palette) {
                    swatch = palette.getDominantSwatch();
                    if(swatch != null){
                        RelativeLayout mContainer = view.findViewById(R.id.mContainer_1);
                        GradientDrawable gradientDrawableBG = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBG);
                        song_title.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.WHITE);
                        song_album.setTextColor(Color.WHITE);
                        lyrics_search_txt.setTextColor(swatch.getBodyTextColor());
                        cancel_button.setColorFilter(swatch.getBodyTextColor());
                        edit_button.setColorFilter(swatch.getBodyTextColor());
                        edit_complete_button.setColorFilter(swatch.getBodyTextColor());
                        search_cancel_button.setColorFilter(swatch.getBodyTextColor());
                        search_refresh_button.setColorFilter(swatch.getBodyTextColor());
                        lyrics_editTextView.setTextColor(swatch.getBodyTextColor());
                        lyrics_textView.setTextColor(swatch.getBodyTextColor());

                        if(tabLayout.getSelectedTabPosition() == 1){
                            tabLayout.setBackgroundColor(swatch.getRgb());
                            my_activity.setStatusBarColor(swatch.getRgb());
                        }
                        else{
                            tabLayout.setBackgroundResource(R.drawable.main_bg);
                            my_activity.setStatusBarColor(requireContext().getResources().getColor(R.color.bg_black));
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Swatch is null", Toast.LENGTH_LONG).show();
                        RelativeLayout mContainer = view.findViewById(R.id.mContainer_1);
                        GradientDrawable gradientDrawableBG = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBG);
                        song_title.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.WHITE);
                        song_album.setTextColor(Color.WHITE);
                        tabLayout.setBackground(gradientDrawableBG);
                        my_activity.setStatusBarColor(Color.BLACK);
                    }
                }
            });
        }
        else{
            defaultArtPresent = true;
            Glide.with(this).asBitmap().load(R.drawable.default_art).into(cover_art);
            RelativeLayout mContainer = view.findViewById(R.id.mContainer_1);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_title.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.WHITE);
            song_album.setTextColor(Color.WHITE);
            tabLayout.setBackgroundResource(R.drawable.main_bg);
            my_activity.setStatusBarColor(requireContext().getResources().getColor(R.color.bg_black));
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        }
        else{
            return totalOut;
        }
    }

    private void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    public void load_search() {
        WebSettings webSettings = search_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        search_webView.setWebViewClient(new MyWebViewClient());

        search_webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        title = song_title.getText().toString();
        artist = artist_name.getText().toString();
        album = song_album.getText().toString();

        search_webView.loadUrl("https://www.google.com/search?q=genius lyrics " + title + " " + artist + " " + album);
    }

    private void searchLyrics(){
        search_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_webView.getVisibility() == View.VISIBLE){
                    search_webView.setVisibility(View.INVISIBLE);
                    search_refresh_button.setVisibility(View.INVISIBLE);
                    search_cancel_button.setVisibility(View.INVISIBLE);
                    lyrics_search_txt.setVisibility(View.INVISIBLE);
                    cover_art.setVisibility(View.VISIBLE);
                }
            }
        });

        search_refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_search();
            }
        });
    }

    private void lyricsHandler() {
        cover_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activated) {
                    Toast.makeText(getContext(), "Hold down for lyrics options", Toast.LENGTH_SHORT).show();
                    activated = true;
                }
            }
        });

        cover_art.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cover_art.setVisibility(View.INVISIBLE);
                lyrics_options_panel.setVisibility(View.VISIBLE);
                inLyricsOptions = true;
                inLyricsMode = false;
                return true;
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lyrics_textView.getVisibility() == View.VISIBLE){
                    lyrics_textView.setVisibility(View.INVISIBLE);
                    cover_art.setVisibility(View.VISIBLE);
                    edit_button.setVisibility(View.INVISIBLE);
                    edit_complete_button.setVisibility(View.INVISIBLE);
                    cancel_button.setVisibility(View.INVISIBLE);
                    lyrics_textView.requestFocus();
                    inLyricsMode = false;
                    inEditLyrics = false;
                }
                else if(lyrics_editTextView.getVisibility() == View.VISIBLE){
                    convertTo_textView();
                    lyrics_textView.setVisibility(View.INVISIBLE);
                    edit_button.setVisibility(View.INVISIBLE);
                    edit_complete_button.setVisibility(View.INVISIBLE);
                    cancel_button.setVisibility(View.INVISIBLE);
                    cover_art.setVisibility(View.VISIBLE);
                    cover_art.requestFocus();
                    inLyricsMode = false;
                    inEditLyrics = false;
                }
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inEditLyrics = true;
                song.setLyrics(lyrics_textView.getText().toString());
                convertTo_editTextView();
                edit_complete_button.setVisibility(View.VISIBLE);
                edit_button.setVisibility(View.INVISIBLE);
                lyrics_textView.setVisibility(View.GONE);
            }
        });

        edit_complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inEditLyrics = false;
                song.setLyrics(lyrics_editTextView.getText().toString());
                convertTo_textView();
                edit_complete_button.setVisibility(View.INVISIBLE);
                edit_button.setVisibility(View.VISIBLE);
                lyrics_textView.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void convertTo_textView() {
        lyrics_textView.setLayoutParams(lyrics_editTextView.getLayoutParams());
        lyrics_textView.setText(lyrics_editTextView.getText());
        lyrics_textView.setTextColor(lyrics_editTextView.getTextColors());
        lyrics_textView.setPadding(lyrics_editTextView.getPaddingLeft(), lyrics_editTextView.getPaddingTop(), lyrics_editTextView.getPaddingRight(), lyrics_editTextView.getPaddingBottom());
        lyrics_textView.setGravity(lyrics_editTextView.getGravity());
        lyrics_textView.setMovementMethod(new ScrollingMovementMethod());

        ViewGroup parentView = (ViewGroup) lyrics_editTextView.getParent();
        int index = parentView.indexOfChild(lyrics_editTextView);
        parentView.removeView(lyrics_editTextView);
        parentView.addView(lyrics_textView, index);

        lyrics_textView.requestFocus();
        lyrics_textView.setMovementMethod(new ScrollingMovementMethod());
        InputMethodManager imm = (InputMethodManager) parentView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(parentView.getWindowToken(), 0);
    }

    private void convertTo_editTextView(){
        lyrics_editTextView.setLayoutParams(lyrics_textView.getLayoutParams());
        lyrics_editTextView.setText(lyrics_textView.getText());
        lyrics_editTextView.setTextColor(lyrics_textView.getTextColors());
        lyrics_editTextView.setPadding(lyrics_textView.getPaddingLeft(), lyrics_textView.getPaddingTop(), lyrics_textView.getPaddingRight(), lyrics_textView.getPaddingBottom());
        lyrics_editTextView.setGravity(lyrics_textView.getGravity());
        lyrics_editTextView.setBackgroundResource(android.R.color.transparent);

        ViewGroup parentView = (ViewGroup) lyrics_textView.getParent();
        int index = parentView.indexOfChild(lyrics_textView);
        parentView.removeView(lyrics_textView);
        parentView.addView(lyrics_editTextView, index);

        lyrics_editTextView.requestFocus();
//        lyrics_editTextView.setEnabled(true);
//        lyrics_editTextView.setFocusable(true);
//        lyrics_editTextView.setFocusableInTouchMode(true);
        lyrics_editTextView.setTextIsSelectable(true);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(lyrics_editTextView, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onResume() {
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, this, BIND_AUTO_CREATE);

        playThreadButton();
        nextThreadButton();
        previousThreadButton();
        loopThreadButton();

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unbindService(this);
    }

    private void previousThreadButton() {
        previous_thread = new Thread(){
            @Override
            public void run() {
                super.run();
                previous_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(search_webView.getVisibility() == View.VISIBLE){
                            search_webView.setVisibility(View.INVISIBLE);
                            search_cancel_button.setVisibility(View.INVISIBLE);
                        }

                        if(song == null){
                            return;
                        }
                        else {
                            previousbuttonClicked();
                            load_search();
                        }
                    }
                });
            }
        };
        previous_thread.start();
    }

    public void previousbuttonClicked() {
        try {
            if (inLoop) {
                Toast.makeText(musicService.getBaseContext(), "Please exist loop mode", Toast.LENGTH_SHORT).show();
            } else {
                if (musicService.isPlaying()) {
                    if (repeatCLicked) {
                        current_position = current_position;
                    } else {
                        if(replayCheck(Integer.parseInt(song.getDuration()))){
                            current_position = current_position;
                        }else {
                            current_position = ((current_position - 1) < 0 ? (songsList.size() - 1) : (current_position - 1));
                        }
                    }

                    musicService.stop();
                    musicService.release();

                    song = songsList.get(current_position);
                    uri = Uri.parse(songsList.get(current_position).getPath());
                    musicService.createMediaPlayer(current_position);

                    if(song.getFaveStatus().equals("0")){
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                    }else if(song.getFaveStatus().equals("1")){
                        favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                    }

                    if(getActivity() != null) {
                        metaData(uri);
                        song_title.setText(songsList.get(current_position).getTitle());
                        artist_name.setText(songsList.get(current_position).getArtist());
                        song_album.setText(songsList.get(current_position).getAlbum());
                        seekBar.setMax(musicService.getDuration() / 1000);

                        if (cover_art.getVisibility() == View.INVISIBLE) {
                            search_webView.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            lyrics_search_txt.setVisibility(View.INVISIBLE);
                            lyrics_textView.setVisibility(View.INVISIBLE);
                            lyrics_options_panel.setVisibility(View.INVISIBLE);

                            edit_button.setVisibility(View.INVISIBLE);
                            edit_complete_button.setVisibility(View.INVISIBLE);
                            cancel_button.setVisibility(View.INVISIBLE);

                            cover_art.setVisibility(View.VISIBLE);
                        }

                        if (inEditLyrics) {
//                            convertTo_textView();
                            inEditLyrics = false;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (musicService != null) {
                                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                                    seekBar.setProgress(mCurrentPosition);
                                    duration_played.setText(formattedTime(mCurrentPosition));
                                }

                                handler.postDelayed(this, 1000);
                            }
                        });
                        playpause_button.setBackgroundResource(R.drawable.ic_pause);
                    }
                    musicService.onCompleted();
                    musicService.showNotification(R.drawable.ic_pause_white, uri);
                    musicService.start();

                    int pos = -1;
                    for (int i = 0; i < myQueues.size(); i++){
                        if(myQueues.get(i).isCurrent()){
                            pos = i;
                        }
                    }
                    QueueListItems item = myQueues.get(pos);
                    myQueues.get(pos).setPlayPosition(item.getPlayPosition() -1);

                    historySongs.add(0, song);
                    if(historyAdapter != null){
                        historyAdapter.notifyDataSetChanged();
                    }

                    recentList.remove(song);
                    recentList.add(0, song);
                    if(recentList.size() > 20){
                        recentList.remove(recentList.size() - 1);
                    }
                    recentAdapter.notifyDataSetChanged();
                } else {
                    if (repeatCLicked) {
                        current_position = current_position;
                    } else {
                        if(replayCheck(Integer.parseInt(song.getDuration()))){
                            current_position = current_position;
                        }else {
                            current_position = ((current_position - 1) < 0 ? (songsList.size() - 1) : (current_position - 1));
                        }
                    }
                    musicService.stop();
                    musicService.release();

                    song = songsList.get(current_position);
                    uri = Uri.parse(songsList.get(current_position).getPath());
                    musicService.createMediaPlayer(current_position);

                    if(song.getFaveStatus().equals("0")){
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                    }else if(song.getFaveStatus().equals("1")){
                        favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                    }

                    if(getActivity() != null) {
                        metaData(uri);
                        song_title.setText(songsList.get(current_position).getTitle());
                        artist_name.setText(songsList.get(current_position).getArtist());
                        song_album.setText(songsList.get(current_position).getAlbum());
                        seekBar.setMax(musicService.getDuration() / 1000);

                        if (cover_art.getVisibility() == View.INVISIBLE) {
                            search_webView.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            lyrics_search_txt.setVisibility(View.INVISIBLE);
                            lyrics_textView.setVisibility(View.INVISIBLE);
                            lyrics_options_panel.setVisibility(View.INVISIBLE);

                            edit_button.setVisibility(View.INVISIBLE);
                            edit_complete_button.setVisibility(View.INVISIBLE);
                            cancel_button.setVisibility(View.INVISIBLE);

                            cover_art.setVisibility(View.VISIBLE);
                        }

                        if (inEditLyrics) {
//                            convertTo_textView();
                            inEditLyrics = false;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (musicService != null) {
                                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                                    seekBar.setProgress(mCurrentPosition);
                                    duration_played.setText(formattedTime(mCurrentPosition));
                                }

                                handler.postDelayed(this, 1000);
                            }
                        });
                        playpause_button.setBackgroundResource(R.drawable.ic_play);
                    }
                    musicService.onCompleted();
                    musicService.showNotification(R.drawable.ic_play_white, uri);

                    int pos = -1;
                    for (int i = 0; i < myQueues.size(); i++){
                        if(myQueues.get(i).isCurrent()){
                            pos = i;
                        }
                    }
                    QueueListItems item = myQueues.get(pos);
                    myQueues.get(pos).setPlayPosition(item.getPlayPosition() - 1);

                    historySongs.add(0, song);
                    if(historyAdapter != null){
                        historyAdapter.notifyDataSetChanged();
                    }

                    recentList.remove(song);
                    recentList.add(0, song);
                    if(recentList.size() > 20){
                        recentList.remove(recentList.size() - 1);
                    }
                    recentAdapter.notifyDataSetChanged();
                }
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(musicService.getBaseContext(), "Invalid File", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextThreadButton() {
        next_thread = new Thread(){
            @Override
            public void run() {
                super.run();
                next_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(search_webView.getVisibility() == View.VISIBLE){
                            search_webView.setVisibility(View.INVISIBLE);
                            search_cancel_button.setVisibility(View.INVISIBLE);
                        }

                        if(song == null){
                            return;
                        }
                        else {
                            nextbuttonClicked();
                            load_search();
                        }
                    }
                });
            }
        };
        next_thread.start();
    }

    public void nextbuttonClicked() {
        try {
            if (inLoop) {
                Toast.makeText(musicService.getBaseContext(), "Please exist loop mode", Toast.LENGTH_SHORT).show();
            } else {
                if (musicService.isPlaying()) {
                    musicService.stop();
                    musicService.release();

                    if (repeatCLicked) {
                        current_position = current_position;
                    } else {
                        current_position = ((current_position + 1) % songsList.size());
                    }

                    song = songsList.get(current_position);
                    uri = Uri.parse(songsList.get(current_position).getPath());
                    musicService.createMediaPlayer(current_position);

                    if(song.getFaveStatus().equals("0")){
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                    }else if(song.getFaveStatus().equals("1")){
                        favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                    }

                    if(getActivity() != null) {
                        metaData(uri);
                        song_title.setText(songsList.get(current_position).getTitle());
                        artist_name.setText(songsList.get(current_position).getArtist());
                        song_album.setText(songsList.get(current_position).getAlbum());
                        seekBar.setMax(musicService.getDuration() / 1000);

                        if (cover_art.getVisibility() == View.INVISIBLE) {
                            search_webView.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            lyrics_search_txt.setVisibility(View.INVISIBLE);
                            lyrics_textView.setVisibility(View.INVISIBLE);
                            lyrics_options_panel.setVisibility(View.INVISIBLE);

                            edit_button.setVisibility(View.INVISIBLE);
                            edit_complete_button.setVisibility(View.INVISIBLE);
                            cancel_button.setVisibility(View.INVISIBLE);

                            cover_art.setVisibility(View.VISIBLE);
                        }

                        if (inEditLyrics) {
                            convertTo_textView();
                            inEditLyrics = false;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (musicService != null) {
                                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                                    seekBar.setProgress(mCurrentPosition);
                                    duration_played.setText(formattedTime(mCurrentPosition));
                                }

                                handler.postDelayed(this, 1000);
                            }
                        });
                        playpause_button.setBackgroundResource(R.drawable.ic_pause);
                    }
                    musicService.onCompleted();
                    musicService.showNotification(R.drawable.ic_pause_white, uri);
                    musicService.start();

                    int pos = -1;
                    for (int i = 0; i < myQueues.size(); i++){
                        if(myQueues.get(i).isCurrent()){
                            pos = i;
                        }
                    }
                    QueueListItems item = myQueues.get(pos);
                    myQueues.get(pos).setPlayPosition(item.getPlayPosition() + 1);

                    historySongs.add(0, song);
                    if(historyAdapter != null){
                        historyAdapter.notifyDataSetChanged();
                    }

                    recentList.remove(song);
                    recentList.add(0, song);
                    if(recentList.size() > 20){
                        recentList.remove(recentList.size() - 1);
                    }
                    recentAdapter.notifyDataSetChanged();
                } else {
                    musicService.stop();
                    musicService.release();

                    if (repeatCLicked) {
                        current_position = current_position;
                    } else {
                        current_position = ((current_position + 1) % songsList.size());
                    }

                    song = songsList.get(current_position);
                    uri = Uri.parse(songsList.get(current_position).getPath());
                    musicService.createMediaPlayer(current_position);

                    if(song.getFaveStatus().equals("0")){
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                    }else if(song.getFaveStatus().equals("1")){
                        favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                    }

                    if(getActivity() != null) {
                        metaData(uri);
                        song_title.setText(songsList.get(current_position).getTitle());
                        artist_name.setText(songsList.get(current_position).getArtist());
                        song_album.setText(songsList.get(current_position).getAlbum());
                        seekBar.setMax(musicService.getDuration() / 1000);

                        if (cover_art.getVisibility() == View.INVISIBLE) {
                            search_webView.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            search_refresh_button.setVisibility(View.INVISIBLE);
                            lyrics_search_txt.setVisibility(View.INVISIBLE);
                            lyrics_textView.setVisibility(View.INVISIBLE);
                            lyrics_options_panel.setVisibility(View.INVISIBLE);

                            edit_button.setVisibility(View.INVISIBLE);
                            edit_complete_button.setVisibility(View.INVISIBLE);
                            cancel_button.setVisibility(View.INVISIBLE);

                            cover_art.setVisibility(View.VISIBLE);
                        }

                        if (inEditLyrics) {
                            convertTo_textView();
                            inEditLyrics = false;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (musicService != null) {
                                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                                    seekBar.setProgress(mCurrentPosition);
                                    duration_played.setText(formattedTime(mCurrentPosition));
                                }

                                handler.postDelayed(this, 1000);
                            }
                        });
                        playpause_button.setBackgroundResource(R.drawable.ic_play);
                    }
                    musicService.onCompleted();
                    musicService.showNotification(R.drawable.ic_play_white, uri);

                    int pos = -1;
                    for (int i = 0; i < myQueues.size(); i++){
                        if(myQueues.get(i).isCurrent()){
                            pos = i;
                        }
                    }
                    QueueListItems item = myQueues.get(pos);
                    myQueues.get(pos).setPlayPosition(item.getPlayPosition() + 1);

                    historySongs.add(0, song);
                    if(historyAdapter != null){
                        historyAdapter.notifyDataSetChanged();
                    }

                    recentList.remove(song);
                    recentList.add(0, song);
                    if(recentList.size() > 20){
                        recentList.remove(recentList.size() - 1);
                    }
                    recentAdapter.notifyDataSetChanged();
                }
            }
        } catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(musicService.getBaseContext(), "Invalid file", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void playThreadButton() {
        play_thread = new Thread(){
            @Override
            public void run() {
                super.run();
                playpause_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(song == null){
                            return;
                        }
                        else {
                            try {
                                playpausebuttonClicked();
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        play_thread.start();
    }

    public void playpausebuttonClicked() {
        if(inLoop){
            if(loopMP.isPlaying()){
                loopMP.pause();
                loopPaused = true;

                if(getActivity() != null) {
                    playpause_button.setImageResource(R.drawable.ic_play);

                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            if (loopMP != null && inLoop) {
                                int mCurrentPosition = loopMP.getCurrentPosition() / 1000;
                                seekBarLoop.setProgress(mCurrentPosition);
                                start_text.setText(formattedTime(mCurrentPosition));

                                if (loopMP.getCurrentPosition() >= end_loop_pos) {
                                    loopMP.pause();

                                    seekBarLoop.setMin(start_loop_pos / 1000);
                                    loopMP.seekTo(start_loop_pos);
                                    seekBarLoop.setMax(end_loop_pos / 1000);
                                    loopMP.start();
                                }
                            }

                            handler.postDelayed(this, 1000);
                        }
                    });
                }
            }
            else{
                loopMP.start();
                loopPaused = false;

                if(getActivity() != null) {
                    playpause_button.setImageResource(R.drawable.ic_pause);
                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            if (loopMP != null && inLoop) {
                                int mCurrentPosition = loopMP.getCurrentPosition() / 1000;
                                seekBarLoop.setProgress(mCurrentPosition);
                                start_text.setText(formattedTime(mCurrentPosition));

                                if (loopMP.getCurrentPosition() >= end_loop_pos) {
                                    loopMP.pause();

                                    seekBarLoop.setMin(start_loop_pos / 1000);
                                    loopMP.seekTo(start_loop_pos);
                                    seekBarLoop.setMax(end_loop_pos / 1000);
                                    loopMP.start();
                                }
                            }

                            handler.postDelayed(this, 1000);
                        }
                    });
                }
            }
        }
        else{
            if (musicService.isPlaying()){
                musicService.showNotification(R.drawable.ic_play_white, uri);
                musicService.pause();
                if(getActivity() != null) {
                    playpause_button.setImageResource(R.drawable.ic_play);
                    playpause_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dull_white)));
                    seekBar.setMax(musicService.getDuration() / 1000);
                    seekBar.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.dull_white)));
                    seekBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.dull_white)));
                    next_button.setColorFilter(getResources().getColor(R.color.dull_white));
                    previous_button.setColorFilter(getResources().getColor(R.color.dull_white));
                    favourite_btn.setColorFilter(getResources().getColor(R.color.dull_white));
                    playlist_btn.setColorFilter(getResources().getColor(R.color.dull_white));
                    duration_played.setTextColor(getResources().getColor(R.color.dull_white));
                    duration_total.setTextColor(getResources().getColor(R.color.dull_white));
                    song_title.setTextColor(getResources().getColor(R.color.dull_white));
                    song_album.setTextColor(getResources().getColor(R.color.dull_white));
                    artist_name.setTextColor(getResources().getColor(R.color.dull_white));
                    cover_art.setPadding(8, 8, 8, 8);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (musicService != null) {
                                int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                                seekBar.setProgress(mCurrentPosition);
                            }
                            handler.postDelayed(this, 1000);
                        }
                    });
                    if(isCurrentList) {
                        play_pause.setImageResource(R.drawable.ic_play_white);
                    }
                }
            }
            else{
                musicService.showNotification(R.drawable.ic_pause_white, uri);
                musicService.start();
                if(getActivity() != null) {
                    playpause_button.setImageResource(R.drawable.ic_pause);
                    playpause_button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    seekBar.setMax(musicService.getDuration() / 1000);
                    seekBar.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
                    seekBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
                    next_button.setColorFilter(Color.WHITE);
                    previous_button.setColorFilter(getResources().getColor(R.color.dull_white));
                    favourite_btn.setColorFilter(getResources().getColor(R.color.dull_white));
                    playlist_btn.setColorFilter(getResources().getColor(R.color.dull_white));
                    duration_played.setTextColor(Color.WHITE);
                    duration_total.setTextColor(Color.WHITE);
                    song_title.setTextColor(Color.WHITE);
                    song_album.setTextColor(Color.WHITE);
                    artist_name.setTextColor(Color.WHITE);
                    cover_art.setPadding(0, 0, 0, 0);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (musicService != null) {
                                int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                                seekBar.setProgress(mCurrentPosition);
                            }

                            handler.postDelayed(this, 1000);
                        }
                    });

                    if(isCurrentList) {
                        play_pause.setImageResource(R.drawable.ic_pause_white);
                    }
                }
            }
        }
    }

    public void playpauseForOtherQueues(int pos, QueueListItems item) {
        songsList = item.getSongs();
        current_position = pos;
        song = songsList.get(current_position);

        if (songsList != null) {
            playpause_button.setImageResource(R.drawable.ic_pause);
            //uri = Uri.parse(songsList.get(position).getPath());
            uri = Uri.parse(song.getPath());
        }
        metaData(uri);
        song_title.setText(songsList.get(current_position).getTitle());
        artist_name.setText(songsList.get(current_position).getArtist());
        song_album.setText(songsList.get(current_position).getAlbum());

        musicService.showNotification(R.drawable.ic_pause_white, uri);
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("ServicePos", current_position);
        getActivity().startService(intent);
    }

    private void loopThreadButton(){
        loop_thread = new Thread(){
            @Override
            public void run() {
                super.run();
                loop_btn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        loopClicked();
                        return true;
                    }
                });
            }
        };
        loop_thread.start();
    }

    public void loopClicked(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.loop_panel, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();

        EditText start_pos_sec, start_pos_min, end_pos_sec, end_pos_min;
        start_pos_sec = dialogView.findViewById(R.id.start_position_seconds);
        start_pos_min = dialogView.findViewById(R.id.start_position_minutes);
        end_pos_sec = dialogView.findViewById(R.id.end_position_seconds);
        end_pos_min = dialogView.findViewById(R.id.end_position_minutes);
        ImageView done_button = dialogView.findViewById(R.id.done_btn);
        TextView error_text = dialogView.findViewById(R.id.error_text);
        ImageView extractBtn = dialogView.findViewById(R.id.extract_loop);

        done_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int start_min, start_sec, end_min, end_sec;

                try {
                    start_min = Integer.valueOf(start_pos_min.getText().toString());
                    start_sec = Integer.valueOf(start_pos_sec.getText().toString());
                    end_min = Integer.valueOf(end_pos_min.getText().toString());
                    end_sec = Integer.valueOf(end_pos_sec.getText().toString());

                    start_loop_pos = ((start_min * 60) + start_sec) * 1000;
                    end_loop_pos = ((end_min * 60) + end_sec) * 1000;
                } catch (NumberFormatException e){
                    Toast.makeText(getContext(), "Empty fields", Toast.LENGTH_SHORT).show();
                    error_text.setVisibility(View.VISIBLE);

                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error_text.setVisibility(View.INVISIBLE);
                        }
                    }, 1500);
                    return;
                }

                try{
                    if (end_loop_pos <= start_loop_pos){
                        throw new InvalidNumberPairException("Invalid range");
                    }
                } catch (InvalidNumberPairException e){
                    Toast.makeText(getContext(), "Invalid range", Toast.LENGTH_SHORT).show();
                    error_text.setVisibility(View.VISIBLE);

                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error_text.setVisibility(View.INVISIBLE);
                        }
                    }, 1500);
                    return;
                }

                try{
                    if(start_loop_pos >= musicService.getDuration() || start_loop_pos < 0 || end_loop_pos <= 0 || end_loop_pos > musicService.getDuration()){
                        throw new InvalidNumberPairException("Outta range");
                    }
                } catch (InvalidNumberPairException e){
                    Toast.makeText(getActivity(), "Out of range", Toast.LENGTH_SHORT).show();
                    error_text.setVisibility(View.VISIBLE);

                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error_text.setVisibility(View.INVISIBLE);
                        }
                    }, 1500);
                    return;
                }

                inLoop = true;
                loop_panel.setVisibility(View.VISIBLE);
                loop_btn.setImageResource(R.drawable.ic_loop_on);
                end_text.setText(end_pos_min.getText().toString() + ":" + end_pos_sec.getText().toString());

                if (loopMP != null) {
                    Toast.makeText(getContext(), "Loop aint null", Toast.LENGTH_SHORT).show();
                    loopMP.stop();
                    loopMP.release();
                    loopMP = MediaPlayer.create(getContext(), uri);
                    loopMP.start();
                    seekBarLoop.setMin(start_loop_pos / 1000);
                    loopMP.seekTo(start_loop_pos);
                    seekBarLoop.setMax(end_loop_pos / 1000);

                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            if (loopMP != null && inLoop){
                                int mCurrentPosition = loopMP.getCurrentPosition() / 1000;
                                seekBarLoop.setProgress(mCurrentPosition);
                                start_text.setText(formattedTime(mCurrentPosition));

                                if(loopMP.getCurrentPosition() >= end_loop_pos){
                                    loopMP.pause();

                                    seekBarLoop.setMin(start_loop_pos / 1000);
                                    loopMP.seekTo(start_loop_pos);
                                    seekBarLoop.setMax(end_loop_pos / 1000);
                                    loopMP.start();
                                }
                            }

                            handler.postDelayed(this, 1000);
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Loop is null", Toast.LENGTH_SHORT).show();
                    loopMP = MediaPlayer.create(getContext(), uri);
                    loopMP.start();
                    seekBarLoop.setMin(start_loop_pos / 1000);
                    loopMP.seekTo(start_loop_pos);
                    seekBarLoop.setMax(end_loop_pos / 1000);

                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            if (loopMP != null && inLoop){
                                int mCurrentPosition = loopMP.getCurrentPosition() / 1000;
                                seekBarLoop.setProgress(mCurrentPosition);
                                start_text.setText(formattedTime(mCurrentPosition));

                                if(loopMP.getCurrentPosition() >= end_loop_pos){
                                    loopMP.pause();

                                    seekBarLoop.setMin(start_loop_pos / 1000);
                                    loopMP.seekTo(start_loop_pos);
                                    seekBarLoop.setMax(end_loop_pos / 1000);
                                    loopMP.start();
                                }
                            }

                            handler.postDelayed(this, 1000);
                        }
                    });
                }

                if(musicService.isPlaying()) {
                    musicService.pause();
                }
                else{
                    playpause_button.setImageResource(R.drawable.ic_pause);
                }

                seekBar.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.icon_grey)));
                seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.icon_grey)));
                seekBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.icon_grey)));
                next_button.setColorFilter(getResources().getColor(R.color.icon_grey));
                previous_button.setColorFilter(getResources().getColor(R.color.icon_grey));
                duration_played.setTextColor(getResources().getColor(R.color.icon_grey));
                duration_total.setTextColor(getResources().getColor(R.color.icon_grey));
                song_title.setTextColor(getResources().getColor(R.color.icon_grey));
                song_album.setTextColor(getResources().getColor(R.color.icon_grey));
                artist_name.setTextColor(getResources().getColor(R.color.icon_grey));
                fast_forward.setColorFilter(getResources().getColor(R.color.icon_grey));
                fast_rewind.setColorFilter(getResources().getColor(R.color.icon_grey));
                shuffle_button.setColorFilter(getResources().getColor(R.color.icon_grey));
                favourite_btn.setColorFilter(getResources().getColor(R.color.icon_grey));
                playlist_btn.setColorFilter(getResources().getColor(R.color.icon_grey));

                alertDialog.dismiss();
            }
        });

        extractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int start_min, start_sec, end_min, end_sec;

                try {
                    start_min = Integer.valueOf(start_pos_min.getText().toString());
                    start_sec = Integer.valueOf(start_pos_sec.getText().toString());
                    end_min = Integer.valueOf(end_pos_min.getText().toString());
                    end_sec = Integer.valueOf(end_pos_sec.getText().toString());

                    start_loop_pos = ((start_min * 60) + start_sec) * 1000;
                    end_loop_pos = ((end_min * 60) + end_sec) * 1000;
                } catch (NumberFormatException e){
                    Toast.makeText(getContext(), "Empty fields", Toast.LENGTH_SHORT).show();
                    error_text.setVisibility(View.VISIBLE);

                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error_text.setVisibility(View.INVISIBLE);
                        }
                    }, 1500);
                    return;
                }

                try{
                    if (end_loop_pos <= start_loop_pos){
                        throw new InvalidNumberPairException("Invalid range");
                    }
                } catch (InvalidNumberPairException e){
                    Toast.makeText(getContext(), "Invalid range", Toast.LENGTH_SHORT).show();
                    error_text.setVisibility(View.VISIBLE);

                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error_text.setVisibility(View.INVISIBLE);
                        }
                    }, 1500);
                    return;
                }

                try{
                    if(start_loop_pos >= musicService.getDuration() || start_loop_pos < 0 || end_loop_pos <= 0 || end_loop_pos > musicService.getDuration()){
                        throw new InvalidNumberPairException("Outta range");
                    }
                } catch (InvalidNumberPairException e){
                    Toast.makeText(getActivity(), "Out of range", Toast.LENGTH_SHORT).show();
                    error_text.setVisibility(View.VISIBLE);

                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            error_text.setVisibility(View.INVISIBLE);
                        }
                    }, 1500);
                    return;
                }

                AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.create_file_popup, null);
                dialogBuilder.setView(dialogView);
                confirmDialog = dialogBuilder.create();

                TextView yes_btn, no_button;
                EditText name;
                name = dialogView.findViewById(R.id.queue_name);

                yes_btn = dialogView.findViewById(R.id.yes_button);
                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            File file = new File(Environment.getExternalStorageDirectory(), "Music");
                            if (!file.exists()) {
                                Toast.makeText(requireContext(), "Permission granted and does not exist", Toast.LENGTH_SHORT).show();
//                                file.mkdirs();
//                                if(file.isDirectory()){
//                                    Toast.makeText(requireContext(), "Directory created", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(requireContext(), "Could not create directory" + "\nPath: " + Environment.getExternalStorageDirectory() + "\nmkdirs: " + file.mkdirs(), Toast.LENGTH_SHORT).show();
//                                }
                            }else{
                                Toast.makeText(requireContext(), "Directory already exists", Toast.LENGTH_SHORT).show();
                            }

                            String inputPath = song.getPath();
                            String outputPath = file.getPath();
                            File trimmedAudioFile = new File(file, name.getText().toString());

                            String[] cmd = new String[]{
                                    "-i", inputPath,
                                    "-ss", String.valueOf(start_loop_pos),
                                    "-to", String.valueOf(end_loop_pos),
                                    trimmedAudioFile.getPath()
                            };

//                            int rc = FFmpeg.execute(cmd);
//                            if (rc == 0) {
//                                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
//                            }
                        }

                        confirmDialog.dismiss();
                    }
                });

                no_button = dialogView.findViewById(R.id.no_button);
                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                    }
                });

                confirmDialog.show();
            }
        });

        alertDialog.show();
    }

    public void completed(){
        if(song != null) {
            musicService.stop();
            musicService.release();

            if(repeatCLicked){
                current_position = current_position;
            }
            else {
                current_position = ((current_position + 1) % songsList.size());
            }

            song = songsList.get(current_position);
            uri = Uri.parse(songsList.get(current_position).getPath());
            musicService.createMediaPlayer(current_position);

            if(song.getFaveStatus().equals("0")){
                favourite_btn.setImageResource(R.drawable.ic_favorite);
            }else if(song.getFaveStatus().equals("1")){
                favourite_btn.setImageResource(R.drawable.ic_fave_filled);
            }

            if(getActivity() != null) {
                metaData(uri);
                song_title.setText(songsList.get(current_position).getTitle());
                artist_name.setText(songsList.get(current_position).getArtist());
                song_album.setText(songsList.get(current_position).getAlbum());
                seekBar.setMax(servicesMediaPlayer.getDuration() / 1000);

                if (cover_art.getVisibility() == View.INVISIBLE) {
                    search_webView.setVisibility(View.INVISIBLE);
                    search_refresh_button.setVisibility(View.INVISIBLE);
                    search_cancel_button.setVisibility(View.INVISIBLE);
                    lyrics_search_txt.setVisibility(View.INVISIBLE);
                    edit_button.setVisibility(View.INVISIBLE);
                    edit_complete_button.setVisibility(View.INVISIBLE);
                    cancel_button.setVisibility(View.INVISIBLE);
                    lyrics_textView.setVisibility(View.INVISIBLE);
                    lyrics_options_panel.setVisibility(View.INVISIBLE);

                    cover_art.setVisibility(View.VISIBLE);
                }

                if (inEditLyrics) {
                    convertTo_textView();
                    inEditLyrics = false;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (servicesMediaPlayer != null) {
                            int mCurrentPosition = servicesMediaPlayer.getCurrentPosition() / 1000;
                            seekBar.setProgress(mCurrentPosition);
                            duration_played.setText(formattedTime(mCurrentPosition));
                        }

                        handler.postDelayed(this, 1000);
                    }
                });

                playpause_button.setBackgroundResource(R.drawable.ic_pause);
                load_search();
            }

            if(musicService != null) {
                musicService.createMediaPlayer(current_position);
                musicService.start();
                musicService.showNotification(R.drawable.ic_pause_white, uri);
                musicService.onCompleted();
            }

            int pos = -1;
            for (int i = 0; i < myQueues.size(); i++){
                if(myQueues.get(i).isCurrent()){
                    pos = i;
                }
            }
            QueueListItems item = myQueues.get(pos);
            myQueues.get(pos).setPlayPosition(item.getPlayPosition() + 1);

            historySongs.add(0, song);
            if(historyAdapter != null){
                historyAdapter.notifyDataSetChanged();
            }

            recentList.remove(song);
            recentList.add(0, song);
            if(recentList.size() > 20){
                recentList.remove(recentList.size() - 1);
            }
            recentAdapter.notifyDataSetChanged();
        }
    }

    private int getQueuePos(String uID){
        for (int i = 0; i < songsList.size(); i++){
            if (songsList.get(i).getId().equals(uID)){
                return i;
            }
        }
        return -1;
    }

    public static int getAlbumPos(String album_title){
        for (int i = 0; i < albumFiles.size(); i++){
            if (albumFiles.get(i).getAlbum().equals(album_title)){
                return i;
            }
        }
        return -1;
    }

    public static int getArtistPos(String artist_name){
        for (int i = 0; i < artistFiles.size(); i++){
            if (artistFiles.get(i).getArtist().equals(artist_name)){
                return i;
            }
        }
        return -1;
    }

    private void addSongToPlayList() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_playlist_layout, null);
        dialogBuilder.setView(dialogView);
        playlistDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.rvPlaylist);
        RelativeLayout newPlaylist = dialogView.findViewById(R.id.newPL_btn);
        TextView header, add, cancel;
        header = dialogView.findViewById(R.id.header_text);
        add = dialogView.findViewById(R.id.add);
        cancel = dialogView.findViewById(R.id.cancel);
        newPlaylist = dialogView.findViewById(R.id.newPL_btn);

        header.setText("Add \"" + song.getTitle() + "\" to playlist");

        final PlaylistClickListener playlistClickListener = new PlaylistClickListener() {
            @Override
            public void onClick(PlaylistItem playlist, int position) {
                boolean isChecked = false;
                for(PlaylistItem item : playLists){
                    if(item.isChecked()){
                        isChecked = true;
                        break;
                    }
                }

                if(isChecked){
                    add.setVisibility(View.VISIBLE);
                }else{
                    add.setVisibility(View.GONE);
                }
            }
        };

        newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylist(add);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(PlaylistItem item : playLists){
                    if(item.isChecked()){
                        item.getSongs().add(song);
                        playlistAdapter.notifyDataSetChanged();
                        playlistDialog.dismiss();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistDialog.dismiss();
            }
        });

        adapterPL = new AddPlaylistAdapter(requireContext(), playlistClickListener);
        adapterPL.submitList(playLists);
        adapterPL.notifyDataSetChanged();
        recyclerView.setAdapter(adapterPL);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        playlistDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (PlaylistItem item : playLists){
                    item.setChecked(false);
                }
            }
        });

        playlistDialog.show();
    }

    private void addPlaylist(TextView add) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.rename_popup, null);
        dialogBuilder.setView(dialogView);
        addPlaylistDialog = dialogBuilder.create();

        TextView header = dialogView.findViewById(R.id.alert_title);
        header.setText("Add playlist name");

        EditText name = dialogView.findViewById(R.id.queue_name);
        TextView yes_btn, no_button;
        name.requestFocus();

        yes_btn = dialogView.findViewById(R.id.yes_button);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean containsTitle = false;
                for(PlaylistItem item : playLists){
                    if(item.getTitle().trim().equalsIgnoreCase(name.getText().toString().trim())){
                        containsTitle = true;
                        break;
                    }
                }
                if(containsTitle){
                    Toast.makeText(requireContext(), "Name already used", Toast.LENGTH_SHORT).show();
                }else {
                    if(name.getText().toString().isEmpty()){
                        Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    }else {
                        PlaylistItem playList = new PlaylistItem();
                        playList.setTitle(name.getText().toString().trim());
                        playList.setChecked(true);
                        playLists.add(0, playList);
                        add.setVisibility(View.VISIBLE);
                        playlistAdapter.notifyDataSetChanged();
                        adapterPL.notifyDataSetChanged();
                        addPlaylistDialog.dismiss();
                    }
                }
            }
        });

        no_button = dialogView.findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylistDialog.dismiss();
            }
        });

        addPlaylistDialog.show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
    }

    private boolean replayCheck(int duration){
        if (musicService.getCurrentPosition() >= ((5) * duration / 100)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}






















