package com.michielo.processors;

import com.michielo.Main;
import com.michielo.player.PlayerData;
import com.michielo.player.PlayerDataManager;
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
    }

    /*
        Main methods
     */

    public boolean enabledTranslations() {
        return this.enableTranslations;
    }

    public void handleTranslation(Player sender, String original) {
        List<PlayerData> players = PlayerDataManager.getInstance().getAllPlayerData();
        players.remove(PlayerDataManager.getInstance().getPlayerData(sender));

        sender.sendMessage(sender.getDisplayName() + " >> " + original);

        for (PlayerData player : players) {
            if (!player.hasChosenLanguage() && player.getLanguage() == null) player.setLanguage("eng");
        }

        // send to all players in same language group without delay
        for (PlayerData player : players) {
            if (player.getLanguage().equals(original)) {
                player.getPlayer().sendMessage(sender.getDisplayName() + " >> " + original);
                players.remove(player);
            }
        }

        // Group players by language using Java Streams
        Map<String, List<PlayerData>> playersByLanguage = players.stream()
                .collect(Collectors.groupingBy(PlayerData::getLanguage));

        // Loop through each language and perform some operation
        for (String language : playersByLanguage.keySet()) {
            List<PlayerData> playersForLanguage = playersByLanguage.get(language);
            String translation = getTranslation(original,
                    PlayerDataManager.getInstance().getPlayerData(sender).getLanguage(), language);
            for (PlayerData player : playersForLanguage) {
                player.getPlayer().sendMessage(sender.getDisplayName() + " >> " + extractTranslation(translation));
            }
        }
    }

    /*
        Util
     */

    private String getTranslation(String text, String src, String target) {
        try {
            String apiUrl = "http://159.69.60.114:8000/translate"; // Update with your actual IP and port

            // Create a URL object with the API endpoint
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Set the content type to JSON
            connection.setRequestProperty("Content-Type", "application/json");

            // Create JSON payload
            String jsonInputString = "{\"text\":\"" + text + "\",\"source_lang\":\"" + src
                    + "\",\"target_lang\":\"" + target + "\"}";

            // Write the JSON payload to the connection's output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Close the connection
            connection.disconnect();

            // return response
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractTranslation(String jsonString) {
        // Find the index of the "translation" key
        int startIndex = jsonString.indexOf("\"translation\":");
        if (startIndex == -1) {
            // "translation" key not found, return original string
            return jsonString;
        }

        // Find the start of the value associated with the "translation" key
        startIndex = jsonString.indexOf("\"", startIndex + 13) + 1;

        // Find the end of the value associated with the "translation" key
        int endIndex = jsonString.indexOf("\"", startIndex);

        if (startIndex == -1 || endIndex == -1) {
            // Issue with JSON structure, return original string
            return jsonString;
        }

        // Extract the value associated with the "translation" key
        return jsonString.substring(startIndex, endIndex);
    }

}
