package com.michielo.processors;

import com.michielo.Main;
import com.michielo.player.PlayerData;
import com.michielo.player.PlayerDataManager;
import com.michielo.util.PrefixUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
            String correctedMessage = replacePlaceholders(player, message, this.globalPrefix);
            Bukkit.broadcastMessage(correctedMessage);
        } else {
            // local chat (on main thread for the getNearbyEntities
            new BukkitRunnable() {
                @Override
                public void run() {
                    String correctedMessage = replacePlaceholders(player, message, localPrefix);
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

    public List<Object> handleInternal(String message, Player sender) throws ExecutionException, InterruptedException {
        // this method returns [0] the list of players to send the message to
        // and [1] the adjusted method

        // should be redundant but I tend to be stupid so just checking
        if (!this.localAndGlobalChat) return null;

        if (message.startsWith(this.GlobalChatPrefix)) {
            String correctedMessage = replacePlaceholders(sender, message, this.globalPrefix);
            List<Object> returnList = new ArrayList<>();
            returnList.add(PlayerDataManager.getInstance().getAllPlayerData());
            returnList.add(correctedMessage);
            return returnList;
        } else {
            CompletableFuture<List<Object>> future = new CompletableFuture<>();
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<PlayerData> playerDataList = new ArrayList<>();
                    Collection<Entity> entityCollection = sender.getWorld().getNearbyEntities(sender.getLocation(),
                            localRadius, localRadius, localRadius);

                    for (Entity e : entityCollection) {
                        if (e instanceof Player p) {
                            playerDataList.add(PlayerDataManager.getInstance().getPlayerData(p));
                        }
                    }

                    String correctedMessage = replacePlaceholders(sender, message, localPrefix);
                    List<Object> returnList = new ArrayList<>();
                    returnList.add(playerDataList);
                    returnList.add(correctedMessage);

                    // Complete the CompletableFuture with the result
                    future.complete(returnList);
                }
            }.runTask(Main.getInstance());
            return future.get();
        }
    }

    /*
        Util
     */

    private String replacePlaceholders(Player player, String message, String original) {
        original = original.replace("%player%", player.getDisplayName());
        original = original.replace("%message%", message);
        if (PrefixUtil.getInstance().getPlayerPrefix(player) != null) {
            original = original.replace("%prefix%", PrefixUtil.getInstance().getPlayerPrefix(player));
        } else {
            original = original.replace("%prefix%", "");
        }
        return ChatColor.translateAlternateColorCodes('&', original);
    }


}
