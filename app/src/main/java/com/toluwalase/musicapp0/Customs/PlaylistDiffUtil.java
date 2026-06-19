package com.toluwalase.musicapp0.Customs;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.toluwalase.musicapp0.Models.PlaylistItem;

public class PlaylistDiffUtil extends DiffUtil.ItemCallback<PlaylistItem>{
    @Override
    public boolean areItemsTheSame(@NonNull PlaylistItem oldItem, @NonNull PlaylistItem newItem) {
        return oldItem.getTitle() == newItem.getTitle();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull PlaylistItem oldItem, @NonNull PlaylistItem newItem) {
        return oldItem == newItem;
    }
}
