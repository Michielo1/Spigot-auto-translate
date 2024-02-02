package com.michielo;

import com.michielo.bukkit.command.LanguageCommand;
import com.michielo.bukkit.listener.ChatListener;
import com.michielo.bukkit.listener.JoinListener;
import com.michielo.bukkit.listener.QuitListener;
import com.michielo.files.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    /*
        Modified singleton implementation
        Modified as the instance is only set in the onEnable method
     */

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    /*
        Variables
     */

    private DataManager dataManager;

    /*
        Overriding onenable & ondisable methods
     */

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Starting...");

        // set instance
        instance = this;

        // load files
        this.saveDefaultConfig();
        this.dataManager = new DataManager(this);

        // load listeners
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        // load commands
        getCommand("language").setExecutor(new LanguageCommand());

        Bukkit.getLogger().info("Started");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Closing...");

        Bukkit.getLogger().info("Closed");
    }

    /*
        Getters
     */

    public DataManager getData() {
        return this.dataManager;
    }

}