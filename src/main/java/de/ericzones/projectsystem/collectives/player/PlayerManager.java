package de.ericzones.projectsystem.collectives.player;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private final ProjectSystem instance;
    private final String fileName = "players.yml";
    private final FileConfig fileConfig;

    private final List<UUID> moderators = new ArrayList<>();

    public PlayerManager(ProjectSystem instance) {
        this.instance = instance;
        fileConfig = new FileConfig(instance, fileName);
        if(!fileConfig.isAvailable())
            System.out.println("[!] Plugin running without Player module ("+fileName+")");
    }

    public void updatePlayer(Player player) {
        if(playerExists(player.getUniqueId())) {
            fileConfig.setEntry(player.getUniqueId().toString()+".username", player.getName());
            ProjectPlayer projectPlayer = getPlayer(player.getUniqueId());
            fileConfig.setEntry(player.getUniqueId().toString()+".connections", String.valueOf(projectPlayer.connections()+1));
            fileConfig.setEntry(player.getUniqueId().toString()+".lastConnection", "Online");
            fileConfig.setEntry(player.getUniqueId().toString()+".group", getPlayerGroup(player).name());
            return;
        }
        fileConfig.setEntry(player.getUniqueId().toString()+".username", player.getName());
        fileConfig.setEntry(player.getUniqueId().toString()+".connections", String.valueOf(1));
        fileConfig.setEntry(player.getUniqueId().toString()+".firstConnection", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")));
        fileConfig.setEntry(player.getUniqueId().toString()+".lastConnection", "Online");
        fileConfig.setEntry(player.getUniqueId().toString()+".moderator", String.valueOf(false));
        fileConfig.setEntry(player.getUniqueId().toString()+".deaths", String.valueOf(0));
        fileConfig.setEntry(player.getUniqueId().toString()+".group", getPlayerGroup(player).name());
    }

    public void updatePlayerConfig(Player player, boolean stopping) {
        fileConfig.setEntry(player.getUniqueId().toString()+".lastConnection", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")));
        if(!stopping)
            fileConfig.setEntry(player.getUniqueId().toString()+".group", getPlayerGroup(player).name());
    }

    public ProjectPlayer getPlayer(UUID uniqueId) {
        String username = fileConfig.getValue(uniqueId.toString()+".username");
        int connections = Integer.parseInt(fileConfig.getValue(uniqueId.toString()+".connections"));
        String firstConnection = fileConfig.getValue(uniqueId.toString()+".firstConnection");
        String lastConnection = fileConfig.getValue(uniqueId.toString()+".lastConnection");
        boolean moderator = Boolean.parseBoolean(fileConfig.getValue(uniqueId.toString()+".moderator"));
        int deaths = Integer.parseInt(fileConfig.getValue(uniqueId.toString()+".deaths"));
        PlayerGroup group = PlayerGroup.valueOf(fileConfig.getValue(uniqueId.toString()+".group"));
        return new ProjectPlayer(uniqueId, username, connections, firstConnection, lastConnection, moderator, deaths, group);
    }

    public ProjectPlayer getPlayer(String username) {
        UUID uniqueId = playerExists(username);
        if(uniqueId == null) return null;
        return getPlayer(uniqueId);
    }

    public boolean playerExists(UUID uniqueId) {
        return fileConfig.containsEntry(uniqueId.toString());
    }

    public UUID playerExists(String username) {
        HashMap<String, String> content = fileConfig.getContent();

        for(String current : content.keySet()) {
            if(!content.get(current).equalsIgnoreCase(username)) continue;
            return UUID.fromString(current.split("\\.")[0]);
        }

        return null;
    }

    public void setDeaths(UUID uniqueId, int deaths) {
        fileConfig.setEntry(uniqueId.toString()+".deaths", String.valueOf(deaths));
    }

    public PlayerGroup getPlayerGroup(Player player) {
        if(player.isOp())
            return PlayerGroup.ADMIN;
        else if(player.hasPermission("projectsystem.group.season1"))
            return PlayerGroup.SEASON1;
        else if(player.hasPermission("projectsystem.group.season2"))
            return PlayerGroup.SEASON2;
        else if(player.hasPermission("projectsystem.group.season3"))
            return PlayerGroup.SEASON3;
        else
            return PlayerGroup.PLAYER;
    }

    // Moderation


    public List<UUID> getActiveModerators() {
        List<UUID> activeModerators = new ArrayList<>();
        moderators.forEach(current -> {
            if(Bukkit.getPlayer(current) != null)
                activeModerators.add(current);
        });
        return activeModerators;
    }

    public boolean isModerator(UUID uniqueId) {
        return moderators.contains(uniqueId);
    }

    public void setModerator(UUID uniqueId, boolean moderator) {
        fileConfig.setEntry(uniqueId.toString()+".moderator", String.valueOf(moderator));

        updateModerator(uniqueId, moderator);
    }

    public void updateModerator(UUID uniqueId, boolean moderator) {
        if(moderator) {
            moderators.add(uniqueId);
            Bukkit.getOnlinePlayers().forEach(this::updateVisibility);
        } else {
            moderators.remove(uniqueId);
            Player player = Bukkit.getPlayer(uniqueId);
            if(player != null)
                Bukkit.getOnlinePlayers().forEach(current -> current.showPlayer(instance, player));

        }
    }

    public void updateVisibility(Player player) {
        if(isModerator(player.getUniqueId()) || player.hasPermission("projectsystem.vanish")) return;
        for(UUID current : this.moderators) {
            Player moderator = Bukkit.getPlayer(current);
            if(moderator == null) continue;
            if(player.getUniqueId() == moderator.getUniqueId()) continue;
            player.hidePlayer(instance, moderator);
        }
    }

    public boolean canPlayerInteract(Player player, Player target) {
        if(isModerator(player.getUniqueId())) return true;
        if(player.hasPermission("projectsystem.vanish")) return true;
        if(!isModerator(target.getUniqueId())) return true;
        return false;
    }

}
