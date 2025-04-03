package com.example.hui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> data = new MutableLiveData<>();
    private MutableLiveData<List<List<String>>> alphabets = new MutableLiveData<>(List.of(Arrays.asList(
            "STANDARD",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", // Index 1-26
            "\\CLEAR", "?", ".", " ", "\\BACKSPACE", ""
    )));
    private MutableLiveData<String> selectedAlphabet = new MutableLiveData<>("STANDARD");
    private MutableLiveData<List<List<String>>> bluetoothDevices = new MutableLiveData<>();
    private MutableLiveData<List<String>> selectedDevice = new MutableLiveData<>();


    public void setData(String value) {
        data.setValue(value);
    }

    public LiveData<String> getReceivedData() {
        return data;
    }

    public LiveData<List<List<String>>> getAlphabets() {
        return alphabets;
    }

    public void setAlphabets(List<List<String>> newAlphabets) {
        alphabets.setValue(newAlphabets);
    }

    public LiveData<String> getAlphabet() {
        return selectedAlphabet;
    }

    public void selectAlphabet(String newAlphabet) {
        selectedAlphabet.setValue(newAlphabet);
    }

    public LiveData<List<List<String>>> getDevices() {
        return bluetoothDevices;
    }

    public void setDevices(List<List<String>> devices) {
        bluetoothDevices.setValue(devices);
    }

    public LiveData<List<String>> getDevice() {
        return selectedDevice;
    }

    public void setDevice(List<String> device) {
        selectedDevice.setValue(device);
    }

    public void addAlphabet(List<String> alphabet) {
        List<List<String>> current = new ArrayList<>(alphabets.getValue());  // Copy to mutable list
        current.add(alphabet);  // Now modification is allowed
        alphabets.setValue(current);  // Update LiveData
    }

    public void removeAlphabet(int alphabetIndex) {
        List<List<String>> current = new ArrayList<>(alphabets.getValue());
        current.remove(alphabetIndex);
        alphabets.setValue(current);  // Update LiveData
    }
}
