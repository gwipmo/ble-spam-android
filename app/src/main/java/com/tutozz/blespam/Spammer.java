package com.tutozz.blespam;

import android.os.Handler;

/**
 * Base abstract class for all BLE Spammer implementations.
 * Provides shared state management (isSpamming) and resource handling (blinkRunnable).
 * All spammer logic must extend this class.
 */
public abstract class Spammer {

    // Shared state, accessible to all derived spammer classes
    protected boolean isSpamming = false;
    protected Runnable blinkRunnable = null;
    protected Handler blinkHandler = null; // Handler specific to the blinking animation

    /**
     * @return true if the spammer is currently running an advertisement cycle.
     */
    public boolean isSpamming() {
        return isSpamming;
    }

    /**
     * Initiates the BLE advertising process.
     * Sets isSpamming = true.
     */
    public abstract void start();

    /**
     * Stops the BLE advertising process and performs necessary cleanup.
     * Sets isSpamming = false.
     * Derived classes must implement BluetoothLeAdvertiser.stopAdvertising().
     */
    public abstract void stop();

    // --- Blink Animation State Management (Retained for MainActivity compatibility) ---

    /**
     * Sets the Runnable used to manage the UI blinking state.
     * @param blinkRunnable The Runnable instance.
     */
    public void setBlinkRunnable(Runnable blinkRunnable) {
        this.blinkRunnable = blinkRunnable;
    }

    /**
     * @return The Runnable instance used for UI blinking.
     */
    public Runnable getBlinkRunnable() {
        return blinkRunnable;
    }
}
