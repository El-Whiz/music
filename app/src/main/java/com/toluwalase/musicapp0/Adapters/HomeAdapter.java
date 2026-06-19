package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Color;
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
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.R;

import java.util.List;

public class HomeAdapter extends ListAdapter<MusicFiles, HomeAdapter.MyViewHolder> {
    Context context;
    SongClickListener songClickListener;

    public HomeAdapter(Context context, SongClickListener songClickListener){
        super(new MusicDiffUtil());
        this.context = context;
        this.songClickListener = songClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView file_art, icon;
        TextView file_title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_title = itemView.findViewById(R.id.file_title);
            file_art = itemView.findViewById(R.id.album_art);
            icon = itemView.findViewById(R.id.file_icon);
        }
    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_music_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(getItem(position).getArtUri()));
        Glide.with(context).load(artUri).placeholder(R.drawable.default_art).error(R.drawable.default_art).into(holder.file_art);

        holder.file_title.setText(getItem(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songClickListener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });
    }
}
