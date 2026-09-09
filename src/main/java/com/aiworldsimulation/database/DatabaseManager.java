package com.aiworldsimulation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages database connections and initialization.
 * Handles SQLite setup and ensures all required tables exist.
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:ai_world_simulation.db";

    /**
     * Get a database connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initialize the database with all required tables.
     */
    public static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                // Create NPCs table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS npcs (" +
                    "  id TEXT PRIMARY KEY," +
                    "  name TEXT NOT NULL," +
                    "  role TEXT NOT NULL," +
                    "  personality TEXT NOT NULL," +
                    "  x INTEGER," +
                    "  y INTEGER," +
                    "  health REAL," +
                    "  energy REAL," +
                    "  hunger REAL," +
                    "  money REAL," +
                    "  current_state TEXT," +
                    "  current_action TEXT," +
                    "  current_goal TEXT," +
                    "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
                );

                // Create Locations table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS locations (" +
                    "  id INTEGER PRIMARY KEY," +
                    "  name TEXT NOT NULL," +
                    "  terrain_type TEXT," +
                    "  x INTEGER," +
                    "  y INTEGER," +
                    "  resource_level REAL," +
                    "  danger_level REAL," +
                    "  discovered INTEGER DEFAULT 0" +
                    ")"
                );

                // Create Memories table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS memories (" +
                    "  id TEXT PRIMARY KEY," +
                    "  npc_id TEXT," +
                    "  type TEXT," +
                    "  description TEXT," +
                    "  location_id INTEGER," +
                    "  importance REAL," +
                    "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  FOREIGN KEY(npc_id) REFERENCES npcs(id)" +
                    ")"
                );

                // Create Decisions table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS decisions (" +
                    "  id TEXT PRIMARY KEY," +
                    "  npc_id TEXT," +
                    "  action TEXT," +
                    "  explanation TEXT," +
                    "  confidence REAL," +
                    "  was_successful INTEGER," +
                    "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  FOREIGN KEY(npc_id) REFERENCES npcs(id)" +
                    ")"
                );

                // Create Events table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS events (" +
                    "  id TEXT PRIMARY KEY," +
                    "  type TEXT," +
                    "  description TEXT," +
                    "  affected_location_id INTEGER," +
                    "  affected_npc_id TEXT," +
                    "  severity REAL," +
                    "  is_resolved INTEGER," +
                    "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
                );

                // Create SavedGames table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS saved_games (" +
                    "  id TEXT PRIMARY KEY," +
                    "  name TEXT NOT NULL," +
                    "  description TEXT," +
                    "  step_count INTEGER," +
                    "  world_width INTEGER," +
                    "  world_height INTEGER," +
                    "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
                );

                // Create Inventory table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS inventory (" +
                    "  id TEXT PRIMARY KEY," +
                    "  npc_id TEXT," +
                    "  item TEXT NOT NULL," +
                    "  FOREIGN KEY(npc_id) REFERENCES npcs(id)" +
                    ")"
                );

                // Create Relationships table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS relationships (" +
                    "  npc_id TEXT," +
                    "  other_npc_id TEXT," +
                    "  score REAL," +
                    "  PRIMARY KEY(npc_id, other_npc_id)," +
                    "  FOREIGN KEY(npc_id) REFERENCES npcs(id)" +
                    ")"
                );

                logger.info("Database initialized successfully");
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found", e);
            throw new RuntimeException("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
