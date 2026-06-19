package com.toluwalase.musicapp0.Customs;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.toluwalase.musicapp0.Models.AlbumItems;

public class AlbumDiffUtil extends DiffUtil.ItemCallback<AlbumItems>{
    @Override
    public boolean areItemsTheSame(@NonNull AlbumItems oldItem, @NonNull AlbumItems newItem) {
        return oldItem.getAlbumId() == newItem.getAlbumId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull AlbumItems oldItem, @NonNull AlbumItems newItem) {
        return oldItem == newItem;
    }
}
