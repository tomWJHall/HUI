package com.example.hui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Interpreter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Interpreter extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DISPLAY = "display";
    private String displayString;

    public Interpreter() {
        // Required empty public constructor
        displayString = "HELLO";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param displayString displayString.
     * @return A new instance of fragment Interpreter.
     */
    // TODO: Rename and change types and number of parameters
    public static Interpreter newInstance(String displayString) {
        Interpreter fragment = new Interpreter();
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
        return inflater.inflate(R.layout.fragment_interpreter, container, false);
    }

    public void setBinaryDisplay(int index) {
        int[] squares = {R.id.square1, R.id.square2, R.id.square3, R.id.square4, R.id.square5};
        String binary = String.format("%5s", Integer.toBinaryString(index)).replace(' ', '0');

        for (int i = 0; i < 5; i++) {
            View square = requireView().findViewById(squares[i]);
            int colour = binary.charAt(i) == '1' ? R.color.flex_on : R.color.flex_off;
            square.setBackgroundResource(colour);
        }
    }
}