package de.ericzones.projectsystem.collectives.punish;

import java.util.UUID;

public class Ban {

    private final UUID uniqueId, creatorUniqueId;
    private final String reason;
    private final Long expiry, creationTime;

    public Ban(UUID uniqueId, UUID creatorUniqueId, String reason, Long durationMinutes) {
        this.uniqueId = uniqueId;
        this.creatorUniqueId = creatorUniqueId;
        this.reason = reason;
        this.expiry = System.currentTimeMillis() + durationMinutes * 60 * 1000;
        this.creationTime = System.currentTimeMillis();
    }

    public Ban(UUID uniqueId, UUID creatorUniqueId, String reason, Long expiry, Long creationTime) {
        this.uniqueId = uniqueId;
        this.creatorUniqueId = creatorUniqueId;
        this.reason = reason;
        this.expiry = expiry;
        this.creationTime = creationTime;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getCreatorUniqueId() {
        return creatorUniqueId;
    }

    public String getReason() {
        return reason;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getExpiry() {
        return expiry;
    }

    public Long getTotalExpiry() {
        return expiry - creationTime;
    }

    public Long getRemainingExpiry() {
        return expiry - System.currentTimeMillis();
    }

}
