package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.communication.CommandManager;
import de.ericzones.projectsystem.collectives.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandspyCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public CommandspyCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("projectsystem.command.commandspy")) {
                player.sendMessage(Message.PREFIX_PLUGIN + Message.NOPERMS);
                return true;
            }
            CommandManager commandManager = instance.getCommandManager();

            if (commandManager.getSpyPlayers().contains(player.getUniqueId())) {
                commandManager.getSpyPlayers().remove(player.getUniqueId());
                player.sendMessage(Message.PREFIX_PLUGIN + "§7You disabled §eCommandspy");
            } else {
                commandManager.getSpyPlayers().add(player.getUniqueId());
                player.sendMessage(Message.PREFIX_PLUGIN + "§7You enabled §eCommandspy");
            }

        } else if(sender instanceof ConsoleCommandSender) {
            CommandManager commandManager = instance.getCommandManager();
            if(commandManager.isConsoleSpying()) {
                commandManager.setConsoleSpying(false);
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_PLUGIN + "§7Console §eCommandspy §7disabled");
            } else {
                commandManager.setConsoleSpying(true);
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_PLUGIN + "§7Console §eCommandspy §7enabled");
            }
        }

        return false;
    }

}
