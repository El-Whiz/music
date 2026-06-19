package com.toluwalase.musicapp0.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PlaylistItem implements Parcelable {
    String title;
    private ArrayList<MusicFiles> songs;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<MusicFiles> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<MusicFiles> songs) {
        this.songs = songs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PlaylistItem() {
        this.title = "";
        this.songs = new ArrayList<>();
        this.isChecked = false;
    }

    protected PlaylistItem(Parcel in) {
        title = in.readString();
        in.readTypedList(songs, MusicFiles.CREATOR);
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(songs);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaylistItem> CREATOR = new Creator<PlaylistItem>() {
        @Override
        public PlaylistItem createFromParcel(Parcel in) {
            return new PlaylistItem(in);
        }

        @Override
        public PlaylistItem[] newArray(int size) {
            return new PlaylistItem[size];
        }
    };
}
