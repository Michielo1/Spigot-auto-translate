package com.michielo.chattypes;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface ChatMethod {

    public List<Player> getPlayers(Location base);
    public String formatText(String text, String sender);

}
