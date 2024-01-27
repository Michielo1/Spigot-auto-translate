package com.michielo.processors;

import com.michielo.Main;
import com.michielo.player.PlayerData;
import com.michielo.player.PlayerDataManager;
import com.michielo.translation.Translator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ChatTranslatorProcessor {

    /*
        Singleton implementation
     */

    private static ChatTranslatorProcessor instance;
    public static ChatTranslatorProcessor getInstance() {
        if (instance == null) instance = new ChatTranslatorProcessor();
        return instance;
    }

    /*
        Class init
        We handle config values at class startup to avoid constantly calling config
     */

    private boolean enableTranslations;
    private boolean allowFreeLanguageChoice;
    private boolean forceLanguage;
    private String forcedLanguage;
    private Translator translator;

    public ChatTranslatorProcessor() {
        FileConfiguration file = Main.getInstance().getConfig();
        this.enableTranslations = file.getBoolean("EnableTranslations");
        this.allowFreeLanguageChoice = file.getBoolean("AllowFreeLanguageChoice");
        this.forceLanguage = file.getBoolean("ForceLanguage");
        this.forcedLanguage = file.getString("ForcedLanguage");

        if (this.forceLanguage && this.allowFreeLanguageChoice) {
            Bukkit.getLogger().severe("You have enabled both free language choice and a forced language!");
            Bukkit.getLogger().severe("As this contradicts each other, we will allow free language choice and not force the language!");
            this.forceLanguage = false;
        }

        this.translator = new Translator();
    }

    /*
        Main methods
     */

    public boolean enabledTranslations() {
        return this.enableTranslations;
    }

    public void handleTranslation(Player sender, String original) {
        List<PlayerData> players = PlayerDataManager.getInstance().getAllPlayerData();

        for (PlayerData player : players) {
            if (!player.hasChosenLanguage() && player.getLanguage() == null) player.setLanguage("eng");
        }

        players.remove(PlayerDataManager.getInstance().getPlayerData(sender));

        sender.sendMessage(getFullMessage(sender.getDisplayName(), original));

        // send to all players in same language group without delay
        for (PlayerData player : players) {
            if (player.getLanguage().equals(original)) {
                player.getPlayer().sendMessage(getFullMessage(sender.getDisplayName(), original));
                players.remove(player);
            }
        }

        // Group players by language using Java Streams
        Map<String, List<PlayerData>> playersByLanguage = players.stream()
                .collect(Collectors.groupingBy(PlayerData::getLanguage));

        // Loop through each language and perform some operation
        for (String language : playersByLanguage.keySet()) {
            List<PlayerData> playersForLanguage = playersByLanguage.get(language);
            String translation = this.translator.getTranslation(original,
                    PlayerDataManager.getInstance().getPlayerData(sender).getLanguage(), language);
            for (PlayerData player : playersForLanguage) {
                player.getPlayer().sendMessage(getFullMessage(sender.getDisplayName(), this.translator.extractTranslation(translation)));
            }
        }
    }

    /*
        Util class
     */

    private String getFullMessage(String sender, String message) {
        String template = Main.getInstance().getConfig().getString("Translator_Prefix");
        template = template.replace("%player%", sender);
        template = template.replace("%message%", message);
        return template;
    }

}
