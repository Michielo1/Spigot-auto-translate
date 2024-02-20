package com.michielo.bukkit.listener;

import com.michielo.playerdata.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerDataManager.getInstance().registerPlayer(e.getPlayer().getUniqueId());
    }

}
