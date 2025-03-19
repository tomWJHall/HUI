package com.example.hui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothReceiver {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard UUID for SPP
    private BluetoothSocket socket;
    private InputStream inputStream;
    private Context context;

    public BluetoothReceiver(Context context) {
        this.context = context;
    }

    public void connectToDevice(String deviceAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return; // Exit the method and wait for the user's response
            }
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            inputStream = socket.getInputStream();
            readData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inputStream.read(buffer);
                String receivedData = new String(buffer, 0, bytes);
                Log.d("BluetoothReceiver", "Received: " + receivedData);
                sendData(receivedData);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendData(String receivedData) {
        Intent intent = new Intent("BluetoothDataReceived");
        intent.putExtra("bluetooth_data", receivedData);
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