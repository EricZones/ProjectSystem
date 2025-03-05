package de.ericzones.projectsystem.collectives.communication;

import de.ericzones.projectsystem.collectives.data.Message;
import de.ericzones.projectsystem.collectives.data.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MessageManager {

    private final Map<UUID, Pair<Long, UUID>> messageCache = new HashMap<>();
    private final List<UUID> spyPlayers = new ArrayList<>();
    private boolean isConsoleSpying = false;

    private final int replySeconds = 5*60;

    public void sendMessage(Player source, Player target, String message) {
        source.sendMessage(Message.PREFIX_MESSAGE + "§7You §8➟ §7" + target.getName() + " §8» §e" + message);
        target.sendMessage(Message.PREFIX_MESSAGE + "§7" + source.getName() + " §8➟ §7You §8» §e" + message);

        messageCache.put(source.getUniqueId(), new Pair<>(System.currentTimeMillis()+ replySeconds *1000, target.getUniqueId()));
        messageCache.put(target.getUniqueId(), new Pair<>(System.currentTimeMillis()+ replySeconds *1000, source.getUniqueId()));

        sendSpyMessage(source, target, message);
    }

    public boolean replyToMessage(Player source, String message) {
        Player target = Bukkit.getPlayer(messageCache.get(source.getUniqueId()).getSecondObject());
        if(target == null) return false;
        sendMessage(source, target, message);
        return true;
    }

    private void sendSpyMessage(Player source, Player target, String message) {
        String spyMessage = Message.PREFIX_MESSAGE+"§7"+source.getName()+" §8➟ §7"+target.getName()+" §8» §e"+message;
        for(UUID current : spyPlayers) {
            Player spyPlayer = Bukkit.getPlayer(current);
            if(spyPlayer == null) continue;
            if(spyPlayer.getUniqueId() == source.getUniqueId() || spyPlayer.getUniqueId() == target.getUniqueId()) continue;
            spyPlayer.sendMessage(spyMessage);
        }
        if(isConsoleSpying)
            Bukkit.getConsoleSender().sendMessage(spyMessage);
    }

    public boolean canReplyMessage(UUID uniqueId) {
        messageCache.keySet().removeIf(current -> messageCache.get(current).getFirstObject() < System.currentTimeMillis());
        return messageCache.containsKey(uniqueId) && Bukkit.getPlayer(messageCache.get(uniqueId).getSecondObject()) != null;
    }

    public List<UUID> getSpyPlayers() {
        return spyPlayers;
    }

    public boolean isConsoleSpying() {
        return isConsoleSpying;
    }

    public void setConsoleSpying(boolean consoleSpying) {
        isConsoleSpying = consoleSpying;
    }

}
