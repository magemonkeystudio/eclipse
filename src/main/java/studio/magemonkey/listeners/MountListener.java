package studio.magemonkey.listeners;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import studio.magemonkey.model.MountData;
import studio.magemonkey.service.MountService;

public class MountListener implements Listener {

    private final MountService mountService;

    public MountListener(MountService mountService) {
        this.mountService = mountService;
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player player && event.getVehicle() instanceof Horse horse) {
            MountData data = new MountData(horse.getUniqueId().toString(), player.getUniqueId().toString(), true);
            mountService.saveMountData(data);
            player.sendMessage("Mount despawned; mount data re-added to the database.");
        }
    }
}
