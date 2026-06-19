package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Fragments.AlbumFragment.inItems;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.AlbumContentAdapter;
import com.toluwalase.musicapp0.Adapters.SearchActivityAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AlbumContentFragment extends Fragment {
    public static ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    RecyclerView contentRV;
    ImageView albumCover, shuffle, sort;
    TextView albumArtist, albumTitle, total_songs;
    View view;
    MainActivity activity;
    TabLayout tabLayout;
    public static ImageView content_back_button;
    public static int scrollPos;
    CardView searchTV;
    AlertDialog alertDialog, prefDialog;
    static SearchAdapter searchAdapter;
    public static AlbumContentAdapter albumContentAdapter;
    String MY_SORT_PREF_ALBUM_CONTENTS = "SortOrderAlbumSongs";

    public AlbumContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_album_content, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
        }

        initViews();
        getBundleArgs();

        content_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.loadAnAlbumFragment(new AlbumItemFragment());
                inItems = true;
            }
        });

        contentRV.setHasFixedSize(true);
        albumContentAdapter = new AlbumContentAdapter(getContext(), songClickListener);
        sortList();
        albumContentAdapter.submitList(albumSongs);
        contentRV.setAdapter(albumContentAdapter);
        contentRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
                showSearchPopUp();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleClicked = true;
                NowPlayingFragment.shuffle_button.setImageResource(R.drawable.ic_shuffle_on);

                ArrayList<MusicFiles> list = new ArrayList<>(albumSongs);
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

                QueueListItems newQueue = new QueueListItems(0, albumArtist.getText().toString() + ": " + albumTitle.getText(), list);
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

//                recentList.remove(getPos(recentList, song.getId()));
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
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_ALBUM_CONTENTS, MODE_PRIVATE).edit();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.album_content_pref_layout, null);
                dialogBuilder.setView(dialogView);
                prefDialog = dialogBuilder.create();
                dialogView.setBackgroundResource(R.drawable.main_bg);

                TextView title_acs, title_desc, date_acs, date_desc, duration_asc, duration_desc, size_asc, size_desc;
                title_acs = dialogView.findViewById(R.id.title_ascending);
                title_desc = dialogView.findViewById(R.id.title_descending);
                date_acs = dialogView.findViewById(R.id.date_ascending);
                date_desc = dialogView.findViewById(R.id.date_descending);
                size_asc = dialogView.findViewById(R.id.size_ascending);
                size_desc = dialogView.findViewById(R.id.size_descending);
                duration_asc = dialogView.findViewById(R.id.duration_ascending);
                duration_desc = dialogView.findViewById(R.id.duration_descending);

                title_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("title", true));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                title_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("title", false));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("date", true));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("date", false));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("size", true));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("size", false));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("duration", true));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumSongs, new MusicComparator("duration", false));
                        albumContentAdapter.submitList(albumSongs);
                        albumContentAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                prefDialog.show();
            }
        });
    }

    private void sortList() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_ALBUM_CONTENTS, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByAlbumAscending");

        switch (sortOrder) {
            case "sortByTitleAscending":
                Collections.sort(albumSongs, new MusicComparator("title", true));
                break;
            case "sortByTitleDescending":
                Collections.sort(albumSongs, new MusicComparator("title", false));
                break;
            case "sortByDurationAscending":
                Collections.sort(albumSongs, new MusicComparator("duration", true));
                break;
            case "sortByDurationDescending":
                Collections.sort(albumSongs, new MusicComparator("duration", false));
                break;
            case "sortBySizeAscending":
                Collections.sort(albumSongs, new MusicComparator("size", true));
                break;
            case "sortBySizeDescending":
                Collections.sort(albumSongs, new MusicComparator("size", false));
                break;
            case "sortByDateAscending":
                Collections.sort(albumSongs, new MusicComparator("date", true));
                break;
            case "sortByDateDescending":
                Collections.sort(albumSongs, new MusicComparator("date", false));
                break;
        }
    }

    private void showSearchPopUp(){
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
            for (MusicFiles song : albumSongs) {
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
//        ArrayList<MusicFiles> filteredList = new ArrayList<>();
//
//        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
//            filteredList.clear();
//            searchAdapter.submitList(filteredList);
//            searchAdapter.notifyDataSetChanged();
//        }else {
//            for (MusicFiles song : albumSongs) {
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

    private void getBundleArgs() {
        Bundle args = getArguments();
        if(args != null){
            scrollPos = args.getInt("Position");
            AlbumItems album_item = args.getParcelable("Item");

            if(albumSongs != null){
                albumSongs.clear();
            }
            int j = 0;
            for (int i = 0 ; i < musicFiles.size() ; i++){
                if(album_item.getAlbum().toLowerCase().equals(musicFiles.get(i).getAlbum().toLowerCase())){
                    albumSongs.add(j, musicFiles.get(i));
                    j++;
                }
            }

            albumTitle.setText(album_item.getAlbum());
            albumArtist.setText(album_item.getArtist());
            total_songs.setText(String.valueOf(albumSongs.size()));

            if(album_item.getArtUri() != null){
                Glide.with(requireContext()).load(album_item.getArtUri()).placeholder(R.drawable.default_art).error(R.drawable.default_art).into(albumCover);
            }else{
                Glide.with(requireContext()).load(R.drawable.default_art).into(albumCover);
            }
        }
    }

    private void initViews(){
        content_back_button = view.findViewById(R.id.album_back_button);
        contentRV = view.findViewById(R.id.recyclerView_album_songs);
        albumCover = view.findViewById(R.id.album_cover);
        albumArtist = view.findViewById(R.id.album_artist);
        albumTitle = view.findViewById(R.id.album_content_name);
        total_songs = view.findViewById(R.id.total_album_songs_text);
        searchTV = view.findViewById(R.id.search_text);
        shuffle = view.findViewById(R.id.quick_shuffle);
        sort = view.findViewById(R.id.sort);
    }

    public final SongClickListener songClickListener = new SongClickListener() {

        @Override
        public void onClick(MusicFiles songs, int position) {
            ArrayList<MusicFiles> list = new ArrayList<>(albumSongs);
            Bundle albumData = new Bundle();

            albumData.putParcelable("Song", songs);

            QueueListItems newQueue = new QueueListItems(0, albumArtist.getText().toString() + ": " + albumTitle.getText(), list);
            newQueue.setCurrent(true);
            newQueue.setSelected(true);
            if(shuffleClicked){
                Collections.shuffle(list);
                list.remove(songs);
                list.add(0, songs);
                position = 0;
                newQueue.setPlayPosition(0);
            }else{
                newQueue.setPlayPosition(position);
            }
            albumData.putInt("SongPos", position);
            albumData.putParcelableArrayList("QueueList", list);

            albumData.putParcelable("QueueItem", newQueue);

            getParentFragmentManager().setFragmentResult("TestData", albumData);


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
//            recentAdapter.notifyItemChanged(getPos(recentList, songs.getId()));
            recentList.remove(songs);
            recentList.add(0, songs);
            if(recentList.size() > 20){
                recentList.remove(recentList.size() - 1);
            }
            recentAdapter.notifyDataSetChanged();

            tabLayout.getTabAt(1).select();
        }
    };

    public final SongClickListener songSearchClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            alertDialog.dismiss();
            contentRV.scrollToPosition(getOriginalPos(songs.getId()));
        }
    };

    private int getOriginalPos(String uID){
        for (int i = 0; i < albumSongs.size(); i++){
            if (albumSongs.get(i).getId().equals(uID)){
                return i;
            }
        }
        return -1;
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