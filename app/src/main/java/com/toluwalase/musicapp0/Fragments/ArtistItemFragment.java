package com.toluwalase.musicapp0.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.toluwalase.musicapp0.Fragments.ArtistContentFragment.scrollPosArtist;
import static com.toluwalase.musicapp0.Fragments.ArtistFragment.inArtistItems;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.ArtistAdapter;
import com.toluwalase.musicapp0.Adapters.ArtistSearchAdapter;
import com.toluwalase.musicapp0.Customs.MusicComparator;
import com.toluwalase.musicapp0.Interfaces.ArtistClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.ArtistItems;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArtistItemFragment extends Fragment {
    View view;
    public static RecyclerView artist_recyclerView;
    MainActivity activity;
    TabLayout tabLayout;
    public static PopupWindow artist_popupWindow;
    public static boolean artistPopupActive;
    AlertDialog alertDialog, prefDialog;
    ArtistSearchAdapter searchAdapter;
    CardView searchTV;
    ImageView sort;
    TextView total_artists;
    String MY_SORT_PREF_ARTIST_ITEMS = "SortOrderArtistItems";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_atrist_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        if (activity != null) {
            tabLayout = activity.findViewById(R.id.tab_layout);
        }
        initViews();

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
                showSearchPopUp();
            }
        });

        artist_recyclerView.setHasFixedSize(true);
        ArtistAdapter artistAdapter = new ArtistAdapter(requireContext(), artistClickListener);
        sortList();
        artistAdapter.submitList(artistFiles);
        artist_recyclerView.setAdapter(artistAdapter);
        artist_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        artist_recyclerView.scrollToPosition(scrollPosArtist);
        total_artists.setText("Total artists: " + String.valueOf(artistFiles.size()));

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = requireContext().getSharedPreferences(MY_SORT_PREF_ARTIST_ITEMS, MODE_PRIVATE).edit();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.artist_item_pref_layout, null);
                dialogBuilder.setView(dialogView);
                prefDialog = dialogBuilder.create();
                dialogView.setBackgroundResource(R.drawable.main_bg);

                TextView artist_asc, artist_desc, total_songs_asc, total_songs_desc, total_albums_asc, total_albums_desc;
                artist_asc = dialogView.findViewById(R.id.artist_ascending);
                artist_desc = dialogView.findViewById(R.id.artist_descending);
                total_songs_asc = dialogView.findViewById(R.id.total_songs_ascending);
                total_songs_desc = dialogView.findViewById(R.id.total_songs_descending);
                total_albums_asc = dialogView.findViewById(R.id.albums_ascending);
                total_albums_desc = dialogView.findViewById(R.id.albums_descending);

                artist_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                            @Override
                            public int compare(ArtistItems o1, ArtistItems o2) {
                                return o1.getArtist().compareTo(o2.getArtist());
                            }
                        });
                        artistAdapter.submitList(artistFiles);
                        artistAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortByArtistAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                artist_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                            @Override
                            public int compare(ArtistItems o1, ArtistItems o2) {
                                return o2.getArtist().compareTo(o1.getArtist());
                            }
                        });
                        artistAdapter.submitList(artistFiles);
                        artistAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortByArtistDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                total_songs_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                            @Override
                            public int compare(ArtistItems o1, ArtistItems o2) {
                                return Integer.compare(o1.getSongsCount(), o2.getSongsCount());
                            }
                        });
                        artistAdapter.submitList(artistFiles);
                        artistAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortBySongsAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                total_songs_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                            @Override
                            public int compare(ArtistItems o1, ArtistItems o2) {
                                return Integer.compare(o2.getSongsCount(), o1.getSongsCount());
                            }
                        });
                        artistAdapter.submitList(artistFiles);
                        artistAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortBySongsDescending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                total_albums_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                            @Override
                            public int compare(ArtistItems o1, ArtistItems o2) {
                                return Integer.compare(o1.getAlbumsCount(), o2.getAlbumsCount());
                            }
                        });
                        artistAdapter.submitList(artistFiles);
                        artistAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortByAlbumAscending");
                        editor.apply();
                        prefDialog.dismiss();
                    }
                });

                total_albums_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                            @Override
                            public int compare(ArtistItems o1, ArtistItems o2) {
                                return Integer.compare(o2.getAlbumsCount(), o1.getAlbumsCount());
                            }
                        });
                        artistAdapter.submitList(artistFiles);
                        artistAdapter.notifyDataSetChanged();
                        editor.putString("artist_sorting", "sortByAlbumDescending");
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
        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_SORT_PREF_ARTIST_ITEMS, MODE_PRIVATE);
        String sortOrder = preferences.getString("artist_sorting", "sortByTitleAscending");

        switch (sortOrder) {
            case "sortByArtistAscending":
                Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                    @Override
                    public int compare(ArtistItems o1, ArtistItems o2) {
                        return o1.getArtist().compareTo(o2.getArtist());
                    }
                });
                break;
            case "sortByArtistDescending":
                Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                    @Override
                    public int compare(ArtistItems o1, ArtistItems o2) {
                        return o2.getArtist().compareTo(o1.getArtist());
                    }
                });
                break;
            case "sortBySongsAscending":
                Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                    @Override
                    public int compare(ArtistItems o1, ArtistItems o2) {
                        return Integer.compare(o1.getSongsCount(), o2.getSongsCount());
                    }
                });
                break;
            case "sortBySongsDescending":
                Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                    @Override
                    public int compare(ArtistItems o1, ArtistItems o2) {
                        return Integer.compare(o2.getSongsCount(), o1.getSongsCount());
                    }
                });
                break;
            case "sortByAlbumAscending":
                Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                    @Override
                    public int compare(ArtistItems o1, ArtistItems o2) {
                        return Integer.compare(o1.getAlbumsCount(), o2.getAlbumsCount());
                    }
                });
                break;
            case "sortByAlbumDescending":
                Collections.sort(artistFiles, new Comparator<ArtistItems>() {
                    @Override
                    public int compare(ArtistItems o1, ArtistItems o2) {
                        return Integer.compare(o2.getAlbumsCount(), o1.getAlbumsCount());
                    }
                });
                break;
        }
    }

    private void getArgs() {
        Bundle args = getArguments();
        if(args != null){
            int pos = args.getInt("ArtistPos");
            ArtistItems item = args.getParcelable("ArtistItem");

            artistClickListener.onClick(item, pos);
        }
    }

    private void initViews() {
        artist_recyclerView = view.findViewById(R.id.recyclerView_artist);
        searchTV = view.findViewById(R.id.search_text);
        total_artists = view.findViewById(R.id.total_songs);
        sort = view.findViewById(R.id.sort);
    }

    private void showSearchPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.search_layout, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        dialogView.setBackgroundResource(R.drawable.main_bg);

        RecyclerView recyclerView = dialogView.findViewById(R.id.search_RV);
        searchAdapter = new ArtistSearchAdapter(getContext(), artistSearchClickListener);
        recyclerView.setAdapter(searchAdapter);
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

        ArrayList<ArtistItems> filteredList = new ArrayList<>();

        if (inputText.isEmpty() || inputText.matches("^\\s+$")){
            filteredList.clear();
            searchAdapter.submitList(filteredList);
            searchAdapter.notifyDataSetChanged();
        }else {
            for (ArtistItems artistItems : artistFiles){
                String artist = artistItems.getArtist().toLowerCase();

                boolean contailsAllUserInput = true;

                for (String userInput : userInputs){
                    if(!artist.contains(userInput)){
                        contailsAllUserInput = false;
                        break;
                    }
                }

                if(contailsAllUserInput){
                    filteredList.add(artistItems);
                }
            }

            searchAdapter.submitList(filteredList);
            searchAdapter.notifyDataSetChanged();
        }
    }

    public final ArtistClickListener artistClickListener = new ArtistClickListener() {
        @Override
        public void onClick(ArtistItems artist, int position) {
            Bundle args = new Bundle();
            args.putParcelable("ArtistItem", artist);
            args.putInt("position", position);
            ArtistContentFragment artistContentFragment = new ArtistContentFragment();
            artistContentFragment.setArguments(args);
            activity.loadAnArtistFragment(artistContentFragment);
            inArtistItems = false;
        }
    };

    public final ArtistClickListener artistSearchClickListener = new ArtistClickListener() {
        @Override
        public void onClick(ArtistItems artist, int position) {
            artist_recyclerView.scrollToPosition(getQueuePos(artist.getArtistId()));
            alertDialog.dismiss();
        }
    };

    private int getQueuePos(String uID){
        for (int i = 0; i < artistFiles.size(); i++){
            if (artistFiles.get(i).getArtistId().equals(uID)){
                return i;
            }
        }
        return -1;
    }
}