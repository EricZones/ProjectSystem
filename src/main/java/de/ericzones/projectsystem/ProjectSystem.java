package de.ericzones.projectsystem;

import de.ericzones.projectsystem.collectives.communication.ChatManager;
import de.ericzones.projectsystem.collectives.boards.TablistManager;
import de.ericzones.projectsystem.collectives.communication.CommandManager;
import de.ericzones.projectsystem.collectives.communication.MessageManager;
import de.ericzones.projectsystem.collectives.player.PlayerListener;
import de.ericzones.projectsystem.collectives.player.PlayerManager;
import de.ericzones.projectsystem.collectives.playtime.IdleManager;
import de.ericzones.projectsystem.collectives.playtime.PlaytimeListener;
import de.ericzones.projectsystem.collectives.playtime.PlaytimeManager;
import de.ericzones.projectsystem.collectives.punish.PunishListener;
import de.ericzones.projectsystem.collectives.punish.PunishManager;
import de.ericzones.projectsystem.collectives.restriction.Restriction;
import de.ericzones.projectsystem.commands.*;
import de.ericzones.projectsystem.listener.ConnectionListener;
import de.ericzones.projectsystem.listener.DeathListener;
import de.ericzones.projectsystem.listener.MiscListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ProjectSystem extends JavaPlugin {

    private PlaytimeManager playtimeManager;
    private PlayerManager playerManager;

    private TablistManager tablistManager;
    private ChatManager chatManager;
    private CommandManager commandManager;
    private MessageManager messageManager;
    private IdleManager idleManager;

    private PunishManager punishManager;
    private Restriction restriction;

    @Override
    public void onEnable() {
        registerModules();
        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable() {
        if(playtimeManager != null) {
            List<UUID> list = new ArrayList<>(playtimeManager.getActivePlaytimes().keySet());
            list.forEach(current -> playtimeManager.savePlaytime(current));
        }
        if(playerManager != null) {
            Bukkit.getOnlinePlayers().forEach(current -> playerManager.updatePlayerConfig(current, true));
        }
    }

    private void registerModules() {
        playtimeManager = new PlaytimeManager(this);
        playerManager = new PlayerManager(this);
        tablistManager = new TablistManager(this);
        chatManager = new ChatManager(this);
        commandManager = new CommandManager();
        messageManager = new MessageManager();
        idleManager = new IdleManager(this);
        punishManager = new PunishManager(this);
        restriction = new Restriction(this);
    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlaytimeListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ConnectionListener(this), this);
        pluginManager.registerEvents(chatManager, this);
        pluginManager.registerEvents(commandManager,  this);
        pluginManager.registerEvents(new DeathListener(this), this);
        pluginManager.registerEvents(new MiscListener(this), this);
        pluginManager.registerEvents(idleManager, this);
        pluginManager.registerEvents(new PunishListener(this), this);
    }

    private void registerCommands() {
        MsgCommand msgCommand = new MsgCommand(this);
        getCommand("msg").setExecutor(msgCommand);
        getCommand("reply").setExecutor(msgCommand);
        getCommand("r").setExecutor(msgCommand);
        getCommand("socialspy").setExecutor(new SocialspyCommand(this));
        getCommand("commandspy").setExecutor(new CommandspyCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand());
        EnderchestCommand enderchestCommand = new EnderchestCommand();
        getCommand("enderchest").setExecutor(enderchestCommand);
        getCommand("ec").setExecutor(enderchestCommand);
        OntimeCommand ontimeCommand = new OntimeCommand(this);
        getCommand("ontime").setExecutor(ontimeCommand);
        getCommand("onlinetime").setExecutor(ontimeCommand);
        getCommand("playtime").setExecutor(ontimeCommand);
        getCommand("info").setExecutor(new InfoCommand(this));
        getCommand("check").setExecutor(new CheckCommand(this));
        VanishCommand vanishCommand = new VanishCommand(this);
        getCommand("vanish").setExecutor(vanishCommand);
        getCommand("v").setExecutor(vanishCommand);
        BanCommand banCommand = new BanCommand(this);
        getCommand("ban").setExecutor(banCommand);
        getCommand("unban").setExecutor(banCommand);
        getCommand("checkban").setExecutor(banCommand);
        getCommand("warn").setExecutor(new WarnCommand());
        getCommand("maintenance").setExecutor(new MaintenanceCommand(this));
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public IdleManager getIdleManager() {
        return idleManager;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }

    public Restriction getRestriction() {
        return restriction;
    }
}
