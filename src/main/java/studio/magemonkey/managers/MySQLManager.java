package studio.magemonkey.managers;

import org.bukkit.configuration.ConfigurationSection;
import studio.magemonkey.Eclipse;
import studio.magemonkey.model.MountData;

import java.sql.*;

public class MySQLManager {
    private final Eclipse plugin;
    private final ConfigurationSection config;
    private Connection connection;

    public MySQLManager(Eclipse plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void initialize() {
        String host = config.getString("host", "localhost");
        int port = config.getInt("port", 3306);
        String database = config.getString("database", "horse_data");
        String username = config.getString("username", "root");
        String password = config.getString("password", "password");
        boolean useSSL = config.getBoolean("useSSL", false);
        boolean allowPublicKeyRetrieval = config.getBoolean("allowPublicKeyRetrieval", false);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=" + useSSL +
                "&autoReconnect=true" +
                (allowPublicKeyRetrieval ? "&allowPublicKeyRetrieval=true" : "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            createTable();
            plugin.getLogger().info("MySQL connection initialized successfully.");
        } catch (Exception e) {
            plugin.getLogger().severe("MySQL initialization error: " + e.getMessage());
        }
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS horse_mounts (" +
                            "uuid VARCHAR(36) PRIMARY KEY," +
                            "entityType VARCHAR(50)," +
                            "color VARCHAR(50)," +
                            "style VARCHAR(50)," +
                            "variant VARCHAR(50)," +
                            "saddle BOOLEAN," +
                            "armor VARCHAR(50)," +
                            "customName VARCHAR(100)," +
                            "jumpStrength DOUBLE" +
                            ")"
            );
        }
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    public void insertMount(String uuid, MountData data) {
        if (connection == null) return;
        String sql = "INSERT INTO horse_mounts (uuid, entityType, color, style, variant, saddle, armor, customName, jumpStrength) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, data.getEntityType());
            ps.setString(3, data.getColor());
            ps.setString(4, data.getStyle());
            ps.setString(5, data.getVariant());
            ps.setBoolean(6, data.hasSaddle());
            ps.setString(7, data.getArmor());
            ps.setString(8, data.getCustomName());
            ps.setDouble(9, data.getJumpStrength());
            ps.executeUpdate();
            plugin.getLogger().info("Inserted mount with UUID: " + uuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("MySQL insert error: " + e.getMessage());
        }
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    public MountData getMount(String uuid) {
        if (connection == null) return null;
        String sql = "SELECT * FROM horse_mounts WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MountData data = new MountData();
                data.setEntityType(rs.getString("entityType"));
                data.setColor(rs.getString("color"));
                data.setStyle(rs.getString("style"));
                data.setVariant(rs.getString("variant"));
                data.setSaddle(rs.getBoolean("saddle"));
                data.setArmor(rs.getString("armor"));
                data.setCustomName(rs.getString("customName"));
                data.setJumpStrength(rs.getDouble("jumpStrength"));
                plugin.getLogger().info("Retrieved mount with UUID: " + uuid);
                return data;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("MySQL query error: " + e.getMessage());
        }
        return null;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("MySQL connection closed.");
            } catch (SQLException e) {
                plugin.getLogger().severe("MySQL close error: " + e.getMessage());
            }
        }
    }
}