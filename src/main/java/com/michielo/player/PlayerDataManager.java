package com.michielo.player;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {

    /*
        Singleton implementation
     */

    private static PlayerDataManager instance;
    public static PlayerDataManager getInstance() {
        if (instance == null) instance = new PlayerDataManager();
        return instance;
    }

    private PlayerDataManager() {
        playerDataMap = new HashMap<>();
    }

    /*
        Map
     */

    private static HashMap<UUID, PlayerData> playerDataMap;

    /*
        Handlers
     */

    public void registerPlayer(Player player) {
        playerDataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public void removePlayer(Player player) {
        playerDataMap.put(player.getUniqueId(), null);
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public List<PlayerData> getAllPlayerData() {
        List<PlayerData> list = new ArrayList<>();
        for (UUID uuid : playerDataMap.keySet()) {
            list.add(getPlayerData(uuid));
        }
        return list;
    }

}
