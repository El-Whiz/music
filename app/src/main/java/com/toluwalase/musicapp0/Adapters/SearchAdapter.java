package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toluwalase.musicapp0.Customs.MusicDiffUtil;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.R;

import java.io.IOException;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    ArrayList<MusicFiles> list;
    private Context mContext;
    SongClickListener mlistener;

    public SearchAdapter(ArrayList<MusicFiles> list, Context mContext, SongClickListener mlistener){
//        super(new MusicDiffUtil());
        this.list = list;
        this.mContext = mContext;
        this.mlistener = mlistener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView file_name, file_artist, file_album, file_duration;
        ImageView album_art;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            file_artist = itemView.findViewById(R.id.music_file_Artist);
            file_album = itemView.findViewById(R.id.music_file_album);
            album_art = itemView.findViewById(R.id.music_img);
            file_duration = itemView.findViewById(R.id.song_duration);
        }
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(getItem(position).getArtUri()));
        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(list.get(position).getArtUri()));
        Glide.with(mContext).load(artUri).placeholder(R.drawable.default_art).error(R.drawable.default_art).into(holder.album_art);

        try {
//            int duration_int = Integer.parseInt(getItem(position).getDuration()) / 1000;
            int duration_int = Integer.parseInt(list.get(position).getDuration()) / 1000;
            String duration_string = formattedTime(duration_int);
            holder.file_duration.setText(duration_string);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
//        holder.file_name.setText(getItem(position).getTitle());
//        holder.file_artist.setText(getItem(position).getArtist());
//        holder.file_album.setText(getItem(position).getAlbum());
        holder.file_name.setText(list.get(position).getTitle());
        holder.file_artist.setText(list.get(position).getArtist());
        holder.file_album.setText(list.get(position).getAlbum());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mlistener.onClick(getItem(holder.getAdapterPosition()), position);

                mlistener.onClick(list.get(holder.getAdapterPosition()), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
}
