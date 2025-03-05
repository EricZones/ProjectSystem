package de.ericzones.projectsystem.collectives.player;

import java.util.UUID;

public record ProjectPlayer(UUID uniqueId, String username, int connections, String firstConnection, String lastConnection, boolean moderator, int deaths, PlayerGroup group) {

}
