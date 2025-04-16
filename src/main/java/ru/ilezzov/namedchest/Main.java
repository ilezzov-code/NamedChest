package ru.ilezzov.namedchest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.namedchest.command.*;
import ru.ilezzov.namedchest.events.listeners.*;
import ru.ilezzov.namedchest.managers.BlockManager;
import ru.ilezzov.namedchest.managers.HoverManager;
import ru.ilezzov.namedchest.managers.BlockHoverManager;
import ru.ilezzov.namedchest.managers.VersionManager;
import ru.ilezzov.namedchest.models.PluginFile;
import ru.ilezzov.namedchest.logging.PaperLogger;
import ru.ilezzov.namedchest.messages.ConsoleMessages;
import ru.ilezzov.namedchest.models.PluginSettings;
import ru.ilezzov.namedchest.utils.ListUtils;
import ru.ilezzov.namedchest.logging.Logger;
import ru.ilezzov.namedchest.utils.Metrics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public final class Main extends JavaPlugin {
    // Plugin instance
    @Getter
    private static Main instance;

    // Logger
    @Getter
    private static Logger pluginLogger;

    // Settings
    @Getter
    private static PluginSettings pluginSettings;

    // Plugin info
    @Getter
    private static String prefix;
    @Getter
    private static List<String> pluginDevelopers;
    @Getter
    private static String pluginVersion;
    @Getter
    private static String pluginContactLink;
    @Getter
    private static boolean outdatedVersion;

    // Managers
    @Getter
    private static BlockManager blockManager;
    @Getter
    private static HoverManager hoverManager;
    @Getter
    private static BlockHoverManager blockHoverManager;
    @Getter
    private static VersionManager versionManager;

    // Files
    @Getter
    private static PluginFile configFile;
    @Getter
    private static PluginFile messagesFile;

    @Override
    public void onEnable() {
        instance = this;
        pluginLogger = new PaperLogger(this);

        // Load plugin files
        loadSettings();
        loadFiles();

        // Load manager
        blockManager = new BlockManager();
        hoverManager = new HoverManager();
        blockHoverManager = new BlockHoverManager();

        // Set plugin info
        loadPluginInfo();

        // Check plugin Version
        checkPluginVersion();

        // Register command and events
        registerCommands();
        registerEvents();

        sendEnableMessage();

        createBstatsMetrics();
    }

    @Override
    public void onDisable() {
        hoverManager.stop();
        blockHoverManager.killEntities();
        sendDisableMessage();
    }

    public static void loadFiles() {
        configFile = new PluginFile(Main.getInstance(), "config.yml");
        messagesFile = new PluginFile(Main.getInstance(), "messages/".concat(configFile.getString("language").concat(".yml")));
    }

    public static void registerCommands() {
        final PluginCommand mainCommand = Main.getInstance().getCommand("namedchest");

        if(mainCommand != null) {
            mainCommand.setExecutor(new MainCommand());
            mainCommand.setTabCompleter(new MainCommand());
        }
    }

    public static void reloadPrefix() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
    }

    public static void reloadEvents() {
        HandlerList.unregisterAll();
        registerEvents();
    }

    private void checkPluginVersion() {
        if (configFile.getBoolean("check_updates")) {
            try {
                versionManager = new VersionManager(pluginVersion, pluginSettings.getUrlToFileVersion());

                if (versionManager.check()) {
                    pluginLogger.info(ConsoleMessages.latestPluginVersion(pluginVersion));
                    outdatedVersion = false;
                } else {
                    pluginLogger.info(ConsoleMessages.outdatedPluginVersion(pluginVersion, versionManager.getCurrentPluginVersion(), pluginSettings.getUrlToDownloadLatestVersion()));
                    outdatedVersion = true;
                }
            } catch (URISyntaxException e) {
                pluginLogger.info(ConsoleMessages.errorOccurred("Invalid link to the GitHub file. link = ".concat(versionManager.getUrlToFileVersion())));
            } catch (IOException | InterruptedException e ) {
                pluginLogger.info(ConsoleMessages.errorOccurred("Couldn't send a request to get the plugin version"));
            }
        }
    }

    private void createBstatsMetrics() {
        if (pluginSettings.isBstatsEnable()) {
            new Metrics(this, pluginSettings.getBstatsPluginId());
        }
    }

    private static void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerHover(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerClickEvent(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new VersionCheckEvent(), Main.getInstance());

        if (configFile.getBoolean("name_settings.cancel_anvil")) {
            Bukkit.getPluginManager().registerEvents(new PlayerUseAnvilEvent(), Main.getInstance());
        }
        if (!configFile.getBoolean("name_settings.save_for_breaking")) {
            Bukkit.getPluginManager().registerEvents(new PlayerBreakBlockEvent(), Main.getInstance());
        }
    }

    private void sendEnableMessage() {
        pluginLogger.info(ConsoleMessages.enablePlugin(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }

    private void sendDisableMessage() {
        pluginLogger.info(ConsoleMessages.disablePlugin(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }

    private void loadSettings() {
        try {
            pluginSettings = new PluginSettings();
        } catch (IOException e) {
            pluginLogger.info("An error occurred when loading the plugin settings");
            throw new RuntimeException(e);
        }
    }

    private void loadPluginInfo() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
        pluginVersion = this.getDescription().getVersion();
        pluginDevelopers = this.getDescription().getAuthors();
        pluginContactLink = this.getDescription().getWebsite();
    }
}
