package com.toluwalase.musicapp0.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toluwalase.musicapp0.MainActivity;
import com.toluwalase.musicapp0.R;
public class MainFragment extends Fragment {
    MainActivity activity;
    View view;
    public static boolean inHome, inPlayList, inFaves, inHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        if (activity != null){
            activity.loadAnHomeFragment(new HomeFragment());
            inHome = true;
            inPlayList = false;
            inFaves = false;
            inHistory = false;
        }
    }
}