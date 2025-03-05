package de.ericzones.projectsystem.collectives.communication;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.data.Pair;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager implements Listener {

    private final ProjectSystem instance;
    private final Map<UUID, Long> playerDelayCache = new HashMap<>();
    private final Map<UUID, Pair<Long, String>> playerRepeatCache = new HashMap<>();

    private final int messageRepeatSeconds = 30;
    private static final String[] socialCommands = new String[]{"/r", "/reply", "/msg"};

    public ChatManager(final ProjectSystem instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        if(!player.hasPermission("projectsystem.chat.admin")) {

            if(isMessageBlocked(player, message)) {
                e.setCancelled(true);
                return;
            }

            message = getLowerCaseMessage(message);
        }
        message = processMessage(message, true);

        Component messageComponent;
        if(player.hasPermission("projectsystem.chat.format"))
            messageComponent = Component.text(getColoredMessage(message, true));
        else
            messageComponent = Component.text(getColoredMessage(message, false));
        messageComponent = messageComponent.color(NamedTextColor.WHITE);

        Component formattedMessage = Component.text().append(getPlayerNameFormat(player)).append(messageComponent).build();
        e.renderer((source, sourceDisplayName, msg, viewer) -> formattedMessage);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if(!isSocialCommand(e.getMessage())) return;
        String command = e.getMessage();

        if(!player.hasPermission("projectsystem.chat.admin")) {
            for(int i = 0; i < socialCommands.length; i++)
                command = command.replace(socialCommands[i]+" ", "");
            if(isMessageBlocked(player, command)) {
                e.setCancelled(true);
                return;
            }
            command = getLowerCaseMessage(e.getMessage());
        }
        command = processMessage(command, false);

        if(player.hasPermission("projectsystem.chat.format"))
            command = getColoredMessage(command, true);
        else
            command = getColoredMessage(command, false);

        e.setMessage(command);
    }

    private boolean isMessageBlocked(Player player, String message) {
        if(isChatDelayed(player.getUniqueId())) {
            player.sendMessage(Message.PREFIX_CHAT + Message.SENTMESSAGETOOFAST);
            return true;
        }
        if(hasRepeatedMessage(player.getUniqueId(), message)) {
            player.sendMessage(Message.PREFIX_CHAT+Message.REPEATEDCHATMESSAGE);
            return true;
        }
        return false;
    }

    private Component getPlayerNameFormat(Player player) {
        Component prefix = instance.getPlayerManager().getPlayerGroup(player).getPrefix();
        Component playerName = Component.text(player.getName()).color(NamedTextColor.GRAY);
        playerName = playerName.append(Component.text(" » ").color(NamedTextColor.DARK_GRAY));

        return Component.text().append(prefix).append(playerName).build();
    }

    public static boolean isSocialCommand(String command) {
        for(int i = 0; i < socialCommands.length; i++) {
            if(command.toLowerCase().startsWith(socialCommands[i]+" "))
                return true;
        }
        return false;
    }

    private String processMessage(String message, boolean plainText) {
        if(plainText) {
            String regex = "^&[0-9a-fk-or] ";
            if (message.matches(regex + ".*")) {
                return message.replaceFirst(regex, "$0").replaceAll("(?<=&[0-9a-fk-or])\\s+", "");
            }
        } else {
            String pattern = "&[0-9a-fk-or]";
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = regex.matcher(message);

            if (matcher.find()) {
                int start = matcher.end();
                return message.substring(0, start) + message.substring(start).replaceAll("\\s+", "");
            }
        }

        return message;
    }

    private String getColoredMessage(String message, boolean advanced) {
        message = message.replace("&0", "§0");
        message = message.replace("&1", "§1");
        message = message.replace("&2", "§2");
        message = message.replace("&3", "§3");
        message = message.replace("&4", "§4");
        message = message.replace("&5", "§5");
        message = message.replace("&6", "§6");
        message = message.replace("&7", "§7");
        message = message.replace("&8", "§8");
        message = message.replace("&9", "§9");
        message = message.replace("&a", "§a");
        message = message.replace("&b", "§b");
        message = message.replace("&c", "§c");
        message = message.replace("&d", "§d");
        message = message.replace("&e", "§e");
        message = message.replace("&f", "§f");
        message = message.replace("&r", "§r");
        if(advanced) {
            message = message.replace("&k", "§k");
            message = message.replace("&l", "§l");
            message = message.replace("&m", "§m");
            message = message.replace("&n", "§n");
            message = message.replace("&o", "§o");
        }
        message = message.trim();
        return message;
    }

    private String getLowerCaseMessage(String message) {
        int uppercaseLetters = 0;
        for(int i = 0; i < message.length(); i++) {
            if (Character.isUpperCase(message.charAt(i)) && Character.isLetter(message.charAt(i)))
                uppercaseLetters++;
        }
        if((float) uppercaseLetters / (float) message.length() > 0.3F)
            return message.toLowerCase();
        return message;
    }

    private boolean isChatDelayed(UUID uniqueId) {
        playerDelayCache.keySet().removeIf(current -> playerDelayCache.get(current) < System.currentTimeMillis());

        if(!playerDelayCache.containsKey(uniqueId)) {
            playerDelayCache.put(uniqueId, System.currentTimeMillis()+750);
            return false;
        }

        if(playerDelayCache.get(uniqueId) > System.currentTimeMillis())
            return true;

        playerDelayCache.put(uniqueId, System.currentTimeMillis()+750);
        return false;
    }

    private boolean hasRepeatedMessage(UUID uniqueId, String message) {
        playerRepeatCache.keySet().removeIf(current -> playerRepeatCache.get(current).getFirstObject() < System.currentTimeMillis());

        if(!playerRepeatCache.containsKey(uniqueId)) {
            playerRepeatCache.put(uniqueId, new Pair<>(System.currentTimeMillis()+ messageRepeatSeconds *1000, message));
            return false;
        }

        if(playerRepeatCache.get(uniqueId).getSecondObject().equalsIgnoreCase(message))
            return true;

        playerRepeatCache.put(uniqueId, new Pair<>(System.currentTimeMillis()+ messageRepeatSeconds *1000, message));
        return false;
    }

}
