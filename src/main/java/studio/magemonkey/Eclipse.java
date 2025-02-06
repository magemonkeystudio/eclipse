// Eclipse.java
package studio.magemonkey;

import org.bukkit.plugin.java.JavaPlugin;
import studio.magemonkey.commands.EclipseCommand;
import studio.magemonkey.listeners.MountListener;
import studio.magemonkey.listeners.MountSpawnListener;
import studio.magemonkey.service.MountService;
import org.bukkit.NamespacedKey;

public class Eclipse extends JavaPlugin {
    private static Eclipse instance;
    private MountService mountService;
    private NamespacedKey mountIdKey;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        mountService = new MountService();
        mountIdKey = new NamespacedKey(this, "mountId");

        getServer().getPluginManager().registerEvents(new MountSpawnListener(mountService, this), this);
        getServer().getPluginManager().registerEvents(new MountListener(mountService), this);

        if (getCommand("eclipse") != null) {
            getCommand("eclipse").setExecutor(new EclipseCommand(this));
        } else {
            getLogger().warning("Command 'eclipse' not found in plugin.yml");
        }

        getLogger().info("Eclipse plugin enabled.");
    }

    @Override
    public void onDisable() {
        if (mountService != null) {
            mountService.closeConnections();
        }
        getLogger().info("Eclipse plugin disabled.");
    }

    public static Eclipse getInstance() {
        return instance;
    }

    public MountService getMountService() {
        return mountService;
    }

    public NamespacedKey getMountIdKey() {
        return mountIdKey;
    }
}