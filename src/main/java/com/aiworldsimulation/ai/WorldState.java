package com.aiworldsimulation.ai;

import com.aiworldsimulation.model.*;
import java.util.*;

/**
 * Represents the current state of the world.
 * Used by the decision engine to evaluate NPC actions.
 */
public class WorldState {
    private List<Location> locations;
    private List<NPC> npcs;
    private List<WorldEvent> activeEvents;
    private int simulationTime;  // In ticks/steps
    private double globalDangerLevel;  // 0 to 1.0

    public WorldState(List<Location> locations, List<NPC> npcs) {
        this.locations = new ArrayList<>(locations);
        this.npcs = new ArrayList<>(npcs);
        this.activeEvents = new ArrayList<>();
        this.simulationTime = 0;
        this.globalDangerLevel = 0.0;
    }

    public void addEvent(WorldEvent event) {
        activeEvents.add(event);
    }

    public void removeEvent(WorldEvent event) {
        activeEvents.remove(event);
    }

    public void updateGlobalDangerLevel() {
        this.globalDangerLevel = locations.stream()
                .mapToDouble(Location::getDangerLevel)
                .average()
                .orElse(0.0);
    }

    // Getters
    public List<Location> getLocations() { return new ArrayList<>(locations); }
    public List<NPC> getNpcs() { return new ArrayList<>(npcs); }
    public List<WorldEvent> getActiveEvents() { return new ArrayList<>(activeEvents); }
    public int getSimulationTime() { return simulationTime; }
    public double getGlobalDangerLevel() { return globalDangerLevel; }

    // Setters
    public void setSimulationTime(int time) { this.simulationTime = time; }
    public void incrementSimulationTime() { this.simulationTime++; }
}
