package studio.magemonkey.managers;

import org.bukkit.configuration.file.FileConfiguration;
import studio.magemonkey.Eclipse;
import java.util.List;

public class ConfigManager {

    private static final FileConfiguration config = Eclipse.getInstance().getConfig();

    public static String getMySQLHost() {
        return config.getString("mysql.host", "localhost");
    }

    public static int getMySQLPort() {
        return config.getInt("mysql.port", 3306);
    }

    public static String getMySQLDatabase() {
        return config.getString("mysql.database", "horse_data");
    }

    public static String getMySQLUsername() {
        return config.getString("mysql.username", "root");
    }

    public static String getMySQLPassword() {
        return config.getString("mysql.password", "password");
    }

    public static boolean useSSL() {
        return config.getBoolean("mysql.useSSL", false);
    }

    public static boolean allowPublicKeyRetrieval() {
        return config.getBoolean("mysql.allowPublicKeyRetrieval", false);
    }

    // Mount item configuration getters
    public static String getMountItemMaterial() {
        return config.getString("mountItem.material", "PAPER");
    }

    public static int getMountItemCustomModelData() {
        return config.getInt("mountItem.customModelData", 500);
    }

    public static String getMountItemDisplayName() {
        return config.getString("mountItem.displayName", "&6Mount Figurance");
    }

    public static List<String> getMountItemLore() {
        return config.getStringList("mountItem.lore");
    }
}
