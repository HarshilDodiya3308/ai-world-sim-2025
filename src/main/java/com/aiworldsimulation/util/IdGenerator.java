package com.aiworldsimulation.util;

import java.util.UUID;

/**
 * Utility class for generating unique identifiers.
 */
public class IdGenerator {
    /**
     * Generate a unique ID for an NPC.
     */
    public static String generateNPCId() {
        return "NPC_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generate a unique ID for a Memory.
     */
    public static String generateMemoryId() {
        return "MEM_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generate a unique ID for a Decision.
     */
    public static String generateDecisionId() {
        return "DEC_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generate a unique ID for a WorldEvent.
     */
    public static String generateEventId() {
        return "EVT_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generate a unique ID for a SavedGame.
     */
    public static String generateSaveGameId() {
        return "SAVE_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generate a generic unique ID.
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
