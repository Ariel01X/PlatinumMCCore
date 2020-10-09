package mc.platinum.com;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public  class pmccommand implements CommandExecutor {
    private Plugin plugin = PMC.getPlugin(PMC.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String pmcmessage = plugin.getConfig().getString("pmcmessage");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
                for (String helpmessage : plugin.getConfig().getStringList("helpmessage")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpmessage));
                }


            } else{
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pmcmessage));

            }

        } else {
            sender.sendMessage("you need to be a player to execute this command");

        }
        return true;
    }
}

