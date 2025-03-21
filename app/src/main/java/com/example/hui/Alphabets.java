package com.example.hui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Alphabets extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DISPLAY = "display";

    private SharedViewModel viewModel;
    private List<List<String>> alphabets = List.of(Arrays.asList(
            "Standard",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", // Index 1-26
            " ", "?", ".", "⌫", "!", "@"
    ));
    private Button lastButton;

    public Alphabets() {
        // Required empty public constructor

    }

    private void openAlphabetFragment(String letter, List<String> alphabet) {
        Alphabet fragment = Alphabet.newInstance(letter);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainView, fragment)
                .addToBackStack(null) // Enables back navigation
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alphabets, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstance) {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if(viewModel.getAlphabets().getValue().isEmpty() || viewModel.getAlphabets().getValue().get(0).get(0).isEmpty()) {
            viewModel.setAlphabets(alphabets);
        }
        alphabets = viewModel.getAlphabets().getValue();

        Log.d("ALPHABETS", alphabets.toString());

        View buttonStandard = view.findViewById(R.id.standard_alphabet);
        buttonStandard.setOnClickListener(v -> openAlphabetFragment("Standard", alphabets.get(0)));

        lastButton = requireView().findViewById(R.id.standard_alphabet);

        for(int i = 1; i < alphabets.size(); i++) {
            int finalI = i;

            Button button = addButtonToLayout(alphabets.get(finalI).get(0));

            if(button != null) {
                button.setOnClickListener(v -> openAlphabetFragment(alphabets.get(finalI).get(0), alphabets.get(finalI)));
            }
        }


        Button buttonAdd = view.findViewById(R.id.addAlphabet);
        buttonAdd.setOnClickListener(v -> addAlphabet());
    }

    private void addAlphabet() {
        promptAlphabetName();
    }

    private Boolean deleteAlphabet(Button buttonToDelete, String alphabetName) {

        deleteButton(buttonToDelete);

        return true;
    }

    private void deleteButton(Button buttonToDelete) {
        ConstraintLayout layout = requireView().findViewById(R.id.alphabetsContainer);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        // Find the button below (with a top constraint to buttonToDelete)
        Button buttonBelow = null;
        Button buttonAbove = null;

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof Button) {
                Button childButton = (Button) child;
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) childButton.getLayoutParams();
                ConstraintLayout.LayoutParams paramsButtonToDelete = (ConstraintLayout.LayoutParams) buttonToDelete.getLayoutParams();

                if (params.topToBottom == buttonToDelete.getId()) {
                    buttonBelow = childButton;
                }
                if (paramsButtonToDelete.topToBottom == childButton.getId()) {
                    buttonAbove = childButton;
                }
            }
        }

        // If there's a button above and a button below, link them
        if (buttonAbove != null && buttonBelow != null) {
            constraintSet.connect(buttonBelow.getId(), ConstraintSet.TOP, buttonAbove.getId(), ConstraintSet.BOTTOM);
        }

        // Apply updated constraints
        constraintSet.applyTo(layout);

        // Remove the button from the layout
        layout.removeView(buttonToDelete);

        for(int i = 0 ; i < alphabets.size() ; i++) {
            if (alphabets.get(i).get(0) == buttonToDelete.getText()) {
                viewModel.removeAlphabet(i);
                break;
            }
        }

    }

    private void promptAlphabetName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Alphabet Name");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String buttonText = input.getText().toString().trim();
            if (!buttonText.isEmpty()) {
                List<String> newAlphabet = Arrays.asList(
                        buttonText,
                        "", "", "", "", "", "", "", "", "", "", "", "", "",
                        "", "", "", "", "", "", "", "", "", "", "", "", "", // Index 1-26
                        "", "", "", "", "", ""
                );
                viewModel.addAlphabet(newAlphabet);

                addButtonToLayout(buttonText);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private Button addButtonToLayout(String buttonText) {
        ConstraintLayout layout = requireView().findViewById(R.id.alphabetsContainer);
        Button addButton = requireView().findViewById(R.id.addAlphabet); // Reference an existing button

        if (lastButton == null) {
            return null; // Ensure the existing button exists
        }

        Button newButton = new Button(getContext());
        newButton.setText(buttonText);
        newButton.setBackgroundColor(Color.parseColor("#143131"));
        newButton.setId(View.generateViewId());

        // Set layout parameters to match existing button
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,  // Width: 0dp (match constraint)
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 68, getResources().getDisplayMetrics()) // Height: 68sp
        );

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
        params.setMargins(margin, margin, margin, 0);
        newButton.setLayoutParams(params);

        // Copy styling from existing button
        newButton.setBackground(lastButton.getBackground());
        newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, lastButton.getTextSize());
        newButton.setTypeface(lastButton.getTypeface());
        newButton.setTextColor(lastButton.getCurrentTextColor());
        newButton.setPadding(
                lastButton.getPaddingLeft(),
                lastButton.getPaddingTop(),
                lastButton.getPaddingRight(),
                lastButton.getPaddingBottom()
        );

        layout.addView(newButton, layout.indexOfChild(addButton));

        newButton.setOnClickListener(v -> openAlphabetFragment(buttonText, new ArrayList<String>()));
        newButton.setOnLongClickListener(v -> deleteAlphabet(newButton, buttonText));

        // Apply constraints programmatically
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        // Position new button BELOW the existing button

        constraintSet.connect(newButton.getId(), ConstraintSet.TOP, lastButton.getId(), ConstraintSet.BOTTOM, 64);
        constraintSet.connect(newButton.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, margin);
        constraintSet.connect(newButton.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, margin);

        constraintSet.connect(addButton.getId(), ConstraintSet.TOP, newButton.getId(), ConstraintSet.BOTTOM, margin);
        // Apply constraints to the layout
        constraintSet.applyTo(layout);
        lastButton = newButton;

        return newButton;
    }
}