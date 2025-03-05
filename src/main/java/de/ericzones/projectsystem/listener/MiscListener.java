package de.ericzones.projectsystem.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.player.PlayerManager;
import de.ericzones.projectsystem.collectives.restriction.Restriction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Iterator;

public class MiscListener implements Listener {

    private final ProjectSystem instance;

    public MiscListener(ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onServerListPing(PaperServerListPingEvent e) {
        Restriction restriction = instance.getRestriction();
        if(restriction != null && restriction.isMaintenance())
            e.motd(restriction.getRestrictedMotd());

        PlayerManager playerManager = instance.getPlayerManager();
        if(playerManager == null) return;

        int removeCount = 0;
        Iterator<PaperServerListPingEvent.ListedPlayerInfo> iterator = e.getListedPlayers().iterator();
        while(iterator.hasNext()) {
            PaperServerListPingEvent.ListedPlayerInfo current = iterator.next();
            if(playerManager.isModerator(current.id())) {
                iterator.remove();
                removeCount++;
            }
        }
        e.setNumPlayers(e.getNumPlayers() - removeCount);

//        Iterator<Player> iterator = e.iterator();
//        while(iterator.hasNext()) {
//            Player player = iterator.next();
//            if(!player.isAllowingServerListings()) continue;
//
//            if(playerManager.isModerator(player.getUniqueId()))
//                iterator.remove();
//        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        for(Player current : Bukkit.getOnlinePlayers()) {
            if(Bukkit.getOnlinePlayers().size() == 3) return;
            if(!instance.getPlayerManager().isModerator(current.getUniqueId())) continue;
            current.setSleepingIgnored(true);
        }
    }

}
