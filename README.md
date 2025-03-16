# ProjectSystem

ProjectSystem is a Minecraft plugin which is applicable to survival multiplayer projects. It adds administrating features, statistics and many more useful additions for a better multiplayer experience.

## Features
- **PlayerManager:**
    - Storing player data in local files to access it via commands
    - Accessible player information for offline players
    - Player groups for admins and seasons with prefixes
- **PlaytimeManager:**
    - Recording playtime of players
    - Idle detection with prefix for idle players
    - Display top playtime players in a ranking
- **PunishManager:**
    - Warning players with custom title message
    - Ban players
    - Login delay to avoid fast reconnecting
- **Messaging:**
    - Spam and repeated message protection
    - Automated lowercase conversion of uppercase-messages
    - Colored messages by using color codes
    - Custom restricted and disabled commands
    - Private messaging
- **Administration:**
    - Check player data for offline and online players
    - Display executed commands by others
    - Open players enderchests and inventories
    - Vanish to hide from other players
- **Others:**
    - Maintenance mode with custom motd
    - Custom tablist
    - Message with coordinates for latest death location
    - Display ping of players

## Installation & Execution
### Requirements
- Java Development Kit (JDK) 21 or higher
- Gradle
- Minecraft Paper 1.21 server

### Build process
```bash
  gradle build
  
  cd build/libs
  ```

### Execution
1. Move 'ProjectSystem-1.0-SNAPSHOT.jar' inside your minecraft servers plugin folder
2. Start your minecraft server

## Controls
| Command      | Result                                     |
|--------------|--------------------------------------------|
| /msg         | Send private messages                      |
| /r           | Reply to latest message                    |
| /commandspy  | Display commands executed by others        |
| /ping        | Display ping of players                    |
| /invsee      | Open a players inventory                   |
| /enderchest  | Open a players enderchest                  |
| /ontime      | Display playtime                           |
| /info        | Display data of online and offline players |
| /check       | Display information about a player         |
| /vanish      | Hide from other players                    |
| /ban         | Ban players                                |
| /checkban    | Check bans of players                      |
| /unban       | Unban players                              |
| /warn        | Warn players with a message                |
| /maintenance | Toggle maintenance mode                    |

- Some commands can be executed with permissions only

## Purpose
The plugin was created for a vanilla multiplayer minecraft project.
It was originally developed in 2023 without git.
