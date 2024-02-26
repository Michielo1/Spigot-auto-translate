package com.michielo.chattypes.impl;

import com.michielo.Main;
import com.michielo.api.LuckpermsUtil;
import com.michielo.chattypes.ChatMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LuckpermsRangedMethod implements ChatMethod {

    private int radius;

    public LuckpermsRangedMethod() {
        this.radius = Main.getInstance().getConfig().getInt("chat_radius");
    }

    @Override
    public List<Player> getPlayers(Location base) {
        Collection<Entity> entities = base.getWorld().getNearbyEntities(base, this.radius, this.radius, this.radius);
        ArrayList<Player> players = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof Player p) {
                players.add(p);
            }
        }

        return players;
    }

    @Override
    public String formatText(String text, Player sender) {

        String prefix = "";
        String suffix = "";

        if (LuckpermsUtil.getInstance().isEnabled()) {
            prefix = LuckpermsUtil.getInstance().getPlayerPrefix(sender);
        }

        String builder = Main.getInstance().getConfig().getString("chat_format");
        builder = builder.replaceAll("%name%", sender.getDisplayName());
        builder = builder.replaceAll("%message%", text);
        builder = builder.replaceAll("%prefix%", prefix);
        builder = builder.replaceAll("%suffix%", suffix);

        return builder;
    }


}
