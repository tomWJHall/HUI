package com.example.hui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Alphabet extends Fragment {
    private static final String ARG_ALPHA = "alphabetName";
    private String alphabetName;
    private List<String> alphabet;
    private SharedViewModel viewModel;
    private List<List<String>> alphabets = new ArrayList<List<String>>();

    public static Alphabet newInstance(String alphabetName) {
        Alphabet fragment = new Alphabet();
        Bundle args = new Bundle();
        args.putString(ARG_ALPHA, alphabetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alphabetName = getArguments().getString(ARG_ALPHA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alphabet, container, false);
        TextView textView = view.findViewById(R.id.alphabet_title);
        textView.setText(alphabetName);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        alphabets = viewModel.getAlphabets().getValue();

        int alphabetIndex;
        for(alphabetIndex = 0 ; alphabetIndex < alphabets.size() ; alphabetIndex++) {
            if(Objects.equals(alphabets.get(alphabetIndex).get(0), alphabetName)) {
                alphabet = alphabets.get(alphabetIndex);
                break;
            }
        }

        List<List<String>> mutableAlphabets = alphabets;
        List<String> mutableAlphabet = mutableAlphabets.get(alphabetIndex);

        GridLayout alphaTable = view.findViewById(R.id.alpha);
        alphaTable.setColumnCount(2);

        List<EditText> editTextList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            if(i == 0) {
                TextView leftTitle = new TextView(requireContext());
                leftTitle.setText("SIGN");
                leftTitle.setTextSize(18);
                leftTitle.setTextColor(Color.WHITE);
                leftTitle.setTypeface(Typeface.DEFAULT_BOLD);
                leftTitle.setPadding(100, 10, 100, 10);
                leftTitle.setTextAlignment(View. TEXT_ALIGNMENT_CENTER);
                leftTitle.setGravity(Gravity.CENTER);
                GridLayout.LayoutParams leftParams = new GridLayout.LayoutParams();
                leftParams.rowSpec = GridLayout.spec(0);
                leftParams.columnSpec = GridLayout.spec(0);
                leftTitle.setLayoutParams(leftParams);

                TextView rightTitle = new TextView(requireContext());
                rightTitle.setText("WORD");
                rightTitle.setTextSize(18);
                rightTitle.setTextColor(Color.WHITE);
                rightTitle.setTypeface(Typeface.DEFAULT_BOLD);
                rightTitle.setPadding(100, 10, 100, 10);
                rightTitle.setTextAlignment(View. TEXT_ALIGNMENT_CENTER);
                rightTitle.setGravity(Gravity.CENTER);
                GridLayout.LayoutParams rightParams = new GridLayout.LayoutParams();
                rightParams.rowSpec = GridLayout.spec(0);
                rightParams.columnSpec = GridLayout.spec(1);
                rightTitle.setLayoutParams(rightParams);

                alphaTable.addView(leftTitle);
                alphaTable.addView(rightTitle);

                continue;
            }

            // Create LinearLayout
            LinearLayout leftColumn = new LinearLayout(getContext());
            leftColumn.setOrientation(LinearLayout.HORIZONTAL);
            leftColumn.setGravity(Gravity.CENTER);
            leftColumn.setPadding(32, 32, 32, 32);

            // Left column params
            GridLayout.LayoutParams leftParams = new GridLayout.LayoutParams();
            leftParams.rowSpec = GridLayout.spec(i);
            leftParams.columnSpec = GridLayout.spec(0);
            leftParams.width = GridLayout.LayoutParams.WRAP_CONTENT;

            leftColumn.setLayoutParams(leftParams);

            for (int j = 0; j < 5; j++) {
                int repeat = 1 << (4 - j + 1);  // 2^(j+1)
                boolean isOn = (i % repeat) < (repeat / 2);

                View square = new View(getContext());

                int squareSize = 10; // dp
                int marginSize = 4;  // dp
                int squarePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, squareSize, getResources().getDisplayMetrics());
                int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginSize, getResources().getDisplayMetrics());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(squarePx, squarePx);
                params.setMargins(marginPx, 0, marginPx, 0); // Left & Right spacing
                square.setLayoutParams(params);
                square.setBackgroundColor(ContextCompat.getColor(getContext(), isOn ? R.color.flex_off : R.color.flex_on));

                leftColumn.addView(square);
            }

            alphaTable.addView(leftColumn, leftParams);

            GridLayout.LayoutParams rightParams = new GridLayout.LayoutParams();
            rightParams.rowSpec = GridLayout.spec(i);
            rightParams.columnSpec = GridLayout.spec(1);
            rightParams.width = GridLayout.LayoutParams.WRAP_CONTENT;

            EditText input = new EditText(requireContext());
            input.setHint("Enter text...");
            input.setText(alphabet.get(i));
            input.setTextSize(16);
            input.setTextColor(Color.WHITE);
            input.setHintTextColor(Color.GRAY);
            input.setPadding(20, 10, 20, 10);
            input.setBackgroundColor(Color.TRANSPARENT);
            input.setLayoutParams(rightParams);  // Set the correct params for input

            editTextList.add(input);

            input.setOnFocusChangeListener((v, keyCode) -> {
                    int index = editTextList.indexOf(v); // Get index of this EditText

                    mutableAlphabet.set(index+1, input.getText().toString()); // Update the value
                });

            input.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    View next = v.focusSearch(View.FOCUS_DOWN); // Find next focusable view
                    if (next != null) {
                        next.requestFocus(); // Move focus to the next EditText
                    }

                    int index = editTextList.indexOf(v); // Get index of this EditText
                    if (index != -1 && index < editTextList.size() - 1) {
                        editTextList.get(index + 1).requestFocus(); // Move to next EditText
                    }

                    return true; // Consume the event
                }
                return false;
            });

            rightParams.setMargins(16, 0, 16, 0);  // Optional padding
            rightParams.width = GridLayout.LayoutParams.MATCH_PARENT;

            alphaTable.addView(input, rightParams);

        }

        Button backButton = view.findViewById(R.id.back_button);
        int finalAlphabetIndex1 = alphabetIndex;
        backButton.setOnClickListener(v -> {
            if(finalAlphabetIndex1 != 0) {
                for(int index = 1 ; index < 32 ; index++) {
                    int editTextIndex = index-1; // Get index of this EditText
                    EditText editTextInput = editTextList.get(editTextIndex);

                    mutableAlphabet.set(index, editTextInput.getText().toString()); // Update the value
                }

                mutableAlphabets.set(finalAlphabetIndex1, mutableAlphabet);
                viewModel.setAlphabets(mutableAlphabets);
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
