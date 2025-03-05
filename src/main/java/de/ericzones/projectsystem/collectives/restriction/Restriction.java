package de.ericzones.projectsystem.collectives.restriction;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.FileConfig;
import de.ericzones.projectsystem.collectives.playtime.Playtime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class Restriction {

    private final String fileName = "maintenance.yml";
    private final FileConfig fileConfig;

    private boolean maintenance;
    private Long duration;

    public Restriction(ProjectSystem instance) {
        fileConfig = new FileConfig(instance, fileName);
        if(!fileConfig.isAvailable())
            System.out.println("[!] Plugin running without Maintenance module ("+fileName+")");
        loadMaintenance();
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public boolean canPlayerJoin(Player player) {
        if(!maintenance)
            return true;
        if(player.hasPermission("projectsystem.maintenance.ignore"))
            return true;
        return false;
    }

    public void setMaintenace(boolean maintenance, Long durationMinutes) {
        this.maintenance = maintenance;
        fileConfig.setEntry("status", String.valueOf(maintenance));
        if(maintenance) {
            this.duration = System.currentTimeMillis() + durationMinutes * 60 * 1000;
            fileConfig.setEntry("duration", String.valueOf(this.duration));
            return;
        }
        fileConfig.setEntry("duration", String.valueOf(0));
        this.duration = 0L;
    }

    private void loadMaintenance() {
        if(!fileConfig.containsEntry("status")) {
            fileConfig.setEntry("status", String.valueOf(false));
            fileConfig.setEntry("duration", String.valueOf(0));
            maintenance = false;
            duration = 0L;
        } else {
            maintenance = Boolean.parseBoolean(fileConfig.getValue("status"));
            duration = Long.parseLong(fileConfig.getValue("duration"));
        }
    }

    public Component getRestrictedMotd() {
        return Component.text("§e§lMinecraft§8 - §7Minecraft Project§8 [§e1.21§8]§r\n §8»§7 Currently in §cMaintenance");
    }

    public Component getRestrictedText(boolean whitelist) {
        Component text = Component.text("§8•● §eMinecraft §8× §7Project§r §8●•\n\n\n§8§m----------------------------§r\n");
        text = text.append(Component.text("§8• §cConnection declined §8•\n\n§8§m--------------\n\n§8• §7Reason §8•\n"));

        if(whitelist)
            text = text.append(Component.text("§cAccount not allowed§r"));
        else {
            text = text.append(Component.text("§cMaintenance§r\n\n§8• §7Remaining §8•\n"));
            text = text.append(Component.text("§c"+ Playtime.formatPlaytime(this.duration - System.currentTimeMillis(), false) +"§r"));
        }

        text = text.append(Component.text("\n\n§8§m----------------------------\n\n\n§7For further §equestions §7reach out to our §eDiscord\n§7Discord §8● §eMinecraft project"));
        return text;
    }

    public Component getDisallowedText(int seconds) {
        return Component.text("Connections just every ").color(NamedTextColor.GRAY)
                .append(Component.text(seconds+" seconds").color(NamedTextColor.RED))
                .append(Component.text(" possible").color(NamedTextColor.GRAY));
    }

}
