package com.toluwalase.musicapp0.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AlbumItems implements Parcelable {
    String path;
    String album;
    String artist;
    String albumId;
    Uri artUri;
    int songCount;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public Uri getArtUri() {
        return artUri;
    }

    public void setArtUri(Uri artUri) {
        this.artUri = artUri;
    }

    public AlbumItems(){

    }

    public AlbumItems(String album, String artist, String id, String path, Uri art){
        this.album = album;
        this.artist = artist;
        this.albumId = id;
        this.path = path;
        this.artUri = art;
    }

    protected AlbumItems(Parcel in) {
        album = in.readString();
        artist = in.readString();
        albumId = in.readString();
        path = in.readString();
        songCount = in.readInt();
        artUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<AlbumItems> CREATOR = new Creator<AlbumItems>() {
        @Override
        public AlbumItems createFromParcel(Parcel in) {
            return new AlbumItems(in);
        }

        @Override
        public AlbumItems[] newArray(int size) {
            return new AlbumItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(albumId);
        dest.writeString(path);
        dest.writeInt(songCount);
        dest.writeParcelable(artUri, flags);
    }
}
