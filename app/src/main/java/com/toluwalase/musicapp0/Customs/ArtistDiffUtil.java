package com.toluwalase.musicapp0.Customs;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.ArtistItems;

public class ArtistDiffUtil extends DiffUtil.ItemCallback<ArtistItems>{
    @Override
    public boolean areItemsTheSame(@NonNull ArtistItems oldItem, @NonNull ArtistItems newItem) {
        return oldItem.getArtistId() == newItem.getArtistId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull ArtistItems oldItem, @NonNull ArtistItems newItem) {
        return oldItem == newItem;
    }
}
