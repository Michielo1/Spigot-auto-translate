package com.michielo.playerdata;

import com.michielo.Language;
import com.michielo.Main;
import com.michielo.api.KauriAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String language;

    public UUID getUuid() {
        return uuid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        String old_language = Main.getInstance().getDataManager().getConfig().getString(player.getUniqueId().toString());
        if (old_language != null) {
            this.language = old_language;
            return;
        }

        if (Main.getInstance().getConfig().getBoolean("auto_language")) {
            KauriAPI ka = new KauriAPI();
            String ka_language = ka.getCountry(Bukkit.getPlayer(uuid));
            if (Language.getAbbreviation(ka_language) == null) {
                this.language = Main.getInstance().getConfig().getString("default_language");
            } else {
                this.language = ka_language;
            }
            player.sendMessage(ChatColor.BOLD + "Your language has been set to: '" + getLanguage() + "'. Change this by using '/language <language>'.");
        }
    }

}
