package studio.magemonkey;

import org.bukkit.plugin.java.JavaPlugin;
import studio.magemonkey.listeners.MountListener;
import studio.magemonkey.managers.ConfigManager;
import studio.magemonkey.managers.MySQLManager;
import studio.magemonkey.commands.Command;

public class Eclipse extends JavaPlugin {
    private ConfigManager configManager;
    private MySQLManager mysqlManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        if (configManager.isMysqlEnabled()) {
            mysqlManager = new MySQLManager(this, configManager.getMysqlConfig());
            mysqlManager.initialize();
        }

        getCommand("eclipse").setExecutor(new Command(this));
        getServer().getPluginManager().registerEvents(new MountListener(this), this);
        getLogger().info("Eclipse enabled.");
    }

    @Override
    public void onDisable() {
        if (mysqlManager != null) {
            mysqlManager.close();
        }
        getLogger().info("Eclipse disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MySQLManager getMySQLManager() {
        return mysqlManager;
    }
}