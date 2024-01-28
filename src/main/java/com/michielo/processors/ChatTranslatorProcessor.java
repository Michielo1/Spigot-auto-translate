package com.michielo.processors;

import com.michielo.Main;
import com.michielo.player.PlayerData;
import com.michielo.player.PlayerDataManager;
import com.michielo.translation.Translator;
import com.michielo.util.PrefixUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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
        if (ChatChannelProcessor.getInstance().usesSeperateChats()) {
            try {
                List<Object> returnList = ChatChannelProcessor.getInstance().handleInternal(original, sender);
                List<PlayerData> playerDataInRange = (List<PlayerData>) returnList.get(0);
                String message = (String) returnList.get(1);

                for (PlayerData player : playerDataInRange) {
                    if (!player.hasChosenLanguage() && player.getLanguage() == null) player.setLanguage("eng");
                }

                playerDataInRange.remove(PlayerDataManager.getInstance().getPlayerData(sender));

                sender.sendMessage(message);

                List<PlayerData> playersToRemove = new ArrayList<>();

                // send to all players in same language group without delay
                for (PlayerData player : playerDataInRange) {
                    if (player.getLanguage().equals(PlayerDataManager.getInstance().getPlayerData(sender).getLanguage())) {
                        player.getPlayer().sendMessage(message);
                        playersToRemove.add(player);
                    }
                }

                for (PlayerData player : playersToRemove) {
                    playerDataInRange.remove(player);
                }

                if (playerDataInRange.isEmpty()) return;

                // Group players by language using Java Streams
                Map<String, List<PlayerData>> playersByLanguage = playerDataInRange.stream()
                        .collect(Collectors.groupingBy(PlayerData::getLanguage));

                // Loop through each language and perform some operation
                for (String language : playersByLanguage.keySet()) {
                    List<PlayerData> playersForLanguage = playersByLanguage.get(language);
                    String translation = this.translator.getTranslation(original,
                            PlayerDataManager.getInstance().getPlayerData(sender).getLanguage(), language);
                    translation = this.translator.extractTranslation(translation);
                    for (PlayerData player : playersForLanguage) {
                        player.getPlayer().sendMessage(message.replace(original, translation));
                    }
                }
            } catch (ExecutionException | InterruptedException ignored) {}
            return;
        }

        List<PlayerData> players = PlayerDataManager.getInstance().getAllPlayerData();

        for (PlayerData player : players) {
            if (!player.hasChosenLanguage() && player.getLanguage() == null) player.setLanguage("eng");
        }

        players.remove(PlayerDataManager.getInstance().getPlayerData(sender));

        sender.sendMessage(getFullMessage(sender, original));

        List<PlayerData> playersToRemove = new ArrayList<>();

        // send to all players in same language group without delay
        for (PlayerData player : players) {
            if (player.getLanguage().equals(PlayerDataManager.getInstance().getPlayerData(sender).getLanguage())) {
                player.getPlayer().sendMessage(getFullMessage(sender, original));
                playersToRemove.add(player);
            }
        }

        for (PlayerData player : playersToRemove) {
            players.remove(player);
        }

        if (players.isEmpty()) return;

        // Group players by language using Java Streams
        Map<String, List<PlayerData>> playersByLanguage = players.stream()
                .collect(Collectors.groupingBy(PlayerData::getLanguage));

        // Loop through each language and perform some operation
        for (String language : playersByLanguage.keySet()) {
            List<PlayerData> playersForLanguage = playersByLanguage.get(language);
            String translation = this.translator.getTranslation(original,
                    PlayerDataManager.getInstance().getPlayerData(sender).getLanguage(), language);
            for (PlayerData player : playersForLanguage) {
                player.getPlayer().sendMessage(getFullMessage(sender, this.translator.extractTranslation(translation)));
            }
        }
    }

    /*
        Util class
     */

    private String getFullMessage(Player sender, String message) {
        String template = Main.getInstance().getConfig().getString("Translator_Prefix");
        template = template.replace("%player%", sender.getDisplayName());
        template = template.replace("%message%", message);
        if (PrefixUtil.getInstance().getPlayerPrefix(sender) != null) {
            template = template.replace("%prefix%", PrefixUtil.getInstance().getPlayerPrefix(sender));
        } else {
            template = template.replace("%prefix%", "");
        }
        return template;
    }

}
