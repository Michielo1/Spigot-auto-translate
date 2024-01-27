package com.michielo.bukkit.listener;

import com.michielo.processors.ChatChannelProcessor;
import com.michielo.processors.ChatTranslatorProcessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {

    // Using MONITOR eventpriority?!??!?!?!
    // If the translator is enabled, we *need* first call on the chatmessage to translate it
    @EventHandler (priority = EventPriority.MONITOR)
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        if (ChatTranslatorProcessor.getInstance().enabledTranslations()) {
            e.setCancelled(true);
            ChatTranslatorProcessor.getInstance().handleTranslation(e.getPlayer(), e.getMessage());
        }
    }

}
