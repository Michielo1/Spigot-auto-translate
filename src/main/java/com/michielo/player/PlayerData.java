package com.michielo.player;

import com.michielo.Main;
import com.michielo.translation.Language;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerData {

    /*
        Variables
     */

    private Player player;
    private boolean hasChosenLanguage;
    private String language;

    /*
        Handlers
     */

    public void setLanguage(String language) {
        this.language = language;
        this.hasChosenLanguage = true;
        Main.getInstance().getData().getConfig().set(getPlayer().getUniqueId().toString(), language);
        Main.getInstance().getData().saveConfig();
    }

    /*
        Getters
     */

    public Player getPlayer() {
        return this.player;
    }

    public boolean hasChosenLanguage() {
        return this.hasChosenLanguage;
    }

    public String getLanguage() {
        if (this.language == null) return null;
        return Language.getAbbreviation(this.language.toUpperCase());
    }

    public PlayerData(Player player) {
        this.player = player;

        FileConfiguration file = Main.getInstance().getData().getConfig();
        if (file.getString(player.getUniqueId().toString()) != null) {
            this.hasChosenLanguage = true;
            this.language = file.getString(player.getUniqueId().toString());
        } else {
            this.hasChosenLanguage = false;
        }
    }

}
