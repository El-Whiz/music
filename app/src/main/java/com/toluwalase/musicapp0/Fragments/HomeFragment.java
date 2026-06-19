package com.toluwalase.musicapp0.Fragments;

import static com.toluwalase.musicapp0.Customs.MusicService.NOTIFICATION_VISIBLE;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inFaves;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHistory;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHome;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inPlayList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.Fragments.SongFragment.allSongs;
import static com.toluwalase.musicapp0.MainActivity.GET_CURRENT_SONG;
import static com.toluwalase.musicapp0.MainActivity.PLAY_LISTS;
import static com.toluwalase.musicapp0.MainActivity.RECENT_SONGS;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;
import static com.toluwalase.musicapp0.SearchActivity.SEARCH_LIST;
import static com.toluwalase.musicapp0.SearchActivity.SEARCH_QUEUE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toluwalase.musicapp0.Adapters.HomeAdapter;
import com.toluwalase.musicapp0.Adapters.PlaylistAdapter;
import com.toluwalase.musicapp0.Database.FaveDB;
import com.toluwalase.musicapp0.Interfaces.ButtonCLickListener;
import com.toluwalase.musicapp0.Interfaces.PlaylistClickListener;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;
import com.toluwalase.musicapp0.SearchActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HomeFragment extends Fragment {
    public static ArrayList<PlaylistItem> playLists = new ArrayList<>();
    static ArrayList<MusicFiles> recommendedList = new ArrayList<>();
    public static ArrayList<MusicFiles> recentList = new ArrayList<>();
    View view;
    CardView searchTV;
    ImageView refresh, add_to_playlist;
    RecyclerView recommendedRV, recentRV, playlistRV;
    LinearLayout recommendedLayout;
    RelativeLayout playlist, recycler, favourites, history_btn;
    MainActivity activity;
    TabLayout tabLayout;
    AlertDialog alertDialog, addPlaylistDialog;
    public static PlaylistAdapter playlistAdapter;
    public static HomeAdapter recommendedAdapter, recentAdapter;
    private static String list_title;
    public static boolean backToHome = false;
    private ArrayList<MusicFiles> mainSearchList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        activity = (MainActivity) getActivity();

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
        }

        if(GET_CURRENT_SONG && !NOTIFICATION_VISIBLE){
            getStoredData();
        }

        if(!backToHome) {
            if (musicFiles.size() > 16) {
                recommendedList = getRecommended(musicFiles, 16);
            }else{
                recommendedLayout.setVisibility(View.GONE);
            }
        }
        recommendedAdapter = new HomeAdapter(requireContext(), recommendedClickListener);
        recommendedAdapter.submitList(recommendedList);
        recommendedAdapter.notifyDataSetChanged();
        recommendedRV.setAdapter(recommendedAdapter);
        recommendedRV.setHasFixedSize(true);
        recommendedRV.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false));

        recentAdapter = new HomeAdapter(requireContext(), recentClickedListener);
        recentAdapter.submitList(recentList);
        recentAdapter.notifyDataSetChanged();
        recentRV.setAdapter(recentAdapter);
        recentRV.setHasFixedSize(true);
        recentRV.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false));

        playlistAdapter = new PlaylistAdapter(requireContext(), playlistClickListener, playClickListener);
        playlistAdapter.submitList(playLists);
        playlistAdapter.notifyDataSetChanged();
        playlistRV.setAdapter(playlistAdapter);
        playlistRV.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

//        searchTV.setVisibility(View.GONE);
//        searchTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SearchActivity.class);
//                startActivityForResult(intent, 1);
//
////                loadSearchPopup();
//            }
//        });

        refresh.setVisibility(View.INVISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendedList.clear();
                recommendedList = getRecommended(musicFiles, 16);
                recommendedAdapter.submitList(recommendedList);
                recommendedAdapter.notifyDataSetChanged();
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recycler.getVisibility() == View.VISIBLE) {
                    recycler.setVisibility(View.GONE);
                    add_to_playlist.setVisibility(View.GONE);
                }else{
                    recycler.setVisibility(View.VISIBLE);
                    add_to_playlist.setVisibility(View.VISIBLE);
                }
            }
        });

        add_to_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylist();
            }
        });

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.loadAnHomeFragment(new FavouritesFragment());
                inFaves = true;
                inHome = false;
            }
        });

        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.loadAnHomeFragment(new HistoryFragment());
                inHome = false;
                inHistory = true;
            }
        });
    }

    private void getStoredData() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        Type playListType = new TypeToken<ArrayList<PlaylistItem>>() {}.getType();
        ArrayList<MusicFiles> recent = gson.fromJson(RECENT_SONGS, listType);
        ArrayList<PlaylistItem> playlistItems = gson.fromJson(PLAY_LISTS, playListType);

        recentList = recent;
        playLists = playlistItems;
    }

    private ArrayList<MusicFiles> getRecommended(ArrayList<MusicFiles> list, int count) {
        Random random = new Random();
        ArrayList<MusicFiles> myList = new ArrayList<>();
        while (myList.size() < count){
            int randomIndex = random.nextInt(list.size());
            MusicFiles randomMusic = list.get(randomIndex);

            if(!myList.contains(randomMusic)){
                myList.add(randomMusic);
            }
        }
        return myList;
    }

//    private void loadSearchPopup() {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
//        View dialogView = getLayoutInflater().inflate(R.layout.search_layout, null);
//        dialogBuilder.setView(dialogView);
//        alertDialog = dialogBuilder.create();
//        dialogView.setBackgroundResource(R.drawable.main_bg);
//
//        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
//        musicAdapter = new MusicAdapter(getContext(), searchSongClickListener);
//        recyclerView.setAdapter(musicAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
//
//        SearchView searchView = dialogView.findViewById(R.id.search_SV);
//        searchView.requestFocus();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                list_title = "Search: " + newText;
//                filterSearch(newText);
//                return false;
//            }
//        });
//
//        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//
//            }
//        });
//
//        alertDialog.show();
//    }

    private void initViews() {
        searchTV = view.findViewById(R.id.search_textView);
        recommendedRV = view.findViewById(R.id.recommended_recycler);
        recentRV = view.findViewById(R.id.recent_recycler);
        refresh = view.findViewById(R.id.refresh);
        playlist = view.findViewById(R.id.playlistRL);
        recycler = view.findViewById(R.id.playlist_recycler);
        playlistRV = view.findViewById(R.id.playlistRV);
        add_to_playlist = view.findViewById(R.id.add);
        favourites = view.findViewById(R.id.favourite_add);
        recommendedLayout = view.findViewById(R.id.recommended_layout);
        history_btn = view.findViewById(R.id.history_layout);
    }

    public final SongClickListener recentClickedListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            Bundle testData = new Bundle();
            testData.putParcelable("Song", songs);
            QueueListItems newQueue = new QueueListItems(0, "All songs", allSongs);
            newQueue.setCurrent(true);
            newQueue.setSelected(true);

            historySongs.add(0, songs);
            if(historyAdapter != null){
                historyAdapter.notifyDataSetChanged();
            }

            recentList.remove(getPos(recentList, songs.getId()));
//            recentList.remove(songs);
            recentList.add(0, songs);

            if(shuffleClicked){
                Collections.shuffle(allSongs);
                allSongs.remove(songs);
                allSongs.add(0, songs);
                position = 0;
                newQueue.setPlayPosition(0);
            }
            else{
                position = getPos(allSongs, songs.getId());
                newQueue.setPlayPosition(position);
            }
            testData.putInt("SongPos", position);
            testData.putParcelableArrayList("QueueList", allSongs);
            testData.putParcelable("QueueItem", newQueue);

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
            recentAdapter.notifyDataSetChanged();
        }
    };

    public final SongClickListener recommendedClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            ArrayList<MusicFiles> list = new ArrayList<>(recommendedList);
            Bundle testData = new Bundle();
            testData.putParcelable("Song", songs);
            QueueListItems newQueue = new QueueListItems(0, "Recommended: " + songs.getTitle(), list);
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

//    public final SongClickListener searchSongClickListener = new SongClickListener() {
//        @Override
//        public void onClick(MusicFiles songs, int position) {
//            Bundle testData = new Bundle();
//            testData.putParcelable("Song", songs);
//            QueueListItems newQueue = new QueueListItems(0, list_title, mainSearchList);
//            newQueue.setCurrent(true);
//            newQueue.setSelected(true);
//            if(shuffleClicked){
//                Collections.shuffle(mainSearchList);
//                mainSearchList.remove(songs);
//                mainSearchList.add(0, songs);
//                position = 0;
//                newQueue.setPlayPosition(0);
//            }
//            else{
//                newQueue.setPlayPosition(position);
//            }
//            testData.putInt("SongPos", position);
//            testData.putParcelableArrayList("QueueList", mainSearchList);
//            testData.putParcelable("QueueItem", newQueue);
//
//            getParentFragmentManager().setFragmentResult("TestData", testData);
//
//            if(NowPlayingFragment.cover_art.getVisibility() == View.INVISIBLE){
//                NowPlayingFragment.search_webView.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.search_refresh_button.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.search_cancel_button.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.lyrics_search_txt.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.edit_button.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.edit_complete_button.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.cancel_button.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.lyrics_textView.setVisibility(View.INVISIBLE);
//                NowPlayingFragment.lyrics_options_panel.setVisibility(View.INVISIBLE);
//
//                NowPlayingFragment.cover_art.setVisibility(View.VISIBLE);
//            }
//
//            if(inEditLyrics){
//                NowPlayingFragment.convertTo_textView();
//                inEditLyrics = false;
//            }
//
//            if(inLoop){
//                if(inLoop) {
//                    inLoop = false;
//                    loop_btn.setImageResource(R.drawable.ic_loop_off);
//                    loop_panel.setVisibility(View.INVISIBLE);
//                    loopMP.release();
//                    loopMP = null;
//                }
//            }
//
//            alertDialog.dismiss();
//            tabLayout.getTabAt(1).select();
//
//            recentList.remove(songs);
//            recentList.add(0, songs);
//            if(recentList.size() > 20){
//                recentList.remove(recentList.size() - 1);
//            }
//            recentAdapter.notifyDataSetChanged();
//        }
//    };

    public final ButtonCLickListener playClickListener = new ButtonCLickListener() {
        @Override
        public void playPL(PlaylistItem item, int position) {
            ArrayList<MusicFiles> list = new ArrayList<>(item.getSongs());
            Bundle testData = new Bundle();
            MusicFiles song = new MusicFiles();
            QueueListItems newQueue = new QueueListItems(0, "Playlist: " + item.getTitle(), list);
            newQueue.setCurrent(true);
            newQueue.setSelected(true);
            position = 0;

            if(shuffleClicked){
                song = list.get(playRandom(list.size()));
                Collections.shuffle(list);
                list.remove(song);
                list.add(position, song);
                newQueue.setPlayPosition(position);
            }
            else{
                song = list.get(position);
                newQueue.setPlayPosition(position);
            }

            testData.putParcelable("Song", song);
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
        }
    };

    public final PlaylistClickListener playlistClickListener = new PlaylistClickListener() {
        @Override
        public void onClick(PlaylistItem playlist, int position) {
            PlaylistFragment playlistFragment = new PlaylistFragment();
            Bundle data = new Bundle();
            data.putParcelable("PlayList", playlist);
            data.putInt("position", position);
            playlistFragment.setArguments(data);
            activity.loadAnHomeFragment(playlistFragment);
            inPlayList = true;
            inHome = false;
        }
    };

//    private void filterSearch(String inputText) {
//        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));
//
//        ArrayList<MusicFiles> filteredList = new ArrayList<>();
//
//        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
//            filteredList.clear();
//            musicAdapter.submitList(filteredList);
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
//            musicAdapter.submitList(filteredList);
//            musicAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            if(data != null) {
                MusicFiles song = data.getParcelableExtra("Song");
                int pos = data.getIntExtra("pos", -1);

                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
                ArrayList<MusicFiles> list = gson.fromJson(SEARCH_LIST, listType);
                QueueListItems queue = gson.fromJson(SEARCH_QUEUE, QueueListItems.class);

                Bundle testData = new Bundle();
                testData.putParcelable("Song", song);
                queue.setCurrent(true);
                queue.setSelected(true);
                if(shuffleClicked){
                    Collections.shuffle(list);
                    list.remove(song);
                    list.add(0, song);
                    pos = 0;
                    queue.setPlayPosition(0);
                }
                else{
                    queue.setPlayPosition(pos);
                }
                testData.putInt("SongPos", pos);
                testData.putParcelableArrayList("QueueList", list);
                testData.putParcelable("QueueItem", queue);

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
            }
        }
    }

    private void addPlaylist() {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.rename_popup, null);
        dialogBuilder.setView(dialogView);
        addPlaylistDialog = dialogBuilder.create();

        TextView header = dialogView.findViewById(R.id.alert_title);
        header.setText("Add playlist name");

        EditText name = dialogView.findViewById(R.id.queue_name);
        TextView yes_btn, no_button;
        name.requestFocus();

        yes_btn = dialogView.findViewById(R.id.yes_button);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean containsTitle = false;
                for(PlaylistItem item : playLists){
                    if(item.getTitle().trim().equalsIgnoreCase(name.getText().toString().trim())){
                        containsTitle = true;
                        break;
                    }
                }
                if(containsTitle){
                    Toast.makeText(requireContext(), "Name already used", Toast.LENGTH_SHORT).show();
                }else {
                    if(name.getText().toString().isEmpty()){
                        Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    }else {
                        PlaylistItem playList = new PlaylistItem();
                        playList.setTitle(name.getText().toString().trim());
                        playLists.add(0, playList);
                        playlistAdapter.notifyDataSetChanged();
                        addPlaylistDialog.dismiss();
                    }
                }
            }
        });

        no_button = dialogView.findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylistDialog.dismiss();
            }
        });

        addPlaylistDialog.show();
    }

    private int playRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    public int getPos(ArrayList<MusicFiles> list, String uID){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getId().equals(uID)){
                return i;
            }
        }
        return -1;
    }
}