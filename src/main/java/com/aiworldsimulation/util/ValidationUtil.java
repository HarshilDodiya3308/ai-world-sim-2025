package com.aiworldsimulation.util;

import com.aiworldsimulation.model.*;

/**
 * Utility class for input validation.
 */
public class ValidationUtil {
    /**
     * Validate NPC parameters.
     */
    public static boolean isValidNPC(String name, NPCRole role, int x, int y) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("NPC name cannot be empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("NPC role cannot be null");
        }
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("NPC coordinates cannot be negative");
        }
        return true;
    }

    /**
     * Validate Location parameters.
     */
    public static boolean isValidLocation(String name, int x, int y) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be empty");
        }
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Location coordinates cannot be negative");
        }
        return true;
    }

    /**
     * Validate that a value is between 0 and 1.
     */
    public static boolean isValidNormalizedValue(double value) {
        if (value < 0 || value > 1.0) {
            throw new IllegalArgumentException("Value must be between 0 and 1.0");
        }
        return true;
    }

    /**
     * Validate that a health value is between 0 and 100.
     */
    public static boolean isValidHealth(double health) {
        if (health < 0 || health > 100) {
            throw new IllegalArgumentException("Health must be between 0 and 100");
        }
        return true;
    }
}
