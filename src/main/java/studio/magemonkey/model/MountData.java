// MountData.java
package studio.magemonkey.model;

import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

public class MountData {
    private final String horseId;
    private final String playerUUID;
    private boolean spawned;
    private Horse.Color color;
    private Horse.Style style;
    private double jumpStrength;
    private double speed;
    private String customName;
    private ItemStack saddle;
    private String armorType; // Changed to store material name

    public MountData(String horseId, String playerUUID, boolean spawned, Horse.Color color,
                     Horse.Style style, double jumpStrength, double speed, String customName,
                     ItemStack saddle, String armorType) {
        this.horseId = horseId;
        this.playerUUID = playerUUID;
        this.spawned = spawned;
        this.color = color;
        this.style = style;
        this.jumpStrength = jumpStrength;
        this.speed = speed;
        this.customName = customName;
        this.saddle = saddle;
        this.armorType = armorType;
    }

    public String getHorseId() { return horseId; }
    public String getPlayerUUID() { return playerUUID; }
    public boolean isSpawned() { return spawned; }
    public void setSpawned(boolean spawned) { this.spawned = spawned; }
    public Horse.Color getColor() { return color; }
    public Horse.Style getStyle() { return style; }
    public double getJumpStrength() { return jumpStrength; }
    public double getSpeed() { return speed; }
    public String getCustomName() { return customName; }
    public ItemStack getSaddle() { return saddle; }
    public String getArmorType() { return armorType; }
}