package de.ericzones.projectsystem.collectives.playtime;

import de.ericzones.projectsystem.ProjectSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeListener implements Listener {

    private final ProjectSystem instance;

    public PlaytimeListener(ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        instance.getPlaytimeManager().recordPlaytime(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        instance.getPlaytimeManager().savePlaytime(player.getUniqueId());
    }

}
