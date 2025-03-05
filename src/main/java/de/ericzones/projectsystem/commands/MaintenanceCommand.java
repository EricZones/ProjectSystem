package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.restriction.Restriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MaintenanceCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public MaintenanceCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player && !sender.hasPermission("projectsystem.command.maintenance")) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "maintenance <on | off> <Minutes>"));
            return true;
        }
        Restriction restriction = instance.getRestriction();

        if(args[0].equalsIgnoreCase("on")) {
            if(restriction.isMaintenance()) {
                sender.sendMessage(Message.PREFIX_PLUGIN+ "§7Currently §cMaintenance §7is already active");
                return true;
            }
            if(args.length < 2) {
                sender.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "maintenance <on | off> <Minutes>"));
                return true;
            }
            long duration;
            try {
                duration = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "maintenance <on | off> <Minutes>"));
                return true;
            }
            if(duration <= 0) {
                sender.sendMessage(Message.PREFIX_PLUGIN + "§7This amount of §cMinutes §7is not valid");
                return true;
            }
            restriction.setMaintenace(true, duration);
            sender.sendMessage(Message.PREFIX_PLUGIN + "§7Maintenance has been enabled");

        } else if(args[0].equalsIgnoreCase("off")) {
            if(!restriction.isMaintenance()) {
                sender.sendMessage(Message.PREFIX_PLUGIN+ "§7Currently §cMaintenance §7is not active");
                return true;
            }
            restriction.setMaintenace(false, 0L);
            sender.sendMessage(Message.PREFIX_PLUGIN + "§7Maintenance has been disabled");

        } else {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.SYNTAX.replace("%REPLACE%", "maintenance <on | off> <Minutes>"));
            return true;
        }

        return false;
    }

}
