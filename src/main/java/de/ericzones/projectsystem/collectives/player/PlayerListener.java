package de.ericzones.projectsystem.collectives.player;

import de.ericzones.projectsystem.ProjectSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ProjectSystem instance;

    public PlayerListener(ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerManager playerManager = instance.getPlayerManager();
        playerManager.updatePlayer(player);

        if(playerManager.getPlayer(player.getUniqueId()).moderator())
            playerManager.updateModerator(player.getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!instance.getPlayerManager().playerExists(e.getEntity().getUniqueId())) {
            System.out.println("[!] Could not save deaths of '" + e.getEntity().getName() + "'");
            return;
        }
        ProjectPlayer player = instance.getPlayerManager().getPlayer(e.getEntity().getUniqueId());
        instance.getPlayerManager().setDeaths(player.uniqueId(), player.deaths()+1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        instance.getPlayerManager().updatePlayerConfig(player, false);
    }

}
