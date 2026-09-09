package com.aiworldsimulation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a memory event for an NPC.
 * NPCs remember important events that influence their future decisions.
 */
public class Memory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String npcId;
    private MemoryType type;
    private String description;
    private int locationId;
    private double importance;     // 0 to 1.0
    private LocalDateTime timestamp;
    private boolean isRelevant;

    public enum MemoryType {
        DANGER("Danger"),
        SUCCESS("Success"),
        FAILURE("Failure"),
        DISCOVERY("Discovery"),
        INTERACTION("Interaction"),
        RESOURCE("Resource"),
        INJURY("Injury");

        private final String label;

        MemoryType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public Memory(String id, String npcId, MemoryType type, String description, 
                 int locationId, double importance) {
        this.id = id;
        this.npcId = npcId;
        this.type = type;
        this.description = description;
        this.locationId = locationId;
        this.importance = Math.max(0, Math.min(1.0, importance));
        this.timestamp = LocalDateTime.now();
        this.isRelevant = true;
    }

    // Getters
    public String getId() { return id; }
    public String getNpcId() { return npcId; }
    public MemoryType getType() { return type; }
    public String getDescription() { return description; }
    public int getLocationId() { return locationId; }
    public double getImportance() { return importance; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRelevant() { return isRelevant; }

    // Setters
    public void setRelevant(boolean relevant) { this.isRelevant = relevant; }

    @Override
    public String toString() {
        return String.format("%s - %s: %s", type.getLabel(), timestamp.toLocalTime(), description);
    }
}
