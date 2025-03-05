package de.ericzones.projectsystem.commands;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.communication.MessageManager;
import de.ericzones.projectsystem.collectives.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MsgCommand implements CommandExecutor {

    private final ProjectSystem instance;

    public MsgCommand(ProjectSystem instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        MessageManager messageManager = instance.getMessageManager();

        if(alias.equalsIgnoreCase("msg")) {
            if(args.length < 2) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.SYNTAX.replace("%REPLACE%", "msg <Player> <Message>"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.NOTONLINE);
                return true;
            }
            if(target.getUniqueId() == player.getUniqueId()) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.MESSAGE_SELF);
                return true;
            }

            if(!instance.getPlayerManager().canPlayerInteract(player, target)) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.NOTONLINE);
                return true;
            }

            String message = "";
            for (int i = 1; i < args.length; i++) {
                message += args[i] + " ";
            }
            message = message.trim();
            messageManager.sendMessage(player, target, message);
            return false;

        } else if(alias.equalsIgnoreCase("reply") || alias.equalsIgnoreCase("r")) {
            if(args.length == 0) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.SYNTAX.replace("%REPLACE%", "reply <Message>"));
                return true;
            }
            if(!messageManager.canReplyMessage(player.getUniqueId())) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.MESSAGE_NOREPLY);
                return true;
            }

            String message = "";
            for (int i = 0; i < args.length; i++) {
                message += args[i] + " ";
            }
            message = message.trim();

            if(!messageManager.replyToMessage(player, message)) {
                player.sendMessage(Message.PREFIX_MESSAGE+Message.NOTONLINE);
                return true;
            }
        }

        return false;
    }

}
