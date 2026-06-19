package com.toluwalase.musicapp0.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SongLyrics implements Parcelable {
    String songName = "";
    String songArtist = "";
    String songAlbum = "";
    String songID;
    String lyrics = "";

    public SongLyrics(String songID, String lyrics){
        this.songID = songID;
        this.lyrics = lyrics;
    }

    protected SongLyrics(Parcel in) {
        songName = in.readString();
        songArtist = in.readString();
        songAlbum = in.readString();
        songID = in.readString();
        lyrics = in.readString();
    }

    public static final Creator<SongLyrics> CREATOR = new Creator<SongLyrics>() {
        @Override
        public SongLyrics createFromParcel(Parcel in) {
            return new SongLyrics(in);
        }

        @Override
        public SongLyrics[] newArray(int size) {
            return new SongLyrics[size];
        }
    };

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(songArtist);
        dest.writeString(songAlbum);
        dest.writeString(songID);
        dest.writeString(lyrics);
    }
}
