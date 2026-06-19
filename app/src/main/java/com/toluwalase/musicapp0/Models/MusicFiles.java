package com.toluwalase.musicapp0.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MusicFiles implements Parcelable {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;
    private String genre;
    private String composer;
    private String number;
    private String year;
    private String size;
    private String bitrate;
    private String date;
    private String fileName;
    private String faveStatus;
    private String artUri;
    private String lyrics = "";

    public MusicFiles(){

    }

    public MusicFiles(String path, String title, String artist, String album, String duration, String id, String genre, String composer, String number, String year, String size,
            String bitrate, String date, String fileName, String art) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id = id;
        this.genre = genre;
        this.composer = composer;
        this.number = number;
        this.year = year;
        this.size = size;
        this.bitrate = bitrate;
        this.date = date;
        this.fileName = fileName;
        this.faveStatus = "0";
        this.artUri = art;
        this.lyrics = "";
    }

    protected MusicFiles (Parcel parcel){
        path = parcel.readString();
        title = parcel.readString();
        artist = parcel.readString();
        album = parcel.readString();;
        duration = parcel.readString();;
        id = parcel.readString();
        genre = parcel.readString();
        composer = parcel.readString();
        number = parcel.readString();
        year = parcel.readString();
        size = parcel.readString();
        bitrate = parcel.readString();
        date = parcel.readString();
        fileName = parcel.readString();
        faveStatus = parcel.readString();
//        artUri = parcel.readParcelable(Uri.class.getClassLoader());
        artUri = parcel.readString();
        lyrics = parcel.readString();
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String duration) {
        this.id = id;
    }

    public String getFaveStatus() {
        return faveStatus;
    }

    public void setFaveStatus(String faveStatus) {
        this.faveStatus = faveStatus;
    }

    public String getArtUri() {
        return artUri;
    }

    public void setArtUri(String artUri) {
        this.artUri = artUri;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public static final Creator<MusicFiles> CREATOR = new Creator<MusicFiles>() {
        @Override
        public MusicFiles createFromParcel(Parcel parcel) {
            return new MusicFiles(parcel);
        }

        @Override
        public MusicFiles[] newArray(int size) {
            return new MusicFiles[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(path);
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(duration);
        parcel.writeString(id);
        parcel.writeString(genre);
        parcel.writeString(composer);
        parcel.writeString(number);
        parcel.writeString(year);
        parcel.writeString(size);
        parcel.writeString(bitrate);
        parcel.writeString(date);
        parcel.writeString(fileName);
        parcel.writeString(faveStatus);
        parcel.writeString(artUri);
        parcel.writeString(lyrics);
//        parcel.writeParcelable(artUri, flags);
    }
}
