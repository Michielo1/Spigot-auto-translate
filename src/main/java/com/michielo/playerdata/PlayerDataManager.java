package com.michielo.playerdata;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {

    /*
        Singleton
     */

    private static PlayerDataManager instance;
    public static PlayerDataManager getInstance() {
        if (instance == null) instance = new PlayerDataManager();
        return instance;
    }

    /*
        Actual methods
     */

    private static HashMap<UUID, PlayerData> playerDataMap = new HashMap<UUID, PlayerData>();

    public PlayerData getPlayer(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public void registerPlayer(UUID uuid) {
        playerDataMap.put(uuid, new PlayerData(uuid));
    }


}
