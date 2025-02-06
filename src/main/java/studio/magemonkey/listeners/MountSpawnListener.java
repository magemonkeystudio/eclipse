package studio.magemonkey.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.managers.ConfigManager;
import studio.magemonkey.model.MountData;
import studio.magemonkey.service.MountService;

public class MountSpawnListener implements Listener {

    private final MountService mountService;

    public MountSpawnListener(MountService mountService) {
        this.mountService = mountService;
    }

    @EventHandler
    public void onPlayerUseMountItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return;

        Material expectedMaterial = Material.valueOf(ConfigManager.getMountItemMaterial().toUpperCase());
        int expectedModelData = ConfigManager.getMountItemCustomModelData();
        if (item.getType() == expectedMaterial && meta.getCustomModelData() == expectedModelData) {
            // Spawn the horse using the configured spawn logic.
            Horse spawnedHorse = player.getWorld().spawn(player.getLocation(), Horse.class);
            MountData data = new MountData(spawnedHorse.getUniqueId().toString(), player.getUniqueId().toString(), true);
            // Delete mount data from the database (since the mount is now active).
            mountService.deleteMountData(data);
            player.sendMessage("Mount spawned; mount data removed from the database.");
        }
    }
}
