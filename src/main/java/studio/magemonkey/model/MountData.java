package studio.magemonkey.model;

public class MountData {

    private final String horseId;      // Unique ID of the horse
    private final String playerUUID;   // Player's UUID
    private boolean spawned;           // Whether the horse is currently spawned

    public MountData(String horseId, String playerUUID, boolean spawned) {
        this.horseId = horseId;
        this.playerUUID = playerUUID;
        this.spawned = spawned;
    }

    public String getHorseId() {
        return horseId;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }

    @Override
    public String toString() {
        return "MountData{" +
                "horseId='" + horseId + '\'' +
                ", playerUUID='" + playerUUID + '\'' +
                ", spawned=" + spawned +
                '}';
    }
}
