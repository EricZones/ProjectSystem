package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.player.PlayerManager;
import de.ericzones.projectsystem.collectives.player.ProjectPlayer;
import de.ericzones.projectsystem.listener.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public VanishCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if(!player.hasPermission("projectsystem.vanish")) {
            player.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
            return true;
        }
        PlayerManager playerManager = instance.getPlayerManager();
        ProjectPlayer projectPlayer = playerManager.getPlayer(player.getUniqueId());

        if(projectPlayer.moderator()) {
            playerManager.setModerator(player.getUniqueId(), false);
            Bukkit.broadcast(ConnectionListener.getConnectionMessage(player, true));
            player.sendMessage(Message.PREFIX_PLUGIN+"§7Disabled §eVanish");
        } else {
            playerManager.setModerator(player.getUniqueId(), true);
            Bukkit.broadcast(ConnectionListener.getConnectionMessage(player, false));
            player.sendMessage(Message.PREFIX_PLUGIN+"§7Enabled §eVanish");
        }
        return false;
    }

}
