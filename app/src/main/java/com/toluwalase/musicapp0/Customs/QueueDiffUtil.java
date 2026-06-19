package com.toluwalase.musicapp0.Customs;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.toluwalase.musicapp0.Models.QueueListItems;

public class QueueDiffUtil extends DiffUtil.ItemCallback<QueueListItems> {
    @Override
    public boolean areItemsTheSame(@NonNull QueueListItems oldItem, @NonNull QueueListItems newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull QueueListItems oldItem, @NonNull QueueListItems newItem) {
        return false;
    }
}
