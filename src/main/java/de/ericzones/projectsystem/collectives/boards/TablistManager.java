package de.ericzones.projectsystem.collectives.boards;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.player.PlayerGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManager {

    private final ProjectSystem instance;

    public TablistManager(ProjectSystem instance) {
        this.instance = instance;
    }

    public void setPlayerList(Player player) {
        Component header = Component.text("\n      §6•§e● Project ●§6•      \n\n");
        Component footer = Component.text("\n\n§8● §7Minecraft 1.21 §8●\n");
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    public void setPlayerTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        Team admins = scoreboard.getTeam("01admins");
        if(admins == null)
            admins = scoreboard.registerNewTeam("01admins");

        Team season1 = scoreboard.getTeam("02season1");
        if(season1 == null)
            season1 = scoreboard.registerNewTeam("02season1");

        Team season2 = scoreboard.getTeam("03season2");
        if(season2 == null)
            season2 = scoreboard.registerNewTeam("03season2");

        Team season3 = scoreboard.getTeam("04season3");
        if(season3 == null)
            season3 = scoreboard.registerNewTeam("04season3");

        Team players = scoreboard.getTeam("05players");
        if(players == null)
            players = scoreboard.registerNewTeam("05players");

        Team idle = scoreboard.getTeam("06idle");
        if(idle == null)
            idle = scoreboard.registerNewTeam("06idle");

        Component text = PlayerGroup.ADMIN.getPrefix();
        admins.prefix(text);
        admins.color(NamedTextColor.GRAY);

        text = PlayerGroup.SEASON1.getPrefix();
        season1.prefix(text);
        season1.color(NamedTextColor.GRAY);

        text = PlayerGroup.SEASON2.getPrefix();
        season2.prefix(text);
        season2.color(NamedTextColor.GRAY);

        text = PlayerGroup.SEASON3.getPrefix();
        season3.prefix(text);
        season3.color(NamedTextColor.GRAY);

        text = PlayerGroup.PLAYER.getPrefix();
        players.prefix(text);
        players.color(NamedTextColor.GRAY);

        text = Component.text("AFK ").color(NamedTextColor.GRAY).append(Component.text("• ").color(NamedTextColor.DARK_GRAY));
        idle.prefix(text);
        idle.color(NamedTextColor.GRAY);

        for(Player current : Bukkit.getOnlinePlayers()) {
            if(instance.getIdleManager() != null && instance.getIdleManager().isPlayerIdle(current.getUniqueId())) {
                idle.addEntry(current.getName());
                continue;
            }

            switch (instance.getPlayerManager().getPlayerGroup(current)) {
                case PlayerGroup.ADMIN:
                    admins.addEntry(current.getName());
                    break;
                case PlayerGroup.SEASON1:
                    season1.addEntry(current.getName());
                    break;
                case PlayerGroup.SEASON2:
                    season2.addEntry(current.getName());
                    break;
                case PlayerGroup.SEASON3:
                    season3.addEntry(current.getName());
                    break;
                default:
                    players.addEntry(current.getName());
            }
        }
    }

}
