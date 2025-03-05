package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.player.ProjectPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public PingCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length == 0) {
                player.sendMessage(Message.PREFIX_PLUGIN+"§7Own ping §8● "+getColoredPing(player.getPing()));
                return false;
            }
            if(args.length != 1) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "ping <Player>"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            return sendTargetPing(player, target);

        } else if(sender instanceof ConsoleCommandSender) {
            if(args.length == 0) {
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "ping <Player>"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            return sendTargetPing(Bukkit.getConsoleSender(), target);
        }
        return false;
    }

    private boolean sendTargetPing(CommandSender sender, Player target) {
        if(target == null) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOTONLINE);
            return true;
        }
        ProjectPlayer projectPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        if(projectPlayer == null || (sender instanceof Player && !instance.getPlayerManager().canPlayerInteract((Player) sender, target))) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOTONLINE);
            return true;
        }
        sender.sendMessage(Message.PREFIX_PLUGIN+"§7Ping §8'"+ projectPlayer.group().getColorPrefix() + "§7" + target.getName() +"§8' ● "+getColoredPing(target.getPing()));
        return false;
    }

    public static String getColoredPing(int ping) {
        String result;
        if(ping >= 100)
            result = "§c"+ping+"ms";
        else if(ping >= 50)
            result = "§6"+ping+"ms";
        else
            result = "§a"+ping+"ms";
        return result;
    }

}
