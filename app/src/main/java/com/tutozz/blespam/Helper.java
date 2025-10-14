package com.tutozz.blespam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import java.util.Random;

/**
 * Utility class providing shared constants, random data generation, and permission checks.
 */
public final class Helper { // Made final to prevent unwanted inheritance

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    // Thread-safe Random instance using ThreadLocal for performance in concurrent environments
    // It's technically better to use SecureRandom for cryptographic needs, but Random is fine here.
    private static final ThreadLocal<Random> RANDOM_THREAD_LOCAL = ThreadLocal.withInitial(Random::new);

    // --- Delay Configuration (UI vs. Advertising) ---

    // UI blinking delay, controlled by the user via + / - buttons.
    // Use Long for milliseconds to match Handler/Thread timing functions.
    public static long uiBlinkDelay = 1000L;
    public static final int[] UI_DELAYS_MS = {20, 50, 100, 200, 500, 1000, 2000, 5000};

    // --- Bluetooth Constants (Renamed for clarity) ---

    // Loop iteration for the old, busy-wait type spammers (less relevant with new Kotlin cycler)
    public static final int MAX_BUSY_WAIT_LOOP = 50_000_000;
    
    // Low latency mode is fixed for the new BleCyclerSpam, but we keep this public for other spammers
    // The actual advertising rate is dictated by AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
    public static final int FLOODING_ROTATION_MS = 1000; // 1 second rotation for the dynamic cycler


    // --- Permission Check Optimization ---

    /**
     * Checks if the minimum required BLE permissions are granted.
     * This logic is updated to correctly handle API 31+ (Android 12+) requirements.
     * @param context The application context.
     * @return true if necessary permissions are granted.
     */
    public static boolean isPermissionGranted(Context context){
        boolean granted = false;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31) and above uses BLUETOOTH_ADVERTISE
            granted = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED;
        }
        
        // For older devices (API < 31), we rely on BLUETOOTH and BLUETOOTH_ADMIN
        if (!granted && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            granted = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        }

        // ACCESS_FINE_LOCATION is often required for BLE scan/advertise on older versions (pre-12)
        // We ensure a location check is included for robust compatibility on older devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
             granted = granted && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        return granted;
    }

    // --- Conversion Methods (Optimized) ---

    /**
     * Converts a hexadecimal string into a byte array.
     * @param hex The hexadecimal string.
     * @return The resulting byte array.
     */
    public static byte[] convertHexToByteArray(String hex) {
        // Renamed variable for clarity
        String normalizedHex = hex.replaceAll("0x", "").toLowerCase(); 

        int length = normalizedHex.length();
        if (length % 2 != 0) {
            // Pad if odd length (e.g., "1" becomes "01")
            normalizedHex = "0" + normalizedHex;
            length++;
        }
        
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(normalizedHex.charAt(i), 16) << 4)
                    + Character.digit(normalizedHex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Generates a random hexadecimal filler string of a given size.
     * @param size The number of hex characters to generate.
     * @return The random hexadecimal string.
     */
    public static String randomHexFiller(int size){
        Random random = RANDOM_THREAD_LOCAL.get(); // Get thread-safe Random instance
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(HEX_DIGITS[random.nextInt(HEX_DIGITS.length)]);
        }
        return sb.toString();
    }
}
