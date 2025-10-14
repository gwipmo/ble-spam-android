package com.tutozz.blespam;

/**
 * Data class representing a single spoofable Google Fast Pair device.
 * Uses 'final' fields to ensure immutability, which is best practice for data objects.
 */
public final class FastPairDevice {

    private final String value;
    private final String name;

    /**
     * Constructs an immutable FastPairDevice.
     * @param value The hex code representing the device Model ID (e.g., "0xCD8256" for Bose NC 700).
     * @param name The human-readable name of the device.
     */
    public FastPairDevice(String value, String name) {
        // We trim/normalize the hex value in the constructor to ensure data consistency
        // Fast Pair IDs are 3 bytes (6 hex characters), typically sent without '0x'
        this.value = value != null ? value.replaceAll("0x", "").toUpperCase() : "";
        this.name = name;
    }

    /**
     * Gets the normalized, cleaned hex value of the device Model ID.
     * It will be in UPPERCASE and guaranteed not to contain the "0x" prefix.
     * @return The hex value string.
     */
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    // Optional: Add a standard toString() for easy debugging
    @Override
    public String toString() {
        return "FastPairDevice{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
