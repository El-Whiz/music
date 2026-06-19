package com.toluwalase.musicapp0.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ArtistItems implements Parcelable {
    String path;
    String artist;
    String artistId;
    String imageUrl;
    ArrayList<MusicFiles> artistSongs;
    int songsCount = -1;
    int albumsCount = -1;

    protected ArtistItems(Parcel in) {
        path = in.readString();
        artist = in.readString();
        artistId = in.readString();
        imageUrl = in.readString();
//        artistSongs = in.createTypedArrayList(MusicFiles.CREATOR);
        in.readTypedList(artistSongs, MusicFiles.CREATOR);
        songsCount = in.readInt();
        albumsCount = in.readInt();
    }

    public static final Creator<ArtistItems> CREATOR = new Creator<ArtistItems>() {
        @Override
        public ArtistItems createFromParcel(Parcel in) {
            return new ArtistItems(in);
        }

        @Override
        public ArtistItems[] newArray(int size) {
            return new ArtistItems[size];
        }
    };

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

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(int songsCount) {
        this.songsCount = songsCount;
    }

    public int getAlbumsCount() {
        return albumsCount;
    }

    public void setAlbumsCount(int albumsCount) {
        this.albumsCount = albumsCount;
    }

    public ArrayList<MusicFiles> getArtistSongs() {
        return artistSongs;
    }

    public void setArtistSongs(ArrayList<MusicFiles> artistSongs) {
        this.artistSongs = artistSongs;
    }

    public ArtistItems(){

    }

    public ArtistItems(String artist, String id, String path){
        this.artist = artist;
        this.artistId = id;
        this.path = path;
        this.artistSongs = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(artist);
        dest.writeString(artistId);
        dest.writeString(imageUrl);
        dest.writeTypedList(artistSongs);
        dest.writeInt(songsCount);
        dest.writeInt(albumsCount);
    }
}
