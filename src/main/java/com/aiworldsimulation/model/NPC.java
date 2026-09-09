package com.aiworldsimulation.model;

import com.aiworldsimulation.ai.Personality;
import java.io.Serializable;
import java.util.*;

/**
 * Represents an autonomous NPC in the simulated world.
 * Each NPC has a role, personality, state, and decision-making capabilities.
 */
public class NPC implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private NPCRole role;
    private Personality personality;
    private int x, y;
    private double health;              // 0 to 100
    private double energy;              // 0 to 100
    private double hunger;              // 0 to 100
    private double money;               // Currency
    private List<String> inventory;     // Items carried
    private Map<String, Double> relationships;  // NPC ID -> relationship score
    private List<Memory> memories;
    private List<Decision> decisionHistory;
    private NPCState currentState;
    private NPCAction currentAction;
    private String currentGoal;
    private int currentLocationId;
    private long lastActionTime;

    public enum NPCState {
        IDLE, MOVING, ACTING, RESTING, TRADING, EXPLORING, HELPING, DANGER
    }

    public enum NPCAction {
        MOVE, EXPLORE, ATTACK, RETREAT, REST, EAT, TRADE, COLLECT_RESOURCE, HELP, HEAL, PATROL, WAIT
    }

    public NPC(String id, String name, NPCRole role, Personality personality, int x, int y) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.personality = personality;
        this.x = x;
        this.y = y;
        this.health = 80.0;
        this.energy = 80.0;
        this.hunger = 30.0;
        this.money = 50.0;
        this.inventory = new ArrayList<>();
        this.relationships = new HashMap<>();
        this.memories = new ArrayList<>();
        this.decisionHistory = new ArrayList<>();
        this.currentState = NPCState.IDLE;
        this.currentAction = NPCAction.WAIT;
        this.currentGoal = "Survive and explore";
        this.currentLocationId = -1;
        this.lastActionTime = System.currentTimeMillis();
    }

    // State Management
    public void takeDamage(double damage) {
        this.health = Math.max(0, health - damage);
    }

    public void heal(double amount) {
        this.health = Math.min(100, health + amount);
    }

    public void consumeEnergy(double amount) {
        this.energy = Math.max(0, energy - amount);
    }

    public void restoreEnergy(double amount) {
        this.energy = Math.min(100, energy + amount);
    }

    public void increaseHunger(double amount) {
        this.hunger = Math.min(100, hunger + amount);
    }

    public void decreaseHunger(double amount) {
        this.hunger = Math.max(0, hunger - amount);
    }

    public void addMoney(double amount) {
        this.money = Math.max(0, money + amount);
    }

    public void removeMoney(double amount) {
        this.money = Math.max(0, money - amount);
    }

    public void addMemory(Memory memory) {
        this.memories.add(memory);
        if (this.memories.size() > 100) {
            this.memories.remove(0);  // Keep only last 100 memories
        }
    }

    public void addDecision(Decision decision) {
        this.decisionHistory.add(decision);
        if (this.decisionHistory.size() > 50) {
            this.decisionHistory.remove(0);  // Keep only last 50 decisions
        }
    }

    public void updateRelationship(String otherNpcId, double change) {
        double current = this.relationships.getOrDefault(otherNpcId, 0.0);
        double updated = Math.max(-100, Math.min(100, current + change));
        this.relationships.put(otherNpcId, updated);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public NPCRole getRole() { return role; }
    public Personality getPersonality() { return personality; }
    public int getX() { return x; }
    public int getY() { return y; }
    public double getHealth() { return health; }
    public double getEnergy() { return energy; }
    public double getHunger() { return hunger; }
    public double getMoney() { return money; }
    public List<String> getInventory() { return inventory; }
    public Map<String, Double> getRelationships() { return relationships; }
    public List<Memory> getMemories() { return new ArrayList<>(memories); }
    public List<Decision> getDecisionHistory() { return new ArrayList<>(decisionHistory); }
    public NPCState getCurrentState() { return currentState; }
    public NPCAction getCurrentAction() { return currentAction; }
    public String getCurrentGoal() { return currentGoal; }
    public int getCurrentLocationId() { return currentLocationId; }
    public long getLastActionTime() { return lastActionTime; }

    // Setters
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setCurrentState(NPCState state) { this.currentState = state; }
    public void setCurrentAction(NPCAction action) { 
        this.currentAction = action;
        this.lastActionTime = System.currentTimeMillis();
    }
    public void setCurrentGoal(String goal) { this.currentGoal = goal; }
    public void setCurrentLocationId(int locationId) { this.currentLocationId = locationId; }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isTired() {
        return energy < 20;
    }

    public boolean isHungry() {
        return hunger > 70;
    }

    public double getDistanceTo(int targetX, int targetY) {
        return Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - HP: %.0f, NRG: %.0f, HNG: %.0f, $%.0f",
                           name, role.getName(), health, energy, hunger, money);
    }
}
