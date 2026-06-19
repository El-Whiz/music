package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.toluwalase.musicapp0.Customs.ArtistDiffUtil;
import com.toluwalase.musicapp0.Interfaces.ArtistClickListener;
import com.toluwalase.musicapp0.Models.ArtistItems;
import com.toluwalase.musicapp0.R;

public class ArtistSearchAdapter extends ListAdapter<ArtistItems, ArtistSearchAdapter.MyHolder> {
    View view;
    private Context mContext;
    ArtistClickListener mlistener;

    public ArtistSearchAdapter(Context mContext, ArtistClickListener mlistener) {
        super(new ArtistDiffUtil());
        this.mContext = mContext;
        this.mlistener = mlistener;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView artist_name;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            artist_name = itemView.findViewById(R.id.album_title);
        }
    }

    @NonNull
    @Override
    public ArtistSearchAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_search_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistSearchAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.artist_name.setText(getItem(position).getArtist());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });
    }
}
