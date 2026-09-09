package com.aiworldsimulation.simulation;

import com.aiworldsimulation.model.*;
import com.aiworldsimulation.ai.*;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the simulated world containing all locations and NPCs.
 * Provides utility methods for finding NPCs, locations, and calculating distances.
 */
public class SimulationWorld {
    private static final Logger logger = LoggerFactory.getLogger(SimulationWorld.class);

    private int worldWidth = 200;
    private int worldHeight = 200;
    private List<Location> locations;
    private List<NPC> npcs;
    private DecisionEngine decisionEngine;
    private ActionExecutor actionExecutor;

    public SimulationWorld(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.locations = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.decisionEngine = new DecisionEngine();
        this.actionExecutor = new ActionExecutor(this);
    }

    /**
     * Initialize the world with default locations.
     * This creates the demo world with various terrain types.
     */
    public void initializeDemoWorld() {
        locations.clear();

        // Forest
        locations.add(new Location(1, "Forest", "Forest", 50, 50, 0.8, 0.4));

        // Village
        locations.add(new Location(2, "Village", "Village", 100, 100, 0.3, 0.1));

        // Market
        locations.add(new Location(3, "Market", "Market", 150, 80, 0.2, 0.05));

        // Mountain
        locations.add(new Location(4, "Mountain", "Mountain", 30, 150, 0.5, 0.8));

        // Lake
        locations.add(new Location(5, "Lake", "Lake", 120, 180, 0.4, 0.3));

        // Dangerous Zone
        locations.add(new Location(6, "Danger Zone", "Wasteland", 170, 170, 0.1, 0.9));

        // Safe Haven
        locations.add(new Location(7, "Safe Haven", "Settlement", 100, 30, 0.3, 0.0));

        logger.info("Initialized demo world with {} locations", locations.size());
    }

    /**
     * Add an NPC to the world.
     */
    public void addNPC(NPC npc) {
        npcs.add(npc);
        logger.info("Added NPC: {} ({}) to world", npc.getName(), npc.getRole().getName());
    }

    /**
     * Remove an NPC from the world.
     */
    public void removeNPC(String npcId) {
        npcs.removeIf(npc -> npc.getId().equals(npcId));
        logger.info("Removed NPC: {} from world", npcId);
    }

    /**
     * Get nearby NPCs within a certain distance.
     */
    public List<NPC> getNearbyNPCs(int x, int y, double distance) {
        return npcs.stream()
                .filter(npc -> npc.getDistanceTo(x, y) <= distance && npc.getDistanceTo(x, y) > 0)
                .collect(Collectors.toList());
    }

    /**
     * Get nearby locations within a certain distance.
     */
    public List<Location> getNearbyLocations(int x, int y, double distance) {
        return locations.stream()
                .filter(loc -> Math.sqrt(Math.pow(loc.getX() - x, 2) + Math.pow(loc.getY() - y, 2)) <= distance)
                .collect(Collectors.toList());
    }

    /**
     * Get all visible locations for an NPC (within vision range).
     */
    public List<Location> getVisibleLocations(NPC npc) {
        return getNearbyLocations(npc.getX(), npc.getY(), 50);
    }

    /**
     * Get the location closest to given coordinates.
     */
    public Location getNearestLocation(int x, int y) {
        return locations.stream()
                .min(Comparator.comparingDouble(loc -> Math.sqrt(
                        Math.pow(loc.getX() - x, 2) + Math.pow(loc.getY() - y, 2))))
                .orElse(null);
    }

    /**
     * Update the world state based on time passage.
     * This is called each simulation tick.
     */
    public void updateWorld() {
        // Update each NPC
        for (NPC npc : npcs) {
            if (!npc.isAlive()) {
                continue;  // Skip dead NPCs
            }

            // Natural resource depletion
            locations.forEach(loc -> {
                if (loc.getResourceLevel() > 0) {
                    loc.setResourceLevel(loc.getResourceLevel() - 0.001);  // Slow depletion
                }
            });

            // Natural danger fluctuation
            locations.forEach(loc -> {
                double dangerChange = (Math.random() - 0.5) * 0.01;  // Small random change
                loc.setDangerLevel(loc.getDangerLevel() + dangerChange);
            });

            // NPC state changes over time
            npc.increaseHunger(0.5);
            npc.consumeEnergy(0.3);
            if (npc.getHealth() < 100) {
                npc.heal(0.2);  // Slow natural healing
            }
        }
    }

    /**
     * Make decisions and execute actions for all NPCs.
     */
    public void simulateStep() {
        WorldState worldState = new WorldState(locations, npcs);
        worldState.updateGlobalDangerLevel();

        for (NPC npc : npcs) {
            if (!npc.isAlive()) {
                continue;
            }

            // Get context for decision
            List<NPC> nearbyNpcs = getNearbyNPCs(npc.getX(), npc.getY(), 10);
            List<Location> visibleLocations = getVisibleLocations(npc);

            // Make decision
            Decision decision = decisionEngine.makeDecision(npc, nearbyNpcs, visibleLocations, worldState);
            npc.addDecision(decision);
            npc.setCurrentAction(decision.getChosenAction());

            // Execute action
            boolean success = actionExecutor.executeAction(npc, decision.getChosenAction());
            decision.setWasSuccessful(success);
        }

        updateWorld();
    }

    // Getters
    public int getWorldWidth() { return worldWidth; }
    public int getWorldHeight() { return worldHeight; }
    public List<Location> getLocations() { return new ArrayList<>(locations); }
    public List<NPC> getNPCs() { return new ArrayList<>(npcs); }
    public DecisionEngine getDecisionEngine() { return decisionEngine; }
    public ActionExecutor getActionExecutor() { return actionExecutor; }

    public int getTotalResources() {
        return (int) locations.stream()
                .mapToDouble(Location::getResourceLevel)
                .sum();
    }

    public double getAverageHealth() {
        return npcs.stream()
                .mapToDouble(NPC::getHealth)
                .average()
                .orElse(0.0);
    }

    public double getAverageEnergy() {
        return npcs.stream()
                .mapToDouble(NPC::getEnergy)
                .average()
                .orElse(0.0);
    }

    public double getTotalMoney() {
        return npcs.stream()
                .mapToDouble(NPC::getMoney)
                .sum();
    }
}
