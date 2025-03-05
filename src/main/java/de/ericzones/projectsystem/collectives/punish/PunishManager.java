package de.ericzones.projectsystem.collectives.punish;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.FileConfig;
import de.ericzones.projectsystem.collectives.playtime.Playtime;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;

public class PunishManager {

    private final ProjectSystem instance;
    private final String fileName = "bans.yml";
    private final FileConfig fileConfig;

    private final List<Ban> bannedPlayers = new ArrayList<>();

    public PunishManager(ProjectSystem instance) {
        this.instance = instance;
        fileConfig = new FileConfig(instance, fileName);
        if(!fileConfig.isAvailable())
            System.out.println("[!] Plugin running without Punish module ("+fileName+")");
        loadStoredBans();
    }

    public Ban addBan(UUID uniqueId, Player creator, String reason, Long durationMinutes) {

        Ban ban = new Ban(uniqueId, creator.getUniqueId(), reason, durationMinutes);
        bannedPlayers.add(ban);
        fileConfig.setEntry(uniqueId.toString()+".creator", creator.getUniqueId().toString());
        fileConfig.setEntry(uniqueId.toString()+".reason", reason);
        fileConfig.setEntry(uniqueId.toString()+".expiry", String.valueOf(ban.getExpiry()));
        fileConfig.setEntry(uniqueId.toString()+".creation", String.valueOf(ban.getCreationTime()));

        Player target = Bukkit.getPlayer(uniqueId);
        if(target != null)
            target.kick(getBannedMessage(ban, true));
        return ban;
    }

    public void removeBan(Ban ban) {
        bannedPlayers.remove(ban);
        fileConfig.setEntry(ban.getUniqueId().toString(), null);
    }

    public Ban getBan(UUID uniqueId) {
        Ban ban = bannedPlayers.stream().filter(current -> current.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
        if(ban == null) return null;
        if(ban.getRemainingExpiry() <= 0) {
            removeBan(ban);
            return null;
        }
        return ban;
    }

    public Component getBannedMessage(Ban ban, boolean newBan) {
        Component text = Component.text("§8•● §eMinecraft §8× §7Project§r §8●•\n\n\n§8§m----------------------------§r\n");
        if(newBan)
            text = text.append(Component.text("§8• §cYou have been banned §8•\n\n§8§m--------------\n\n§8• §7"));
        else
            text = text.append(Component.text("§8• §cYou are banned §8•\n\n§8§m--------------\n\n§8• §7"));

        text = text.append(Component.text("§7Reason §8•\n§c" + ban.getReason() + "§r\n\n§8• §7"));

        if(newBan)
            text = text.append(Component.text("§7Duration §8•\n§c" + Playtime.formatPlaytime(ban.getTotalExpiry(), false)));
        else
            text = text.append(Component.text("§7Remaining §8•\n§c" + Playtime.formatPlaytime(ban.getRemainingExpiry(), false)));

        text = text.append(Component.text("\n\n§8§m----------------------------\n\n\n§7For further §equestions §7reach out to our §eDiscord\n§7Discord §8● §eMinecraft project"));
        return text;
    }

    private void loadStoredBans() {
        Set<String> keys = fileConfig.getMainKeys();
        List<UUID> uuids = new ArrayList<>();

        for(String current : keys) {
            uuids.add(UUID.fromString(current));
        }

        for (UUID current : uuids) {
            this.bannedPlayers.add(loadStoredBan(current));
        }
    }

    private Ban loadStoredBan(UUID uniqueId) {
        UUID creatorUniqueId = UUID.fromString(fileConfig.getValue(uniqueId.toString()+".creator"));
        String reason = fileConfig.getValue(uniqueId.toString()+".reason");
        long expiry = Long.parseLong(fileConfig.getValue(uniqueId.toString()+".expiry"));
        long creationTime = Long.parseLong(fileConfig.getValue(uniqueId.toString()+".creation"));
        return new Ban(uniqueId, creatorUniqueId, reason, expiry, creationTime);
    }

}
