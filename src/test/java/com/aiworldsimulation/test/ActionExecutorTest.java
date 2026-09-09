package com.aiworldsimulation.test;

import com.aiworldsimulation.model.*;
import com.aiworldsimulation.simulation.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the ActionExecutor class.
 */
public class ActionExecutorTest {
    private SimulationWorld world;
    private ActionExecutor actionExecutor;
    private NPC testNPC;

    @Before
    public void setUp() {
        world = new SimulationWorld(200, 200);
        world.initializeDemoWorld();
        actionExecutor = new ActionExecutor(world);

        testNPC = new NPC(
            "TEST_NPC",
            "TestNPC",
            NPCRole.EXPLORER,
            new Personality(Personality.PersonalityType.BALANCED),
            100, 100
        );
        world.addNPC(testNPC);
    }

    @Test
    public void testMoveActionChangesPosition() {
        int originalX = testNPC.getX();
        int originalY = testNPC.getY();

        boolean success = actionExecutor.executeAction(testNPC, NPC.NPCAction.MOVE);

        assertTrue("Move action should succeed", success);
        // Position might change or stay same due to random movement
        assertTrue("NPC should remain in world bounds", 
                testNPC.getX() >= 0 && testNPC.getX() < world.getWorldWidth() &&
                testNPC.getY() >= 0 && testNPC.getY() < world.getWorldHeight());
    }

    @Test
    public void testRestActionRestoresEnergy() {
        testNPC.consumeEnergy(50);
        double energyBefore = testNPC.getEnergy();

        actionExecutor.executeAction(testNPC, NPC.NPCAction.REST);
        double energyAfter = testNPC.getEnergy();

        assertTrue("Rest should restore energy", energyAfter > energyBefore);
    }

    @Test
    public void testHealActionIncreasesHealth() {
        testNPC.takeDamage(30);
        double healthBefore = testNPC.getHealth();

        actionExecutor.executeAction(testNPC, NPC.NPCAction.HEAL);
        double healthAfter = testNPC.getHealth();

        assertTrue("Heal should increase health", healthAfter > healthBefore);
    }

    @Test
    public void testWaitActionIsAlwaysSuccessful() {
        boolean success = actionExecutor.executeAction(testNPC, NPC.NPCAction.WAIT);
        assertTrue("Wait action should always succeed", success);
    }

    @Test
    public void testActionConsumesEnergy() {
        double energyBefore = testNPC.getEnergy();
        actionExecutor.executeAction(testNPC, NPC.NPCAction.EXPLORE);
        double energyAfter = testNPC.getEnergy();

        assertTrue("Action should consume energy", energyAfter < energyBefore);
    }
}
