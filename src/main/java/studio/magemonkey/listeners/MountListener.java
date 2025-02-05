package studio.magemonkey.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import studio.magemonkey.Eclipse;
import studio.magemonkey.service.MountService;

public class MountListener implements Listener {
    private final Eclipse plugin;
    private final MountService mountService;

    public MountListener(Eclipse plugin) {
        this.plugin = plugin;
        this.mountService = new MountService(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT_CLICK"))
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasDisplayName())
            return;

        String configDisplayName = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("mountItem.displayName", "&6Mount Figurance"));
        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (!displayName.equals(ChatColor.stripColor(configDisplayName)))
            return;

        String uuid = item.getItemMeta().getPersistentDataContainer()
                .get(mountService.getMountIdKey(), PersistentDataType.STRING);

        if (uuid == null || uuid.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No mount data found on this item.");
            return;
        }

        mountService.retrieveMount(player, uuid, item);
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player player)) return;
        if (!(event.getVehicle() instanceof Horse horse)) return;
        if (!horse.isTamed()) return;

        mountService.saveMount(player, horse);
    }
}