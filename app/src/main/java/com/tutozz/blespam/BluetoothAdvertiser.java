package com.tutozz.blespam;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.util.Log;

/**
 * Utility class to manage low-level Android Bluetooth LE Advertising calls.
 * Used by older spammer implementations (Continuity, FastPair) for cyclical advertising.
 * The core settings are optimized for high-frequency flooding.
 */
public class BluetoothAdvertiser {
    
    private static final String TAG = "BleAdvertiser";
    
    // Use final keyword for objects that should not be reassigned
    private final BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private final AdvertiseSettings settings;
    private final AdvertiseCallback advertiseCallback;
    private final boolean isReady;

    @SuppressLint("MissingPermission")
    public BluetoothAdvertiser() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // 1. Robust initialization and readiness check
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth not available or not enabled.");
            mBluetoothLeAdvertiser = null;
            isReady = false;
        } else {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (mBluetoothLeAdvertiser == null) {
                Log.e(TAG, "Bluetooth LE advertising not supported on this device.");
                isReady = false;
            } else {
                isReady = true;
            }
        }
        
        // 2. Define fixed, aggressive advertising settings (Low Latency, High Power)
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false) // Pure spam/broadcast mode
                .setTimeout(0) // Indefinite timeout (though we stop it manually)
                .build();

        // 3. Define the callback for success/failure logging
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                // Log the success for debugging, but keep it brief as this is called very often
                // Log.d(TAG, "Advertising cycle started.");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.e(TAG, "Advertising start failed. Code: " + errorCode);
                // Common error codes: 
                // 1: ADVERTISE_FAILED_DATA_TOO_LARGE
                // 3: ADVERTISE_FAILED_TOO_MANY_ADVERTISERS
            }
        };
    }

    /**
     * Attempts to start advertising the given data payload.
     * @param data The data to advertise (required).
     * @param scanResponse The scan response data (optional, can be null).
     */
    @SuppressLint("MissingPermission")
    public void advertise(AdvertiseData data, AdvertiseData scanResponse) {
        if (!isReady || mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Advertising skipped: Device not ready or advertiser is null.");
            return;
        }
        
        try {
            mBluetoothLeAdvertiser.startAdvertising(settings, data, scanResponse, advertiseCallback);
        } catch (Exception e) {
            Log.e(TAG, "Error during startAdvertising: " + e.getMessage());
        }
    }

    /**
     * Stops the currently running advertisement cycle.
     */
    @SuppressLint("MissingPermission")
    public void stopAdvertising() {
        if (!isReady || mBluetoothLeAdvertiser == null) {
            return;
        }
        
        try {
            // Note: The callback used here *must* be the same one used in startAdvertising
            mBluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            // Log.d(TAG, "Advertising stopped.");
        } catch (Exception e) {
            // Catch RuntimeException that can occur if Bluetooth is suddenly disabled
            Log.e(TAG, "Error during stopAdvertising: " + e.getMessage());
        }
    }
}
