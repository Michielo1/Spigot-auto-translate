package com.michielo.chattypes.impl;

import com.michielo.Main;
import com.michielo.chattypes.ChatMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DefaultMethod implements ChatMethod {


    @Override
    public List<Player> getPlayers(Location base) {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public String formatText(String text, Player sender) {
        String builder = Main.getInstance().getConfig().getString("chat_format");
        if (sender != null) builder = builder.replaceAll("%name%", sender.getDisplayName());
        if (text != null) builder = builder.replaceAll("%message%", text);
        return builder;
    }

}
