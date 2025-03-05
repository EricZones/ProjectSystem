package de.ericzones.projectsystem.collectives.playtime;

import de.ericzones.projectsystem.ProjectSystem;
import de.ericzones.projectsystem.collectives.data.FileConfig;
import org.bukkit.Bukkit;

import java.util.*;

public class PlaytimeManager {

    private final String fileName = "playtimes.yml";

    private final HashMap<UUID, Playtime> playtimes;
    private final FileConfig fileConfig;

    private final Map<UUID, Playtime> sortedPlaytimeCache;
    private long playtimeCacheMillis = 0;

    public PlaytimeManager(ProjectSystem instance) {
        playtimes = new HashMap<>();
        fileConfig = new FileConfig(instance, fileName);
        if(!fileConfig.isAvailable())
            System.out.println("[!] Plugin running without Playtime module ("+fileName+")");
        sortedPlaytimeCache = new LinkedHashMap<>();
    }

    public void recordPlaytime(UUID uniqueId) {
        if(!fileConfig.containsEntry(uniqueId.toString())) {
            fileConfig.setEntry(uniqueId.toString(), "0");
            playtimes.put(uniqueId, new Playtime(0L));
            return;
        }
        playtimes.put(uniqueId, new Playtime(Long.parseLong(fileConfig.getValue(uniqueId.toString()))));
    }

    public void savePlaytime(UUID uniqueId) {
        Playtime playtime = playtimes.get(uniqueId);
        if(playtime == null) {
            Bukkit.getConsoleSender().sendMessage("§8[§c!§8] §7Player '"+uniqueId.toString()+"' left without playtime");
            return;
        }
        fileConfig.setEntry(uniqueId.toString(), String.valueOf(playtime.getTotalPlaytime()));
        playtimes.remove(uniqueId);
    }

    public void resetPlaytime(UUID uniqueId) {
        fileConfig.setEntry(uniqueId.toString(), "0");
        playtimes.remove(uniqueId);
        recordPlaytime(uniqueId);
    }

    public Playtime getPlaytime(UUID uniqueId) {
        return isOnline(uniqueId) ? playtimes.get(uniqueId) : getStoredPlaytime(uniqueId);
    }

    public HashMap<UUID, Playtime> getActivePlaytimes() {
        return playtimes;
    }

    public Map<UUID, Playtime> getSortedPlaytimes() {
        if(isSortedPlaytimesCached())
            return sortedPlaytimeCache;

        Map<UUID, Playtime> playtimes = new HashMap<>(this.playtimes);
        getStoredPlaytimes().forEach((uniqueId, playtime) -> {
            if(!playtimes.containsKey(uniqueId))
                playtimes.put(uniqueId, playtime);
        });

        Map<UUID, Playtime> sortedPlaytimes = sortPlaytimes(playtimes);
        this.sortedPlaytimeCache.putAll(sortedPlaytimes);
        this.playtimeCacheMillis = System.currentTimeMillis() + 30 * 1000;

        return sortedPlaytimes;
    }

    private boolean isSortedPlaytimesCached() {
        if(sortedPlaytimeCache.isEmpty()) return false;
        if(playtimeCacheMillis < System.currentTimeMillis()) {
            sortedPlaytimeCache.clear();
            return false;
        }
        return true;
    }

    private Map<UUID, Playtime> sortPlaytimes(Map<UUID, Playtime> playtimes) {
        List<Map.Entry<UUID, Playtime>> entryList = new ArrayList<>(playtimes.entrySet());
        entryList.sort((entry1, entry2) -> {
            Playtime playtime1 = entry1.getValue();
            Playtime playtime2 = entry2.getValue();
            long playtime1Value = playtime1.isActive() ? playtime1.getTotalPlaytime() : playtime1.getPreviousPlaytime();
            long playtime2Value = playtime2.isActive() ? playtime2.getTotalPlaytime() : playtime2.getPreviousPlaytime();
            return Long.compare(playtime2Value, playtime1Value);
        });

        Map<UUID, Playtime> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<UUID, Playtime> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private HashMap<UUID, Playtime> getStoredPlaytimes() {
        HashMap<UUID, Playtime> storedPlaytimes = new HashMap<>();
        fileConfig.getContent().forEach((uniqueId, value) -> {
            storedPlaytimes.put(UUID.fromString(uniqueId), new Playtime(Long.parseLong(value), 0));
        });
        return storedPlaytimes;
    }

    private Playtime getStoredPlaytime(UUID uniqueId) {
        String playtimeMillis = fileConfig.getValue(uniqueId.toString());
        if(playtimeMillis == null) return null;
        return new Playtime(Long.parseLong(playtimeMillis), 0);
    }

    private boolean isOnline(UUID uniqueId) {
        return playtimes.containsKey(uniqueId);
    }

}
