package com.aiworldsimulation.database;

import com.aiworldsimulation.model.*;
import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Locations.
 */
public class LocationDao {
    private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);

    /**
     * Save a location to the database.
     */
    public static void saveLocation(Location location) {
        String sql = "INSERT OR REPLACE INTO locations (id, name, terrain_type, x, y, resource_level, danger_level, discovered) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, location.getId());
            pstmt.setString(2, location.getName());
            pstmt.setString(3, location.getTerrainType());
            pstmt.setInt(4, location.getX());
            pstmt.setInt(5, location.getY());
            pstmt.setDouble(6, location.getResourceLevel());
            pstmt.setDouble(7, location.getDangerLevel());
            pstmt.setInt(8, location.isDiscovered() ? 1 : 0);
            pstmt.executeUpdate();
            logger.debug("Saved location: {}", location.getName());
        } catch (SQLException e) {
            logger.error("Error saving location: {}", location.getName(), e);
        }
    }

    /**
     * Load a location from the database by ID.
     */
    public static Location loadLocation(int id) {
        String sql = "SELECT * FROM locations WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Location location = new Location(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("terrain_type"),
                    rs.getInt("x"),
                    rs.getInt("y"),
                    rs.getDouble("resource_level"),
                    rs.getDouble("danger_level")
                );
                location.setDiscovered(rs.getInt("discovered") == 1);
                return location;
            }
        } catch (SQLException e) {
            logger.error("Error loading location: {}", id, e);
        }
        return null;
    }

    /**
     * Load all locations from the database.
     */
    public static List<Location> loadAllLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT id FROM locations";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Location location = loadLocation(rs.getInt("id"));
                if (location != null) {
                    locations.add(location);
                }
            }
            logger.info("Loaded {} locations from database", locations.size());
        } catch (SQLException e) {
            logger.error("Error loading locations", e);
        }
        return locations;
    }
}
