// MySQLManager.java
package studio.magemonkey.managers;

import studio.magemonkey.model.MountData;
import java.sql.*;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

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
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS mounts (" +
                "horse_id VARCHAR(36) NOT NULL, " +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "spawned BOOLEAN NOT NULL, " +
                "color VARCHAR(32), " +
                "style VARCHAR(32), " +
                "jump_strength DOUBLE, " +
                "speed DOUBLE, " +
                "custom_name VARCHAR(64), " +
                "saddle TEXT, " +
                "armor VARCHAR(50), " + // Changed to VARCHAR for material name
                "PRIMARY KEY (horse_id, player_uuid)" +
                ");";
        connection.createStatement().executeUpdate(createTable);
    }

    public static MySQLManager getInstance() {
        if (instance == null) {
            instance = new MySQLManager();
        }
        return instance;
    }

    public void saveMountData(MountData data) {
        try {
            String query = "INSERT INTO mounts (horse_id, player_uuid, spawned, color, style, jump_strength, speed, custom_name, saddle, armor) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE spawned = ?, color = ?, style = ?, jump_strength = ?, speed = ?, custom_name = ?, saddle = ?, armor = ?";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, data.getHorseId());
            ps.setString(2, data.getPlayerUUID());
            ps.setBoolean(3, data.isSpawned());
            ps.setString(4, data.getColor().name());
            ps.setString(5, data.getStyle().name());
            ps.setDouble(6, data.getJumpStrength());
            ps.setDouble(7, data.getSpeed());
            ps.setString(8, data.getCustomName());
            ps.setString(9, serializeItemStack(data.getSaddle()));
            ps.setString(10, data.getArmorType());

            ps.setBoolean(11, data.isSpawned());
            ps.setString(12, data.getColor().name());
            ps.setString(13, data.getStyle().name());
            ps.setDouble(14, data.getJumpStrength());
            ps.setDouble(15, data.getSpeed());
            ps.setString(16, data.getCustomName());
            ps.setString(17, serializeItemStack(data.getSaddle()));
            ps.setString(18, data.getArmorType());

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

    public MountData getMountData(String horseId, String playerUUID) {
        try {
            String query = "SELECT * FROM mounts WHERE horse_id = ? AND player_uuid = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, horseId);
            ps.setString(2, playerUUID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new MountData(
                        rs.getString("horse_id"),
                        rs.getString("player_uuid"),
                        rs.getBoolean("spawned"),
                        Horse.Color.valueOf(rs.getString("color")),
                        Horse.Style.valueOf(rs.getString("style")),
                        rs.getDouble("jump_strength"),
                        rs.getDouble("speed"),
                        rs.getString("custom_name"),
                        deserializeItemStack(rs.getString("saddle")),
                        rs.getString("armor")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String serializeItemStack(ItemStack item) {
        try {
            if (item == null) return "";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private ItemStack deserializeItemStack(String data) {
        try {
            if (data == null || data.isEmpty()) return null;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}