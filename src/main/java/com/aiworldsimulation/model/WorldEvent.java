package com.aiworldsimulation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a random event occurring in the simulated world.
 * Events affect NPCs, locations, and the overall simulation state.
 */
public class WorldEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private WorldEventType type;
    private String description;
    private int affectedLocationId;
    private String affectedNpcId;
    private LocalDateTime timestamp;
    private double severity;      // 0 to 1.0
    private boolean isResolved;

    public enum WorldEventType {
        RESOURCE_DISCOVERY("Resource Discovery"),
        WEATHER_CHANGE("Weather Change"),
        RESOURCE_SHORTAGE("Resource Shortage"),
        DANGER_DETECTED("Danger Detected"),
        NPC_INJURED("NPC Injured"),
        MARKET_CHANGE("Market Change"),
        LOCATION_DISCOVERY("Location Discovery"),
        NPC_HELP("NPC Help");

        private final String label;

        WorldEventType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public WorldEvent(String id, WorldEventType type, String description, 
                     int affectedLocationId, String affectedNpcId, double severity) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.affectedLocationId = affectedLocationId;
        this.affectedNpcId = affectedNpcId;
        this.severity = Math.max(0, Math.min(1.0, severity));
        this.timestamp = LocalDateTime.now();
        this.isResolved = false;
    }

    // Getters
    public String getId() { return id; }
    public WorldEventType getType() { return type; }
    public String getDescription() { return description; }
    public int getAffectedLocationId() { return affectedLocationId; }
    public String getAffectedNpcId() { return affectedNpcId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getSeverity() { return severity; }
    public boolean isResolved() { return isResolved; }

    // Setters
    public void setResolved(boolean resolved) { this.isResolved = resolved; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", type.getLabel(), timestamp.toLocalTime(), description);
    }
}
