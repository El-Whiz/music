package com.toluwalase.musicapp0.Fragments;

import static com.toluwalase.musicapp0.Fragments.AlbumContentFragment.albumSongs;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inEditLyrics;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.inLoop;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loopMP;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_btn;
import static com.toluwalase.musicapp0.Fragments.NowPlayingFragment.loop_panel;
import static com.toluwalase.musicapp0.MainActivity.shuffleClicked;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.toluwalase.musicapp0.Adapters.AlbumContentAdapter;
import com.toluwalase.musicapp0.Interfaces.AlbumClickListener;
import com.toluwalase.musicapp0.Interfaces.SongClickListener;
import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.Models.AlbumItems;
import com.toluwalase.musicapp0.Models.MusicFiles;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlbumFragment extends Fragment {
    View view;
    MainActivity activity;
    TabLayout tabLayout;
    public static boolean inItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_album, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        if (activity != null){
            tabLayout = activity.findViewById(R.id.tab_layout);
            activity.loadAnAlbumFragment(new AlbumItemFragment());
            inItems = true;
        }
    }
}
































