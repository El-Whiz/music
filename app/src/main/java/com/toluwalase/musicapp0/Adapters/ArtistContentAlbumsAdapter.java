package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.toluwalase.musicapp0.Customs.AlbumDiffUtil;
import com.toluwalase.musicapp0.Customs.ArtistDiffUtil;
import com.toluwalase.musicapp0.Customs.MusicDiffUtil;
import com.toluwalase.musicapp0.Interfaces.AlbumClickListener;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.ArtistItems;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;

public class ArtistContentAlbumsAdapter extends ListAdapter<AlbumItems, ArtistContentAlbumsAdapter.MyViewHolder> {
    int current_pos, outline_color;
    private Context mContext;
    AlbumClickListener mlistener;

    public ArtistContentAlbumsAdapter(Context mContext, AlbumClickListener mlistener) {
        super(new AlbumDiffUtil());
        this.mContext = mContext;
        this.mlistener = mlistener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView album_title;
        ImageView album_art;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            album_title = itemView.findViewById(R.id.album_title);
            album_art = itemView.findViewById(R.id.album_art);
        }
    }

    @NonNull
    @Override
    public ArtistContentAlbumsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.artist_album_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistContentAlbumsAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(getItem(position).getArtUri() != null){
            Glide.with(mContext).load(getItem(position).getArtUri()).error(R.drawable.default_art).into(holder.album_art);
        }else{
            Glide.with(mContext).load(R.drawable.default_art).into(holder.album_art);
        }
        holder.album_title.setText(getItem(position).getAlbum());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public boolean onLongClick(final View v) {
                return false;
            }
        });

        if(position == current_pos){
            holder.itemView.setBackgroundResource(R.drawable.song_holder_design);
            Drawable background = holder.itemView.getBackground();
            if(background instanceof LayerDrawable){
                LayerDrawable layerDrawable = (LayerDrawable) background;
                GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.getDrawable(0);
                gradientDrawable.setStroke(1, outline_color);
            }
        }
        else{
            holder.itemView.setBackgroundResource(0);
        }
    }

    public void changeAlbumOutline(int position, int color){
        current_pos = position;
        outline_color = color;
        notifyDataSetChanged();
    }
}
