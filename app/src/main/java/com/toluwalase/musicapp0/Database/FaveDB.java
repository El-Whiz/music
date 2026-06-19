package com.toluwalase.musicapp0.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.toluwalase.musicapp0.Models.MusicFiles;

import java.util.ArrayList;

public class FaveDB extends SQLiteOpenHelper {
    private static int DB_VERSION = 4;
    private static String DB_NAME = "Faves";
    private static String TABLE_NAME = "FavesTable";
    public static String KEY_ID = "ID";
    public static String SONG_TITLE = "SongTitle";
    public static String SONG_ARTIST = "SongArtist";
    public static String SONG_ALBUM = "SongAlbum";
    public static String SONG_PATH = "SongPath";
    public static String SONG_DURATION = "SongDuration";
    public static String SONG_GENRE = "SongGenre";
    public static String SONG_COMPOSER = "SongComposer";
    public static String SONG_NUMBER = "SongNumber";
    public static String SONG_YEAR = "SongYear";
    public static String SONG_SIZE = "SongSize";
    public static String SONG_DATE = "SongDate";
    public static String SONG_BITRATE = "SongBitrate";
    public static String SONG_FILE_NAME = "SongFileName";
    public static String SONG_ART = "SongArt";
    public static String FAVE_STATUS = "FaveStatus";
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " TEXT," + SONG_TITLE + " TEXT," +
            SONG_ARTIST + " TEXT," + SONG_ALBUM + " TEXT," + SONG_PATH + " TEXT," + SONG_DURATION + " TEXT," + SONG_GENRE + " TEXT,"
            + SONG_COMPOSER + " TEXT," + SONG_NUMBER + " TEXT," + SONG_YEAR + " TEXT," + SONG_SIZE + " TEXT," + SONG_DATE + " TEXT,"
            + SONG_BITRATE + " TEXT," + SONG_FILE_NAME + " TEXT," + SONG_ART + " TEXT," + FAVE_STATUS + " TEXT)";

    public FaveDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Create empty table
    public void insertEmpty(ArrayList<MusicFiles> list){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < list.size() - 1; i++){//
            contentValues.put(KEY_ID, list.get(i).getId());//
            contentValues.put(FAVE_STATUS, "0");

            database.insert(TABLE_NAME, null, contentValues);
        }
    }

    //Insert data into database
    public void insertIntoDB(String title, String artist, String album, String ID, String genre, String path, String duration, String composer,
                             String number, String date, String year, String size, String bitrate, String fileName, String art, String faveStatus){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_TITLE, title);
        contentValues.put(SONG_ARTIST, artist);
        contentValues.put(SONG_ALBUM, album);
        contentValues.put(KEY_ID, ID);
        contentValues.put(SONG_GENRE, genre);
        contentValues.put(SONG_PATH, path);
        contentValues.put(SONG_DURATION, duration);
        contentValues.put(SONG_COMPOSER, composer);
        contentValues.put(SONG_NUMBER, number);
        contentValues.put(SONG_DATE, date);
        contentValues.put(SONG_YEAR, year);
        contentValues.put(SONG_SIZE, size);
        contentValues.put(SONG_BITRATE, bitrate);
        contentValues.put(SONG_FILE_NAME, fileName);
        contentValues.put(SONG_ART, art);
        contentValues.put(FAVE_STATUS, faveStatus);
        database.insert(TABLE_NAME, null, contentValues);
    }

    public void insertList(ArrayList<MusicFiles> list){
        for(MusicFiles item : list){
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SONG_TITLE, item.getTitle());
            contentValues.put(SONG_ARTIST, item.getArtist());
            contentValues.put(SONG_ALBUM, item.getAlbum());
            contentValues.put(KEY_ID, item.getId());
            contentValues.put(SONG_GENRE, item.getGenre());
            contentValues.put(SONG_PATH, item.getPath());
            contentValues.put(SONG_DURATION, item.getDuration());
            contentValues.put(SONG_COMPOSER, item.getComposer());
            contentValues.put(SONG_NUMBER, item.getNumber());
            contentValues.put(SONG_DATE, item.getDate());
            contentValues.put(SONG_YEAR, item.getYear());
            contentValues.put(SONG_SIZE, item.getSize());
            contentValues.put(SONG_BITRATE, item.getBitrate());
            contentValues.put(SONG_FILE_NAME, item.getFileName());
            contentValues.put(SONG_ART, item.getArtUri());
            contentValues.put(FAVE_STATUS, item.getFaveStatus());
            database.insert(TABLE_NAME, null, contentValues);
        }
    }

    public Cursor readAllData(String id){
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + KEY_ID + "=" + id + "";
        return database.rawQuery(sql, null, null);
    }

    public void removeFromFave(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET " + FAVE_STATUS + " ='0' WHERE " + KEY_ID + "=" + id + "";
        database.execSQL(sql);
    }

    public Cursor selectAllFaves(){
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + FAVE_STATUS + " ='1'";
        return database.rawQuery(sql, null, null);
    }
}
