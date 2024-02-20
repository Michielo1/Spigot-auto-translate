package com.michielo.bukkit.listener;

import com.michielo.processor.ChatDeliveryProcessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private ChatDeliveryProcessor cdp;

    public PlayerChatListener() {
        this.cdp = new ChatDeliveryProcessor();
    }

    // Lowest as we're delivering the message to the users
    // we should always have last dips because of this
    @EventHandler (priority = EventPriority.LOWEST)
    public void onChatMessage(AsyncPlayerChatEvent e) {
        // handle earlier cancels
        if (e.isCancelled()) return;

        // cancel and process
        e.setCancelled(true);
        cdp.deliverChat(e.getPlayer(), e.getMessage());
    }

}
