package com.aiworldsimulation.simulation;

import com.aiworldsimulation.model.*;
import java.util.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the overall simulation lifecycle.
 * Handles simulation start, pause, resume, stop, and step operations.
 * Runs on a background thread to avoid blocking the UI.
 */
public class SimulationManager {
    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);

    private SimulationWorld world;
    private ScheduledExecutorService executorService;
    private SimulationState state;
    private int simulationSpeed;  // Milliseconds between steps
    private long stepCount;
    private List<SimulationListener> listeners;
    private ScheduledFuture<?> simulationTask;

    public enum SimulationState {
        STOPPED, RUNNING, PAUSED
    }

    public interface SimulationListener {
        void onSimulationStep(SimulationWorld world, long stepCount);
        void onSimulationStateChanged(SimulationState newState);
        void onEventOccurred(WorldEvent event);
    }

    public SimulationManager(SimulationWorld world) {
        this.world = world;
        this.executorService = Executors.newScheduledThreadPool(1);
        this.state = SimulationState.STOPPED;
        this.simulationSpeed = 1000;  // 1 second per step by default
        this.stepCount = 0;
        this.listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Start the simulation.
     */
    public void start() {
        if (state == SimulationState.STOPPED) {
            state = SimulationState.RUNNING;
            stepCount = 0;
            startSimulationLoop();
            notifyStateChanged();
            logger.info("Simulation started");
        }
    }

    /**
     * Pause the simulation.
     */
    public void pause() {
        if (state == SimulationState.RUNNING) {
            state = SimulationState.PAUSED;
            if (simulationTask != null) {
                simulationTask.cancel(false);
            }
            notifyStateChanged();
            logger.info("Simulation paused at step {}", stepCount);
        }
    }

    /**
     * Resume the paused simulation.
     */
    public void resume() {
        if (state == SimulationState.PAUSED) {
            state = SimulationState.RUNNING;
            startSimulationLoop();
            notifyStateChanged();
            logger.info("Simulation resumed");
        }
    }

    /**
     * Stop the simulation completely.
     */
    public void stop() {
        if (state != SimulationState.STOPPED) {
            state = SimulationState.STOPPED;
            if (simulationTask != null) {
                simulationTask.cancel(false);
            }
            notifyStateChanged();
            logger.info("Simulation stopped");
        }
    }

    /**
     * Execute a single simulation step.
     */
    public void step() {
        world.simulateStep();
        stepCount++;
        notifyStep();
        logger.debug("Simulation step: {}", stepCount);
    }

    /**
     * Reset the simulation.
     */
    public void reset() {
        stop();
        stepCount = 0;
        world.getNPCs().forEach(npc -> npc.getDecisionHistory().clear());
        world.getNPCs().forEach(npc -> npc.getMemories().clear());
        logger.info("Simulation reset");
    }

    /**
     * Set the simulation speed.
     * Lower values = faster simulation
     */
    public void setSimulationSpeed(int millisecondsBetweenSteps) {
        this.simulationSpeed = Math.max(10, millisecondsBetweenSteps);
        if (state == SimulationState.RUNNING) {
            // Restart with new speed
            if (simulationTask != null) {
                simulationTask.cancel(false);
            }
            startSimulationLoop();
        }
        logger.info("Simulation speed set to {} ms/step", simulationSpeed);
    }

    private void startSimulationLoop() {
        simulationTask = executorService.scheduleAtFixedRate(
            this::step,
            0,
            simulationSpeed,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Add a listener to be notified of simulation events.
     */
    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     */
    public void removeListener(SimulationListener listener) {
        listeners.remove(listener);
    }

    private void notifyStep() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationStep(world, stepCount);
        }
    }

    private void notifyStateChanged() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationStateChanged(state);
        }
    }

    public void notifyEvent(WorldEvent event) {
        for (SimulationListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    // Getters
    public SimulationState getState() { return state; }
    public long getStepCount() { return stepCount; }
    public int getSimulationSpeed() { return simulationSpeed; }
    public SimulationWorld getWorld() { return world; }

    /**
     * Shutdown the simulation manager.
     */
    public void shutdown() {
        stop();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Simulation manager shutdown");
    }
}
