package com.tutozz.blespam;

/**
 * Data class representing a single spoofable Apple Continuity/Proximity device or action.
 * Uses 'final' fields to ensure immutability, which is best practice for data objects.
 */
public final class ContinuityDevice {

    public enum type {
        DEVICE, ACTION
    }

    private final String value;
    private final String name;
    private final type deviceType;

    /**
     * Constructs an immutable ContinuityDevice.
     * @param value The hex code representing the device/action type (e.g., "0x0E20" for AirPods Pro).
     * @param name The human-readable name of the device/action.
     * @param deviceType Whether this is a DEVICE modal or an ACTION modal.
     */
    public ContinuityDevice(String value, String name, type deviceType) {
        // We trim/normalize the values in the constructor to ensure data consistency
        this.value = value != null ? value.replaceAll("0x", "").toUpperCase() : "";
        this.name = name;
        this.deviceType = deviceType;
    }

    /**
     * Gets the normalized, cleaned hex value of the device/action.
     * It will be in UPPERCASE and guaranteed not to contain the "0x" prefix.
     * @return The hex value string.
     */
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
    
    public type getDeviceType(){
        return deviceType;
    }

    // Optional: Add a standard toString() for easy debugging
    @Override
    public String toString() {
        return "ContinuityDevice{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", deviceType=" + deviceType +
                '}';
    }
}
