package com.horrorcoresoftware.core;


/**
 * Manages timing operations for the game loop.
 */
class Timer {
    /**
     * Gets the current time in seconds.
     * @return Current time in seconds
     */
    public static double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }
}
