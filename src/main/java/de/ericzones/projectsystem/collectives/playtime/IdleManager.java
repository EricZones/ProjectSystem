package de.ericzones.projectsystem.collectives.playtime;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.boards.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdleManager implements Listener {

    private final ProjectSystem instance;
    private final Map<UUID, Long> idlingCache = new HashMap<>();

    private final long idlingOffset = 10 * 60 * 1000;

    public IdleManager(ProjectSystem instance) {
        this.instance = instance;
        startScheduler();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        idlingCache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(isPlayerIdle(e.getPlayer().getUniqueId())) {
            idlingCache.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + idlingOffset);
            Bukkit.getOnlinePlayers().forEach(current -> instance.getTablistManager().setPlayerTeams(current));
        } else
            idlingCache.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + idlingOffset);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(isPlayerIdle(e.getPlayer().getUniqueId())) {
            idlingCache.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + idlingOffset);
            Bukkit.getOnlinePlayers().forEach(current -> instance.getTablistManager().setPlayerTeams(current));
        } else
            idlingCache.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + idlingOffset);
    }

    public boolean isPlayerIdle(UUID uniqueId) {
        if(instance.getPlayerManager().isModerator(uniqueId))
            return false;
        if(idlingCache.containsKey(uniqueId) && idlingCache.get(uniqueId) < System.currentTimeMillis()) {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player != null)
                player.setSleepingIgnored(true);
            return true;
        } else {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player != null)
                player.setSleepingIgnored(false);
            return false;
        }
    }

    private void startScheduler() {
        instance.getServer().getScheduler().runTaskTimer(instance, new Runnable() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().isEmpty()) return;

                TablistManager tablistManager = instance.getTablistManager();
                if(tablistManager == null) return;
                Bukkit.getOnlinePlayers().forEach(tablistManager::setPlayerTeams);

            }
        }, 1200, 1200);
    }

}
