package com.michielo.translation;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Translator {

    public static String getTranslation(String text, String src, String target) {
        // pre-checks
        if (Language.getAbbreviation(src) == null ||
                Language.getAbbreviation(target) == null ||
                src.equals(target)) {
            Bukkit.getLogger().severe("Invalid translation request!");
            Bukkit.getLogger().severe("Details: " + text + " | " + src + " | " + target);
            return null;
        }

        if (!src.equals("ENG") && !target.equals("ENG")) {
            // we have to translate indirectly
            return getTranslation(getTranslation(text, src, "ENG"), "ENG", target);
        }
        return getTranslationAPI(text, src, target);
    }

    public static String extractTranslation(String jsonString) {
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

    private static String getTranslationAPI(String text, String src, String target) {
        try {
            String apiUrl = "http://159.69.60.114:8000/translate";

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

}
