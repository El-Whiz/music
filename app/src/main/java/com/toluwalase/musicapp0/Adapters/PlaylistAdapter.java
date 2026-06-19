package com.toluwalase.musicapp0.Adapters;

import static com.toluwalase.musicapp0.Fragments.HomeFragment.playLists;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.playlistAdapter;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.queueAdapter;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.queueList;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.total_songs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.toluwalase.musicapp0.Customs.PlaylistDiffUtil;
import com.toluwalase.musicapp0.Interfaces.ButtonCLickListener;
import com.toluwalase.musicapp0.Interfaces.PlaylistClickListener;
import com.toluwalase.musicapp0.Interfaces.QueueClickListener;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

public class PlaylistAdapter extends ListAdapter<PlaylistItem, PlaylistAdapter.MyViewHolder> {
    AlertDialog optionsDialog, renameDialog, confirmDialog, playlistDialog, addPlaylistDialog, queueList_dialog;
    Context context;
    PlaylistClickListener playlistClickListener;
    ButtonCLickListener playPLListiner;
    static int posPL = -1;
    AddPlaylistAdapter adapterPL;

    public PlaylistAdapter (Context context, PlaylistClickListener playlistClickListener, ButtonCLickListener playPLListiner){
        super(new PlaylistDiffUtil());
        this.context = context;
        this.playlistClickListener = playlistClickListener;
        this.playPLListiner = playPLListiner;
    }
    @NonNull
    @Override
    public PlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(getItem(position).getTitle());
        holder.song_count.setText(String.valueOf(getItem(position).getSongs().size()) + " songs");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistClickListener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsPopup(position);
            }
        });
    }

    private void showOptionsPopup(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.playlist_options_layout, null);
        dialogBuilder.setView(dialogView);
        optionsDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        TextView title = dialogView.findViewById(R.id.title_text);
        RelativeLayout play, add_queue, add_playlist, add_favourites, rename, share, remove, add_any_queue;
        play = dialogView.findViewById(R.id.playPL);
        add_queue = dialogView.findViewById(R.id.queue);
        add_playlist = dialogView.findViewById(R.id.playlistRL);
        add_favourites = dialogView.findViewById(R.id.favourite_add);
        rename = dialogView.findViewById(R.id.rename);
        share = dialogView.findViewById(R.id.share_playlist);
        remove = dialogView.findViewById(R.id.remove);
        add_any_queue = dialogView.findViewById(R.id.any_queue);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.dismiss();
                playPLListiner.playPL(getItem(position), position);
            }
        });

        title.setText(getItem(position).getTitle());

        add_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queueList != null) {
                    for (MusicFiles song : getItem(position).getSongs()){
                        if(!queueList.contains(song)){
                            queueList.add(song);
                            total_songs.setText(String.valueOf(queueList.size()));
                            queueAdapter.notifyDataSetChanged();
                        }
                    }
                    Toast.makeText(context, "Songs added", Toast.LENGTH_SHORT).show();
                }
                optionsDialog.dismiss();
            }
        });

        add_any_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.dismiss();
                showQueueList(position);
            }
        });

        add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.dismiss();
                addSongsToPlayList(position);
            }
        });

        add_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenamePopup(position);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                loadConfirmPopup(position);
            }
        });

        optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        optionsDialog.show();
    }

    private void showRenamePopup(int pos) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.rename_popup, null);
        dialogBuilder.setView(dialogView);
        renameDialog = dialogBuilder.create();

        EditText rename = dialogView.findViewById(R.id.queue_name);
        TextView yes_btn, no_button;
        rename.setHint(getItem(pos).getTitle());
        rename.requestFocus();

        yes_btn = dialogView.findViewById(R.id.yes_button);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean containsTitle = false;
                for(PlaylistItem item : playLists){
                    if(item.getTitle().trim().equalsIgnoreCase(rename.getText().toString().trim())){
                        containsTitle = true;
                        break;
                    }
                }
                if (containsTitle) {
                    Toast.makeText(context, "Name already used", Toast.LENGTH_SHORT).show();
                }else {
                    playLists.get(pos).setTitle(rename.getText().toString().trim());
                    notifyItemChanged(pos);
                    renameDialog.dismiss();
                }
            }
        });

        no_button = dialogView.findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });

        renameDialog.show();
    }

    private void showQueueList(int pos) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.queue_panel, null);
        dialogBuilder.setView(dialogView);
        queueList_dialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        QueueClickListener queueClickListener = null;
        queueClickListener = new QueueClickListener() {
            @Override
            public void onClick(QueueListItems item, int position) {
                for (MusicFiles song : getItem(pos).getSongs()){
                    if(!item.getSongs().contains(song)){
                        item.getSongs().add(song);
                    }
                }
                Toast.makeText(context, "Songs added", Toast.LENGTH_SHORT).show();
                queueList_dialog.dismiss();
            }
        };

        AddQueueAdapter queueListAdapter = new AddQueueAdapter(context, queueClickListener);
        queueListAdapter.submitList(myQueues);
        RecyclerView queue_recycler = dialogView.findViewById(R.id.queue_panel_recyclerView);
        queue_recycler.setAdapter(queueListAdapter);
        queue_recycler.setHasFixedSize(true);
        queue_recycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        queueList_dialog.show();
    }

    private void loadConfirmPopup(int pos) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.confirm_popup, null);
        dialogBuilder.setView(dialogView);
        confirmDialog = dialogBuilder.create();

        TextView queue_title, yes_btn, no_button;
        queue_title = dialogView.findViewById(R.id.queue_name);
        queue_title.setText(getItem(pos).getTitle());

        yes_btn = dialogView.findViewById(R.id.yes_button);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playLists.remove(pos);
                notifyDataSetChanged();
                confirmDialog.dismiss();
                optionsDialog.dismiss();
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

    private void addSongsToPlayList(int pos) {
        posPL = pos;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_playlist_layout, null);
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

        header.setText("Add \"" + getItem(posPL).getTitle() + "\" songs to a playlist");

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

            private void addPlaylist(TextView add) {
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.rename_popup, null);
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
                            Toast.makeText(context, "Name already used", Toast.LENGTH_SHORT).show();
                        }else {
                            if(name.getText().toString().isEmpty()){
                                Toast.makeText(context, "Name can't be empty", Toast.LENGTH_SHORT).show();
                            }else {
                                PlaylistItem playList = new PlaylistItem();
                                playList.setTitle(name.getText().toString().trim());
                                playList.setChecked(true);
                                playLists.add(0, playList);
                                add.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();
                                adapterPL.notifyDataSetChanged();
                                posPL = pos + 1;
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
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(PlaylistItem item : playLists){
                    if(item.isChecked()){
                        for (MusicFiles song : getItem(posPL).getSongs()){
                            if(!item.getSongs().contains(song)){
                                item.getSongs().add(song);
                            }
                        }
                    }
                    playlistAdapter.notifyDataSetChanged();
                    playlistDialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistDialog.dismiss();
            }
        });

        adapterPL = new AddPlaylistAdapter(context, playlistClickListener);
        adapterPL.submitList(playLists);
        adapterPL.notifyDataSetChanged();
        recyclerView.setAdapter(adapterPL);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, song_count;
        ImageView options;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playlist_name);
            song_count = itemView.findViewById(R.id.song_count);
            options = itemView.findViewById(R.id.options);
        }
    }
}
