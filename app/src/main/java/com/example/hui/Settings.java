package com.example.hui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Settings extends Fragment {
    private List<List<String>> connectedDevices = new ArrayList<>();
    private List<String> connectedDevice;

    public Settings() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (getArguments() != null) {
            String connectedDevicesString = getArguments().getString("connectedDevices");
            assert connectedDevicesString != null;
            Log.d("Connected Bluetooth Devices", connectedDevicesString);

            Spinner mySpinner = view.findViewById(R.id.bluetoothSelector);

            if(connectedDevicesString.isEmpty()) {
                List<String> connectedDevicesStrings = Arrays.asList(connectedDevicesString.split(";"));
                connectedDevicesStrings.forEach(device -> connectedDevices.add(Arrays.asList(device.split("|"))));

                SharedPreferences prefs = requireActivity().getSharedPreferences("BluetoothSelections", Context.MODE_PRIVATE);
//                Set<String> defaultEntries = connectedDevices.stream().map(subList -> subList.get(0)).collect(Collectors.toSet());
                Set<String> defaultEntries = new HashSet<>(Arrays.asList("Option 1", "Option 2", "Option 3"));
                Set<String> spinnerData = prefs.getStringSet("spinner_entries", defaultEntries);

                List<String> entriesList = new ArrayList<>(spinnerData);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, entriesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mySpinner.setAdapter(adapter);
            }

            Button setsButton = view.findViewById(R.id.applyChangesButton);
            setsButton.setOnClickListener(v -> {
                String selectedDevice = mySpinner.getSelectedItem().toString();
                if(selectedDevice.equals("none") || selectedDevice.isEmpty()) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Bluetooth Device")
                            .setMessage("There is no device selected, please connect to your bluetooth device.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Do something when OK is pressed
                                dialog.dismiss();
                            })
                            .show();
                }
                else {
                    ((MainActivity) requireActivity()).receiveData(selectedDevice);
                }
            });
        }

        return view;
    }
}