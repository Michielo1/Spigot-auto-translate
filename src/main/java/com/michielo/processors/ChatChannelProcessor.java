package com.michielo.processors;

import com.michielo.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class ChatChannelProcessor {

    /*
        Singleton implementation
     */

    private static ChatChannelProcessor instance;
    public static ChatChannelProcessor getInstance() {
        if (instance == null) instance = new ChatChannelProcessor();
        return instance;
    }

    /*
        Class init
        We handle config values at class startup to avoid constantly calling config
     */

    private boolean localAndGlobalChat;
    private String GlobalChatPrefix;
    private int localRadius;
    private String localPrefix;
    private String globalPrefix;

    public ChatChannelProcessor() {
        FileConfiguration file = Main.getInstance().getConfig();
        this.localAndGlobalChat = file.getBoolean("LocalAndGlobalChat");
        this.GlobalChatPrefix = file.getString("GlobalChatPrefix");
        this.localRadius = file.getInt("LocalRadius");
        this.localPrefix = file.getString("Local_Prefix");
        this.globalPrefix = file.getString("Global_Prefix");
    }

    /*
        Main methods
     */

    public boolean usesSeperateChats() {
        return this.localAndGlobalChat;
    }

    // This method is a boolean that returns if the event was handled successfully
    public boolean handle(Player player, String message) {
        // should be redundant but I tend to be stupid so just checking
        if (!this.localAndGlobalChat) return false;

        if (message.startsWith(this.GlobalChatPrefix)) {
            // global chat
            String correctedMessage = replacePlaceholders(player.getDisplayName(), message, this.globalPrefix);
            Bukkit.broadcastMessage(correctedMessage);
        } else {
            // local chat (on main thread for the getNearbyEntities
            new BukkitRunnable() {
                @Override
                public void run() {
                    String correctedMessage = replacePlaceholders(player.getDisplayName(), message, localPrefix);
                    Collection<Entity> entityCollection = player.getWorld().getNearbyEntities(player.getLocation(),
                            localRadius, localRadius, localRadius);

                    for (Entity e : entityCollection) {
                        if (e instanceof Player p) {
                            p.sendMessage(correctedMessage);
                        }
                    }
                }
            }.runTask(Main.getInstance());
        }
        return true;
    }

    /*
        Util
     */

    private String replacePlaceholders(String playerName, String message, String original) {
        original = original.replace("%player%", playerName);
        original = original.replace("%message%", message);
        return ChatColor.translateAlternateColorCodes('&', original);
    }


}
