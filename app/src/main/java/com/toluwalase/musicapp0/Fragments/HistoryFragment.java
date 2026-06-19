package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Customs.MusicService.NOTIFICATION_VISIBLE;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.backToHome;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHome;
import static com.toluwalase.musicapp0.Fragments.MainFragment.inHistory;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.MainActivity.GET_CURRENT_SONG;
import static com.toluwalase.musicapp0.MainActivity.PLAY_HISTORY;
import static com.toluwalase.musicapp0.MainActivity.PLAY_LISTS;
import static com.toluwalase.musicapp0.MainActivity.RECENT_SONGS;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toluwalase.musicapp0.Adapters.MusicAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HistoryFragment extends Fragment {
    View view;
    public static ArrayList<MusicFiles> historySongs = new ArrayList<>();
    MainActivity activity;
    TabLayout tabLayout;
    ImageView back, add, shuffle;
    TextView songCount, title;
    CardView searchTV;
    RecyclerView historyRV;
    AlertDialog prefDialog, searchDialog;
    public static MusicAdapter historyAdapter;
    SearchAdapter searchAdapter;
    String MY_SORT_PREF_HISTORY = "SortOrderHistory";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
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

        if(!NOTIFICATION_VISIBLE && GET_CURRENT_SONG){
            getStoredData();
        }

        historyRV.setHasFixedSize(true);
        historyAdapter = new MusicAdapter(requireContext(), songClickListener, null, historySongs);
        historyAdapter.submitList(historySongs);
        historyAdapter.notifyDataSetChanged();
        historyRV.setAdapter(historyAdapter);
        historyRV.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        songCount.setText(String.valueOf(historySongs.size()) + " songs");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.loadAnHomeFragment(new HomeFragment());
                inHome = true;
                inHistory = false;
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

                ArrayList<MusicFiles> list = new ArrayList<>(historySongs);
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

                QueueListItems newQueue = new QueueListItems(0, "History", list);
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
    }

    private void getStoredData() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        ArrayList<MusicFiles> savedList = gson.fromJson(PLAY_HISTORY, listType);

        historySongs = savedList;
    }

    private void initViews() {
        back = view.findViewById(R.id.back_button);
        searchTV = view.findViewById(R.id.search_text);
        shuffle = view.findViewById(R.id.quick_shuffle);
        add = view.findViewById(R.id.play_list);
        historyRV = view.findViewById(R.id.playlistRV);
        title = view.findViewById(R.id.playlist_title);
        songCount = view.findViewById(R.id.playlist_song_count);
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
            for (MusicFiles song : historySongs) {
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
//            for (MusicFiles song : historySongs) {
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
            ArrayList<MusicFiles> myList = new ArrayList<>(historySongs);
            Bundle testData = new Bundle();
            testData.putParcelable("Song", songs);
            QueueListItems newQueue = new QueueListItems(0, "History", myList);
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
            historyAdapter.notifyDataSetChanged();

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
            historyRV.scrollToPosition(getOriginalPos(songs.getId()));
            searchDialog.dismiss();
        }
    };

    private int playRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private int getOriginalPos(String UID){
        for (int i = 0; i < historySongs.size(); i++){
            if (historySongs.get(i).getId().equals(UID)){
                return i;
            }
        }
        return -1;
    }
}