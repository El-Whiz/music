package com.toluwalase.musicapp0.Interfaces;

import android.content.IntentSender;

import com.toluwalase.musicapp0.Models.MusicFiles;

public interface DeleteListener {
    void delete(MusicFiles song, int position) throws IntentSender.SendIntentException;
}
