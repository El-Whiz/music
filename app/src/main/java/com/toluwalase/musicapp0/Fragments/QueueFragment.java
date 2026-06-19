package com.toluwalase.musicapp0.Fragments;

import static com.toluwalase.musicapp0.Customs.MusicService.NOTIFICATION_VISIBLE;
import static com.toluwalase.musicapp0.Customs.MusicService.playActionListener;
import static com.toluwalase.musicapp0.Customs.MusicService.servicesMediaPlayer;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.songsList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.start_text;
import static com.toluwalase.musicapp0.MainActivity.GET_CURRENT_SONG;
import static com.toluwalase.musicapp0.MainActivity.LIST;
import static com.toluwalase.musicapp0.MainActivity.POS;
import static com.toluwalase.musicapp0.MainActivity.QUEUES;
import static com.toluwalase.musicapp0.MainActivity.SEEKBAR_POS;
import static com.toluwalase.musicapp0.MainActivity.STATUS;
import static com.toluwalase.musicapp0.MainActivity.TITLE_LIST;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toluwalase.musicapp0.Adapters.QueueAdapter;
import com.toluwalase.musicapp0.Adapters.QueueListAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Interfaces.QueueClickListener;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueFragment extends Fragment {
    public static String LIST_TITLE = null;
    public static QueueAdapter queueAdapter;
    public static QueueListAdapter queueListAdapter;
    public static RecyclerView queue_recyclerView_songs;
    private static RecyclerView queue_recyclerView_lists;
    public static TextView total_songs, queue_title;
    public static ImageView play_pause;
    RelativeLayout queue_header;
    private TabLayout tabLayout;
    public static ArrayList<MusicFiles> queueList = new ArrayList<>();
    public static ArrayList<QueueListItems> myQueues = new ArrayList<>();
    public static boolean fromQueueFrag;
    public static boolean isCurrentList = false;
    public static boolean inPlayMode = false;
    int positionX;
    CardView searchTV;
    View view;
    AlertDialog alertDialog, queue_dialog;
    static SearchAdapter searchAdapter;
    static ArrayList<MusicFiles> currentList = new ArrayList<>();
    static QueueListItems queueItem, currentQueue;

    public static QueueFragment newInstance() {
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_queue, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        showQueueListPopup();

        getParentFragmentManager().setFragmentResultListener("TestData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                //Add queues
                QueueListItems item = result.getParcelable("QueueItem");
                queueListAdapter = new QueueListAdapter(requireContext(), queueClickListener);
                queue_recyclerView_lists.setAdapter(queueListAdapter);
                queue_recyclerView_lists.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                if(myQueues != null) {
                    for (int i = 0; i < myQueues.size(); i++) {
                        if (myQueues.get(i).getTitle().equals(item.getTitle())) {
                            myQueues.remove(i);
                        }
                    }
                    for (QueueListItems data : myQueues){
                        data.setCurrent(false);
                        data.setSelected(false);
                    }
                }
                myQueues.add(item);
                isCurrentList = true;
                queueListAdapter.submitList(myQueues);

                //Add songs
                queueList = result.getParcelableArrayList("QueueList");
                currentList = queueList;
                queueAdapter = new QueueAdapter(getContext(), songClickListener, new NowPlayingFragment());
                queueAdapter.submitList(queueList);
                queueAdapter.notifyDataSetChanged();
                LIST_TITLE = item.getTitle();
                queue_title.setText(item.getTitle());
                total_songs.setText(queueList.size() + " songs");
                queue_recyclerView_songs.setAdapter(queueAdapter);
                queue_recyclerView_songs.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                //Pass song
                MusicFiles song = result.getParcelable("Song");
                positionX = result.getInt("SongPos");
                Bundle dataX = new Bundle();
                dataX.putParcelable("PlaySong", song);
                dataX.putInt("PlaySongPos", positionX);
                dataX.putParcelableArrayList("CurrentQueue", queueList);
                getParentFragmentManager().setFragmentResult("PlayData", dataX);
                play_pause.setImageResource(R.drawable.ic_pause_white);
                play_pause.setColorFilter(Color.WHITE);
                queue_title.setTextColor(Color.WHITE);
                total_songs.setTextColor(Color.WHITE);

                queue_recyclerView_songs.scrollToPosition(positionX);
            }
        });

        queue_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = -1;
                if(myQueues != null && myQueues.size() > 1){
                    for (int i = 0; i < myQueues.size(); i++){
                        if(myQueues.get(i).isCurrent()){
                            pos = i;
                        }
                    }
                    QueueListItems que = myQueues.get(pos);
                    myQueues.remove(pos);
                    queueListAdapter.notifyDataSetChanged();
                    myQueues.add(myQueues.size(), que);
                    queueListAdapter.notifyDataSetChanged();
                }
                queue_dialog.show();
            }
        });

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchPopUp();
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playActionListener != null){
                    if(isCurrentList) {
                        playActionListener.playpausebuttonClicked();
                    }else{
                        playActionListener.playpauseForOtherQueues(queueItem.getPlayPosition(), queueItem);
                        play_pause.setImageResource(R.drawable.ic_pause_white);
                        int pos = -1;
                        if(myQueues != null){
                            for (int i = 0; i < myQueues.size(); i++){
                                if(myQueues.get(i).isSelected()){
                                    pos = i;
                                }
                            }
                            for(QueueListItems que: myQueues){
                                que.setCurrent(false);
                            }
                            myQueues.get(pos).setCurrent(true);
                            isCurrentList = true;
                            queueAdapter.notifyDataSetChanged();
                            queueListAdapter.notifyDataSetChanged();
                            play_pause.setColorFilter(Color.WHITE);
                            queue_title.setTextColor(Color.WHITE);
                            total_songs.setTextColor(Color.WHITE);
                        }
                    }
                }
            }
        });

        if(GET_CURRENT_SONG && !NOTIFICATION_VISIBLE){
            getStoredData();
        }

        if(NOTIFICATION_VISIBLE){
            getCurrentData();
        }
    }

    private void getCurrentData() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        Type queueListType = new TypeToken<ArrayList<QueueListItems>>() {}.getType();
        ArrayList<MusicFiles> storedList = gson.fromJson(LIST, listType);
        ArrayList<QueueListItems> queues = gson.fromJson(QUEUES, queueListType);
        String title_list = TITLE_LIST;

        myQueues = queues;
        if(myQueues != null){
            for(QueueListItems item : myQueues){
                if(item.isCurrent()){
                    item.setSelected(true);
                }else{
                    item.setSelected(false);
                }
            }
        }
        queueListAdapter = new QueueListAdapter(requireContext(), queueClickListener);
        queue_recyclerView_lists.setAdapter(queueListAdapter);
        queue_recyclerView_lists.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        queueListAdapter.submitList(myQueues);
        queue_title.setText(title_list);
        isCurrentList = true;

        queueList = storedList;
        currentList = storedList;
        songsList = storedList;
        queueAdapter = new QueueAdapter(getContext(), songClickListener, new NowPlayingFragment());
        queueAdapter.submitList(queueList);
        queueAdapter.notifyDataSetChanged();
        queue_title.setText(title_list);
        if(queueList != null) {
            total_songs.setText(String.valueOf(queueList.size()) + " songs");
        }
        queue_recyclerView_songs.setAdapter(queueAdapter);
        queue_recyclerView_songs.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        if(servicesMediaPlayer.isPlaying()){
            play_pause.setImageResource(R.drawable.ic_pause_white);
        }else {
            play_pause.setImageResource(R.drawable.ic_play_white);
        }
    }

    private void getStoredData() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        Type queueListType = new TypeToken<ArrayList<QueueListItems>>() {}.getType();
        ArrayList<MusicFiles> storedList = gson.fromJson(LIST, listType);
        ArrayList<QueueListItems> queues = gson.fromJson(QUEUES, queueListType);
        int storedPos = POS;
        int seekBarPos = SEEKBAR_POS;
        String title_list = TITLE_LIST;

        myQueues = queues;
        if(myQueues != null){
            for(QueueListItems item : myQueues){
                if(item.isCurrent()){
                    item.setSelected(true);
                }else{
                    item.setSelected(false);
                }
            }
        }
        queueListAdapter = new QueueListAdapter(requireContext(), queueClickListener);
        queue_recyclerView_lists.setAdapter(queueListAdapter);
        queue_recyclerView_lists.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        queueListAdapter.submitList(myQueues);

        isCurrentList = true;
        queueList = storedList;
        currentList = storedList;
        queueAdapter = new QueueAdapter(getContext(), songClickListener, new NowPlayingFragment());
        queueAdapter.submitList(queueList);
        queueAdapter.notifyDataSetChanged();
        queue_title.setText(title_list);
        total_songs.setText(queueList.size() + " songs");
        queue_recyclerView_songs.setAdapter(queueAdapter);
        queue_recyclerView_songs.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        play_pause.setImageResource(R.drawable.ic_play_white);

        MusicFiles song = queueList.get(storedPos);
        song.setFaveStatus(STATUS);
        Bundle dataX = new Bundle();
        dataX.putParcelable("PlaySong", song);
        dataX.putInt("PlaySongPos", storedPos);
        dataX.putInt("SeekbarPos", seekBarPos);
        dataX.putParcelableArrayList("CurrentQueue", queueList);
        getParentFragmentManager().setFragmentResult("StoredData", dataX);

        tabLayout.getTabAt(1).select();
    }

    public void showSearchPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.search_layout, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
//        searchAdapter = new SearchAdapter(getContext(), songSearchClickListener);
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
            }
        });

        alertDialog.show();
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
            for (MusicFiles song : queueList) {
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
//            searchAdapter.submitList(filteredList);
//            searchAdapter.notifyDataSetChanged();
//        }else {
//            for (MusicFiles song : queueList) {
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

    private void initViews() {
        searchTV = view.findViewById(R.id.search_text);
        queue_header = view.findViewById(R.id.header_queue);
//        queue_header.setVisibility(View.GONE);
        queue_recyclerView_songs = view.findViewById(R.id.recyclerView_queue);
        total_songs = view.findViewById(R.id.total_items);
        queue_title = view.findViewById(R.id.queue_title);
        queue_title.setSelected(true);
        play_pause = view.findViewById(R.id.queue_play_pause);
    }

    private void showQueueListPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.queue_panel, null);
        dialogBuilder.setView(dialogView);
        queue_dialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        queue_recyclerView_lists = dialogView.findViewById(R.id.queue_panel_recyclerView);
        queue_recyclerView_lists.setHasFixedSize(true);

        TextView clearQueues = dialogView.findViewById(R.id.remove_all_queues);
        clearQueues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (QueueListItems item : myQueues){
                    if(item.isCurrent()){
                        currentQueue = item;
                        break;
                    }
                }
                myQueues.clear();
                myQueues.add(currentQueue);
                queueListAdapter.notifyDataSetChanged();
            }
        });

        queue_recyclerView_lists.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(myQueues.size() > 1){
                    clearQueues.setVisibility(View.VISIBLE);
                }else{
                    clearQueues.setVisibility(View.GONE);
                }
            }
        });
    }

    public final SongClickListener songSearchClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            alertDialog.dismiss();
            queue_recyclerView_songs.scrollToPosition(getOriginalPos(songs.getId()));
        }
    };

    public final SongClickListener songClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            fromQueueFrag = true;
            MainActivity.fromMainSearch = false;
            SongFragment.fromSongFrag = false;

            queueList = currentList;
            isCurrentList = true;
            LIST_TITLE = queue_title.getText().toString();

            Bundle data = new Bundle();
            data.putParcelable("PlaySong", songs);
            data.putInt("PlaySongPos", position);
            data.putParcelableArrayList("CurrentQueue", currentList);
            getParentFragmentManager().setFragmentResult("PlayData", data);

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

            historySongs.add(0, songs);
            if(historyAdapter != null){
                historyAdapter.notifyDataSetChanged();
            }

//            recentList.remove(getPos(recentList, songs.getId()));
            recentList.remove(songs);
            recentList.add(0, songs);
            if(recentList.size() > 20){
                recentList.remove(recentList.size() - 1);
            }
            recentAdapter.notifyDataSetChanged();

            int pos = -1;
            for (int i = 0; i < myQueues.size(); i++){
                if(myQueues.get(i).isSelected()){
                    pos = i;
                }
            }
            for (QueueListItems queueListItems : myQueues){
                queueListItems.setCurrent(false);
            }
            myQueues.get(pos).setCurrent(true);
            myQueues.get(pos).setPlayPosition(position);
            queueListAdapter.notifyDataSetChanged();

            queueAdapter.changeOutlineColor(position, Color.GREEN);
            queueAdapter.notifyDataSetChanged();

            play_pause.setImageResource(R.drawable.ic_pause_white);
            play_pause.setColorFilter(Color.WHITE);
            queue_title.setTextColor(Color.WHITE);
            total_songs.setTextColor(Color.WHITE);
        }
    };

    public final QueueClickListener queueClickListener = new QueueClickListener() {
        @Override
        public void onClick(QueueListItems item, int position) {
            currentList = item.getSongs();

            if(item.isCurrent()){
                isCurrentList = true;
                if(servicesMediaPlayer.isPlaying()) {
                    play_pause.setImageResource(R.drawable.ic_pause_white);
                }else{
                    play_pause.setImageResource(R.drawable.ic_play_white);
                }
                play_pause.setColorFilter(Color.WHITE);
                queue_title.setTextColor(Color.WHITE);
                total_songs.setTextColor(Color.WHITE);
            }else{
                isCurrentList = false;
                play_pause.setImageResource(R.drawable.ic_play_white);
                play_pause.setColorFilter(Color.DKGRAY);
                queue_title.setTextColor(Color.DKGRAY);
                total_songs.setTextColor(Color.DKGRAY);
            }

            queueAdapter.submitList(item.getSongs());
            queueAdapter.notifyDataSetChanged();
            queue_title.setText(item.getTitle());
            total_songs.setText(String.valueOf(currentList.size()) + " songs");
            queueItem = item;
            Toast.makeText(requireContext(), String.valueOf(item.getPlayPosition()), Toast.LENGTH_SHORT).show();
            queue_dialog.dismiss();
        }
    };

    public static int getOriginalPos(String uID){
        for (int i = 0; i < queueList.size(); i++){
            if (queueList.get(i).getId().equals(uID)){
                return i;
            }
        }
        return -1;
    }

    public static int getQueuePos(String title){
        for(int i = 0; i < myQueues.size(); i++){
            if(myQueues.get(i).getTitle().equals(title)){
                return i;
            }
        }

        return -1;
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