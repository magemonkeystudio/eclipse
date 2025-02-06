package studio.magemonkey.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Bukkit;
import studio.magemonkey.managers.ConfigManager;
import studio.magemonkey.model.MountData;
import studio.magemonkey.service.MountService;
import studio.magemonkey.Eclipse;

public class MountSpawnListener implements Listener {
    private final MountService mountService;
    private final Eclipse plugin;

    public MountSpawnListener(MountService mountService, Eclipse plugin) {
        this.mountService = mountService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseMountItem(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasCustomModelData()) return;

        Material expectedMaterial = Material.valueOf(ConfigManager.getMountItemMaterial().toUpperCase());
        int expectedModelData = ConfigManager.getMountItemCustomModelData();

        if (item.getType() != expectedMaterial || item.getItemMeta().getCustomModelData() != expectedModelData) return;

        String mountId = item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(plugin, "mountId"),
                PersistentDataType.STRING
        );

        if (mountId == null || mountId.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No mount data found on this item.");
            return;
        }

        MountData mountData = mountService.getMountData(mountId, player.getUniqueId().toString());
        if (mountData == null) {
            player.sendMessage(ChatColor.RED + "No mount data found for ID: " + mountId);
            return;
        }

        Horse horse = player.getWorld().spawn(player.getLocation(), Horse.class);
        horse.setTamed(true);
        horse.setOwner(player);
        horse.setColor(mountData.getColor());
        horse.setStyle(mountData.getStyle());
        horse.setJumpStrength(mountData.getJumpStrength());
        horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mountData.getSpeed());

        if (mountData.getCustomName() != null) {
            horse.setCustomName(mountData.getCustomName());
        }

        if (item.getAmount() <= 1) {
            player.getInventory().remove(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            HorseInventory horseInv = horse.getInventory();
            if (mountData.getSaddle() != null) {
                try {
                    horseInv.setSaddle(new ItemStack(Material.SADDLE));
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to set horse saddle: " + e.getMessage());
                    horse.getWorld().dropItemNaturally(horse.getLocation(), new ItemStack(Material.SADDLE));
                }
            }

            String armorType = mountData.getArmorType();
            if (armorType != null && !"NONE".equals(armorType)) {
                try {
                    Material armorMaterial = Material.valueOf(armorType);
                    horseInv.setArmor(new ItemStack(armorMaterial));
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to set horse armor: " + e.getMessage());
                    horse.getWorld().dropItemNaturally(horse.getLocation(), new ItemStack(Material.valueOf(armorType)));
                }
            }
        }, 2L);

        mountService.deleteMountData(mountData);
        player.sendMessage(ChatColor.GREEN + "Mount spawned successfully!");
        event.setCancelled(true);
    }
}