package com.michielo.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PrefixUtil {

    private boolean isEnabled;

    /*
        Singleton implementation
     */

    private static PrefixUtil instance;
    public static PrefixUtil getInstance() {
        if (instance == null) instance = new PrefixUtil();
        return instance;
    }

    private PrefixUtil() {
        isEnabled = false;
        Plugin luckPermsPlugin = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");

        if (luckPermsPlugin == null || !luckPermsPlugin.isEnabled()) {
            Bukkit.getLogger().severe("Attempted to fetch prefix but Luckperms hasnt been found!");
            return;
        }
        if (!setupLuckPerms()) {
            Bukkit.getLogger().severe("Attempted to fetch prefix but Luckperms hasnt been found!");
        }
        isEnabled = true;
    }

    /*
        Main stuff
     */

    private LuckPerms luckPerms;

    // Method to get the prefix of a player
    public String getPlayerPrefix(Player player) {
        if (!isEnabled) return "";
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());

        if (user != null) {
            QueryOptions queryOptions = QueryOptions.builder(QueryMode.CONTEXTUAL).build();
            return user.getCachedData().getMetaData(queryOptions).getPrefix();
        }

        return "";
    }

    /*
        Util
     */

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> serviceProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (serviceProvider != null) {
            luckPerms = serviceProvider.getProvider();
            return true;
        }
        return false;
    }


}
