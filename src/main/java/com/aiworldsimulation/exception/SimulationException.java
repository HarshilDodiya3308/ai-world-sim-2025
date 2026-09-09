package com.aiworldsimulation.exception;

/**
 * Thrown when a simulation operation fails.
 */
public class SimulationException extends RuntimeException {
    public SimulationException(String message) {
        super(message);
    }

    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }
}
