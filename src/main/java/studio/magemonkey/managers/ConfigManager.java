package studio.magemonkey.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import studio.magemonkey.Eclipse;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final Eclipse plugin;
    private File mountDataFile;
    private FileConfiguration mountDataConfig;
    private boolean mysqlEnabled;
    private ConfigurationSection mysqlConfig;

    public ConfigManager(Eclipse plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        loadYAMLConfig();
        loadMySQLConfig();
    }

    private void loadYAMLConfig() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().severe("Failed to create plugin data folder!");
        }
        mountDataFile = new File(dataFolder, "mounts.yml");
        try {
            if (!mountDataFile.exists() && !mountDataFile.createNewFile()) {
                plugin.getLogger().severe("Failed to create mounts.yml file!");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error creating mounts.yml: " + e.getMessage());
        }
        mountDataConfig = YamlConfiguration.loadConfiguration(mountDataFile);
    }

    private void loadMySQLConfig() {
        mysqlEnabled = plugin.getConfig().getBoolean("mysql.enabled", false);
        mysqlConfig = plugin.getConfig().getConfigurationSection("mysql");
        plugin.getLogger().info("MySQL enabled: " + mysqlEnabled);
    }

    public boolean isMysqlEnabled() {
        return mysqlEnabled;
    }

    public ConfigurationSection getMysqlConfig() {
        return mysqlConfig;
    }

    public FileConfiguration getMountDataConfig() {
        return mountDataConfig;
    }

    public File getMountDataFile() {
        return mountDataFile;
    }
}