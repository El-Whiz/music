package com.toluwalase.musicapp0;

import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.toluwalase.musicapp0.Adapters.MusicAdapter;
import com.toluwalase.musicapp0.Adapters.SearchActivityAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Fragments.NowPlayingFragment;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.QueueListItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    ImageView back_button;
    SearchView searchView;
    RecyclerView recyclerView;
    private ArrayList<MusicFiles> mainSearchList = new ArrayList<>();
    static SearchActivityAdapter searchAdapter;
    public static String SEARCH_LIST = null;
    public static String SEARCH_QUEUE = null;
    private static String list_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list_title = "Search: " + newText;
//                filterSearch(newText);
                filter(newText);
                return false;
            }
        });

//        recyclerView.setHasFixedSize(true);
//        searchAdapter = new SearchActivityAdapter(this, songClickListener);
//        recyclerView.setAdapter(searchAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void initViews() {
        back_button = findViewById(R.id.back_button);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.main_search_panel_recyclerView);
    }

    private void filter(String inputText){
        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));

        ArrayList<MusicFiles> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();
            searchAdapter = new SearchActivityAdapter(filteredList, this, songClickListener);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }else {
            for (MusicFiles song : musicFiles) {
                String title = song.getTitle().toLowerCase();
                String album = song.getAlbum().toLowerCase();
                String artist = song.getArtist().toLowerCase();

                boolean containsAllUserInput = true;

                for (String userInput : userInputs) {
                    if (!title.contains(userInput) && !album.contains(userInput) && !artist.contains(userInput)) {
                        containsAllUserInput = false;
                        break;
                    }
                }

                if (containsAllUserInput) {
                    filteredList.add(song);
                }
            }

            mainSearchList = filteredList;
            searchAdapter = new SearchActivityAdapter(filteredList, this, songClickListener);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }
    }

//    private void filterSearch(String inputText) {
//        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));
//
//        ArrayList<MusicFiles> filteredList = new ArrayList<>();
//
//        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
//            filteredList.clear();
////            searchAdapter.submitList(filteredList);
//            searchAdapter = new SearchActivityAdapter(filteredList, this, songClickListener);
//            searchAdapter.notifyDataSetChanged();
//        }else {
//            for (MusicFiles song : musicFiles) {
//                String title = song.getTitle().toLowerCase();
//                String album = song.getAlbum().toLowerCase();
//                String artist = song.getArtist().toLowerCase();
//
//                boolean containsAllUserInput = true;
//
//                for (String userInput : userInputs) {
//                    if (!title.contains(userInput) && !album.contains(userInput) && !artist.contains(userInput)) {
//                        containsAllUserInput = false;
//                        break;
//                    }
//                }
//
//                if (containsAllUserInput) {
//                    filteredList.add(song);
//                }
//            }
//
//            mainSearchList = filteredList;
////            searchAdapter.submitList(filteredList);
//            searchAdapter = new SearchActivityAdapter(filteredList, this, songClickListener);
//            searchAdapter.notifyDataSetChanged();
//        }
//    }

    public final SongClickListener songClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            ArrayList<MusicFiles> list = new ArrayList<>(mainSearchList);

            QueueListItems newQueue = new QueueListItems(0, list_title, list);

            if(NowPlayingFragment.cover_art.getVisibility() == View.INVISIBLE){
                NowPlayingFragment.search_webView.setVisibility(View.INVISIBLE);
                NowPlayingFragment.search_refresh_button.setVisibility(View.INVISIBLE);
                NowPlayingFragment.search_cancel_button.setVisibility(View.INVISIBLE);
                NowPlayingFragment.lyrics_search_txt.setVisibility(View.INVISIBLE);
                NowPlayingFragment.edit_button.setVisibility(View.INVISIBLE);
                NowPlayingFragment.edit_complete_button.setVisibility(View.INVISIBLE);
                NowPlayingFragment.cancel_button.setVisibility(View.INVISIBLE);
                NowPlayingFragment.lyrics_textView.setVisibility(View.INVISIBLE);
                NowPlayingFragment.lyrics_options_panel.setVisibility(View.INVISIBLE);

                NowPlayingFragment.cover_art.setVisibility(View.VISIBLE);
            }

            if(inEditLyrics){
                NowPlayingFragment.convertTo_textView();
                inEditLyrics = false;
            }

            if(inLoop){
                if(inLoop) {
                    inLoop = false;
                    loop_btn.setImageResource(R.drawable.ic_loop_off);
                    loop_panel.setVisibility(View.INVISIBLE);
                    loopMP.release();
                    loopMP = null;
                }
            }

            Gson gson = new Gson();
            String searchListString = gson.toJson(list);
            String queue = gson.toJson(newQueue);
            SEARCH_LIST = searchListString;
            SEARCH_QUEUE = queue;

            Intent intent = new Intent();
            intent.putExtra("Song", songs);
            intent.putExtra("pos", position);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                Snackbar.make(recyclerView, "File deleted", Snackbar.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(recyclerView, "Cancelled", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}































