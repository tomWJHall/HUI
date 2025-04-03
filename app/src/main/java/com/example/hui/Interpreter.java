package com.example.hui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private SharedViewModel viewModel;
    private List<List<String>> alphabets = new ArrayList<List<String>>();
    private List<String> selectedAlphabet = new ArrayList<String>();

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
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getReceivedData().observe(getViewLifecycleOwner(), this::setBinaryDisplay);

        return inflater.inflate(R.layout.fragment_interpreter, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        alphabets = viewModel.getAlphabets().getValue();

        int alphaIndex = 0;
        assert alphabets != null;
        for(List<String> a : alphabets) {
            if(a.get(0).equals(viewModel.getAlphabet().getValue())) {
                selectedAlphabet = a;
                break;
            }
            alphaIndex++;
        }

        Spinner selectAlphabet = view.findViewById(R.id.alphabetSpinner);
        List<String> newOptions = alphabets.stream()
                .map(alpha -> alpha.get(0))
                .collect(Collectors.toList()); // Collect into a List

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                newOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectAlphabet.setAdapter(adapter); // Update the spinner

        selectAlphabet.setSelection(alphaIndex);

        selectAlphabet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                for(List<String> alpha : alphabets) {
                    if(Objects.equals(alpha.get(0), selectedItem)) {
                        selectedAlphabet = alpha;
                        viewModel.selectAlphabet(selectedItem);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no selection is made
            }
        });
    }

    public void setBinaryDisplay(Object binary) {
        Log.d("Binary", binary.toString());
        int[] squares = {R.id.square1, R.id.square2, R.id.square3, R.id.square4, R.id.square5};

        for (int i = 0; i < 5; i++) {
            View square = requireView().findViewById(squares[i]);
            int colour = binary.toString().charAt(i) == '1' ? R.color.flex_on : R.color.flex_off;
            square.setBackgroundResource(colour);
        }

        if(Integer.parseInt(binary.toString(), 2) == 0) {
            displayString = "";
        }
        else {
            displayString = selectedAlphabet.get(Integer.parseInt(binary.toString(), 2));
        }

        TextView displayView = requireView().findViewById(R.id.displayText);
        displayView.setText(displayString);
    }
}