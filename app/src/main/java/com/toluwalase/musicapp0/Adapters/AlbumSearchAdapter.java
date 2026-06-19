package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.toluwalase.musicapp0.Customs.AlbumDiffUtil;
import com.toluwalase.musicapp0.Interfaces.AlbumClickListener;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.R;

public class AlbumSearchAdapter extends ListAdapter<AlbumItems, AlbumSearchAdapter.MyHolder> {
    View view;
    private Context mContext;
    AlbumClickListener mlistener;

    public AlbumSearchAdapter(Context mContext, AlbumClickListener mlistener) {
        super(new AlbumDiffUtil());
        this.mContext = mContext;
        this.mlistener = mlistener;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView album_title;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_title = itemView.findViewById(R.id.album_title);
        }
    }

    @NonNull
    @Override
    public AlbumSearchAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_search_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSearchAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.album_title.setText(getItem(position).getAlbum());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });
    }
}
