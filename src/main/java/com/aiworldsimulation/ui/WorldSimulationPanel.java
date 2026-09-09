package com.aiworldsimulation.ui;

import com.aiworldsimulation.simulation.*;
import com.aiworldsimulation.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Custom JPanel that renders the simulated world with NPCs, locations, and visual indicators.
 */
public class WorldSimulationPanel extends JPanel {
    private SimulationWorld world;
    private SimulationManager simulationManager;
    private NPC selectedNPC;
    private static final int CELL_SIZE = 10;

    public WorldSimulationPanel(SimulationWorld world, SimulationManager simulationManager) {
        this.world = world;
        this.simulationManager = simulationManager;
        setPreferredSize(new Dimension(world.getWorldWidth() * CELL_SIZE, world.getWorldHeight() * CELL_SIZE));
        setBackground(new Color(15, 15, 25));
        setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2));

        // Add mouse listener for NPC selection
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int gridX = e.getX() / CELL_SIZE;
                int gridY = e.getY() / CELL_SIZE;
                selectNPCAt(gridX, gridY);
                repaint();
            }
        });
    }

    private void selectNPCAt(int x, int y) {
        selectedNPC = world.getNPCs().stream()
                .filter(npc -> npc.getX() == x && npc.getY() == y)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw locations
        drawLocations(g2d);

        // Draw NPCs
        drawNPCs(g2d);

        // Draw grid
        drawGrid(g2d);

        // Draw selected NPC indicator
        if (selectedNPC != null) {
            drawSelectedNPCIndicator(g2d);
        }
    }

    private void drawLocations(Graphics2D g2d) {
        for (Location location : world.getLocations()) {
            int x = location.getX() * CELL_SIZE;
            int y = location.getY() * CELL_SIZE;
            int size = 30;

            // Color based on danger level
            float hue = (float) (location.getDangerLevel() * 0.3f);  // Red for danger
            Color color = Color.getHSBColor(hue, 0.7f, 0.6f);
            g2d.setColor(color);
            g2d.fillRect(x - size/2, y - size/2, size, size);

            // Draw location name
            g2d.setColor(new Color(200, 200, 220));
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(location.getName().substring(0, Math.min(3, location.getName().length())), 
                           x - 10, y + 5);
        }
    }

    private void drawNPCs(Graphics2D g2d) {
        for (NPC npc : world.getNPCs()) {
            int x = npc.getX() * CELL_SIZE;
            int y = npc.getY() * CELL_SIZE;
            int size = 12;

            // Color based on health
            float healthPercent = (float) npc.getHealth() / 100.0f;
            Color healthColor = new Color(
                (int) (255 * (1 - healthPercent)),
                (int) (255 * healthPercent),
                100
            );

            g2d.setColor(healthColor);
            g2d.fillOval(x - size/2, y - size/2, size, size);

            // Draw state indicator
            g2d.setColor(new Color(100, 200, 255));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawOval(x - size/2 - 2, y - size/2 - 2, size + 4, size + 4);

            // Draw NPC name
            g2d.setColor(new Color(200, 200, 220));
            g2d.setFont(new Font("Arial", Font.PLAIN, 7));
            g2d.drawString(npc.getName().substring(0, 1), x - 3, y - 8);
        }
    }

    private void drawSelectedNPCIndicator(Graphics2D g2d) {
        int x = selectedNPC.getX() * CELL_SIZE;
        int y = selectedNPC.getY() * CELL_SIZE;
        g2d.setColor(new Color(0, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - 20, y - 20, 40, 40);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(50, 50, 60));
        g2d.setStroke(new BasicStroke(0.5f));
        for (int i = 0; i < world.getWorldWidth(); i += 20) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, getHeight());
        }
        for (int i = 0; i < world.getWorldHeight(); i += 20) {
            g2d.drawLine(0, i * CELL_SIZE, getWidth(), i * CELL_SIZE);
        }
    }

    public NPC getSelectedNPC() {
        return selectedNPC;
    }
}
