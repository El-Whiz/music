package com.toluwalase.musicapp0.Adapters;

import static com.toluwalase.musicapp0.MainActivity.SHARED_FILE_PATH;
import static com.toluwalase.musicapp0.Fragments.FavouritesFragment.faveSongs;
import static com.toluwalase.musicapp0.Fragments.FavouritesFragment.favesAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.playLists;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.playlistAdapter;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.current_position;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.favourite_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.musicService;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.playpause_button;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.getOriginalPos;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.queueAdapter;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.queueList;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.total_songs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Customs.MusicDiffUtil;
import com.toluwalase.musicapp0.Database.FaveDB;
import com.toluwalase.musicapp0.EditTagsActivity;
import com.toluwalase.musicapp0.Interfaces.DeleteListener;
import com.toluwalase.musicapp0.Interfaces.PlaylistClickListener;
import com.toluwalase.musicapp0.Interfaces.QueueClickListener;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MusicAdapter extends ListAdapter<MusicFiles, MusicAdapter.MyViewHolder> implements MediaPlayer.OnCompletionListener {
    AlertDialog alertDialog, dialog, dialogInfo, queueList_dialog, playlistDialog, addPlaylistDialog;
    Handler handler = new Handler();
    ArrayList<MusicFiles> allSongs;
    MediaPlayer mp;
    Thread preview_thread;
    TabLayout tabLayout;
    MainActivity mainActivity;
    boolean wasPlaying, wasInLoop;
    public static final int PAGE_SIZE = 20;
    public static int currentPage = 0;
    private Context mContext;
    SongClickListener mlistener;
    DeleteListener deleteListener;
    AddPlaylistAdapter adapterPL;
    private FaveDB favDB;

    public MusicAdapter(Context mContext, SongClickListener mlistener, DeleteListener listener, ArrayList<MusicFiles> allSongs){
        super(new MusicDiffUtil());
        this.mContext = mContext;
        this.mlistener = mlistener;
        this.deleteListener = listener;
        this.allSongs = allSongs;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        dialog.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView file_name, file_artist, file_album, file_duration;
        ImageView album_art, options_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_duration = itemView.findViewById(R.id.song_duration);
            file_name = itemView.findViewById(R.id.music_file_name);
            file_artist = itemView.findViewById(R.id.music_file_Artist);
            file_album = itemView.findViewById(R.id.music_file_album);
            album_art = itemView.findViewById(R.id.music_img);
            options_button = itemView.findViewById(R.id.options);
        }
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favDB = new FaveDB(mContext);
        SharedPreferences preferences = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean("firstStart", true);
        if(firstStart){
            createTableOnFirstStart();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(getItem(position).getArtUri()));
//        Glide.with(mContext).load(artUri).error(R.drawable.default_art).into(holder.album_art);
        Glide.with(mContext).load(Uri.parse(getItem(position).getPath())).error(R.drawable.default_art).into(holder.album_art);

        readCursor(getItem(position));
        holder.itemView.setBackgroundResource(0);
        try {
            int duration_int = Integer.parseInt(getItem(position).getDuration()) / 1000;
            String duration_string = formattedTime(duration_int);
            holder.file_duration.setText(duration_string);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        holder.file_name.setText(getItem(position).getTitle());
        holder.file_artist.setText(getItem(position).getArtist());
        holder.file_album.setText(getItem(position).getAlbum());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });

        holder.options_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsPopup(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                return false;
            }
        });
    }

    private void readCursor(MusicFiles item) {
        Cursor cursor = favDB.readAllData(item.getId());
        SQLiteDatabase database = favDB.getReadableDatabase();
        try{
            while (cursor.moveToNext()){
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(FaveDB.FAVE_STATUS));
                item.setFaveStatus(status);

                if(status != null && status.equals("1")){
                } else if (status != null && status.equals("0")) {
                }
            }
        }
        finally {
            if(cursor != null && cursor.isClosed()){
                cursor.close();
            }
            database.close();
        }
    }

    private void showOptionsPopup(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.options_layout, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        TextView title, faveText;
        ImageView faveIcon;
        faveIcon = dialogView.findViewById(R.id.favourite);
        title = dialogView.findViewById(R.id.title_text);
        faveText = dialogView.findViewById(R.id.fave_text);
        if(getItem(position).getFaveStatus().equals("0")){
            faveText.setText("Add to favourites");
            faveIcon.setImageResource(R.drawable.ic_favorite);
        }else{
            faveText.setText("Remove from favourites");
            faveIcon.setImageResource(R.drawable.ic_fave_filled);
        }
        RelativeLayout info, play_next, preview, add_queue, add_playlist, add_favourites, edit_tags, share, delete, add_any_queue;
        info = dialogView.findViewById(R.id.info);
        play_next = dialogView.findViewById(R.id.play_next);
        preview = dialogView.findViewById(R.id.preview_song);
        add_queue = dialogView.findViewById(R.id.queue);
        add_playlist = dialogView.findViewById(R.id.playlistRL);
        add_favourites = dialogView.findViewById(R.id.favourite_add);
        edit_tags = dialogView.findViewById(R.id.edit_tags);
        share = dialogView.findViewById(R.id.share_playlist);
        delete = dialogView.findViewById(R.id.delete_item);
        add_any_queue = dialogView.findViewById(R.id.any_queue);

        title.setText(getItem(position).getTitle());

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopup(position);
            }
        });

        play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null){
                    if(getItem(position) == song){
                        Toast.makeText(mContext, "This is the currently playin' song", Toast.LENGTH_SHORT).show();
                    }else {
                        if (queueList.contains(getItem(position))) {
                            int refPos = queueList.indexOf(getItem(position));
                            int currentPos = queueList.indexOf(song);
                            if(refPos < currentPos){
                                queueList.remove(getItem(position));
                                queueAdapter.notifyDataSetChanged();
                                queueList.add((getOriginalPos(song.getId()) + 1), getItem(position));
                                queueAdapter.notifyDataSetChanged();
                                current_position = current_position - 1;
                                Toast.makeText(mContext, getItem(position).getTitle() + " will play next", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                            else if(refPos > currentPos) {
                                queueList.remove(getItem(position));
                                queueAdapter.notifyDataSetChanged();
                                queueList.add((getOriginalPos(song.getId()) + 1), getItem(position));
                                queueAdapter.notifyDataSetChanged();
                                Toast.makeText(mContext, getItem(position).getTitle() + " will play next", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        } else {
                            queueList.add((getOriginalPos(song.getId()) + 1), getItem(position));
                            queueAdapter.notifyDataSetChanged();
                            Toast.makeText(mContext, getItem(position).getTitle() + " will play next", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                }
            }
        });

        preview_thread = new Thread(){
            @Override
            public void run() {
                super.run();

                preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPreviewPopup(position);
                    }
                });
            }
        };
        preview_thread.start();

        add_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song != null) {
                    if (queueList.contains(getItem(position))) {
                        Toast.makeText(mContext, "Song already in queue", Toast.LENGTH_SHORT).show();
                    } else {
                        queueList.add((queueList.size()), getItem(position));
                        total_songs.setText(String.valueOf(queueList.size()));
                        queueAdapter.notifyDataSetChanged();
                        Toast.makeText(mContext, getItem(position).getTitle() + " has been added to current queue", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }
            }
        });

        add_any_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQueueList(position);
                alertDialog.dismiss();
            }
        });

        add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                addSongToPlayList(position);
            }
        });

        add_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicFiles item = getItem(position);

                if(item.getFaveStatus().equals("0")){
                    item.setFaveStatus("1");
                    favDB.insertIntoDB(item.getTitle(), item.getArtist(), item.getAlbum(), item.getId(), item.getGenre(), item.getPath(), item.getDuration(), item.getComposer(), item.getNumber(),
                            item.getDate(), item.getYear(), item.getSize(), item.getBitrate(), item.getFileName(), item.getArtUri(), item.getFaveStatus());
                    faveSongs.add(item);
                    if(song != null && getItem(position).getId().equals(song.getId())){
                        favourite_btn.setImageResource(R.drawable.ic_fave_filled);
                    }
                }
                else{
                    item.setFaveStatus("0");
                    favDB.removeFromFave(item.getId());
                    for(MusicFiles itemSong : faveSongs){
                        if(itemSong.getId().equals(item.getId())){
                            faveSongs.remove(itemSong);
                            break;
                        }
                    }
                    if(song != null && getItem(position).getId().equals(song.getId())){
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                    }
                }
                if(favesAdapter != null) {
                    favesAdapter.notifyDataSetChanged();
                }
                notifyItemChanged(position);
                alertDialog.dismiss();
            }
        });

        edit_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditTagsActivity.class);
                alertDialog.dismiss();
                mContext.startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareSong(position);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                alertDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                try {
                    deleteListener.delete(getItem(position), position);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
                notifyDataSetChanged();
//                alertDialog.dismiss();
//                try {
//                    delete_file(position, v);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        alertDialog.show();
    }

    private void shareSong(int position) throws FileNotFoundException {
        if(getItem(position) != null){
            File musicMP3 = convertToFile(getItem(position));
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri fileUri = null;
            try {
               fileUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", musicMP3);
            }catch(IllegalArgumentException e){
                File newFile = new File(Environment.getExternalStorageDirectory(), "Music");
                if (!newFile.exists()) {
                    Toast.makeText(mContext, "Permission granted and does not exist", Toast.LENGTH_SHORT).show();
                    newFile.mkdirs();
                    if(newFile.isDirectory()){
                        Toast.makeText(mContext, "Directory created", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, "Could not create directory" + "\nPath: " + Environment.getExternalStorageDirectory() + "\nmkdirs: " + newFile.mkdirs(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, "Directory already exists", Toast.LENGTH_SHORT).show();
                }
                File share = new File(newFile, getItem(position).getFileName());
                SHARED_FILE_PATH = share.getPath();

                try(InputStream inputStream = new FileInputStream(getItem(position).getPath()); OutputStream outputStream = new FileOutputStream(share)){
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while((bytesRead = inputStream.read(buffer)) != -1){
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }catch (IOException exception){
                    e.printStackTrace();
                }

                fileUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", share);
            }
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share song"));
        }
    }

    private void showInfoPopup(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.info_layout, null);
        dialogBuilder.setView(dialogView);
        dialogInfo = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        TextView file_name, title, album, artist, genre, composer, track_number, duration, year, size, bitrate, format, date, location;
        ImageView cover_art = dialogView.findViewById(R.id.album_cover);

        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(getItem(position).getArtUri()));
        Glide.with(mContext).load(artUri).error(R.drawable.default_art).into(cover_art);

        file_name = dialogView.findViewById(R.id.file_name);
        file_name.setText(getItem(position).getFileName());

        title = dialogView.findViewById(R.id.title);
        title.setText(getItem(position).getTitle());

        artist = dialogView.findViewById(R.id.artist);
        artist.setText(getItem(position).getArtist());

        album = dialogView.findViewById(R.id.album);
        album.setText(getItem(position).getAlbum());

        genre = dialogView.findViewById(R.id.genre);
        genre.setText(getItem(position).getGenre());

        composer = dialogView.findViewById(R.id.composer);
        composer.setText(getItem(position).getComposer());

        track_number = dialogView.findViewById(R.id.track_number);
        track_number.setText(getItem(position).getNumber());

        duration = dialogView.findViewById(R.id.duration);
        try {
            int durationTotal = Integer.parseInt(getItem(position).getDuration()) / 1000;
            duration.setText(formattedTime(durationTotal));
        }catch(NumberFormatException e){
            e.printStackTrace();
        }

        year = dialogView.findViewById(R.id.year);
        year.setText(getItem(position).getYear());

        size = dialogView.findViewById(R.id.size);
        long i = Long.valueOf(getItem(position).getSize()) / (1024 * 1024);
        size.setText(String.valueOf(i) + "mb");

        bitrate = dialogView.findViewById(R.id.bitrate);
        bitrate.setText(getItem(position).getBitrate());

        format = dialogView.findViewById(R.id.format);
        format.setText(MimeTypeMap.getFileExtensionFromUrl(getItem(position).getPath()));

        date = dialogView.findViewById(R.id.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mmaa", Locale.getDefault());
        Date x = new Date(Long.valueOf(getItem(position).getDate()));
        date.setText(dateFormat.format(x));

        location = dialogView.findViewById(R.id.location);
        location.setText(getItem(position).getPath());

        ImageView cancel = dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfo.dismiss();
            }
        });

        dialogInfo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        dialogInfo.show();
        alertDialog.dismiss();
    }

    private void showPreviewPopup(int position) {
        mainActivity = (MainActivity) mContext;
        alertDialog.dismiss();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.preview_layout, null);
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        SeekBar seekBar = dialogView.findViewById(R.id.seek_bar_1);
        TextView total_duration, played_duration, title, artist, album;
        ImageView cover_art;
        played_duration = dialogView.findViewById(R.id.played_duration_1);
        total_duration = dialogView.findViewById(R.id.total_duration_1);
        title = dialogView.findViewById(R.id.song_name_1);
        artist = dialogView.findViewById(R.id.song_artist);
        album = dialogView.findViewById(R.id.song_album);
        cover_art = dialogView.findViewById(R.id.album_cover);

        album.setText(getItem(position).getAlbum());
        artist.setText(getItem(position).getArtist());
        title.setText(getItem(position).getTitle());
        try {
            int durationTotal = Integer.parseInt(getItem(position).getDuration()) / 1000;
            total_duration.setText(formattedTime(durationTotal));
        } catch (NullPointerException | NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(mContext, "Invalid file", Toast.LENGTH_SHORT).show();
            playpause_button.setImageResource(R.drawable.ic_play);
        }

        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(getItem(position).getArtUri()));
        Glide.with(mContext).load(artUri).error(R.drawable.default_art).into(cover_art);

        Uri uri = Uri.parse(getItem(position).getPath());

        if(inLoop){
            if(loopMP.isPlaying()){
                loopMP.pause();
                wasInLoop = true;
            }else{
                wasInLoop = false;
            }
        }

        mp = MediaPlayer.create(mContext, uri);
        if(musicService.isPlaying()){
            wasPlaying = true;
            musicService.pause();
            try {
                mp.start();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        else{
            wasPlaying = false;
            mp.start();
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mp != null && fromUser){
                    mp.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        try {
            seekBar.setMax(mp.getDuration() / 1000);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mp != null){
                    int mCurrentPosition = mp.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    played_duration.setText(formattedTime(mCurrentPosition));
                }

                handler.postDelayed(this, 1000);
            }
        });

        try {
            mp.setOnCompletionListener(this);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mp != null){
                    mp.stop();

                    if(wasPlaying) {
                        if (musicService != null && song != null) {
                            musicService.start();
                        }
                    }

                    if(wasInLoop){
                        if(loopMP != null){
                            loopMP.start();
                        }
                    }
                }
            }
        });

        dialog.show();
    }

    private void showQueueList(int pos) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.queue_panel, null);
        dialogBuilder.setView(dialogView);
        queueList_dialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        QueueClickListener queueClickListener = null;
        queueClickListener = new QueueClickListener() {
            @Override
            public void onClick(QueueListItems item, int position) {
                int size = item.getSongs().size();
                item.getSongs().add(size, getItem(pos));
                queueList_dialog.dismiss();
            }
        };

        AddQueueAdapter queueListAdapter = new AddQueueAdapter(mContext, queueClickListener);
        queueListAdapter.submitList(myQueues);
        RecyclerView queue_recycler = dialogView.findViewById(R.id.queue_panel_recyclerView);
        queue_recycler.setAdapter(queueListAdapter);
        queue_recycler.setHasFixedSize(true);
        queue_recycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        queueList_dialog.show();
    }

    private void addSongToPlayList(int pos) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.add_playlist_layout, null);
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

        header.setText("Add \"" + getItem(pos).getTitle() + "\" to playlist");

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
                        item.getSongs().add(getItem(pos));
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

        adapterPL = new AddPlaylistAdapter(mContext, playlistClickListener);
        adapterPL.submitList(playLists);
        adapterPL.notifyDataSetChanged();
        recyclerView.setAdapter(adapterPL);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

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
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.rename_popup, null);
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
                    Toast.makeText(mContext, "Name already used", Toast.LENGTH_SHORT).show();
                }else {
                    if(name.getText().toString().isEmpty()){
                        Toast.makeText(mContext, "Name can't be empty", Toast.LENGTH_SHORT).show();
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

    private String formattedTime(int duration) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(duration % 60);
        String minutes = String.valueOf(duration / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        }
        else{
            return totalOut;
        }
    }

    private void createTableOnFirstStart() {
        favDB.insertEmpty(allSongs);
        SharedPreferences preferences = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void delete_file(int position, View v) throws IntentSender.SendIntentException {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(getItem(position).getId()));

        try {
            mContext.getContentResolver().delete(contentUri, null, null);
        } catch (SecurityException e) {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(contentUri);
                pendingIntent = MediaStore.createDeleteRequest(mContext.getContentResolver(), uris);
            } else {
                if (e instanceof RecoverableSecurityException) {
                    RecoverableSecurityException exception = (RecoverableSecurityException) e;
                    pendingIntent = exception.getUserAction().getActionIntent();
                }
            }
            if (pendingIntent != null) {
                IntentSender intentSender = pendingIntent.getIntentSender();
                ((Activity) mContext).startIntentSenderForResult(intentSender, 100, null, 0, 0, 0, null);
            }
        }
    }

    private File convertToFile(MusicFiles song){
        String path = song.getPath();
        return new File(path);
    }
}






















