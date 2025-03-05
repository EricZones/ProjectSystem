package de.ericzones.projectsystem.collectives.communication;

import de.ericzones.projectsystem.collectives.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandManager implements Listener {

    private final String[] disabledCommands = new String[]{"reload", "rl", "stop", "version", "ver", "help", "minecraft:help", "pardon", "ban-ip",
            "me", "tell", "?", "minecraft:tell", "minecraft:me", "list", "minecraft:list", "pardon-ip", "say", "tellraw",
            "minecraft:tellraw", "minecraft:say", "minecraft:ban", "minecraft:ban-ip", "minecraft:pardon", "minecraft:pardon-ip",
            "teammsg", "minecraft:teammsg", "tm", "minecraft:tm", "w", "minecraft:w", "minecraft:msg"};
    private final String[] restrictedCommands = new String[]{"pl", "plugins", "restart", "spigot:restart", "timings", "bukkit:timings",
            "op", "minecraft:op", "deop", "minecraft:deop"};

    private final List<UUID> spyPlayers = new ArrayList<>();
    private boolean isConsoleSpying = false;

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if(!player.hasPermission("projectsystem.command.admin")) {
            if (isDisabledCommand(e.getMessage())) {
                e.setCancelled(true);
                player.sendMessage(Message.PREFIX_PLUGIN + Message.NOCOMMAND);
                return;
            }

            if (isRestrictedCommand(e.getMessage()) && !player.hasPermission("projectsystem.command.restricted")) {
                e.setCancelled(true);
                player.sendMessage(Message.PREFIX_PLUGIN + Message.NOPERMS);
                return;
            }
        }

        if(!ChatManager.isSocialCommand(e.getMessage()))
            sendSpyMessage(player, e.getMessage());
    }

    private void sendSpyMessage(Player player, String command) {
        String spyMessage = Message.PREFIX_PLUGIN + "§7" + player.getName() + " §8➟ §7" + command;
        for(UUID current : spyPlayers) {
            Player spyPlayer = Bukkit.getPlayer(current);
            if(spyPlayer == null) continue;
            if(spyPlayer.getUniqueId() == player.getUniqueId()) continue;
            spyPlayer.sendMessage(spyMessage);
        }
        if(isConsoleSpying)
            Bukkit.getConsoleSender().sendMessage(spyMessage);
    }

    private boolean isDisabledCommand(String command) {
        for(int i = 0; i < disabledCommands.length; i++) {
            if(command.toLowerCase().startsWith("/"+disabledCommands[i]+" ") || command.equalsIgnoreCase("/"+disabledCommands[i]))
                return true;
        }
        return false;
    }

    private boolean isRestrictedCommand(String command) {
        for(int i = 0; i < restrictedCommands.length; i++) {
            if(command.toLowerCase().startsWith("/"+restrictedCommands[i]+" ") || command.equalsIgnoreCase("/"+restrictedCommands[i]))
                return true;
        }
        return false;
    }

    public List<UUID> getSpyPlayers() {
        return spyPlayers;
    }

    public boolean isConsoleSpying() {
        return isConsoleSpying;
    }

    public void setConsoleSpying(boolean consoleSpying) {
        isConsoleSpying = consoleSpying;
    }
}
