package de.ericzones.projectsystem.listener;

import com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent;
import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.player.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final ProjectSystem instance;

    public ConnectionListener(ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        if(instance.getRestriction().canPlayerJoin(player)) return;
        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, instance.getRestriction().getRestrictedText(false));
    }

    @EventHandler
    public void onWhitelistCheck(ProfileWhitelistVerifyEvent e) {
        if(e.isWhitelistEnabled() && !e.isWhitelisted()) {
            e.kickMessage(instance.getRestriction().getRestrictedText(true));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        PlayerManager playerManager = instance.getPlayerManager();
        if(playerManager.isModerator(player.getUniqueId())) {
            e.joinMessage(null);
        } else {
            Component message = getConnectionMessage(player, true);
            e.joinMessage(message);
        }

        Bukkit.getOnlinePlayers().forEach(playerManager::updateVisibility);

        instance.getTablistManager().setPlayerList(player);
        Bukkit.getOnlinePlayers().forEach(current -> instance.getTablistManager().setPlayerTeams(current));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        PlayerManager playerManager = instance.getPlayerManager();
        if(playerManager.isModerator(player.getUniqueId())) {
            e.quitMessage(null);
        } else {
            Component message = getConnectionMessage(player, false);
            e.quitMessage(message);
        }
    }

    public static Component getConnectionMessage(Player player, boolean join) {
        Component template;
        if(join) {
            template = Component.text("[").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("+").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("] ").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false));
        } else {
            template = Component.text("[").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("-").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .append(Component.text("] ").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false));
        }
        Component playerName = Component.text(player.getName()).color(NamedTextColor.GRAY);
        return Component.text().append(template).append(playerName).build();
    }

}
