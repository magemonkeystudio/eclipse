package studio.magemonkey.service;

import studio.magemonkey.model.MountData;
import studio.magemonkey.managers.MySQLManager;

public class MountService {

    /**
     * Saves mount data to the database (called when the mount is despawned).
     */
    public void saveMountData(MountData data) {
        data.setSpawned(true);
        MySQLManager.getInstance().saveMountData(data);
    }

    /**
     * Deletes mount data from the database (called when the mount is spawned).
     */
    public void deleteMountData(MountData data) {
        MySQLManager.getInstance().deleteMountData(data);
    }
}
