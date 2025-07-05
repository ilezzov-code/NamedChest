package ru.ilezzov.namedchest;

import com.sk89q.worldguard.WorldGuard;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.namedchest.api.ApiImpl;
import ru.ilezzov.namedchest.api.NamedChestAPI;
import ru.ilezzov.namedchest.events.EventManager;
import ru.ilezzov.namedchest.file.PluginFile;
import ru.ilezzov.namedchest.logging.Logger;
import ru.ilezzov.namedchest.logging.PaperLogger;
import ru.ilezzov.namedchest.managers.BlockHoverManager;
import ru.ilezzov.namedchest.managers.BlockTypeManager;
import ru.ilezzov.namedchest.managers.VersionManager;
import ru.ilezzov.namedchest.messages.ConsoleMessages;
import ru.ilezzov.namedchest.settings.PluginSettings;
import ru.ilezzov.namedchest.stats.PluginStats;
import ru.ilezzov.namedchest.utils.ListUtils;

import java.io.IOException;
import java.io.Serial;
import java.net.URISyntaxException;
import java.util.List;

import static ru.ilezzov.namedchest.commands.CommandManager.loadCommands;
import static ru.ilezzov.namedchest.messages.ConsoleMessages.*;

public final class Main extends JavaPlugin {
    // API
    @Getter
    private static NamedChestAPI api;

    @Getter
    private static Logger pluginLogger;
    @Getter
    private static Main instance;

    // Settings
    @Getter
    private static PluginSettings pluginSettings;

    // Plugin info
    @Getter
    private static String prefix;
    @Getter
    private static String pluginVersion;
    @Getter
    private static String pluginContactLink;
    @Getter
    private static List<String> pluginDevelopers;
    @Getter
    private static boolean outdatedVersion;
    @Getter
    private static String messageLanguage;
    @Getter
    @Setter
    private static boolean supportWorldGuard = false;

    // Files
    @Getter
    private static PluginFile configFile;
    @Getter
    private static PluginFile messagesFile;

    // Managers
    @Getter
    private static VersionManager versionManager;
    @Getter
    private static BlockTypeManager blockTypeManager;
    @Getter
    private static BlockHoverManager blockHoverManager;

    // Events
    @Getter
    private static EventManager eventManager;

    @Getter
    private static WorldGuard worldGuard;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginLogger = new PaperLogger(this);
        instance = this;
        api = loadApi();

        // Load files
        loadSettings();
        loadFiles();

        // Load plugin info
        loadPluginInfo();

        // Check plugin version
        checkPluginVersion();

        // Load WorldGuard
        loadWorldGuard();

        // Load managers
        loadManagers();

        // Load commands and events
        loadCommands();
        loadEvents();

        // Load Metrics
        loadMetrics();

        // Send enable message
        sendEnableMessage();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (blockHoverManager != null) {
            blockHoverManager.stopTask();
            blockHoverManager.killAll();
        }
    }

    public static void checkPluginVersion() {
        if (configFile.getBoolean("check_updates")) {
            try {
                versionManager = new VersionManager(pluginVersion, pluginSettings.getUrlToFileVersion());

                if (versionManager.check()) {
                    pluginLogger.info(latestPluginVersion(pluginVersion));
                    outdatedVersion = false;
                } else {
                    pluginLogger.info(legacyPluginVersion(pluginVersion, versionManager.getCurrentPluginVersion(), pluginSettings.getUrlToDownloadLatestVersion()));
                    outdatedVersion = true;
                }
            } catch (URISyntaxException e) {
                pluginLogger.info(errorOccurred("Invalid link to the GitHub file. link = ".concat(versionManager.getUrlToFileVersion())));
            } catch (IOException | InterruptedException e ) {
                pluginLogger.info(errorOccurred("Couldn't send a request to get the plugin version"));
            }
        }
    }


    public static void loadWorldGuard() {
        final boolean supportWorldGuard = configFile.getBoolean("support_worldguard");

        if (!supportWorldGuard) {
            return;
        }

        final Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (worldGuardPlugin == null) {
            return;
        }

        worldGuard = WorldGuard.getInstance();
        setSupportWorldGuard(true);
        pluginLogger.info(ConsoleMessages.supportedWorldGuard());
    }

    private void loadSettings() {
        try {
            pluginSettings = new PluginSettings();
        } catch (IOException e) {
            pluginLogger.info(errorOccurred(e.getMessage()));
        }
    }

    public static void loadFiles() {
        configFile = new PluginFile(Main.getInstance(), "config.yml");
        messageLanguage = configFile.getString("language");
        messagesFile = new PluginFile(Main.getInstance(), "messages/".concat(messageLanguage).concat(".yml"));
    }

    public static NamedChestAPI loadApi() {
        return new ApiImpl();
    }

    private void loadEvents() {
        eventManager = new EventManager(this);
        eventManager.loadEvents();
    }

    private void loadPluginInfo() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
        pluginVersion = this.getDescription().getVersion();
        pluginDevelopers = this.getDescription().getAuthors();
        pluginContactLink = this.getDescription().getWebsite();
    }

    public static void reloadPluginInfo() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
    }

    private void loadManagers() {
        blockTypeManager = new BlockTypeManager();
        blockHoverManager = new BlockHoverManager();
    }

    private void loadMetrics() {
        new PluginStats(this);
    }

    private void sendEnableMessage() {
        pluginLogger.info(pluginEnable(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }
}
