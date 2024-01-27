package com.michielo.bukkit.command;

import com.michielo.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Specify a language!");
            return true;
        }

        if (sender instanceof Player p) {
            String language = args[0];
            PlayerDataManager.getInstance().getPlayerData(p).setLanguage(language);
            return true;
        }

        return true;
    }
}
