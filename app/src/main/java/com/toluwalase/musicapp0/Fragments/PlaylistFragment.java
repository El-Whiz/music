package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.backToHome;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inFaves;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHome;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inPlayList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.MusicAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlaylistFragment extends Fragment {
    static ArrayList<MusicFiles> playlistSongs = new ArrayList<>();
    MainActivity activity;
    TabLayout tabLayout;
    AlertDialog searchDialog, addDialog, prefDialog;
    CardView searchTV;
    RecyclerView playlistRecycler;
    PlaylistItem playlist;
    View view;
    SearchAdapter musicSearchAdapter, searchAdapter;
    MusicAdapter songAdapter;
    ImageView back, add, sort, shuffle;
    TextView playlistCount, title;
    String MY_SORT_PREF_PLAYLIST_SONGS = "SortOrderPlaylistSongs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_playlist, container, false);
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
        getArgs();

        songAdapter = new MusicAdapter(requireContext(), songClickListener, null, playlistSongs);
        sortList();
        songAdapter.submitList(playlistSongs);
        songAdapter.notifyDataSetChanged();
        playlistRecycler.setAdapter(songAdapter);
        playlistRecycler.setHasFixedSize(true);
        playlistRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToPlayList();
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

                ArrayList<MusicFiles> list = new ArrayList<>(playlistSongs);
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

                QueueListItems newQueue = new QueueListItems(0, "Playlist: " + title.getText().toString(), list);
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
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_PLAYLIST_SONGS, MODE_PRIVATE).edit();

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
                        Collections.sort(playlistSongs, new MusicComparator("title", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                title_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("title", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("artist", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByArtistAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("artist", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByArtistDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("album", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("album", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                genre_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("genre", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByGenreAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                genre_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("genre", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByGenreDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("date", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("date", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("year", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("year", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("size", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("size", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("duration", true));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(playlistSongs, new MusicComparator("duration", false));
                        songAdapter.submitList(playlistSongs);
                        songAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                prefDialog.show();
            }
        });
    }

    private void getArgs() {
        Bundle args = getArguments();
        if(args != null){
            playlist = args.getParcelable("PlayList");
            assert playlist != null;
            playlistSongs = playlist.getSongs();
            title.setText(playlist.getTitle());
            playlistCount.setText(String.valueOf(playlist.getSongs().size()) + " songs");
        }
    }

    private void initViews() {
        back = view.findViewById(R.id.back_button);
        searchTV = view.findViewById(R.id.search_text);
        shuffle = view.findViewById(R.id.quick_shuffle);
        sort = view.findViewById(R.id.sort);
        add = view.findViewById(R.id.play_list);
        playlistRecycler = view.findViewById(R.id.playlistRV);
        title = view.findViewById(R.id.playlist_title);
        playlistCount = view.findViewById(R.id.playlist_song_count);
    }

    private void addToPlayList() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.search_layout, null);
        dialogBuilder.setView(dialogView);
        addDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
//        musicSearchAdapter = new SearchAdapter(requireContext(), searchClickListener);
//        recyclerView.setAdapter(musicSearchAdapter);
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
//                addSearchFilter(newText);
                addFilter(newText, recyclerView);
                return false;
            }
        });

        addDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        addDialog.show();
    }

    public final SongClickListener songClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            ArrayList<MusicFiles> myList = new ArrayList<>(playlistSongs);
            Bundle testData = new Bundle();
            testData.putParcelable("Song", songs);
            QueueListItems newQueue = new QueueListItems(0, "Playlist: " + title.getText().toString(), myList);
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

            recentList.remove(songs);
            recentList.add(0, songs);
            if(recentList.size() > 20){
                recentList.remove(recentList.size() - 1);
            }
            recentAdapter.notifyDataSetChanged();
        }
    };

    public final SongClickListener searchClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            playlistSongs.add(0, songs);
            songAdapter.notifyDataSetChanged();
            addDialog.dismiss();
        }
    };

    public final SongClickListener songSearchClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            playlistRecycler.scrollToPosition(getOriginalPos(songs.getId()));
            searchDialog.dismiss();
        }
    };

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

    private void addFilter(String inputText, RecyclerView recyclerView) {
        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));

        ArrayList<MusicFiles> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();
            musicSearchAdapter = new SearchAdapter(filteredList, requireContext(), searchClickListener);
            recyclerView.setAdapter(musicSearchAdapter);
            musicSearchAdapter.notifyDataSetChanged();
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

            musicSearchAdapter = new SearchAdapter(filteredList, requireContext(), searchClickListener);
            recyclerView.setAdapter(musicSearchAdapter);
            musicSearchAdapter.notifyDataSetChanged();
        }
    }

//    private void addSearchFilter(String inputText) {
//        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));
//
//        ArrayList<MusicFiles> filteredList = new ArrayList<>();
//
//        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
//            filteredList.clear();
////            musicSearchAdapter.submitList(filteredList);
//            musicSearchAdapter.notifyDataSetChanged();
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
////            musicSearchAdapter.submitList(filteredList);
//            musicSearchAdapter.notifyDataSetChanged();
//        }
//    }

    private void filter(String inputText, RecyclerView recyclerView) {
        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));

        ArrayList<MusicFiles> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();
            searchAdapter = new SearchAdapter(filteredList, requireContext(), songSearchClickListener);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }else {
            for (MusicFiles song : playlistSongs) {
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
//            for (MusicFiles song : playlistSongs) {
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

    private void sortList() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_PLAYLIST_SONGS, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByTitleAscending");

        switch (sortOrder) {
            case "sortByTitleAscending":
                Collections.sort(playlistSongs, new MusicComparator("title", true));
                break;
            case "sortByTitleDescending":
                Collections.sort(playlistSongs, new MusicComparator("title", false));
                break;
            case "sortByArtistAscending":
                Collections.sort(playlistSongs, new MusicComparator("artist", true));
                break;
            case "sortByArtistDescending":
                Collections.sort(playlistSongs, new MusicComparator("artist", false));
                break;
            case "sortByAlbumAscending":
                Collections.sort(playlistSongs, new MusicComparator("album", true));
                break;
            case "sortByAlbumDescending":
                Collections.sort(playlistSongs, new MusicComparator("album", false));
                break;
            case "sortByGenreAscending":
                Collections.sort(playlistSongs, new MusicComparator("genre", true));
                break;
            case "sortByGenreDescending":
                Collections.sort(playlistSongs, new MusicComparator("genre", false));
                break;
            case "sortByDurationAscending":
                Collections.sort(playlistSongs, new MusicComparator("duration", true));
                break;
            case "sortByDurationDescending":
                Collections.sort(playlistSongs, new MusicComparator("duration", false));
                break;
            case "sortByYearAscending":
                Collections.sort(playlistSongs, new MusicComparator("year", true));
                break;
            case "sortByYearDescending":
                Collections.sort(playlistSongs, new MusicComparator("year", false));
                break;
            case "sortBySizeAscending":
                Collections.sort(playlistSongs, new MusicComparator("size", true));
                break;
            case "sortBySizeDescending":
                Collections.sort(playlistSongs, new MusicComparator("size", false));
                break;
            case "sortByDateAscending":
                Collections.sort(playlistSongs, new MusicComparator("date", true));
                break;
            case "sortByDateDescending":
                Collections.sort(playlistSongs, new MusicComparator("date", false));
                break;
        }
    }

    private int getOriginalPos(String UID){
        for (int i = 0; i < playlistSongs.size(); i++){
            if (playlistSongs.get(i).getId().equals(UID)){
                return i;
            }
        }
        return -1;
    }

    private int playRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }
}