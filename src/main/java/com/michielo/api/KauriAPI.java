package com.michielo.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.michielo.Main;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class KauriAPI {

    private String licenseKey;

    public KauriAPI() {
        String lk = Main.getInstance().getConfig().getString("license_key");
        if (!lk.isEmpty()) this.licenseKey = lk;
    }

    /**
     * Utilizing https://funkemunky.cc/vpn?ip=%s&license=%s&cache=%s
     * @param player The player to get the region language for
     */
    public String getCountry(Player player) {
        try {
            // get IP
            String ip = player.getAddress().getAddress().getHostAddress();

            // create JsonObject
            JsonObject jsonObject;

            URL url;

            if (licenseKey == null) {
                url = new URL("https://funkemunky.cc/vpn?ip=" + ip + "&cache=true");
            } else {
                url = new URL("https://funkemunky.cc/vpn?ip=" + ip + "&license=" + licenseKey + "&cache=true");
            }

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");

            BufferedReader br;
            if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
            }

            String str = br.readLine();
            jsonObject = new JsonParser().parse(str).getAsJsonObject();
            http.disconnect();

            String country = jsonObject.get("countryCode").getAsString();
            return country;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
