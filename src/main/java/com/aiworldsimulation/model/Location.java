package com.aiworldsimulation.model;

import java.io.Serializable;

/**
 * Represents a location in the simulated world.
 * Each location has unique properties like resources, danger level, and terrain type.
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String terrainType;
    private int x;
    private int y;
    private double resourceLevel;      // 0.0 to 1.0
    private double dangerLevel;        // 0.0 to 1.0
    private double safetyLevel;        // 0.0 to 1.0
    private int discoveredCount;       // How many NPCs have discovered this location
    private boolean isDiscovered;

    public Location(int id, String name, String terrainType, int x, int y, 
                   double resourceLevel, double dangerLevel) {
        this.id = id;
        this.name = name;
        this.terrainType = terrainType;
        this.x = x;
        this.y = y;
        this.resourceLevel = Math.max(0, Math.min(1.0, resourceLevel));
        this.dangerLevel = Math.max(0, Math.min(1.0, dangerLevel));
        this.safetyLevel = 1.0 - dangerLevel;
        this.discoveredCount = 0;
        this.isDiscovered = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTerrainType() { return terrainType; }
    public int getX() { return x; }
    public int getY() { return y; }
    public double getResourceLevel() { return resourceLevel; }
    public void setResourceLevel(double level) { 
        this.resourceLevel = Math.max(0, Math.min(1.0, level)); 
    }
    public double getDangerLevel() { return dangerLevel; }
    public void setDangerLevel(double level) { 
        this.dangerLevel = Math.max(0, Math.min(1.0, level));
        this.safetyLevel = 1.0 - dangerLevel;
    }
    public double getSafetyLevel() { return safetyLevel; }
    public int getDiscoveredCount() { return discoveredCount; }
    public void incrementDiscoveredCount() { this.discoveredCount++; }
    public boolean isDiscovered() { return isDiscovered; }
    public void setDiscovered(boolean discovered) { this.isDiscovered = discovered; }

    @Override
    public String toString() {
        return String.format("%s [%s] - Resources: %.1f%%, Danger: %.1f%%", 
                           name, terrainType, resourceLevel * 100, dangerLevel * 100);
    }
}
