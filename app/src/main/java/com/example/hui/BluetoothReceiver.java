package com.example.hui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothReceiver {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard UUID for SPP
    private BluetoothSocket socket;
    private InputStream inputStream;
    private final Context context;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public BluetoothReceiver(Context context) {
        this.context = context;
    }

    public void connectToDevice(String deviceAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return; // Exit the method and wait for the user's response
                }
            }
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            inputStream = socket.getInputStream();
            readData();
        } catch (IOException e) {
            Log.d("Connect Error", e.toString());
            e.printStackTrace();
        }
    }

    private void readData() {
        executorService.execute(() -> {
            byte[] buffer = new byte[10];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes == 10) {
                        mainHandler.post(() -> sendData(buffer));
                    }
                } catch (IOException e) {
                    Log.d("Read Data Error", e.toString());
                    e.printStackTrace();
                    break;
                }
            }
        });
    }

    private void sendData(byte[] receivedData) {
        Intent intent = new Intent("BluetoothDataReceived");
        String encoded = Base64.encodeToString(receivedData, Base64.DEFAULT); // payload is byte[10]
        intent.putExtra("bluetooth_data", encoded);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void closeConnection() {
        try {
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}