package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.communication.MessageManager;
import de.ericzones.projectsystem.collectives.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SocialspyCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public SocialspyCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("projectsystem.command.socialspy")) {
                player.sendMessage(Message.PREFIX_PLUGIN + Message.NOPERMS);
                return true;
            }
            MessageManager messageManager = instance.getMessageManager();

            if (messageManager.getSpyPlayers().contains(player.getUniqueId())) {
                messageManager.getSpyPlayers().remove(player.getUniqueId());
                player.sendMessage(Message.PREFIX_MESSAGE + "§7Disabled §eSocialspy");
            } else {
                messageManager.getSpyPlayers().add(player.getUniqueId());
                player.sendMessage(Message.PREFIX_MESSAGE + "§7Enabled §eSocialspy");
            }
        } else if(sender instanceof ConsoleCommandSender) {
            MessageManager messageManager = instance.getMessageManager();
            if(messageManager.isConsoleSpying()) {
                messageManager.setConsoleSpying(false);
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_MESSAGE + "§7Console §eSocialspy §7disabled");
            } else {
                messageManager.setConsoleSpying(true);
                Bukkit.getConsoleSender().sendMessage(Message.PREFIX_MESSAGE + "§7Console §eSocialspy §7enabled");
            }
        }

        return false;
    }

}
