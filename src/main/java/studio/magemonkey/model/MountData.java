package studio.magemonkey.model;

import org.bukkit.configuration.file.FileConfiguration;

public class MountData {
    private String entityType;
    private String color;
    private String style;
    private String variant;
    private boolean saddle;
    private String armor;
    private String customName;
    private double jumpStrength;

    // Getters
    public String getEntityType() { return entityType; }
    public String getColor() { return color; }
    public String getStyle() { return style; }
    public String getVariant() { return variant; }
    public boolean hasSaddle() { return saddle; }
    public String getArmor() { return armor; }
    public String getCustomName() { return customName; }
    public double getJumpStrength() { return jumpStrength; }

    // Setters
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setColor(String color) { this.color = color; }
    public void setStyle(String style) { this.style = style; }
    public void setVariant(String variant) { this.variant = variant; }
    public void setSaddle(boolean saddle) { this.saddle = saddle; }
    public void setArmor(String armor) { this.armor = armor; }
    public void setCustomName(String customName) { this.customName = customName; }
    public void setJumpStrength(double jumpStrength) { this.jumpStrength = jumpStrength; }

    public static MountData fromYaml(FileConfiguration config, String uuid) {
        if (!config.contains(uuid)) return null;
        MountData data = new MountData();
        data.entityType = config.getString(uuid + ".entityType");
        data.color = config.getString(uuid + ".horse.color");
        data.style = config.getString(uuid + ".horse.style");
        data.variant = config.getString(uuid + ".horse.variant");
        data.saddle = config.getBoolean(uuid + ".horse.saddle", false);
        data.armor = config.getString(uuid + ".horse.armor", "NONE");
        data.customName = config.getString(uuid + ".horse.customName");
        data.jumpStrength = config.getDouble(uuid + ".horse.jumpStrength", 0.7);
        return data;
    }
}