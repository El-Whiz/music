package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.backToHome;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recommendedList;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inFaves;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHome;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inPlayList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.FavesAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Database.FaveDB;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FavouritesFragment extends Fragment {
    public static ArrayList<MusicFiles> faveSongs = new ArrayList<>();
    View view;
    MainActivity activity;
    TabLayout tabLayout;
    ImageView back, add, sort, shuffle;
    TextView songCount, title;
    CardView searchTV;
    RecyclerView favesRV;
    public static FavesAdapter favesAdapter;
    static SearchAdapter searchAdapter;
    AlertDialog prefDialog, searchDialog;
    String MY_SORT_PREF_FAVE_SONGS = "SortOrderFaveSongs";
    FaveDB faveDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favourites, container, false);
        activity = (MainActivity) getActivity();

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        faveDB = new FaveDB(requireContext());
        favesRV.setHasFixedSize(true);
        favesRV.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        loadFavesData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.loadAnHomeFragment(new HomeFragment());
                inHome = true;
                inPlayList = false;
                inFaves = false;
                backToHome = true;
            }
        });

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchPopUp();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleClicked = true;
                NowPlayingFragment.shuffle_button.setImageResource(R.drawable.ic_shuffle_on);

                ArrayList<MusicFiles> list = new ArrayList<>(faveSongs);
                int position = playRandom(list.size() - 1);
                MusicFiles song = list.get(position);

                Bundle testData = new Bundle();
                testData.putParcelable("Song", song);
                Collections.shuffle(list);
                list.remove(song);
                list.add(0, song);
                position = 0;
                testData.putInt("SongPos", position);
                testData.putParcelableArrayList("QueueList", list);

                QueueListItems newQueue = new QueueListItems(0, "Favourites", list);
                newQueue.setCurrent(true);
                newQueue.setSelected(true);
                newQueue.setPlayPosition(0);
                testData.putParcelable("QueueItem", newQueue);

                getParentFragmentManager().setFragmentResult("TestData", testData);

                if(NowPlayingFragment.cover_art.getVisibility() == View.INVISIBLE){
                    NowPlayingFragment.search_webView.setVisibility(View.INVISIBLE);
                    NowPlayingFragment.search_refresh_button.setVisibility(View.INVISIBLE);
                    NowPlayingFragment.search_refresh_button.setVisibility(View.INVISIBLE);
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

                historySongs.add(0, song);
                if(historyAdapter != null){
                    historyAdapter.notifyDataSetChanged();
                }

                recentList.remove(song);
                recentList.add(0, song);
                if(recentList.size() > 20){
                    recentList.remove(recentList.size() - 1);
                }
                recentAdapter.notifyDataSetChanged();

                tabLayout.getTabAt(1).select();
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_FAVE_SONGS, MODE_PRIVATE).edit();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.song_pref_layout, null);
                dialogBuilder.setView(dialogView);
                prefDialog = dialogBuilder.create();
                dialogView.setBackgroundResource(R.drawable.main_bg);

                TextView title_acs, title_desc, artist_asc, artist_desc, album_asc, album_desc, genre_asc, genre_desc, date_acs, date_desc, year_asc, year_desc, duration_asc, duration_desc, size_asc, size_desc;
                title_acs = dialogView.findViewById(R.id.title_ascending);
                title_desc = dialogView.findViewById(R.id.title_descending);
                artist_asc = dialogView.findViewById(R.id.artist_ascending);
                artist_desc = dialogView.findViewById(R.id.artist_descending);
                album_asc = dialogView.findViewById(R.id.album_ascending);
                album_desc = dialogView.findViewById(R.id.album_descending);
                genre_asc = dialogView.findViewById(R.id.genre_ascending);
                genre_desc = dialogView.findViewById(R.id.genre_descending);
                date_acs = dialogView.findViewById(R.id.date_ascending);
                date_desc = dialogView.findViewById(R.id.date_descending);
                year_asc = dialogView.findViewById(R.id.year_ascending);
                year_desc = dialogView.findViewById(R.id.year_descending);
                size_asc = dialogView.findViewById(R.id.size_ascending);
                size_desc = dialogView.findViewById(R.id.size_descending);
                duration_asc = dialogView.findViewById(R.id.duration_ascending);
                duration_desc = dialogView.findViewById(R.id.duration_descending);

                title_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("title", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                title_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("title", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("artist", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByArtistAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("artist", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByArtistDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("album", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("album", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                genre_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("genre", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByGenreAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                genre_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("genre", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByGenreDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("date", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("date", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("year", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("year", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("size", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("size", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("duration", true));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(faveSongs, new MusicComparator("duration", false));
//                        favesAdapter.submitList(faveSongs);
                        favesAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                prefDialog.show();
            }
        });
    }

    private void loadFavesData() {
        if(faveSongs != null){
            faveSongs.clear();
        }

        SQLiteDatabase database = faveDB.getReadableDatabase();
        Cursor cursor = faveDB.selectAllFaves();
        try {
            while (cursor.moveToNext()){
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_TITLE));
                @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_ARTIST));
                @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_ALBUM));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(FaveDB.KEY_ID));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_DURATION));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_PATH));
                @SuppressLint("Range") String genre = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_GENRE));
                @SuppressLint("Range") String composer = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_COMPOSER));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_NUMBER));
                @SuppressLint("Range") String year = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_YEAR));
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_SIZE));
                @SuppressLint("Range") String fileName = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_FILE_NAME));
                @SuppressLint("Range") String bitrate = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_BITRATE));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_DATE));
                @SuppressLint("Range") String art = cursor.getString(cursor.getColumnIndex(FaveDB.SONG_ART));
                MusicFiles fave = new MusicFiles(path, title, artist, album, duration, id, genre, composer, number, year, size, bitrate, date, fileName, art);
                fave.setFaveStatus("1");
                faveSongs.add(fave);
            }
        }
        finally {
            if(cursor != null && cursor.isClosed()){
                cursor.close();
            }
            database.close();
        }

        favesAdapter = new FavesAdapter(requireContext(), songClickListener, faveSongs);
        songCount.setText(String.valueOf(faveSongs.size()) + " songs");
        sortList();
//        favesAdapter.submitList(faveSongs);
        favesRV.setAdapter(favesAdapter);
    }

    private void initViews() {
        back = view.findViewById(R.id.back_button);
        searchTV = view.findViewById(R.id.search_text);
        shuffle = view.findViewById(R.id.quick_shuffle);
        sort = view.findViewById(R.id.sort);
        add = view.findViewById(R.id.play_list);
        favesRV = view.findViewById(R.id.playlistRV);
        title = view.findViewById(R.id.playlist_title);
        songCount = view.findViewById(R.id.playlist_song_count);
    }

    private void sortList() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_FAVE_SONGS, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByTitleAscending");

        switch (sortOrder) {
            case "sortByTitleAscending":
                Collections.sort(faveSongs, new MusicComparator("title", true));
                break;
            case "sortByTitleDescending":
                Collections.sort(faveSongs, new MusicComparator("title", false));
                break;
            case "sortByArtistAscending":
                Collections.sort(faveSongs, new MusicComparator("artist", true));
                break;
            case "sortByArtistDescending":
                Collections.sort(faveSongs, new MusicComparator("artist", false));
                break;
            case "sortByAlbumAscending":
                Collections.sort(faveSongs, new MusicComparator("album", true));
                break;
            case "sortByAlbumDescending":
                Collections.sort(faveSongs, new MusicComparator("album", false));
                break;
            case "sortByGenreAscending":
                Collections.sort(faveSongs, new MusicComparator("genre", true));
                break;
            case "sortByGenreDescending":
                Collections.sort(faveSongs, new MusicComparator("genre", false));
                break;
            case "sortByDurationAscending":
                Collections.sort(faveSongs, new MusicComparator("duration", true));
                break;
            case "sortByDurationDescending":
                Collections.sort(faveSongs, new MusicComparator("duration", false));
                break;
            case "sortByYearAscending":
                Collections.sort(faveSongs, new MusicComparator("year", true));
                break;
            case "sortByYearDescending":
                Collections.sort(faveSongs, new MusicComparator("year", false));
                break;
            case "sortBySizeAscending":
                Collections.sort(faveSongs, new MusicComparator("size", true));
                break;
            case "sortBySizeDescending":
                Collections.sort(faveSongs, new MusicComparator("size", false));
                break;
            case "sortByDateAscending":
                Collections.sort(faveSongs, new MusicComparator("date", true));
                break;
            case "sortByDateDescending":
                Collections.sort(faveSongs, new MusicComparator("date", false));
                break;
        }
    }

    private void filter(String inputText, RecyclerView recyclerView) {
        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));

        ArrayList<MusicFiles> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();
            searchAdapter = new SearchAdapter(filteredList, requireContext(), songSearchClickListener);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }else {
            for (MusicFiles song : faveSongs) {
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

            searchAdapter = new SearchAdapter(filteredList, requireContext(), songSearchClickListener);
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
//            searchAdapter.notifyDataSetChanged();
//        }else {
//            for (MusicFiles song : faveSongs) {
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
////            searchAdapter.submitList(filteredList);
//            searchAdapter.notifyDataSetChanged();
//        }
//    }

    private void showSearchPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.search_layout, null);
        dialogBuilder.setView(dialogView);
        searchDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
//        searchAdapter = new SearchAdapter(requireContext(), songSearchClickListener);
//        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        SearchView searchView = dialogView.findViewById(R.id.search_SV);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                filterSearch(newText);
                filter(newText, recyclerView);
                return false;
            }
        });

        searchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        searchDialog.show();
    }

    public final SongClickListener songClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            ArrayList<MusicFiles> myList = new ArrayList<>(faveSongs);
            Bundle testData = new Bundle();
            testData.putParcelable("Song", songs);
            QueueListItems newQueue = new QueueListItems(0, "Favourites", myList);
            newQueue.setCurrent(true);
            newQueue.setSelected(true);
            if(shuffleClicked){
                Collections.shuffle(myList);
                myList.remove(songs);
                myList.add(0, songs);
                position = 0;
                newQueue.setPlayPosition(0);
            }
            else{
                newQueue.setPlayPosition(position);
            }
            testData.putInt("SongPos", position);
            testData.putParcelableArrayList("QueueList", myList);
            testData.putParcelable("QueueItem", newQueue);
            int i = myQueues.size();
            getParentFragmentManager().setFragmentResult("TestData", testData);

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

            tabLayout.getTabAt(1).select();

            historySongs.add(0, songs);
            if(historyAdapter != null){
                historyAdapter.notifyDataSetChanged();
            }

            recentList.remove(songs);
            recentList.add(0, songs);
            if(recentList.size() > 20){
                recentList.remove(recentList.size() - 1);
            }
            recentAdapter.notifyDataSetChanged();
        }
    };

    public final SongClickListener songSearchClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            favesRV.scrollToPosition(getOriginalPos(songs.getId()));
            searchDialog.dismiss();
        }
    };

    private int playRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private int getOriginalPos(String UID){
        for (int i = 0; i < faveSongs.size(); i++){
            if (faveSongs.get(i).getId().equals(UID)){
                return i;
            }
        }
        return -1;
    }
}