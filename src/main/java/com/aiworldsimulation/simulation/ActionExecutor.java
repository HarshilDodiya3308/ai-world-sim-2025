package com.aiworldsimulation.simulation;

import com.aiworldsimulation.model.*;
import com.aiworldsimulation.ai.*;
import java.util.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes NPC actions and updates the world state.
 * This class translates decisions into real changes in the simulation.
 */
public class ActionExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ActionExecutor.class);

    private Random random;
    private SimulationWorld world;

    public ActionExecutor(SimulationWorld world) {
        this.world = world;
        this.random = new Random();
    }

    /**
     * Execute an action for an NPC.
     * Updates NPC state, world state, and returns whether the action was successful.
     */
    public boolean executeAction(NPC npc, NPC.NPCAction action) {
        try {
            switch (action) {
                case MOVE:
                    return executeMove(npc);
                case EXPLORE:
                    return executeExplore(npc);
                case ATTACK:
                    return executeAttack(npc);
                case RETREAT:
                    return executeRetreat(npc);
                case REST:
                    return executeRest(npc);
                case EAT:
                    return executeEat(npc);
                case TRADE:
                    return executeTrade(npc);
                case COLLECT_RESOURCE:
                    return executeCollectResource(npc);
                case HELP:
                    return executeHelp(npc);
                case HEAL:
                    return executeHeal(npc);
                case PATROL:
                    return executePatrol(npc);
                case WAIT:
                    return executeWait(npc);
                default:
                    return false;
            }
        } catch (Exception e) {
            logger.error("Error executing action {} for NPC {}", action, npc.getName(), e);
            return false;
        }
    }

    private boolean executeMove(NPC npc) {
        // Move NPC randomly or towards a goal
        int newX = npc.getX() + random.nextInt(3) - 1;  // -1, 0, or 1
        int newY = npc.getY() + random.nextInt(3) - 1;

        newX = Math.max(0, Math.min(world.getWorldWidth() - 1, newX));
        newY = Math.max(0, Math.min(world.getWorldHeight() - 1, newY));

        npc.setPosition(newX, newY);
        npc.consumeEnergy(5.0);
        npc.setCurrentState(NPC.NPCState.MOVING);

        logger.debug("NPC {} moved to ({}, {})", npc.getName(), newX, newY);
        return true;
    }

    private boolean executeExplore(NPC npc) {
        // Move to a location and mark it as discovered
        List<Location> nearbyLocations = world.getNearbyLocations(npc.getX(), npc.getY(), 10);

        for (Location loc : nearbyLocations) {
            if (!loc.isDiscovered()) {
                loc.setDiscovered(true);
                loc.incrementDiscoveredCount();
                npc.addMemory(new Memory(
                    UUID.randomUUID().toString(),
                    npc.getId(),
                    Memory.MemoryType.DISCOVERY,
                    "Discovered " + loc.getName(),
                    loc.getId(),
                    0.8
                ));
                break;
            }
        }

        npc.consumeEnergy(8.0);
        npc.setCurrentState(NPC.NPCState.EXPLORING);
        logger.debug("NPC {} explored area", npc.getName());
        return true;
    }

    private boolean executeAttack(NPC npc) {
        // Find nearby enemy NPCs and attack
        List<NPC> nearbyNpcs = world.getNearbyNPCs(npc.getX(), npc.getY(), 5);
        
        if (!nearbyNpcs.isEmpty()) {
            NPC target = nearbyNpcs.get(0);
            double damage = 10.0 + random.nextDouble() * 15.0;
            target.takeDamage(damage);
            npc.consumeEnergy(10.0);
            npc.setCurrentState(NPC.NPCState.ACTING);
            logger.debug("NPC {} attacked {} for {}", npc.getName(), target.getName(), damage);
            return true;
        }
        return false;
    }

    private boolean executeRetreat(NPC npc) {
        // Move away from danger
        int newX = npc.getX() + random.nextInt(5) - 2;
        int newY = npc.getY() + random.nextInt(5) - 2;

        newX = Math.max(0, Math.min(world.getWorldWidth() - 1, newX));
        newY = Math.max(0, Math.min(world.getWorldHeight() - 1, newY));

        npc.setPosition(newX, newY);
        npc.consumeEnergy(6.0);
        npc.setCurrentState(NPC.NPCState.DANGER);
        logger.debug("NPC {} retreated", npc.getName());
        return true;
    }

    private boolean executeRest(NPC npc) {
        // Restore energy
        npc.restoreEnergy(20.0);
        npc.consumeEnergy(2.0);  // Small cost
        npc.increaseHunger(5.0);
        npc.setCurrentState(NPC.NPCState.RESTING);
        logger.debug("NPC {} rested", npc.getName());
        return true;
    }

    private boolean executeEat(NPC npc) {
        // Consume food from inventory or gain it from world
        if (!npc.getInventory().isEmpty() && npc.getInventory().contains("Food")) {
            npc.getInventory().remove("Food");
            npc.decreaseHunger(40.0);
            npc.consumeEnergy(2.0);
            npc.restoreEnergy(5.0);
            logger.debug("NPC {} ate from inventory", npc.getName());
            return true;
        } else {
            // Try to find food in current location
            List<Location> locationsHere = world.getNearbyLocations(npc.getX(), npc.getY(), 1);
            if (!locationsHere.isEmpty() && locationsHere.get(0).getResourceLevel() > 0.5) {
                npc.decreaseHunger(30.0);
                npc.consumeEnergy(3.0);
                logger.debug("NPC {} found food in location", npc.getName());
                return true;
            }
        }
        return false;
    }

    private boolean executeTrade(NPC npc) {
        // Trade with nearby NPCs
        List<NPC> nearbyNpcs = world.getNearbyNPCs(npc.getX(), npc.getY(), 3);
        
        if (!nearbyNpcs.isEmpty()) {
            NPC trader = nearbyNpcs.get(0);
            double amount = Math.min(10.0, npc.getMoney());
            npc.removeMoney(amount);
            trader.addMoney(amount);
            npc.addMoney(amount * 0.8);  // Get something back
            npc.consumeEnergy(3.0);
            npc.setCurrentState(NPC.NPCState.TRADING);
            npc.updateRelationship(trader.getId(), 5.0);
            logger.debug("NPC {} traded with {}", npc.getName(), trader.getName());
            return true;
        }
        return false;
    }

    private boolean executeCollectResource(NPC npc) {
        // Collect resources from current location
        List<Location> locationsHere = world.getNearbyLocations(npc.getX(), npc.getY(), 1);
        
        if (!locationsHere.isEmpty()) {
            Location loc = locationsHere.get(0);
            if (loc.getResourceLevel() > 0.2) {
                npc.getInventory().add("Resource_" + UUID.randomUUID().toString().substring(0, 8));
                loc.setResourceLevel(loc.getResourceLevel() - 0.1);
                npc.addMoney(5.0);
                npc.consumeEnergy(5.0);
                npc.setCurrentState(NPC.NPCState.ACTING);
                logger.debug("NPC {} collected resource", npc.getName());
                return true;
            }
        }
        return false;
    }

    private boolean executeHelp(NPC npc) {
        // Help nearby injured NPCs
        List<NPC> nearbyNpcs = world.getNearbyNPCs(npc.getX(), npc.getY(), 3);
        
        NPC injuredNpc = nearbyNpcs.stream()
                .filter(n -> n.getHealth() < 50)
                .findFirst()
                .orElse(null);

        if (injuredNpc != null) {
            injuredNpc.heal(15.0);
            npc.consumeEnergy(10.0);
            npc.setCurrentState(NPC.NPCState.ACTING);
            npc.updateRelationship(injuredNpc.getId(), 10.0);
            injuredNpc.updateRelationship(npc.getId(), 10.0);
            logger.debug("NPC {} helped {}", npc.getName(), injuredNpc.getName());
            return true;
        }
        return false;
    }

    private boolean executeHeal(NPC npc) {
        // Self-heal
        npc.heal(15.0);
        npc.consumeEnergy(8.0);
        npc.setCurrentState(NPC.NPCState.RESTING);
        logger.debug("NPC {} healed itself", npc.getName());
        return true;
    }

    private boolean executePatrol(NPC npc) {
        // Move in a specific area
        int newX = npc.getX() + random.nextInt(3) - 1;
        int newY = npc.getY() + random.nextInt(3) - 1;

        newX = Math.max(0, Math.min(world.getWorldWidth() - 1, newX));
        newY = Math.max(0, Math.min(world.getWorldHeight() - 1, newY));

        npc.setPosition(newX, newY);
        npc.consumeEnergy(4.0);
        npc.setCurrentState(NPC.NPCState.MOVING);
        logger.debug("NPC {} patrolled", npc.getName());
        return true;
    }

    private boolean executeWait(NPC npc) {
        // Do nothing, just wait and slowly recover
        npc.restoreEnergy(2.0);
        npc.increaseHunger(2.0);
        npc.setCurrentState(NPC.NPCState.IDLE);
        logger.debug("NPC {} waited", npc.getName());
        return true;
    }
}
