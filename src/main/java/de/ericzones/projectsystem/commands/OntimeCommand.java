package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.player.ProjectPlayer;
import de.ericzones.projectsystem.collectives.playtime.Playtime;
import de.ericzones.projectsystem.collectives.playtime.PlaytimeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OntimeCommand implements CommandExecutor {

    private final ProjectSystem instance;

    private final List<Component> topMessageCache = new LinkedList<>();
    private long topMessageMillis = 0;

    public OntimeCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            PlaytimeManager playtimeManager = instance.getPlaytimeManager();

            if(args.length == 0) {
                player.sendMessage(Message.PREFIX_PLUGIN+"§7Own playtime §8● §e"+ Playtime.formatPlaytime(playtimeManager.getPlaytime(player.getUniqueId()).getTotalPlaytime(), true));
                return false;
            }

            if(args.length != 1) {
                player.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "ontime <Player | top>"));
                return true;
            }

            if(args[0].equalsIgnoreCase("top")) {
                sendTopMessage(player);
            } else if(args[0].equalsIgnoreCase("reset")) {

                if (!player.hasPermission("projectsystem.ontime.reset")) {
                    player.sendMessage(Message.PREFIX_PLUGIN + Message.NOPERMS);
                    return true;
                }
                playtimeManager.resetPlaytime(player.getUniqueId());
                player.sendMessage(Message.PREFIX_PLUGIN+ "§7Own playtime has been reset");

            } else {
                return sendTargetMessage(player, args[0]);
            }

        } else if(sender instanceof ConsoleCommandSender) {
            if(args.length != 1) {
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "ontime <Player | top>"));
                return true;
            }

            if(args[0].equalsIgnoreCase("top")) {
                sendTopMessage(Bukkit.getConsoleSender());
            } else {
                return sendTargetMessage(Bukkit.getConsoleSender(), args[0]);
            }

        }
        return false;
    }

    private boolean sendTargetMessage(CommandSender sender, String name) {
        Player target = Bukkit.getPlayer(name);
        ProjectPlayer projectPlayer;

        if(target != null)
            projectPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        else
            projectPlayer = instance.getPlayerManager().getPlayer(name);

        if(projectPlayer == null) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOTONLINE);
            return true;
        }

        if(target != null)
            sender.sendMessage(Message.PREFIX_PLUGIN+"§7Playtime §8'"+ projectPlayer.group().getColorPrefix() + "§7" + target.getName() +"§8' ● §e"+ Playtime.formatPlaytime(instance.getPlaytimeManager().getPlaytime(target.getUniqueId()).getTotalPlaytime(), true));
        else
            sender.sendMessage(Message.PREFIX_PLUGIN+"§7Playtime §8'"+ projectPlayer.group().getColorPrefix() + "§7" + projectPlayer.username() +"§8' ● §e"+ Playtime.formatPlaytime(instance.getPlaytimeManager().getPlaytime(projectPlayer.uniqueId()).getPreviousPlaytime(), true));

        return false;
    }

    private void sendTopMessage(CommandSender sender) {
        if(!isTopMessageCached()) {
            Map<UUID, Playtime> playtimes = instance.getPlaytimeManager().getSortedPlaytimes();

            int count = 1;
            outerloop:
            for(UUID current : playtimes.keySet()) {
                String playtimeText;

                Playtime playtime = playtimes.get(current);
                if(playtime.isActive())
                    playtimeText = Playtime.formatPlaytime(playtime.getTotalPlaytime(), false);
                else
                    playtimeText = Playtime.formatPlaytime(playtime.getPreviousPlaytime(), false);

                ProjectPlayer projectPlayer = instance.getPlayerManager().getPlayer(current);
                Component prefix = projectPlayer.group().getPrefix();
                Component name = Component.text(projectPlayer.username()).color(NamedTextColor.GRAY);

                switch (count) {
                    case 1:
                        topMessageCache.add(Component.text(" × ").color(NamedTextColor.DARK_GRAY)
                                .append(Component.text("➊ ").color(NamedTextColor.GOLD))
                                .append(prefix).append(name).append(Component.text(" ● ").color(NamedTextColor.DARK_GRAY))
                                .append(Component.text(playtimeText).color(NamedTextColor.YELLOW)));
                        break;
                    case 2:
                        topMessageCache.add(Component.text(" × ").color(NamedTextColor.DARK_GRAY)
                                .append(Component.text("➋ ").color(NamedTextColor.GRAY))
                                .append(prefix).append(name).append(Component.text(" ● ").color(NamedTextColor.DARK_GRAY))
                                .append(Component.text(playtimeText).color(NamedTextColor.YELLOW)));
                        break;
                    case 3:
                        topMessageCache.add(Component.text(" × ").color(NamedTextColor.DARK_GRAY)
                                .append(Component.text("➌ ").color(NamedTextColor.RED))
                                .append(prefix).append(name).append(Component.text(" ● ").color(NamedTextColor.DARK_GRAY))
                                .append(Component.text(playtimeText).color(NamedTextColor.YELLOW)));
                        break;
                    case 4:
                        topMessageCache.add(Component.text(" × ").color(NamedTextColor.DARK_GRAY)
                                .append(Component.text("❹ ").color(NamedTextColor.DARK_GRAY))
                                .append(prefix).append(name).append(Component.text(" ● ").color(NamedTextColor.DARK_GRAY))
                                .append(Component.text(playtimeText).color(NamedTextColor.YELLOW)));
                        break;
                    case 5:
                        topMessageCache.add(Component.text(" × ").color(NamedTextColor.DARK_GRAY)
                                .append(Component.text("❺ ").color(NamedTextColor.DARK_GRAY))
                                .append(prefix).append(name).append(Component.text(" ● ").color(NamedTextColor.DARK_GRAY))
                                .append(Component.text(playtimeText).color(NamedTextColor.YELLOW)));
                        break outerloop;
                }
                count++;
            }
            topMessageMillis = System.currentTimeMillis() + 30 * 1000;
        }

        sender.sendMessage(" ");
        sender.sendMessage(" ");
        sender.sendMessage("§8§m-------------§r §7● §eTop Playtime §7● §8§m-------------");
        sender.sendMessage(" ");

        Iterator<Component> iterator = topMessageCache.iterator();
        while(iterator.hasNext()) {
            sender.sendMessage(iterator.next());
        }

        sender.sendMessage(" ");
    }

    private boolean isTopMessageCached() {
        if(topMessageCache.isEmpty()) return false;
        if(topMessageMillis < System.currentTimeMillis()) {
            topMessageCache.clear();
            return false;
        }
        return true;
    }

}
