package studio.magemonkey.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.managers.ConfigManager;
import studio.magemonkey.model.MountData;
import studio.magemonkey.service.MountService;
import java.util.List;

public class MountListener implements Listener {

    private final MountService mountService;

    public MountListener(MountService mountService) {
        this.mountService = mountService;
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player player && event.getVehicle() instanceof Horse horse) {
            // Only proceed if the horse is tamed and the player is its owner.
            if (!horse.isTamed() || horse.getOwner() == null || !horse.getOwner().equals(player)) {
                return;
            }
            // Re-add mount data to the database (indicating the mount is now despawned)
            MountData data = new MountData(horse.getUniqueId().toString(), player.getUniqueId().toString(), true);
            mountService.saveMountData(data);
            player.sendMessage(ChatColor.GREEN + "Mount despawned; mount data re-added to the database.");

            // Despawn the horse
            horse.remove();

            // Create the mount item from configuration
            String materialStr = ConfigManager.getMountItemMaterial();
            Material mountMaterial = Material.valueOf(materialStr.toUpperCase());
            ItemStack mountItem = new ItemStack(mountMaterial);
            ItemMeta meta = mountItem.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(ConfigManager.getMountItemCustomModelData());
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getMountItemDisplayName()));
                List<String> lore = ConfigManager.getMountItemLore();
                meta.setLore(lore);
                mountItem.setItemMeta(meta);
            }
            // Add the mount item to the player's inventory
            player.getInventory().addItem(mountItem);
            player.sendMessage(ChatColor.GREEN + "Mount item added to your inventory.");
        }
    }
}
