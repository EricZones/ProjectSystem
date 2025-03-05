package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.collectives.data.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class WarnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player && !sender.hasPermission("projectsystem.command.warn")) {
            sender.sendMessage(Message.PREFIX_PLUGIN+Message.NOPERMS);
            return true;
        }

        if(args.length < 2) {
            sender.sendMessage(Message.PREFIX_PUNISH+Message.SYNTAX.replace("%REPLACE%", "warn <Player> <Reason>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage(Message.PREFIX_PUNISH+Message.NOTONLINE);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1) {
                builder.append(" ");
            }
        }

        target.sendMessage(Message.PREFIX_PUNISH+"§7You have been §cwarned§7! Reason §8● §c"+builder.toString());
        Component title = Component.text("Warn").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
        Component subtitle = Component.text("Reason ").color(NamedTextColor.GRAY).append(Component.text("● ").color(NamedTextColor.DARK_GRAY).append(Component.text(builder.toString()).color(NamedTextColor.YELLOW)));
        Title message = Title.title(title, subtitle, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(10), Duration.ofSeconds(1)));
        target.showTitle(message);

        sender.sendMessage(Message.PREFIX_PUNISH+"§7Player §8'§7"+target.getName()+"§8' §7has been warned. Reason §8● §c"+builder.toString());

        return false;
    }
}
