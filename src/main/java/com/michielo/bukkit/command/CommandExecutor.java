package com.michielo.bukkit.command;

import com.michielo.Language;
import com.michielo.Main;
import com.michielo.playerdata.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {

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
            PlayerDataManager.getInstance().getPlayer(p.getUniqueId()).setLanguage(language);
            Main.getInstance().getDataManager().getConfig().set(p.getUniqueId().toString(), language);
            Main.getInstance().getDataManager().saveConfig();
            p.sendMessage(ChatColor.GREEN + "Successfully changed your language!");
            return true;
        }

        return true;
    }

}
