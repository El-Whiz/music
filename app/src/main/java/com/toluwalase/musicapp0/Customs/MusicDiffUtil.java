package com.toluwalase.musicapp0.Customs;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.toluwalase.musicapp0.Models.MusicFiles;

public class MusicDiffUtil extends DiffUtil.ItemCallback<MusicFiles>{
    @Override
    public boolean areItemsTheSame(@NonNull MusicFiles oldItem, @NonNull MusicFiles newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull MusicFiles oldItem, @NonNull MusicFiles newItem) {
        return oldItem == newItem;
    }
}
