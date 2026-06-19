package com.toluwalase.musicapp0.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class QueueListItems implements Parcelable {
    private int position;
    private int playPosition = -1;
    private String title;
    private ArrayList<MusicFiles> songs;
    private boolean isSelected;
    private boolean isCurrent;

    public QueueListItems() {

    }

    protected QueueListItems(Parcel in) {
        position = in.readInt();
        title = in.readString();
        in.readTypedList(songs, MusicFiles.CREATOR);
        isSelected = in.readByte() != 0;
        isCurrent = in.readByte() != 0;
    }

    public static final Creator<QueueListItems> CREATOR = new Creator<QueueListItems>() {
        @Override
        public QueueListItems createFromParcel(Parcel in) {
            return new QueueListItems(in);
        }

        @Override
        public QueueListItems[] newArray(int size) {
            return new QueueListItems[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MusicFiles> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<MusicFiles> songs) {
        this.songs = songs;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    public QueueListItems(int position, String title, ArrayList<MusicFiles> songs) {
        this.position = position;
        this.title = title;
        this.songs = songs;
        this.isSelected = false;
        this.isCurrent = false;
        this.playPosition = -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeString(title);
        dest.writeTypedList(songs);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isCurrent ? 1 : 0));
        dest.writeInt(playPosition);
    }
}
