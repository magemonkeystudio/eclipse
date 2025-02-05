package studio.magemonkey.service;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import studio.magemonkey.Eclipse;
import studio.magemonkey.model.MountData;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MountService {
    private final Eclipse plugin;
    private final NamespacedKey mountIdKey;

    public MountService(Eclipse plugin) {
        this.plugin = plugin;
        this.mountIdKey = new NamespacedKey(plugin, "mountId");
    }

    public void saveMountFromPlayer(Player player) {
        Entity mount = player.getVehicle();
        if (mount == null) {
            player.sendMessage(ChatColor.RED + "You are not riding any mount.");
            return;
        }
        if (!(mount instanceof Horse)) {
            player.sendMessage(ChatColor.RED + "Only horses can be saved automatically.");
            return;
        }
        saveMount(player, (Horse) mount);
    }

    public void saveMount(Player player, Horse horse) {
        if (!horse.isTamed()) return;

        String uuid = UUID.randomUUID().toString();
        MountData data = createMountData(horse);

        if (plugin.getConfigManager().isMysqlEnabled()) {
            plugin.getMySQLManager().insertMount(uuid, data);
        } else {
            saveToYaml(uuid, data);
        }

        horse.remove();
        ItemStack mountItem = buildMountItem(uuid);
        player.getInventory().addItem(mountItem);
        player.sendMessage(ChatColor.GREEN + "Your mount has been saved as a configurable item.");
    }

    private MountData createMountData(Horse horse) {
        MountData data = new MountData();
        data.setEntityType(horse.getType().name());
        data.setColor(horse.getColor().name());
        data.setStyle(horse.getStyle().name());
        data.setSaddle(horse.getInventory().getSaddle() != null &&
                horse.getInventory().getSaddle().getType() == Material.SADDLE);
        data.setArmor((horse.getInventory().getArmor() != null &&
                horse.getInventory().getArmor().getType() != Material.AIR) ?
                horse.getInventory().getArmor().getType().name() : "NONE");
        data.setCustomName(horse.getCustomName());
        data.setJumpStrength(horse.getJumpStrength());
        return data;
    }

    private void saveToYaml(String uuid, MountData data) {
        var config = plugin.getConfigManager().getMountDataConfig();
        var file = plugin.getConfigManager().getMountDataFile();

        config.set(uuid + ".entityType", data.getEntityType());
        config.set(uuid + ".horse.color", data.getColor());
        config.set(uuid + ".horse.style", data.getStyle());
        config.set(uuid + ".horse.saddle", data.hasSaddle());
        config.set(uuid + ".horse.armor", data.getArmor());
        if (data.getCustomName() != null) {
            config.set(uuid + ".horse.customName", data.getCustomName());
        }
        config.set(uuid + ".horse.jumpStrength", data.getJumpStrength());

        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save mount data: " + e.getMessage());
        }
    }

    public ItemStack buildMountItem(String uuid) {
        var config = plugin.getConfig();
        Material material = Material.valueOf(
                config.getString("mountItem.material", "PAPER").toUpperCase()
        );

        int customModelData = config.getInt("mountItem.customModelData", 0);
        String displayName = ChatColor.translateAlternateColorCodes('&',
                config.getString("mountItem.displayName", "&6Mount Figurance")
        );

        List<String> loreList = config.getStringList("mountItem.lore");
        if (loreList.isEmpty()) {
            loreList = Collections.singletonList("&7Right-click to retrieve this mount");
        }

        loreList.replaceAll(str -> ChatColor.translateAlternateColorCodes('&', str));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(loreList);
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            meta.getPersistentDataContainer().set(mountIdKey, PersistentDataType.STRING, uuid);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void retrieveMount(Player player, String uuid, ItemStack item) {
        plugin.getLogger().info("Retrieving mount with UUID: " + uuid);
        MountData mountData = plugin.getConfigManager().isMysqlEnabled() ?
                plugin.getMySQLManager().getMount(uuid) :
                MountData.fromYaml(plugin.getConfigManager().getMountDataConfig(), uuid);

        if (mountData == null) {
            player.sendMessage(ChatColor.RED + "No mount data found for ID: " + uuid);
            return;
        }

        spawnMount(player, mountData, item);
    }

    private void spawnMount(Player player, MountData mountData, ItemStack item) {
        try {
            EntityType entityType = EntityType.valueOf(mountData.getEntityType());
            Entity spawned = player.getWorld().spawnEntity(player.getLocation(), entityType);

            if (spawned instanceof Tameable tameable) {
                tameable.setOwner(player);
            }

            if (spawned instanceof Horse horse) {
                horse.setColor(Horse.Color.valueOf(mountData.getColor()));
                horse.setStyle(Horse.Style.valueOf(mountData.getStyle()));

                String customName = mountData.getCustomName();
                if (customName != null && !customName.isEmpty()) {
                    horse.setCustomName(customName);
                }

                horse.setJumpStrength(mountData.getJumpStrength());

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (mountData.hasSaddle()) {
                        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                    }
                    if (!"NONE".equals(mountData.getArmor())) {
                        horse.getInventory().setArmor(new ItemStack(Material.valueOf(mountData.getArmor())));
                    }
                }, 2L);
            }

            player.sendMessage(ChatColor.GREEN + "Mount retrieved successfully!");

            if (item.getAmount() <= 1) {
                player.getInventory().remove(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid mount type.");
        }
    }

    public NamespacedKey getMountIdKey() {
        return mountIdKey;
    }
}