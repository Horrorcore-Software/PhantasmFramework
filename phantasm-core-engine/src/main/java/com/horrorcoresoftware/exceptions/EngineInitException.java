package com.horrorcoresoftware.exceptions;

/**
 * Custom exception class for engine-specific errors.
 */
public class EngineInitException extends Exception {
    /**
     * Creates a new engine initialization exception.
     * @param message The error message
     */
    public EngineInitException(String message) {
        super(message);
    }

    /**
     * Creates a new engine initialization exception with a cause.
     * @param message The error message
     * @param cause The underlying cause
     */
    public EngineInitException(String message, Throwable cause) {
        super(message, cause);
    }
}