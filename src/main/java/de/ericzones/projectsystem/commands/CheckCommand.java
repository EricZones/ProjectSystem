package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.listener.DeathListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CheckCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public CheckCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.hasPermission("projectsystem.command.check.self")) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
                return true;
            }
            if(args.length == 0) {
                return sendPlayerInfo(player, null);
            }

            if(args.length != 1) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "check <Player>"));
                return true;
            }
            if(!player.hasPermission("projectsystem.command.check.other")) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
                return true;
            }

            return sendPlayerInfo(player, args[0]);

        } else if(sender instanceof ConsoleCommandSender) {
            if(args.length != 1) {
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "check <Player>"));
                return true;
            }
            return sendPlayerInfo(Bukkit.getConsoleSender(), args[0]);
        }
        return false;
    }

    private boolean sendPlayerInfo(CommandSender sender, String name) {
        Player target;
        if(name == null)
            target = (Player) sender;
        else
            target = Bukkit.getPlayer(name);

        if(target == null || (sender instanceof Player && !instance.getPlayerManager().canPlayerInteract((Player) sender, target))) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOTONLINE);
            return true;
        }

        sender.sendMessage(" ");
        sender.sendMessage("§8§m---------§r §7● §ePlayer Check §7● §8§m---------");
        sender.sendMessage(" §8× §7Name §8● §e"+ target.getName());
        sender.sendMessage(" §8× §7Ping §8● §e"+ PingCommand.getColoredPing(target.getPing()));
        sender.sendMessage(" §8× §7Coordinates §8● §7X: §e"+target.getLocation().getBlockX()+" §7Y: §e"+target.getLocation().getBlockY()+" §7Z: §e"+target.getLocation().getBlockZ());
        sender.sendMessage(" §8× §7Dimension §8● "+ DeathListener.getDimension(target.getLocation()));
        sender.sendMessage(" §8× §7Hearts §8● §c"+ (((int) target.getHealth())/2) + "❤");
        return false;
    }

}
