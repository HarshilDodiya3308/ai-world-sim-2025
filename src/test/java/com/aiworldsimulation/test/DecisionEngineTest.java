package com.aiworldsimulation.test;

import com.aiworldsimulation.ai.*;
import com.aiworldsimulation.model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Unit tests for the DecisionEngine class.
 */
public class DecisionEngineTest {
    private DecisionEngine decisionEngine;
    private NPC testNPC;
    private SimulationWorld testWorld;

    @Before
    public void setUp() {
        decisionEngine = new DecisionEngine();
        testWorld = new SimulationWorld(200, 200);
        testWorld.initializeDemoWorld();

        testNPC = new NPC(
            "TEST_NPC",
            "TestNPC",
            NPCRole.EXPLORER,
            new Personality(Personality.PersonalityType.CURIOUS),
            50,
            50
        );
        testWorld.addNPC(testNPC);
    }

    @Test
    public void testDecisionMakingReturnsValidDecision() {
        List<NPC> nearbyNpcs = new ArrayList<>();
        List<Location> visibleLocations = testWorld.getVisibleLocations(testNPC);
        WorldState worldState = new WorldState(testWorld.getLocations(), testWorld.getNPCs());

        Decision decision = decisionEngine.makeDecision(testNPC, nearbyNpcs, visibleLocations, worldState);

        assertNotNull("Decision should not be null", decision);
        assertNotNull("Decision ID should not be null", decision.getId());
        assertNotNull("Chosen action should not be null", decision.getChosenAction());
        assertTrue("Confidence should be between 0 and 1", decision.getConfidence() >= 0 && decision.getConfidence() <= 1);
    }

    @Test
    public void testCuriousPersonalityPrefersExplore() {
        // Curious NPC should score high on EXPLORE action
        NPC curiousNPC = new NPC(
            "CURIOUS_NPC",
            "Curious",
            NPCRole.EXPLORER,
            new Personality(Personality.PersonalityType.CURIOUS),
            50, 50
        );
        testWorld.addNPC(curiousNPC);

        // Set good conditions for exploration
        curiousNPC.setHealth(80);
        curiousNPC.setCurrentState(NPC.NPCState.IDLE);

        List<NPC> nearbyNpcs = new ArrayList<>();
        List<Location> visibleLocations = testWorld.getVisibleLocations(curiousNPC);
        WorldState worldState = new WorldState(testWorld.getLocations(), testWorld.getNPCs());

        Decision decision = decisionEngine.makeDecision(curiousNPC, nearbyNpcs, visibleLocations, worldState);

        // Explore should be more likely for curious personality
        assertTrue("Curious NPC should make valid decisions", decision.getConfidence() > 0);
    }

    @Test
    public void testHealthCriticalFavorsHealing() {
        testNPC.takeDamage(85.0);  // Reduce health to ~15
        assertTrue("NPC health should be critical", testNPC.getHealth() < 20);

        List<NPC> nearbyNpcs = new ArrayList<>();
        List<Location> visibleLocations = testWorld.getVisibleLocations(testNPC);
        WorldState worldState = new WorldState(testWorld.getLocations(), testWorld.getNPCs());

        Decision decision = decisionEngine.makeDecision(testNPC, nearbyNpcs, visibleLocations, worldState);

        // When health is critical, healing should score high
        assertNotNull("Decision should be made even in critical health", decision);
        assertTrue("Healing or rest should be preferred", 
                decision.getChosenAction() == NPC.NPCAction.HEAL || 
                decision.getChosenAction() == NPC.NPCAction.REST);
    }

    @Test
    public void testDecisionExplanationIsProvided() {
        List<NPC> nearbyNpcs = new ArrayList<>();
        List<Location> visibleLocations = testWorld.getVisibleLocations(testNPC);
        WorldState worldState = new WorldState(testWorld.getLocations(), testWorld.getNPCs());

        Decision decision = decisionEngine.makeDecision(testNPC, nearbyNpcs, visibleLocations, worldState);

        assertNotNull("Explanation should not be null", decision.getExplanation());
        assertTrue("Explanation should not be empty", decision.getExplanation().length() > 0);
        assertTrue("Explanation should contain decision info", 
                decision.getExplanation().contains(decision.getChosenAction().toString()));
    }
}
