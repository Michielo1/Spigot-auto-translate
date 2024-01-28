package com.michielo.bukkit.command;

import com.michielo.player.PlayerDataManager;
import com.michielo.translation.Language;
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
            sender.sendMessage(ChatColor.RED + "Supported languages: " + Language.getAllAbbreviationsFormatted());
            return true;
        }

        if (args.length == 1 && sender instanceof Player p) {
            String language = args[0];
            if (Language.getAbbreviation(language) == null) {
                p.sendMessage(ChatColor.RED + "Unrecognised language!");
                p.sendMessage(ChatColor.RED + "Supported languages: " + Language.getAllAbbreviationsFormatted());
                return true;
            }
            PlayerDataManager.getInstance().getPlayerData(p).setLanguage(language);
            p.sendMessage(ChatColor.GREEN + "Successfully changed your language!");
            return true;
        }

        return true;
    }
}
