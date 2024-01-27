package com.michielo.bukkit.listener;

import com.michielo.player.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerDataManager.getInstance().registerPlayer(e.getPlayer());
    }

}
