package com.toluwalase.musicapp0.Interfaces;

import android.content.IntentSender;
import android.view.View;

import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;

public interface ButtonCLickListener {
    void playPL(PlaylistItem item, int position);
}
