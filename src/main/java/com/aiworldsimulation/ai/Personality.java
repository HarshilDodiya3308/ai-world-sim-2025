package com.aiworldsimulation.ai;

import java.io.Serializable;

/**
 * Represents the personality traits of an NPC.
 * Personalities influence decision-making by modifying action scores.
 */
public class Personality implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private PersonalityType type;
    private double aggression;        // 0 to 1.0 - tendency to attack
    private double defensiveness;     // 0 to 1.0 - tendency to retreat/protect
    private double curiosity;         // 0 to 1.0 - tendency to explore
    private double greed;             // 0 to 1.0 - tendency to collect resources
    private double helpfulness;       // 0 to 1.0 - tendency to help others
    private double caution;           // 0 to 1.0 - tendency to avoid danger

    public enum PersonalityType {
        AGGRESSIVE,
        DEFENSIVE,
        CURIOUS,
        GREEDY,
        HELPFUL,
        BALANCED
    }

    public Personality(PersonalityType type) {
        this.type = type;
        initializeTraits(type);
    }

    /**
     * Initialize personality traits based on the personality type.
     * Each type has different default values for the six traits.
     */
    private void initializeTraits(PersonalityType type) {
        switch (type) {
            case AGGRESSIVE:
                this.aggression = 0.8;
                this.defensiveness = 0.2;
                this.curiosity = 0.5;
                this.greed = 0.4;
                this.helpfulness = 0.2;
                this.caution = 0.1;
                break;
            case DEFENSIVE:
                this.aggression = 0.2;
                this.defensiveness = 0.8;
                this.curiosity = 0.3;
                this.greed = 0.3;
                this.helpfulness = 0.5;
                this.caution = 0.8;
                break;
            case CURIOUS:
                this.aggression = 0.3;
                this.defensiveness = 0.3;
                this.curiosity = 0.9;
                this.greed = 0.4;
                this.helpfulness = 0.4;
                this.caution = 0.4;
                break;
            case GREEDY:
                this.aggression = 0.4;
                this.defensiveness = 0.4;
                this.curiosity = 0.5;
                this.greed = 0.9;
                this.helpfulness = 0.1;
                this.caution = 0.3;
                break;
            case HELPFUL:
                this.aggression = 0.1;
                this.defensiveness = 0.4;
                this.curiosity = 0.5;
                this.greed = 0.1;
                this.helpfulness = 0.9;
                this.caution = 0.5;
                break;
            case BALANCED:
            default:
                this.aggression = 0.5;
                this.defensiveness = 0.5;
                this.curiosity = 0.5;
                this.greed = 0.5;
                this.helpfulness = 0.5;
                this.caution = 0.5;
                break;
        }
    }

    // Getters
    public PersonalityType getType() { return type; }
    public double getAggression() { return aggression; }
    public double getDefensiveness() { return defensiveness; }
    public double getCuriosity() { return curiosity; }
    public double getGreed() { return greed; }
    public double getHelpfulness() { return helpfulness; }
    public double getCaution() { return caution; }

    /**
     * Calculate the influence multiplier for a specific action based on this personality.
     * Returns values between 0.5 and 1.5 (50% to 150% of base score).
     */
    public double getInfluenceForAction(String actionName) {
        double influence = 1.0;  // Base multiplier

        // Attack actions influenced by aggression and caution
        if (actionName.equals("ATTACK")) {
            influence = 0.7 + (aggression * 0.6) - (caution * 0.3);
        }
        // Retreat actions influenced by defensiveness and caution
        else if (actionName.equals("RETREAT")) {
            influence = 0.7 + (defensiveness * 0.6) + (caution * 0.3);
        }
        // Exploration influenced by curiosity
        else if (actionName.equals("EXPLORE")) {
            influence = 0.7 + (curiosity * 0.6) - (caution * 0.2);
        }
        // Trading influenced by greed
        else if (actionName.equals("TRADE")) {
            influence = 0.8 + (greed * 0.4);
        }
        // Resource collection influenced by greed
        else if (actionName.equals("COLLECT_RESOURCE")) {
            influence = 0.8 + (greed * 0.4);
        }
        // Helping influenced by helpfulness
        else if (actionName.equals("HELP")) {
            influence = 0.7 + (helpfulness * 0.6);
        }
        // Healing influenced by helpfulness and defensiveness
        else if (actionName.equals("HEAL")) {
            influence = 0.8 + (helpfulness * 0.3) + (defensiveness * 0.2);
        }
        // Rest influenced by caution
        else if (actionName.equals("REST")) {
            influence = 0.8 + (caution * 0.3);
        }

        return Math.max(0.5, Math.min(1.5, influence));
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
