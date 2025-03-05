package de.ericzones.projectsystem.collectives.playtime;

public class Playtime {

    private final long previousPlaytime;
    private final long connectionTime;

    public Playtime(long previousPlaytime) {
        this.previousPlaytime = previousPlaytime;
        this.connectionTime = System.currentTimeMillis();
    }

    public Playtime(long previousPlaytime, long connectionTime) {
        this.previousPlaytime = previousPlaytime;
        this.connectionTime = connectionTime;
    }

    public long getPreviousPlaytime() {
        return previousPlaytime;
    }

    public long getConnectionTime() {
        return connectionTime;
    }

    public long getCurrentPlaytime() {
        return System.currentTimeMillis() - connectionTime;
    }

    public long getTotalPlaytime() {
        return getCurrentPlaytime() + previousPlaytime;
    }

    public boolean isActive() {
        return connectionTime != 0;
    }

    public static String formatPlaytime(long playtimeMillis, boolean includeSeconds) {
        long seconds = (playtimeMillis / 1000) % 60;
        long minutes = (playtimeMillis / (1000 * 60)) % 60;
        long hours = (playtimeMillis / (1000 * 60 * 60)) % 24;
        long days = playtimeMillis / (1000 * 60 * 60 * 24);

        if(includeSeconds)
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        else
            return String.format("%dd %dh %dm", days, hours, minutes);
    }

}
