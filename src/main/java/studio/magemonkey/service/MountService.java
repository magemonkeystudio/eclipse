// MountService.java
package studio.magemonkey.service;

import studio.magemonkey.model.MountData;
import studio.magemonkey.managers.MySQLManager;

public class MountService {
    public void saveMountData(MountData data) {
        MySQLManager.getInstance().saveMountData(data);
    }

    public void deleteMountData(MountData data) {
        MySQLManager.getInstance().deleteMountData(data);
    }

    public MountData getMountData(String horseId, String playerUUID) {
        return MySQLManager.getInstance().getMountData(horseId, playerUUID);
    }

    public void closeConnections() {
        MySQLManager.getInstance().closeConnection();
    }
}