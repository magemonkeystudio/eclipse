package studio.magemonkey;

import org.bukkit.plugin.java.JavaPlugin;
import studio.magemonkey.commands.EclipseCommand;
import studio.magemonkey.listeners.MountListener;
import studio.magemonkey.listeners.MountSpawnListener;
import studio.magemonkey.service.MountService;

public class Eclipse extends JavaPlugin {

    private static Eclipse instance;
    private MountService mountService;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        mountService = new MountService();

        // Register listeners for mount spawn and despawn events
        getServer().getPluginManager().registerEvents(new MountSpawnListener(mountService), this);
        getServer().getPluginManager().registerEvents(new MountListener(mountService), this);

        // Register the unified "eclipse" command executor with a null-check to avoid NPE
        if (getCommand("eclipse") != null) {
            getCommand("eclipse").setExecutor(new EclipseCommand(this));
        } else {
            getLogger().warning("Command 'eclipse' not found in plugin.yml");
        }

        getLogger().info("Eclipse plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Eclipse plugin disabled.");
    }

    public static Eclipse getInstance() {
        return instance;
    }

    /**
     * This method is used externally (e.g. by command executors) to obtain the MountService.
     */
    public MountService getMountService() {
        return mountService;
    }
}
