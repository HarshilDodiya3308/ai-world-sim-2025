package com.aiworldsimulation.exception;

/**
 * Thrown when an invalid NPC operation is attempted.
 */
public class InvalidNPCException extends RuntimeException {
    public InvalidNPCException(String message) {
        super(message);
    }

    public InvalidNPCException(String message, Throwable cause) {
        super(message, cause);
    }
}
