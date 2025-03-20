package com.example.hui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String pageName = "Interpreter";
    private List<BluetoothDevice> connectedDevices;
    private BluetoothDevice connectedDevice;
    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothReceiver receiver = new BluetoothReceiver(this);
    private String bluetoothData = "";
    private SharedViewModel sharedViewModel;

    private final List<List<String>> alphabets = Arrays.asList(Arrays.asList(
            "",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", // Index 1-26
            " ", "?", ".", "⌫", "!", "@"
    ));

    private List<String> selectedAlphabet = Arrays.asList(
            "",  // Index 0 (empty)
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", // Index 1-26
            " ", "?", ".", "⌫", "!", "@"
    );



    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint({"SetTextI18n", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                return;
            }
        }

//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());

        Log.d("Bluetooth Devices", connectedDevices.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainView, new Interpreter());
        transaction.commit();

        Button interButton = findViewById(R.id.interpreter);
        Log.d("interButton", interButton.toString());
        interButton.setOnClickListener(v -> {
            pageName = "Interpreter";
            TextView titleView = findViewById(R.id.pageName);
            if (!titleView.getText().toString().equals(pageName)) {
                titleView.setText(pageName);

                FragmentManager fragmentManagerI = getSupportFragmentManager();
                fragmentManagerI.beginTransaction()
                        .replace(R.id.mainView, new Interpreter())
                        .commit();

                Log.d("Clicked", "INTERPRETER");
            }
        });

        Button alphaButton = findViewById(R.id.alphabets);
        Log.d("alphaButton", alphaButton.toString());
        alphaButton.setOnClickListener(v -> {
            pageName = "Alphabets";
            TextView titleView = findViewById(R.id.pageName);
            if (!titleView.getText().toString().equals(pageName)) {
                titleView.setText(pageName);

                FragmentManager fragmentManagerA = getSupportFragmentManager();
                fragmentManagerA.beginTransaction()
                        .replace(R.id.mainView, new Alphabets())
                        .commit();

                Log.d("Clicked", "ALPHABETS");
            }
        });

        Button setsButton = findViewById(R.id.settings);
        Log.d("setsButton", setsButton.toString());
        setsButton.setOnClickListener(v -> {
            Settings settingsFragment = new Settings();

            pageName = "Settings";
            TextView titleView = findViewById(R.id.pageName);
            if (!titleView.getText().toString().equals(pageName)) {
                titleView.setText(pageName);

                FragmentManager fragmentManagerS = getSupportFragmentManager();
                FragmentTransaction transactionS = fragmentManagerS.beginTransaction();

                // Initialize the string before appending to avoid null issues
                StringBuilder connectedDevicesString = new StringBuilder();
                for (BluetoothDevice device : connectedDevices) {
                    connectedDevicesString.append(device.getName()).append("|").append(device.getAddress()).append(";");
                }

                Bundle bundle = new Bundle();
                bundle.putString("connectedDevices", connectedDevicesString.toString());
                settingsFragment.setArguments(bundle);

                transactionS.replace(R.id.mainView, settingsFragment).commit();

                Log.d("Clicked", "SETTINGS");
            }
        });

    }

    // Add this method outside onCreate but inside the MainActivity class
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("Permissions", "onRequestPermissionsResult called");

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Bluetooth permission granted!");
                // Retry your Bluetooth logic here
            } else {
                Log.e("Permissions", "Bluetooth permission denied!");
            }
        }
    }

    private final BroadcastReceiver bluetoothDataReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedData = intent.getStringExtra("bluetooth_data");
            if (receivedData != null) {
                Log.d("MainActivity", "Received: " + receivedData);
                bluetoothData = receivedData;
                sharedViewModel.setData(bluetoothData);
            }

            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                assert device != null;
                Log.d("Bluetooth", "Device connected: " + device.getName());
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                assert device != null;
                Log.d("Bluetooth", "Device disconnected: " + device.getName());
            }
        }
    };

    public void receiveData(String selectedDeviceName) {
        Log.d("Selected Bluetooth Device", selectedDeviceName);
        Log.d("Connected Bluetooth devices", connectedDevices.toString());
        if(!connectedDevices.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            connectedDevice = connectedDevices.stream()
                    .filter(device -> selectedDeviceName.equals(device.getName()))
                    .findFirst()
                    .orElse(null);

            if(connectedDevice != null) {
                Log.d("Connection", selectedDeviceName + " connected. Starting data stream.");
                receiver.connectToDevice(connectedDevice.getAddress());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(bluetoothDataReceiver, new IntentFilter("BluetoothDataReceived"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bluetoothDataReceiver);
    }
}