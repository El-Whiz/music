package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Fragments.AlbumContentFragment.scrollPos;
import static com.toluwalase.musicapp0.Fragments.AlbumFragment.inItems;
import static com.toluwalase.musicapp0.MainActivity.albumFiles;
import static com.toluwalase.musicapp0.MainActivity.artistFiles;
import static com.toluwalase.musicapp0.MainActivity.musicFiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.AlbumAdapter;
import com.toluwalase.musicapp0.Adapters.AlbumSearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Interfaces.AlbumClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.ArtistItems;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumItemFragment extends Fragment{
    public static RecyclerView recyclerView_album_item;
    View view;
    AlertDialog alertDialog, prefDialog;
    AlbumSearchAdapter albumAdapterSearch;
    MainActivity activity;
    TabLayout tabLayout;
    CardView searchTV;
    TextView total_albums;
    ImageView sort;
    String MY_SORT_PREF_ALBUM_ITEMS = "SortOrderAlbumsItems";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        initViews();

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
        }

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
                showSearchPopUp();
            }
        });

        recyclerView_album_item.setHasFixedSize(true);
        AlbumAdapter albumAdapter = new AlbumAdapter(requireContext(), albumClickListener);
        sortList();
        albumAdapter.submitList(albumFiles);
        albumAdapter.notifyDataSetChanged();
        recyclerView_album_item.setAdapter(albumAdapter);
        recyclerView_album_item.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView_album_item.scrollToPosition(scrollPos);
        total_albums.setText("Total albums: " + String.valueOf(albumFiles.size()));

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_ALBUM_ITEMS, MODE_PRIVATE).edit();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.album_items_pref_layout, null);
                dialogBuilder.setView(dialogView);
                prefDialog = dialogBuilder.create();
                dialogView.setBackgroundResource(R.drawable.main_bg);

                TextView album_asc, album_desc,  artist_asc, artist_desc, total_songs_asc, total_songs_desc, total_albums_asc, total_albums_desc;
                artist_asc = dialogView.findViewById(R.id.artist_ascending);
                artist_desc = dialogView.findViewById(R.id.artist_descending);
                total_songs_asc = dialogView.findViewById(R.id.total_songs_ascending);
                total_songs_desc = dialogView.findViewById(R.id.total_songs_descending);
                album_asc = dialogView.findViewById(R.id.album_ascending);
                album_desc = dialogView.findViewById(R.id.album_descending);

                album_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                            @Override
                            public int compare(AlbumItems o1, AlbumItems o2) {
                                return o1.getAlbum().compareTo(o2.getAlbum());
                            }
                        });
                        albumAdapter.submitList(albumFiles);
                        albumAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                album_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                            @Override
                            public int compare(AlbumItems o1, AlbumItems o2) {
                                return o2.getAlbum().compareTo(o1.getAlbum());
                            }
                        });
                        albumAdapter.submitList(albumFiles);
                        albumAdapter.notifyDataSetChanged();
                        editor.putString("sorting", "sortByAlbumDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                            @Override
                            public int compare(AlbumItems o1, AlbumItems o2) {
                                return o1.getArtist().compareTo(o2.getArtist());
                            }
                        });
                        albumAdapter.submitList(albumFiles);
                        albumAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortByArtistAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                            @Override
                            public int compare(AlbumItems o1, AlbumItems o2) {
                                return o2.getArtist().compareTo(o1.getArtist());
                            }
                        });
                        albumAdapter.submitList(albumFiles);
                        albumAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortByArtistDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                total_songs_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                            @Override
                            public int compare(AlbumItems o1, AlbumItems o2) {
                                return Integer.compare(o1.getSongCount(), o2.getSongCount());
                            }
                        });
                        albumAdapter.submitList(albumFiles);
                        albumAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortBySongsAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                total_songs_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                            @Override
                            public int compare(AlbumItems o1, AlbumItems o2) {
                                return Integer.compare(o2.getSongCount(), o1.getSongCount());
                            }
                        });
                        albumAdapter.submitList(albumFiles);
                        albumAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortBySongsDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                prefDialog.show();
            }
        });

        getArgs();
    }

    private void sortList() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_ALBUM_ITEMS, MODE_PRIVATE);
        String sortOrder = preferences.getString("artist_sorting", "sortByTitleAscending");

        switch (sortOrder) {
            case "sortByArtistAscending":
                Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                    @Override
                    public int compare(AlbumItems o1, AlbumItems o2) {
                        return o1.getArtist().compareTo(o2.getArtist());
                    }
                });
                break;
            case "sortByArtistDescending":
                Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                    @Override
                    public int compare(AlbumItems o1, AlbumItems o2) {
                        return o2.getArtist().compareTo(o1.getArtist());
                    }
                });
                break;
            case "sortBySongsAscending":
                Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                    @Override
                    public int compare(AlbumItems o1, AlbumItems o2) {
                        return Integer.compare(o1.getSongCount(), o2.getSongCount());
                    }
                });
                break;
            case "sortBySongsDescending":
                Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                    @Override
                    public int compare(AlbumItems o1, AlbumItems o2) {
                        return Integer.compare(o2.getSongCount(), o1.getSongCount());
                    }
                });
                break;
            case "sortByAlbumAscending":
                Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                    @Override
                    public int compare(AlbumItems o1, AlbumItems o2) {
                        return o1.getAlbum().compareTo(o2.getAlbum());
                    }
                });
                break;
            case "sortByAlbumDescending":
                Collections.sort(albumFiles, new Comparator<AlbumItems>() {
                    @Override
                    public int compare(AlbumItems o1, AlbumItems o2) {
                        return o2.getAlbum().compareTo(o1.getAlbum());
                    }
                });
                break;
        }
    }

    private void initViews() {
        recyclerView_album_item = view.findViewById(R.id.recyclerView_albums);
        searchTV = view.findViewById(R.id.search_text);
        total_albums = view.findViewById(R.id.total_songs);
        sort = view.findViewById(R.id.sort);
    }

    private void getArgs() {
        Bundle args1 = getArguments();
        if(args1 != null){
            int pos = args1.getInt("AlbumPos");
            AlbumItems item = args1.getParcelable("AlbumItem");
            albumClickListener.onClick(item, pos);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_album_item, container, false);
        return view;
    }

    private void showSearchPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.search_layout, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
        albumAdapterSearch = new AlbumSearchAdapter(getContext(), albumSearchClickListener);
        recyclerView.setAdapter(albumAdapterSearch);
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
                filterSearch(newText);
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

    private void filterSearch(String inputText) {
        List<String> userInputs = Arrays.asList(inputText.toLowerCase().split(" "));

        ArrayList<AlbumItems> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();
            albumAdapterSearch.submitList(filteredList);
            albumAdapterSearch.notifyDataSetChanged();
        }else {
        for (AlbumItems albumItems : albumFiles){
            String artist = albumItems.getArtist().toLowerCase();
            String album = albumItems.getAlbum().toLowerCase();

            boolean contailsAllUserInput = true;

            for (String userInput : userInputs){
                if(!album.contains(userInput) && !artist.contains(userInput)){
                    contailsAllUserInput = false;
                    break;
                }
            }

            if(contailsAllUserInput){
                filteredList.add(albumItems);
            }
        }

        albumAdapterSearch.submitList(filteredList);
        albumAdapterSearch.notifyDataSetChanged();
        }
    }

    public final AlbumClickListener albumClickListener = new AlbumClickListener() {
        @Override
        public void onClick(AlbumItems album, int position) {
            Bundle data = new Bundle();
            data.putInt("Position", position);
            data.putParcelable("Item", album);

            AlbumContentFragment albumContentFragment = new AlbumContentFragment();
            albumContentFragment.setArguments(data);
            activity.loadAnAlbumFragment(albumContentFragment);
            inItems = false;
        }
    };

    public final AlbumClickListener albumSearchClickListener = new AlbumClickListener() {
        @Override
        public void onClick(AlbumItems album, int position) {
            recyclerView_album_item.scrollToPosition(getQueuePos(album.getAlbumId()));
            alertDialog.dismiss();
        }
    };

    private int getQueuePos(String uID){
        for (int i = 0; i < albumFiles.size(); i++){
            if (albumFiles.get(i).getAlbumId().equals(uID)){
                return i;
            }
        }
        return -1;
    }
}