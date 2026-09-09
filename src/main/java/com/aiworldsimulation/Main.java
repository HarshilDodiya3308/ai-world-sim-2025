package com.aiworldsimulation;

import com.aiworldsimulation.database.DatabaseManager;
import com.aiworldsimulation.ui.MainWindow;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the AI World Simulation Engine application.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting AI World Simulation Engine...");
        
        try {
            // Initialize database
            DatabaseManager.initializeDatabase();
            logger.info("Database initialized successfully");

            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Launch UI on Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                logger.info("Launching main window...");
                new MainWindow();
            });
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }
}
