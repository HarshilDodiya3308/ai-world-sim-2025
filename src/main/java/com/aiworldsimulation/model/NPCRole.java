package com.aiworldsimulation.model;

import java.io.Serializable;

/**
 * Represents the role/profession of an NPC.
 * Different roles have different capabilities and decision-making biases.
 */
public enum NPCRole implements Serializable {
    GUARD("Guard", "Protects and patrols areas"),
    TRADER("Trader", "Buys and sells resources"),
    EXPLORER("Explorer", "Discovers new areas"),
    MEDIC("Medic", "Heals and helps others"),
    FARMER("Farmer", "Collects and grows resources"),
    SCOUT("Scout", "Gathers information and reconnaissance");

    private final String name;
    private final String description;

    NPCRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
