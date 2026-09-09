package com.aiworldsimulation.test;

import com.aiworldsimulation.model.*;
import com.aiworldsimulation.ai.Personality;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the NPC class.
 */
public class NPCTest {
    private NPC testNPC;

    @Before
    public void setUp() {
        testNPC = new NPC(
            "TEST_NPC_1",
            "TestNPC",
            NPCRole.GUARD,
            new Personality(Personality.PersonalityType.DEFENSIVE),
            50, 50
        );
    }

    @Test
    public void testNPCInitialization() {
        assertEquals("NPC ID should match", "TEST_NPC_1", testNPC.getId());
        assertEquals("NPC name should match", "TestNPC", testNPC.getName());
        assertEquals("NPC role should match", NPCRole.GUARD, testNPC.getRole());
        assertEquals("NPC X position should be 50", 50, testNPC.getX());
        assertEquals("NPC Y position should be 50", 50, testNPC.getY());
    }

    @Test
    public void testTakeDamage() {
        double healthBefore = testNPC.getHealth();
        testNPC.takeDamage(20);
        double healthAfter = testNPC.getHealth();

        assertEquals("Health should decrease by damage amount", healthBefore - 20, healthAfter, 0.01);
    }

    @Test
    public void testHeal() {
        testNPC.takeDamage(50);
        double healthBefore = testNPC.getHealth();
        testNPC.heal(20);
        double healthAfter = testNPC.getHealth();

        assertEquals("Health should increase by heal amount", healthBefore + 20, healthAfter, 0.01);
    }

    @Test
    public void testHealthBoundaries() {
        testNPC.takeDamage(200);  // Try to reduce below 0
        assertTrue("Health should not go below 0", testNPC.getHealth() >= 0);

        testNPC.heal(500);  // Try to increase above 100
        assertTrue("Health should not exceed 100", testNPC.getHealth() <= 100);
    }

    @Test
    public void testEnergyManagement() {
        double energyBefore = testNPC.getEnergy();
        testNPC.consumeEnergy(10);
        double energyAfter = testNPC.consumeEnergy(10);
        assertEquals("Energy should decrease", energyBefore, testNPC.getEnergy() + 20, 0.01);
    }

    @Test
    public void testIsAlive() {
        assertTrue("NPC should be alive with health > 0", testNPC.isAlive());
        testNPC.takeDamage(100);
        assertFalse("NPC should be dead with health <= 0", testNPC.isAlive());
    }

    @Test
    public void testHungerManagement() {
        double hungerBefore = testNPC.getHunger();
        testNPC.increaseHunger(10);
        assertTrue("Hunger should increase", testNPC.getHunger() > hungerBefore);

        testNPC.decreaseHunger(5);
        assertTrue("Hunger should decrease", testNPC.getHunger() < (hungerBefore + 10));
    }

    @Test
    public void testMemoryStorage() {
        Memory memory = new Memory(
            "MEM_1",
            testNPC.getId(),
            Memory.MemoryType.DISCOVERY,
            "Found a new location",
            1,
            0.8
        );
        testNPC.addMemory(memory);
        assertTrue("Memory should be stored", testNPC.getMemories().contains(memory));
    }

    @Test
    public void testRelationshipManagement() {
        testNPC.updateRelationship("OTHER_NPC", 10);
        double relationship = testNPC.getRelationships().get("OTHER_NPC");
        assertEquals("Relationship should be updated", 10.0, relationship, 0.01);

        testNPC.updateRelationship("OTHER_NPC", 5);
        relationship = testNPC.getRelationships().get("OTHER_NPC");
        assertEquals("Relationship should accumulate", 15.0, relationship, 0.01);
    }
}
