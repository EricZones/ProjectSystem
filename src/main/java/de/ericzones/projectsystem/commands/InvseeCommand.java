package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.collectives.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if(!player.hasPermission("projectsystem.command.invsee")) {
            player.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
            return true;
        }
        if(args.length != 1) {
            player.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "invsee <Player>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            player.sendMessage(Message.PREFIX_PLUGIN+Message.NOTONLINE);
            return true;
        }
        player.openInventory(target.getInventory());
        return false;
    }

}
