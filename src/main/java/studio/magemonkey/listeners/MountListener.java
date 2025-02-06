// MountListener.java
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
import org.bukkit.inventory.HorseInventory;
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
        if (!(event.getExited() instanceof Player player) || !(event.getVehicle() instanceof Horse horse)) {
            return;
        }

        if (!horse.isTamed() || horse.getOwner() == null || !horse.getOwner().equals(player)) {
            return;
        }

        HorseInventory horseInv = horse.getInventory();
        String armorType = horseInv.getArmor() != null ? horseInv.getArmor().getType().name() : "NONE";

        MountData data = new MountData(
                horse.getUniqueId().toString(),
                player.getUniqueId().toString(),
                false,
                horse.getColor(),
                horse.getStyle(),
                horse.getJumpStrength(),
                horse.getAttribute(org.bukkit.attribute.Attribute.MOVEMENT_SPEED).getBaseValue(),
                horse.getCustomName(),
                horseInv.getSaddle(),
                armorType
        );
        mountService.saveMountData(data);

        horse.remove();

        Material mountMaterial = Material.valueOf(ConfigManager.getMountItemMaterial().toUpperCase());
        ItemStack mountItem = new ItemStack(mountMaterial);
        ItemMeta meta = mountItem.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(ConfigManager.getMountItemCustomModelData());
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getMountItemDisplayName()));
            List<String> lore = ConfigManager.getMountItemLore();
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }
            meta.setLore(lore);
            mountItem.setItemMeta(meta);
        }

        player.getInventory().addItem(mountItem);
        player.sendMessage(ChatColor.GREEN + "Mount despawned and item returned to your inventory.");
    }
}