package com.example.hui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Alphabets#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Alphabets extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DISPLAY = "display";
    private String displayString;

    public Alphabets() {
        // Required empty public constructor
        displayString = "HELLO";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param displayString displayString.
     * @return A new instance of fragment Alphabets.
     */
    // TODO: Rename and change types and number of parameters
    public static Alphabets newInstance(String displayString) {
        Alphabets fragment = new Alphabets();
        Bundle args = new Bundle();
        args.putString(ARG_DISPLAY, displayString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            displayString = getArguments().getString(ARG_DISPLAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alphabets, container, false);
    }
}