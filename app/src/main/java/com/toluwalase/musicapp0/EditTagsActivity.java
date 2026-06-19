package com.toluwalase.musicapp0;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import javax.sql.DataSource;

public class EditTagsActivity extends AppCompatActivity {
    EditText tag_title, tag_artist, tag_album, tag_genre, tag_year;
    ImageView edit_back_btn, edit_done_btn, edit_tag_art;

    String path;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tags);

        initViews();
//        getSongData();
//        path = getIntent().getStringExtra("path");

        edit_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_done_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
//                updateSongDetails();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void updateSongDetails() {
        String updated_title, updated_artist, updated_album, updated_genre, updated_year;
        updated_title = tag_title.getText().toString();
        updated_artist = tag_artist.getText().toString();

        File file = new File(path);
        Uri uri = Uri.fromFile(file);
//        DefaultDataSourceFactory
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, uri);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
        catch (Exception e) {
        }
        finally {
//            retriever.release();
        }
    }

    private void getSongData() {
        String original_title, original_album, original_artist, original_genre, original_year, song_art, uri;
        uri = getIntent().getStringExtra("Uri");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art == null){
            Glide.with(this).asBitmap().load(R.drawable.default_art).into(edit_tag_art);
        }
        else {
            Glide.with(this).asBitmap().load(art).into(edit_tag_art);
        }

        original_title = getIntent().getStringExtra("title");
        tag_title.setText(original_title);
        original_artist = getIntent().getStringExtra("artist");
        tag_artist.setText(original_artist);
    }

    private void initViews() {
        tag_album = findViewById(R.id.edit_tag_album);
        tag_artist = findViewById(R.id.edit_tag_artist);
        tag_title = findViewById(R.id.edit_tag_title);
        tag_genre = findViewById(R.id.edit_tag_genre);
        tag_year = findViewById(R.id.edit_tag_year);
        edit_back_btn = findViewById(R.id.edit_back_button);
        edit_done_btn = findViewById(R.id.edit_done_button);
        edit_tag_art = findViewById(R.id.edit_tag_art);
    }
}