package studio.magemonkey.managers;

import studio.magemonkey.model.MountData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLManager {

    private static MySQLManager instance;
    private Connection connection;

    private MySQLManager() {
        try {
            String url = "jdbc:mysql://" + ConfigManager.getMySQLHost() + ":" +
                    ConfigManager.getMySQLPort() + "/" + ConfigManager.getMySQLDatabase() +
                    "?useSSL=" + ConfigManager.useSSL() +
                    "&allowPublicKeyRetrieval=" + ConfigManager.allowPublicKeyRetrieval();
            connection = DriverManager.getConnection(url, ConfigManager.getMySQLUsername(), ConfigManager.getMySQLPassword());
            String createTable = "CREATE TABLE IF NOT EXISTS mounts (" +
                    "horse_id VARCHAR(36) NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "spawned BOOLEAN NOT NULL, " +
                    "PRIMARY KEY (horse_id, player_uuid)" +
                    ");";
            connection.createStatement().executeUpdate(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MySQLManager getInstance() {
        if (instance == null) {
            instance = new MySQLManager();
        }
        return instance;
    }

    public void saveMountData(MountData data) {
        try {
            String query = "INSERT INTO mounts (horse_id, player_uuid, spawned) VALUES (?, ?, ?)" +
                    " ON DUPLICATE KEY UPDATE spawned = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, data.getHorseId());
            ps.setString(2, data.getPlayerUUID());
            ps.setBoolean(3, data.isSpawned());
            ps.setBoolean(4, data.isSpawned());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMountData(MountData data) {
        try {
            String query = "DELETE FROM mounts WHERE horse_id = ? AND player_uuid = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, data.getHorseId());
            ps.setString(2, data.getPlayerUUID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
