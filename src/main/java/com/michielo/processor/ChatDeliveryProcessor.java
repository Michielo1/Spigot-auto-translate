package com.michielo.processor;

import com.michielo.Main;
import com.michielo.api.Translator;
import com.michielo.chattypes.ChatFactory;
import com.michielo.chattypes.ChatMethod;
import com.michielo.chattypes.ChatTypes;
import com.michielo.playerdata.PlayerData;
import com.michielo.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatDeliveryProcessor {

    private ChatTypes chatType;
    private ChatFactory factory;
    private Translator translator;

    public ChatDeliveryProcessor() {
        String value = Main.getInstance().getConfig().getString("chat_type");
        
        if (value.equalsIgnoreCase("default")) {
            this.chatType = ChatTypes.DEFAULT;
        } else if (value.equalsIgnoreCase("default_ranged")) {
            this.chatType = ChatTypes.DEFAULT_RANGED;
        } else if (value.equalsIgnoreCase("luckperms")) {
            this.chatType = ChatTypes.LUCKPERMS;
        } else if (value.equalsIgnoreCase("luckperms_ranged")) {
            this.chatType = ChatTypes.LUCKPERMS_RANGED;
        } else {
            this.chatType = ChatTypes.DEFAULT;
            Bukkit.getLogger().severe("Invalid configuration file! Defaulting to default chattype");
        }

        factory = new ChatFactory();

        translator = new Translator();
    }

    public void deliverChat(Player sender, String message) {
        // get chat method
        ChatMethod method = factory.getMethod(chatType);

        // send sender his own message
        sender.sendMessage(method.formatText(message, sender));

        // get targets according to chatmethod
        List<Player> targets = method.getPlayers(sender.getLocation());
        targets.remove(sender);

        // define all playerdata
        List<PlayerData> targetData = new ArrayList<>();
        for (Player p : targets) {
            targetData.add(PlayerDataManager.getInstance().getPlayer(p.getUniqueId()));
        }

        // group playerdata by language
        Map<String, List<PlayerData>> playerDataByLanguage = targetData.stream()
                .collect(Collectors.groupingBy(PlayerData::getLanguage));

        // translate for each language
        Map<String, String> translationMap = new HashMap<>();
        for (String language : playerDataByLanguage.keySet()) {
            if (language.equalsIgnoreCase(PlayerDataManager.getInstance().getPlayer(sender.getUniqueId()).getLanguage())) {
                translationMap.put(language, message);
                continue;
            }

            translationMap.put(language, translator.translate(message,
                    PlayerDataManager.getInstance().getPlayer(sender.getUniqueId()).getLanguage(), language));
        }

        // dispatch messages
        for (PlayerData pdata : targetData) {
            Bukkit.getPlayer(pdata.getUuid()).sendMessage(method.formatText(translationMap.get(pdata.getLanguage()),
                    sender));
        }
    }

}
