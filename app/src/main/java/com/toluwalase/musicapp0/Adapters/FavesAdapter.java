package com.toluwalase.musicapp0.Adapters;

import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.favourite_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.song;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
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
import com.toluwalase.musicapp0.Database.FaveDB;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;

public class FavesAdapter extends RecyclerView.Adapter<FavesAdapter.MyViewHolder> {
    private Context mContext;
    SongClickListener mlistener;
    ArrayList<MusicFiles> allfaves;
    FaveDB faveDB;

    public FavesAdapter(Context mContext, SongClickListener mlistener, ArrayList<MusicFiles> allfaves){
//        super(new MusicDiffUtil());
        this.mContext = mContext;
        this.mlistener = mlistener;
        this.allfaves = allfaves;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fave_item, parent, false);
        faveDB = new FaveDB(mContext);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri artUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(allfaves.get(position).getArtUri()));
        Glide.with(mContext).load(artUri).error(R.drawable.default_art).into(holder.album_art);

        holder.itemView.setBackgroundResource(0);
        try {
            int duration_int = Integer.parseInt(allfaves.get(position).getDuration()) / 1000;
            String duration_string = formattedTime(duration_int);
            holder.file_duration.setText(duration_string);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        holder.file_name.setText(allfaves.get(position).getTitle());
        holder.file_artist.setText(allfaves.get(position).getArtist());
        holder.file_album.setText(allfaves.get(position).getAlbum());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(allfaves.get(holder.getAdapterPosition()), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allfaves.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView file_name, file_artist, file_album, file_duration;
        ImageView album_art, addFave;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_duration = itemView.findViewById(R.id.song_duration);
            file_name = itemView.findViewById(R.id.music_file_name);
            file_artist = itemView.findViewById(R.id.music_file_Artist);
            file_album = itemView.findViewById(R.id.music_file_album);
            album_art = itemView.findViewById(R.id.music_img);
            addFave = itemView.findViewById(R.id.fave_icon);

            //remove from faves
            addFave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(song != null && allfaves.get(position).getId().equals(song.getId())){
                        favourite_btn.setImageResource(R.drawable.ic_favorite);
                    }
                    musicFiles.get(getSongPos(allfaves.get(position).getId())).setFaveStatus("0");
                    faveDB.removeFromFave(allfaves.get(position).getId());
                    removeItem(position);
                }
            });
        }
    }

    private void removeItem(int position) {
        allfaves.remove(allfaves.get(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allfaves.size());
    }

    private int getSongPos(String uID){
        for (int i = 0; i < musicFiles.size(); i++){
            if (musicFiles.get(i).getId().equals(uID)){
                return i;
            }
        }
        return -1;
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
