package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Fragments.ArtistFragment.inArtistItems;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historyAdapter;
import static com.toluwalase.musicapp0.Fragments.HistoryFragment.historySongs;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentAdapter;
import static com.toluwalase.musicapp0.Fragments.HomeFragment.recentList;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.MainActivity.albumFiles;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.toluwalase.musicapp0.Adapters.ArtistContentAlbumsAdapter;
import com.toluwalase.musicapp0.Adapters.ArtistContentSongsAdapter;
import com.toluwalase.musicapp0.Adapters.SearchAdapter;
import com.toluwalase.musicapp0.Customs.ArtistInfoResponse;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Interfaces.AlbumClickListener;
import com.toluwalase.musicapp0.Interfaces.LastFMApiService;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.ArtistItems;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArtistContentFragment extends Fragment {
    public ArrayList<MusicFiles> artistSongs = new ArrayList<>();
    public ArrayList<AlbumItems> albumItems = new ArrayList<>();
    ArrayList<MusicFiles> currentList = new ArrayList<>();
    View view;
    public static int scrollPosArtist;
    int previous = -1;
    ImageView backButton, shuffle, sort, artistImage;
    RecyclerView artistSongsRV, artistAlbumsRV;
    TextView artist_name, total_songs, total_albums, artist_album;
    MainActivity activity;
    TabLayout tabLayout;
    public static ArtistContentSongsAdapter songsAdapter;
    ArtistContentAlbumsAdapter albumsAdapter;
    public boolean singleItem = false;
    public static int scrollPos;
    CardView searchTV;
    AlertDialog alertDialog, prefDialog;
    static SearchAdapter searchAdapter;
    String MY_SORT_PREF_ARTIST_CONTENTS = "SortOrderArtistSongs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_artist_content, container, false);
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
        getArgs();

        artistSongsRV.setHasFixedSize(true);
        songsAdapter = new ArtistContentSongsAdapter(requireContext(), songClickListener);
        sortList();
        songsAdapter.submitList(artistSongs);
        songsAdapter.notifyDataSetChanged();
        artistSongsRV.setAdapter(songsAdapter);
        artistSongsRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        artistAlbumsRV.setHasFixedSize(true);
        albumsAdapter = new ArtistContentAlbumsAdapter(requireContext(), albumClickListener);
        albumsAdapter.submitList(albumItems);
        artistAlbumsRV.setAdapter(albumsAdapter);
        artistAlbumsRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.loadAnArtistFragment(new ArtistItemFragment());
                inArtistItems = true;
            }
        });

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

                ArrayList<MusicFiles> list = new ArrayList<>(currentList);
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

                QueueListItems newQueue = new QueueListItems(0, artist_name.getText().toString() + ": " + artist_album.getText(), list);
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

                tabLayout.getTabAt(1).select();

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
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_ARTIST_CONTENTS, MODE_PRIVATE).edit();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.artist_content_song_pref_layout, null);
                dialogBuilder.setView(dialogView);
                prefDialog = dialogBuilder.create();
                dialogView.setBackgroundResource(R.drawable.main_bg);

                TextView title_acs, title_desc, album_asc, album_desc, date_acs, date_desc, year_asc, year_desc, duration_asc, duration_desc, size_asc, size_desc;
                title_acs = dialogView.findViewById(R.id.title_ascending);
                title_desc = dialogView.findViewById(R.id.title_descending);
                album_asc = dialogView.findViewById(R.id.album_ascending);
                album_desc = dialogView.findViewById(R.id.album_descending);
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
                        Collections.sort(currentList, new MusicComparator("title", true));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                title_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("title", false));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByTitleDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("album", true));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("album", false));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_acs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("date", true));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("date", false));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDateDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("year", true));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                year_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("year", false));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByYearDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("size", true));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                size_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("size", false));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortBySizeDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("duration", true));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByDurationAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                duration_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(currentList, new MusicComparator("duration", false));
                        songsAdapter.submitList(currentList);
                        songsAdapter.notifyDataSetChanged();
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
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_ARTIST_CONTENTS, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByAlbumAscending");

        switch (sortOrder) {
            case "sortByTitleAscending":
                Collections.sort(artistSongs, new MusicComparator("title", true));
                break;
            case "sortByTitleDescending":
                Collections.sort(artistSongs, new MusicComparator("title", false));
                break;
            case "sortByAlbumAscending":
                Collections.sort(artistSongs, new MusicComparator("album", true));
                break;
            case "sortByAlbumDescending":
                Collections.sort(artistSongs, new MusicComparator("album", false));
                break;
            case "sortByDurationAscending":
                Collections.sort(artistSongs, new MusicComparator("duration", true));
                break;
            case "sortByDurationDescending":
                Collections.sort(artistSongs, new MusicComparator("duration", false));
                break;
            case "sortByYearAscending":
                Collections.sort(artistSongs, new MusicComparator("year", true));
                break;
            case "sortByYearDescending":
                Collections.sort(artistSongs, new MusicComparator("year", false));
                break;
            case "sortBySizeAscending":
                Collections.sort(artistSongs, new MusicComparator("size", true));
                break;
            case "sortBySizeDescending":
                Collections.sort(artistSongs, new MusicComparator("size", false));
                break;
            case "sortByDateAscending":
                Collections.sort(artistSongs, new MusicComparator("date", true));
                break;
            case "sortByDateDescending":
                Collections.sort(artistSongs, new MusicComparator("date", false));
                break;
        }
    }

    private void getArgs() {
        Bundle args = getArguments();
        if(args != null) {
            scrollPosArtist = args.getInt("position");
            ArtistItems item = args.getParcelable("ArtistItem");
            assert item != null;

            if(artistSongs != null){
                artistSongs.clear();
            }
            int j = 0;
            for (int i = 0 ; i < musicFiles.size() ; i++){
                if(item.getArtist().equalsIgnoreCase(musicFiles.get(i).getArtist())){
                    artistSongs.add(j, musicFiles.get(i));
                    j++;
                }
            }
            currentList = artistSongs;

            if(albumItems != null){
                albumItems.clear();
            }
            int k = 0;
            for(int i = 0; i < albumFiles.size(); i++){
                if(item.getArtist().equalsIgnoreCase(albumFiles.get(i).getArtist())){
                    albumItems.add(k, albumFiles.get(i));
                    k++;
                }
            }

            artist_name.setText(item.getArtist());
            total_albums.setText(String.valueOf(artistSongs.size()));
            total_songs.setText(String.valueOf(albumItems.size()));
        }
    }

    private void initViews() {
        searchTV = view.findViewById(R.id.search_text);
        backButton = view.findViewById(R.id.artist_back_button);
        artistAlbumsRV = view.findViewById(R.id.recyclerView_artist_albums);
        artistSongsRV = view.findViewById(R.id.recyclerView_artist_songs);
        artist_name = view.findViewById(R.id.artist_name);
        total_albums = view.findViewById(R.id.total_albums);
        total_songs = view.findViewById(R.id.total_song);
        artist_album = view.findViewById(R.id.album_name);
        artist_album.setSelected(true);
        shuffle = view.findViewById(R.id.quick_shuffle);
        sort = view.findViewById(R.id.sort);
        artistImage = view.findViewById(R.id.album_cover);
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
            for (MusicFiles song : currentList) {
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
//            for (MusicFiles song : currentList) {
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
            ArrayList<MusicFiles> list = new ArrayList<>(currentList);
            Bundle artistData = new Bundle();

            artistData.putParcelable("Song", songs);

            QueueListItems newQueue = new QueueListItems(0, artist_name.getText().toString() + ": " + artist_album.getText(), list);
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
            artistData.putInt("SongPos", position);
            artistData.putParcelableArrayList("QueueList", list);

            artistData.putParcelable("QueueItem", newQueue);

            getParentFragmentManager().setFragmentResult("TestData", artistData);


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

            tabLayout.getTabAt(1).select();
        }
    };

    public final SongClickListener songSearchClickListener = new SongClickListener() {
        @Override
        public void onClick(MusicFiles songs, int position) {
            alertDialog.dismiss();
            artistSongsRV.scrollToPosition(getOriginalPos(songs.getId()));
        }
    };

    private int getOriginalPos(String uID){
        for (int i = 0; i < currentList.size(); i++){
            if (currentList.get(i).getId().equals(uID)){
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

    private int playRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    public final AlbumClickListener albumClickListener = new AlbumClickListener() {
        @Override
        public void onClick(AlbumItems album, int position) {
            ArrayList<MusicFiles> songs = new ArrayList<>();

            int j = 0;
            for (int i = 0; i < musicFiles.size(); i++) {
                if (album.getAlbum().toLowerCase().equals(musicFiles.get(i).getAlbum().toLowerCase())) {
                    songs.add(j, musicFiles.get(i));
                    j++;
                }
            }

            if(!singleItem) {
                currentList = songs;
                songsAdapter.submitList(songs);
                songsAdapter.notifyDataSetChanged();
                total_albums.setText(String.valueOf(songs.size()));
                albumsAdapter.changeAlbumOutline(position, Color.WHITE);
                previous = position;
                artist_album.setText(album.getAlbum());
                singleItem = true;
            }else{
                if(position == previous){
                    currentList = artistSongs;
                    albumsAdapter.changeAlbumOutline(position, 0);
                    songsAdapter.submitList(artistSongs);
                    songsAdapter.notifyDataSetChanged();
                    total_albums.setText(String.valueOf(artistSongs.size()));
                    previous = -1;
                    artist_album.setText("All");
                    singleItem = false;
                }else{
                    currentList = songs;
                    songsAdapter.submitList(songs);
                    songsAdapter.notifyDataSetChanged();
                    total_albums.setText(String.valueOf(songs.size()));
                    albumsAdapter.changeAlbumOutline(position, Color.WHITE);
                    previous = position;
                    artist_album.setText(album.getAlbum());
                    singleItem = true;
                }
            }
        }
    };
}