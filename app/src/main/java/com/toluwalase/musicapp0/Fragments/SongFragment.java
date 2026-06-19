package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_ALBUM;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_ART;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_ARTIST;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_BITRATE;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_COMPOSER;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_DATE;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_DURATION;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_FILE_NAME;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_GENRE;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_ID;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_NUMBER;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_PATH;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_SIZE;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_TITLE;
import static com.toluwalase.musicapp0.Database.MusicDB.COLUMN_YEAR;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.MainActivity.fromMainSearch;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.MusicAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Database.MusicDB;
import com.toluwalase.musicapp0.Interfaces.DeleteListener;
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

public class SongFragment extends Fragment {
    View view;
    RecyclerView mainRecyclerView;
    MainActivity activity;
    public static MusicAdapter musicAdapter;
    static SearchAdapter searchAdapter;
    TextView total_songs;
    TabLayout tabLayout;
    ImageView quick_shuffle, sort;
    boolean inFragment, inSearchPopUp;
    public static boolean fromSongFrag;
    AlertDialog alertDialog, prefDialog;
    CardView searchTV;
    String MY_SORT_PREF_SONGS = "SortOrderSongs";
    public static ArrayList<MusicFiles> allSongs = new ArrayList<>(musicFiles);
//    List<MusicFiles> dbSongs = new ArrayList<>();
    MusicFiles item = new MusicFiles();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public static SongFragment newInstance() {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        inFragment = true;
        inSearchPopUp = false;

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_song, container, false);
        initViews();

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
                showSearchPopUp();
            }
        });

        mainRecyclerView.setHasFixedSize(true);
        musicAdapter = new MusicAdapter(getContext(), songClickListener, deleteListener, musicFiles);
        sortList(allSongs);
        musicAdapter.submitList(allSongs);
        musicAdapter.notifyDataSetChanged();
        mainRecyclerView.setAdapter(musicAdapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        total_songs.setText("Total songs: " + String.valueOf(allSongs.size()));

        total_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRecyclerView.scrollToPosition(1);
            }
        });

        quick_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleClicked = true;
                NowPlayingFragment.shuffle_button.setImageResource(R.drawable.ic_shuffle_on);

                ArrayList<MusicFiles> list = new ArrayList<>(allSongs);
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

                QueueListItems newQueue = new QueueListItems(0, "All songs", list);
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

//                recentList.remove(recentList.get(getPos(recentList, song)));
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
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_SONGS, MODE_PRIVATE).edit();

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
                        Collections.sort(allSongs, new MusicComparator("title", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                title_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("title", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("artist", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByArtistAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("artist", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByArtistDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("album", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("album", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                genre_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("genre", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByGenreAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                genre_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("genre", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByGenreDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("date", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("date", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("year", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("year", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("size", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("size", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("duration", true));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(allSongs, new MusicComparator("duration", false));
                        musicAdapter.submitList(allSongs);
                        musicAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                prefDialog.show();
            }
        });

        return view;
    }

    private void sortList(List<MusicFiles> list) {
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_SONGS, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByTitleAscending");

        switch (sortOrder) {
            case "sortByTitleAscending":
                Collections.sort(list, new MusicComparator("title", true));
                break;
            case "sortByTitleDescending":
                Collections.sort(list, new MusicComparator("title", false));
                break;
            case "sortByArtistAscending":
                Collections.sort(list, new MusicComparator("artist", true));
                break;
            case "sortByArtistDescending":
                Collections.sort(list, new MusicComparator("artist", false));
                break;
            case "sortByAlbumAscending":
                Collections.sort(list, new MusicComparator("album", true));
                break;
            case "sortByAlbumDescending":
                Collections.sort(list, new MusicComparator("album", false));
                break;
            case "sortByGenreAscending":
                Collections.sort(list, new MusicComparator("genre", true));
                break;
            case "sortByGenreDescending":
                Collections.sort(list, new MusicComparator("genre", false));
                break;
            case "sortByDurationAscending":
                Collections.sort(list, new MusicComparator("duration", true));
                break;
            case "sortByDurationDescending":
                Collections.sort(list, new MusicComparator("duration", false));
                break;
            case "sortByYearAscending":
                Collections.sort(list, new MusicComparator("year", true));
                break;
            case "sortByYearDescending":
                Collections.sort(list, new MusicComparator("year", false));
                break;
            case "sortBySizeAscending":
                Collections.sort(list, new MusicComparator("size", true));
                break;
            case "sortBySizeDescending":
                Collections.sort(list, new MusicComparator("size", false));
                break;
            case "sortByDateAscending":
                Collections.sort(list, new MusicComparator("date", true));
                break;
            case "sortByDateDescending":
                Collections.sort(list, new MusicComparator("date", false));
                break;
        }
    }

    private void initViews() {
        mainRecyclerView = view.findViewById(R.id.recyclerView_songs);
        quick_shuffle = view.findViewById(R.id.quick_shuffle);
        searchTV = view.findViewById(R.id.search_text);
        total_songs = view.findViewById(R.id.total_songs);
        sort = view.findViewById(R.id.sort);
    }

    private void filter(String inputText, RecyclerView recyclerView) {
        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));

        ArrayList<MusicFiles> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();

            searchAdapter = new SearchAdapter(filteredList, requireContext(), songClickListener);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }else {
            for (MusicFiles song : allSongs) {
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

            searchAdapter = new SearchAdapter(filteredList, requireContext(), songClickListener);
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
//            searchAdapter.submitList(filteredList);
//            searchAdapter.notifyDataSetChanged();
//        }else {
//            for (MusicFiles song : allSongs) {
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
//            searchAdapter.submitList(filteredList);
//            searchAdapter.notifyDataSetChanged();
//        }
//    }

    public final SongClickListener songClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            if (inFragment) {
                fromSongFrag = true;
                fromMainSearch = false;
                QueueFragment.fromQueueFrag = false;

                ArrayList<MusicFiles> list = new ArrayList<>(allSongs);
                Bundle testData = new Bundle();
                testData.putParcelable("Song", songs);
                QueueListItems newQueue = new QueueListItems(0, "All songs", list);
                newQueue.setCurrent(true);
                newQueue.setSelected(true);
                if(shuffleClicked){
                    Collections.shuffle(list);
                    list.remove(songs);
                    list.add(0, songs);
                    position = 0;
                    newQueue.setPlayPosition(0);
                }
                else{
                    newQueue.setPlayPosition(position);
                }
                testData.putInt("SongPos", position);
                testData.putParcelableArrayList("QueueList", list);
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

//                recentList.remove(getPos(recentList, songs.getId()));
                recentList.remove(songs);
                recentList.add(0, songs);
                if(recentList.size() > 20){
                    recentList.remove(recentList.size() - 1);
                }
                recentAdapter.notifyDataSetChanged();
            }

            if(inSearchPopUp){
                alertDialog.dismiss();
                mainRecyclerView.scrollToPosition(getOriginalPos(songs.getId()));
            }
        }
    };

    public final DeleteListener deleteListener = new DeleteListener() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void delete(MusicFiles song, int position) throws IntentSender.SendIntentException {
            item = song;
            delete_file(song, position);
        }
    };

    private void showSearchPopUp(){
        inFragment = false;
        inSearchPopUp = true;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.search_layout, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
//        searchAdapter = new SearchAdapter(getContext(), songClickListener);
//        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

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

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                inFragment = true;
                inSearchPopUp = false;
            }
        });

        alertDialog.show();
    }

    private int getOriginalPos(String uID){
        for (int i = 0; i < allSongs.size(); i++){
            if (allSongs.get(i).getId().equals(uID)){
                return i;
            }
        }
        return -1;
    }

    public int getPos(ArrayList<MusicFiles> list, MusicFiles song){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getPath().equals(song.getPath())){
                return i;
            }
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void delete_file(MusicFiles song, int position) throws IntentSender.SendIntentException {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(song.getId()));

        try {
            requireContext().getContentResolver().delete(contentUri, null, null);
        } catch (SecurityException e) {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(contentUri);
                pendingIntent = MediaStore.createDeleteRequest(requireContext().getContentResolver(), uris);
            } else {
                if (e instanceof RecoverableSecurityException) {
                    RecoverableSecurityException exception = (RecoverableSecurityException) e;
                    pendingIntent = exception.getUserAction().getActionIntent();
                }
            }
            if (pendingIntent != null) {
                IntentSender intentSender = pendingIntent.getIntentSender();
                startIntentSenderForResult(intentSender, 100, null, 0, 0, 0, null);
            }
        }
    }

    private int playRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                Snackbar.make(view, "File deleted", Snackbar.LENGTH_LONG).show();
                allSongs.remove(item);
                sortList(allSongs);
                musicAdapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(view, "Cancelled", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}



















