package com.toluwalase.musicapp0.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.toluwalase.musicapp0.Models.MusicFiles;

import java.util.ArrayList;
import java.util.List;

public class MusicDB extends SQLiteOpenHelper {
    private static int DB_VERSION = 1;
    private static String DB_NAME = "music_app.db";
    private static String TABLE_NAME = "AllSongs";
    public static String COLUMN_ID = "SongID";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_ARTIST = "artist";
    public static String COLUMN_ALBUM = "album";
    public static String COLUMN_PATH = "path";
    public static String COLUMN_DURATION = "duration";
    public static String COLUMN_GENRE = "genre";
    public static String COLUMN_COMPOSER = "composer";
    public static String COLUMN_NUMBER = "number";
    public static String COLUMN_YEAR = "year";
    public static String COLUMN_SIZE = "size";
    public static String COLUMN_DATE = "date_added DATETIME DEFAULT CURRENT_TIMESTAMP";
    public static String COLUMN_BITRATE = "bitrate";
    public static String COLUMN_FILE_NAME = "fileName";
    public static String COLUMN_ART = "srt";
    public static String COLUMN_FSTATUS = "faveStatus";

    public static final String TABLE_CREATE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            COLUMN_ID + " TEXT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_ARTIST + " TEXT, " +
            COLUMN_ALBUM + " TEXT, " +
            COLUMN_GENRE + " TEXT, " +
            COLUMN_FILE_NAME + " TEXT, " +
            COLUMN_DURATION + " TEXT, " +
            COLUMN_PATH + " TEXT, " +
            COLUMN_ART + " TEXT, " +
            COLUMN_COMPOSER + " TEXT, " +
            COLUMN_NUMBER + " TEXT, " +
            COLUMN_YEAR + " TEXT, " +
            COLUMN_SIZE + " TEXT, " +
            COLUMN_BITRATE + " TEXT, " +
            COLUMN_FSTATUS + " TEXT, " +
            COLUMN_DATE + ")";

    public MusicDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Create empty table
    public void insertEmptyTable(ArrayList<MusicFiles> list){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < list.size() - 1; i++){
            contentValues.put(COLUMN_ID, list.get(i).getId());

            database.insert(TABLE_NAME, null, contentValues);
        }
    }

    //Insert data into database
    public void addSong(String title, String artist, String album, String ID, String genre, String path, String duration, String composer,
                             String number, String date, String year, String size, String bitrate, String fileName, String art, String faveStatus){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_ARTIST, artist);
        contentValues.put(COLUMN_ALBUM, album);
        contentValues.put(COLUMN_ID, ID);
        contentValues.put(COLUMN_GENRE, genre);
        contentValues.put(COLUMN_PATH, path);
        contentValues.put(COLUMN_DURATION, duration);
        contentValues.put(COLUMN_COMPOSER, composer);
        contentValues.put(COLUMN_NUMBER, number);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_YEAR, year);
        contentValues.put(COLUMN_SIZE, size);
        contentValues.put(COLUMN_BITRATE, bitrate);
        contentValues.put(COLUMN_FILE_NAME, fileName);
        contentValues.put(COLUMN_ART, art);
        contentValues.put(COLUMN_FSTATUS, faveStatus);
        database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor readAllData(String id){
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + COLUMN_ID + "=" + id + "";
        return database.rawQuery(sql, null, null);
    }

    public Cursor getRecentlyAdded(){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " > ?" + " ORDER BY " + COLUMN_DATE + " DESC LIMIT 10";

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor selectAllSongs(){
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        return database.rawQuery(sql, null, null);
    }
}
