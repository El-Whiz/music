package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toluwalase.musicapp0.Customs.AlbumDiffUtil;
import com.toluwalase.musicapp0.Interfaces.AlbumClickListener;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.R;

import java.io.IOException;

public class AlbumAdapter extends ListAdapter<AlbumItems, AlbumAdapter.MyHolder> {
    View view;
    private Context mContext;
    AlbumClickListener mlistener;

    public AlbumAdapter(Context mContext, AlbumClickListener mlistener){
        super(new AlbumDiffUtil());
        this.mContext = mContext;
        this.mlistener = mlistener;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        CardView album_item;
        ImageView album_art;
        TextView album_title, album_artist, total_songs;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_item = itemView.findViewById(R.id.album_item);
            album_art = itemView.findViewById(R.id.album_art);
            album_title = itemView.findViewById(R.id.album_title);
            album_artist = itemView.findViewById(R.id.album_artist);
            total_songs = itemView.findViewById(R.id.album_songs_total);
        }
    }

    @NonNull
    @Override
    public AlbumAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {
        if(getItem(position).getArtUri() != null){
            Glide.with(mContext).load(getItem(position).getArtUri()).error(R.drawable.default_art).into(holder.album_art);
        }else{
            Glide.with(mContext).load(R.drawable.default_art).into(holder.album_art);
        }
        holder.album_title.setText(getItem(position).getAlbum());
        holder.album_artist.setText(getItem(position).getArtist());
        holder.total_songs.setText(String.valueOf(getItem(position).getSongCount()) + " songs");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });
    }
}




























