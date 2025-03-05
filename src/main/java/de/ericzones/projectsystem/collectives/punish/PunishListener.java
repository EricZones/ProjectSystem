package de.ericzones.projectsystem.collectives.punish;

import de.ericzones.projectsystem.ProjectSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishListener implements Listener {

    private final ProjectSystem instance;
    private final Map<UUID, Long> loginDelayCache = new HashMap<>();
    private final int delaySeconds = 30;

    public PunishListener(ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        UUID uniqueId = e.getUniqueId();

        if(isLoginDelayed(uniqueId)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, instance.getRestriction().getDisallowedText(delaySeconds));
            return;
        }

        PunishManager punishManager = instance.getPunishManager();
        if(punishManager == null) return;
        Ban ban = punishManager.getBan(uniqueId);
        if(ban == null) return;
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, punishManager.getBannedMessage(ban, false));
    }

    private boolean isLoginDelayed(UUID uniqueId) {
        loginDelayCache.keySet().removeIf(current -> loginDelayCache.get(current) < System.currentTimeMillis());

        if(!loginDelayCache.containsKey(uniqueId)) {
            loginDelayCache.put(uniqueId, System.currentTimeMillis() + delaySeconds * 1000);
            return false;
        }

        if(loginDelayCache.get(uniqueId) > System.currentTimeMillis())
            return true;

        loginDelayCache.put(uniqueId, System.currentTimeMillis() + delaySeconds * 1000);
        return false;
    }

}
