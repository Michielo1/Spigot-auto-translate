package com.michielo.chattypes.impl;

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
    public String formatText(String text, String sender) {
        return sender + " >> " + text;
    }

}
