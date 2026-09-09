package com.aiworldsimulation;

import com.aiworldsimulation.ui.MainWindow;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the AI World Simulation Engine application.
 * This class initializes the application and launches the main GUI window.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== AI World Simulation Engine Starting ===");
        
        // Launch the Swing application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
                logger.info("Main window launched successfully");
            } catch (Exception e) {
                logger.error("Failed to launch main window", e);
                System.err.println("Fatal error: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}
