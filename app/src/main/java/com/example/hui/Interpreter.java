package com.example.hui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import kotlin.ParameterName;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
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
    private boolean enableSpeech = false;
    private TextToSpeech t1;
    private boolean enableSMS = false;
    private List<String> recipient;

    public Interpreter() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            displayString = getArguments().getString(ARG_DISPLAY);
        }

        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getReceivedData().observe(getViewLifecycleOwner(), this::setBinaryDisplay);

        if (ContextCompat.checkSelfPermission(requireContext(),  Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

        return inflater.inflate(R.layout.fragment_interpreter, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        ImageButton speechButton = view.findViewById(R.id.speakButton);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSpeech = !enableSpeech;
                speechButton.setImageResource(enableSpeech ? android.R.drawable.ic_lock_silent_mode_off : android.R.drawable.ic_lock_silent_mode);
                if(enableSpeech) {
                    speakText("Speech enabled");
                }
            }
        });

        ImageButton smsButton = view.findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSMS = !enableSMS;
                smsButton.setImageResource(enableSMS ? android.R.drawable.stat_notify_chat : android.R.drawable.button_onoff_indicator_off);

                if(enableSMS) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    if(recipient != null && recipient instanceof ArrayList && recipient.size() == 3) {
                        Toast.makeText(getContext(), "You will be sending messages to: " + recipient.get(1) + " - " + recipient.get(2), Toast.LENGTH_LONG).show();
                    }
                    else {
                        pickContactLauncher.launch(intent);
                    }
                }
            }
        });
        smsButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            pickContactLauncher.launch(intent);
            return true;
        });

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

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private final ActivityResultLauncher<Intent> pickContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri contactUri = data.getData();
                        getContactDetails(contactUri);
                    }
                }
            }
    );

    private void getContactDetails(Uri contactUri) {
        Cursor cursor = requireContext().getContentResolver().query(
                contactUri,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            String contactId = cursor.getString(idIndex);
            String contactName = cursor.getString(nameIndex);

            cursor.close();

            String phoneNumber = getPhoneNumber(contactId); // Fetch phone number

            List<String> contact = new ArrayList<String>();
            contact.add(contactId);
            contact.add(contactName);
            contact.add(phoneNumber);

            recipient = contact;

            Toast.makeText(getContext(), "You will be sending messages to: " + recipient.get(1) + " - " + recipient.get(2), Toast.LENGTH_SHORT).show();
        }
    }
    private String getPhoneNumber(String contactId) {
        Cursor phoneCursor = requireContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );

        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            int numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phoneNumber = phoneCursor.getString(numberIndex);

            phoneCursor.close();

            return phoneNumber;
        }

        return null;
    }

    public void speakText(String speak) {
        t1.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void setBinaryDisplay(byte[] data) {
        Log.d("Binary", Arrays.toString(data));

        if(data[0] == 0xC && enableSMS) sendSMS(recipient.get(2), displayString);
        else {
            int[] squares = {R.id.square1, R.id.square2, R.id.square3, R.id.square4, R.id.square5};

            StringBuilder binaryBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                int analogSensorValue = data[i*2] << 8 + data[i*2+1];

                boolean fingerDown = analogSensorValue > 100; // To replace with ML method

                int colour = fingerDown ? R.color.flex_on : R.color.flex_off;

                View square = requireView().findViewById(squares[i]);
                square.setBackgroundResource(colour);

                binaryBuilder.append(fingerDown ? "1" : "0");
            }

            String binary = binaryBuilder.toString();

            if (Integer.parseInt(binary, 2) == 0 || Objects.equals(selectedAlphabet.get(Integer.parseInt(binary, 2)), "\\CLEAR")) {
                displayString = "";
            } else if (Objects.equals(selectedAlphabet.get(Integer.parseInt(binary, 2)), "\\BACKSPACE")) {
                if (selectedAlphabet.contains(" ")) {
                    displayString = displayString.substring(0, displayString.length() - 1);
                } else {
                    displayString = displayString.substring(0, displayString.lastIndexOf(" "));
                }
            } else {
                if (selectedAlphabet.get(32).equals("\\T")) {
                    displayString += " ";
                }
                displayString += selectedAlphabet.get(Integer.parseInt(binary, 2));

                if(!selectedAlphabet.contains(" ")) {
                    speakText(selectedAlphabet.get(Integer.parseInt(binary, 2)));
                }
                else if(selectedAlphabet.get(Integer.parseInt(binary, 2)).equals(" ")) {
                    String[] words = displayString.split(" ");

                    String toSpeak;
                    int howFarBack = 2;
                    do {
                        toSpeak = words[words.length - howFarBack];
                        howFarBack++;
                    } while(toSpeak.equals(" ") || toSpeak.isEmpty());

                    speakText(toSpeak);
                }
            }

            TextView displayView = requireView().findViewById(R.id.displayText);
            displayView.setText(displayString);
        }
    }
}