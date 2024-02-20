package com.michielo;

import com.michielo.bukkit.command.CommandExecutor;
import com.michielo.bukkit.listener.PlayerChatListener;
import com.michielo.bukkit.listener.PlayerJoinListener;
import com.michielo.files.DataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    /*
        Singleton implementation
     */

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private DataManager dataManager;

    @Override
    public void onEnable() {

        // setting instance
        instance = this;

        // loading files
        this.saveDefaultConfig();
        this.dataManager = new DataManager(this);

        // registering listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        // register commands
        getCommand("language").setExecutor(new CommandExecutor());
    }

    @Override
    public void onDisable() {

    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

}