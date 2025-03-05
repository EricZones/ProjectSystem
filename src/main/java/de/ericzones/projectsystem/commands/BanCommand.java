package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.player.ProjectPlayer;
import de.ericzones.projectsystem.collectives.playtime.Playtime;
import de.ericzones.projectsystem.collectives.punish.Ban;
import de.ericzones.projectsystem.collectives.punish.PunishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BanCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public BanCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player && !sender.hasPermission("projectsystem.command.ban")) {
            sender.sendMessage(Message.PREFIX_PLUGIN + Message.NOPERMS);
            return true;
        }
        if(alias.equalsIgnoreCase("unban") || alias.equalsIgnoreCase("checkban")) {
            if(args.length != 1) {
                sender.sendMessage(Message.PREFIX_PUNISH + Message.SYNTAX.replace("%REPLACE%", alias+" <Player>"));
                return true;
            }

            ProjectPlayer projectPlayer = instance.getPlayerManager().getPlayer(args[0]);
            if(projectPlayer == null) {
                sender.sendMessage(Message.PREFIX_PUNISH+Message.NOTONLINE);
                return true;
            }

            PunishManager punishManager = instance.getPunishManager();
            Ban ban = punishManager.getBan(projectPlayer.uniqueId());
            if(ban == null) {
                sender.sendMessage(Message.PREFIX_PUNISH+"§7Player §8'§7"+projectPlayer.username()+"§8' §7is not banned");
                return true;
            }

            if(alias.equalsIgnoreCase("unban")) {
                punishManager.removeBan(ban);
                sender.sendMessage(Message.PREFIX_PUNISH+"§7Player §8'§7"+projectPlayer.username()+"§8' §7has been unbanned");

            } else if(alias.equalsIgnoreCase("checkban")) {
                sendBanInformation(sender, projectPlayer, ban);
            }

        } else if(alias.equalsIgnoreCase("ban")) {
            if(!(sender instanceof Player)) return true;

            if(args.length < 3) {
                sender.sendMessage(Message.PREFIX_PUNISH + Message.SYNTAX.replace("%REPLACE%", "ban <Player> <Minutes> <Reason>"));
                return true;
            }

            UUID uniqueId = null;
            String username = null;
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                if(((Player) sender).getUniqueId() == target.getUniqueId()) {
                    sender.sendMessage(Message.PREFIX_PUNISH+"§7You can not ban yourself");
                    return true;
                }
                if(target.hasPermission("projectsystem.punish.ignore")) {
                    sender.sendMessage(Message.PREFIX_PUNISH+"§7This §cPlayer §7can not be banned");
                    return true;
                }
                uniqueId = target.getUniqueId();
                username = target.getName();
            } else {
                ProjectPlayer projectPlayer = instance.getPlayerManager().getPlayer(args[0]);
                if(projectPlayer == null) {
                    sender.sendMessage(Message.PREFIX_PUNISH+Message.NOTONLINE);
                    return true;
                }
                uniqueId = projectPlayer.uniqueId();
                username = projectPlayer.username();
            }

            PunishManager punishManager = instance.getPunishManager();
            if(punishManager.getBan(uniqueId) != null) {
                sender.sendMessage(Message.PREFIX_PUNISH+"§7This §cPlayer §7is already banned");
                return true;
            }

            long duration;
            try {
                duration = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.PREFIX_PUNISH + Message.SYNTAX.replace("%REPLACE%", "ban <Player> <Minutes> <Reason>"));
                return true;
            }
            if(duration <= 0) {
                sender.sendMessage(Message.PREFIX_PUNISH + "§7This §cBan Duration §7is not valid");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                builder.append(args[i]);
                if (i < args.length - 1) {
                    builder.append(" ");
                }
            }

            punishManager.addBan(uniqueId, (Player) sender, builder.toString(), duration);
            sender.sendMessage(Message.PREFIX_PUNISH+"§7Player §8'§7" + username + "§8' §7has been banned");
        }


        return false;
    }

    private void sendBanInformation(CommandSender sender, ProjectPlayer projectPlayer, Ban ban) {
        sender.sendMessage(" ");
        sender.sendMessage("§8§m---------§r §7● §cBan Check §7● §8§m---------");
        sender.sendMessage(" §8× §7Name §8● §c"+ projectPlayer.username());
        sender.sendMessage(" §8× §7Remaining §8● §c"+ Playtime.formatPlaytime(ban.getRemainingExpiry(), false));
        sender.sendMessage(" §8× §7Total §8● §c"+ Playtime.formatPlaytime(ban.getTotalExpiry(), false));
        sender.sendMessage(" §8× §7Reason §8● §c"+ ban.getReason());
        sender.sendMessage(" §8× §7Created §8● §c"+ new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(ban.getCreationTime())));
        ProjectPlayer creator = instance.getPlayerManager().getPlayer(ban.getCreatorUniqueId());
        if(creator != null)
            sender.sendMessage(" §8× §7Creator §8● §c"+ creator.username());
    }

}
