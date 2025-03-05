package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.player.ProjectPlayer;
import de.ericzones.projectsystem.collectives.playtime.Playtime;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public InfoCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.hasPermission("projectsystem.command.info.self")) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
                return true;
            }
            if(args.length == 0) {
                return sendPlayerInfo(player, null);
            }

            if(args.length != 1) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "info <Player>"));
                return true;
            }
            if(!player.hasPermission("projectsystem.command.info.other")) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
                return true;
            }

            return sendPlayerInfo(player, args[0]);

        } else if(sender instanceof ConsoleCommandSender) {
            if(args.length != 1) {
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "info <Player>"));
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

        ProjectPlayer projectPlayer;
        if(target != null)
            projectPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        else
            projectPlayer = instance.getPlayerManager().getPlayer(name);

        if(projectPlayer == null || (sender instanceof Player && target != null && !instance.getPlayerManager().canPlayerInteract((Player) sender, target))) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOTONLINE);
            return true;
        }

        Playtime playtime = instance.getPlaytimeManager().getPlaytime(projectPlayer.uniqueId());
        sender.sendMessage(" ");
        sender.sendMessage("§8§m---------§r §7● §ePlayer Info §7● §8§m---------");
        sender.sendMessage(" §8× §7Name §8● §e"+ (target != null ? target.getName() : projectPlayer.username()));
        sender.sendMessage(" §8× §7Group §8● §e"+projectPlayer.group().getColorCode()+projectPlayer.group().getName());
        sender.sendMessage(" §8× §7Playtime §8● §e"+ (playtime.isActive() ? Playtime.formatPlaytime(playtime.getTotalPlaytime(), true) : Playtime.formatPlaytime(playtime.getPreviousPlaytime(), true)));
        sender.sendMessage(" §8× §7Online since §8● §e"+ (playtime.isActive() ? Playtime.formatPlaytime(playtime.getCurrentPlaytime(), true) : "/"));
        sender.sendMessage(" §8× §7Connections §8● §e"+projectPlayer.connections());
        sender.sendMessage(" §8× §7First time §8● §e"+projectPlayer.firstConnection());
        sender.sendMessage(" §8× §7Last time §8● §e"+projectPlayer.lastConnection());
        sender.sendMessage(" §8× §7Deaths §8● §e"+projectPlayer.deaths());
        sender.sendMessage(" §8× §7Moderator §8● §e"+ (projectPlayer.moderator() ? "§aYes" : "§cNo"));
        return false;
    }



}
