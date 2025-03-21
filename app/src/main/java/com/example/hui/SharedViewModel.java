package com.example.hui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> data = new MutableLiveData<>();
    private MutableLiveData<List<List<String>>> alphabets = new MutableLiveData<>(new ArrayList<>());

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
