package net.peacefulcraft.scavengerhunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.scavengerhunt.ScavengerHunt;
import net.peacefulcraft.scavengerhunt.io.PlayerDataHandler;

public class ScavengerHuntCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (label.equalsIgnoreCase("scavengerhunt") || label.equalsIgnoreCase("sh")) {
            if(args.length == 0) {
                sender.sendMessage("scavengerhunt [ progress ]");
                return true;
            }

            if(args[0].equalsIgnoreCase("progress")) {
                Player p = (Player) sender;
                PlayerDataHandler playerData = null;
                if (ScavengerHunt.getDataCache().containsKey(p.getUniqueId())) {
                    playerData = ScavengerHunt.getDataCache().get(p.getUniqueId());
                } else {
                    playerData = ScavengerHunt.loadPlayerData(p);
                }

                int remainder = 10 - playerData.numKeys();
                if(remainder == -1) {
                    p.sendMessage(ScavengerHunt.getPrefix() + ChatColor.WHITE + " You have found no pumpkins!");
                } else {
                    p.sendMessage(ScavengerHunt.getPrefix() + ChatColor.WHITE + " You have " + String.valueOf(remainder) + " pumpkins left!");
                }
                return true;
            }
        }

        return true;
    }
    
}
