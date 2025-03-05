package de.ericzones.projectsystem.listener;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.player.PlayerManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final ProjectSystem instance;

    public DeathListener(ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Location location = e.getEntity().getLocation();
        String dimension = getDimension(location);
        e.getEntity().sendMessage(Message.PREFIX_PLUGIN+"§7Last death §8● §7X: §e"+location.getBlockX()+" §7Y: §e"+location.getBlockY()+" §7Z: §e"+location.getBlockZ()+" §8("+dimension+"§8)");

        PlayerManager playerManager = instance.getPlayerManager();
        if(playerManager.isModerator(e.getEntity().getUniqueId())) {
            e.setKeepInventory(true);
            e.deathMessage(null);
        }
    }

    public static String getDimension(Location location) {
        if(location.getWorld() == null) return null;
        String dimension = "§aOverworld";
        if(location.getWorld().getEnvironment() == World.Environment.NETHER)
            dimension = "§cNether";
        else if(location.getWorld().getEnvironment() == World.Environment.THE_END)
            dimension = "§dEnd";
        return dimension;
    }

}
