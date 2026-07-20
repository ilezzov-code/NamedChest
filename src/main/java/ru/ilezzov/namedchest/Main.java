package ru.ilezzov.namedchest;

import com.sk89q.worldguard.WorldGuard;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.namedchest.command.CommandManager;
import ru.ilezzov.namedchest.event.EventManager;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;
import ru.ilezzov.namedchest.message.MessageManager;
import ru.ilezzov.namedchest.version.VersionControl;
import ru.ilezzov.namedchest.version.VersionData;
import ru.ilezzov.namedchest.version.VersionManager;
import ru.ilezzov.namedchest.version.VersionType;

import java.io.File;

import static ru.ilezzov.namedchest.BuildConfig.*;

public final class Main extends JavaPlugin {
    @Getter
    private PluginLogger pluginLogger;

    @Getter
    private ConsoleMessage consoleMessage;

    @Getter
    private ConfigFile configFile;
    @Getter
    private MessageFile messageFile;

    @Getter
    private MessageManager messageManager;
    @Getter
    private VersionManager versionManager;
    @Getter
    private VersionControl versionControl;

    @Getter
    private EventManager eventManager;

    @Getter
    private Metrics metrics;

    @Getter
    private WorldGuard worldGuard;
    @Getter
    private boolean supportWorldGuard = false;

    @Override
    public void onEnable() {
        final boolean isDebug = Boolean.getBoolean("ilezzov.debug");

        this.pluginLogger = new PluginLogger(this, isDebug);
        this.consoleMessage = new ConsoleMessage("messages");

        this.configFile = this.loadConfig();
        if (configFile.debug) {
            this.pluginLogger.setDebug(true);
            this.pluginLogger.debug(this.consoleMessage.getMessage("debug.enabled"));
        }

        final String messageFileName = this.configFile.language.concat(".yml");

        this.messageFile = loadMessageFile(messageFileName);
        this.messageManager = new MessageManager(this);

        this.pluginLogger.debug(this.consoleMessage.getMessage("file.message.loaded", messageFileName));

        this.versionManager = new VersionManager(this.pluginLogger, this.consoleMessage);
        this.versionManager.loadVersionData();

        if (this.configFile.versionControl.checkOnStartup) {
            if (!this.checkVersion()) {
                this.stop();
                return;
            }
        }

        this.versionControl = new VersionControl(this);
        this.versionControl.startBackgroundCheckTask();
        this.versionControl.startCriticalNotifyTask();

        this.eventManager = new EventManager(this);
        eventManager.registerEvents();

        new CommandManager(this).loadCommands();

        this.loadMetrics();
        this.loadWorldGuard();

        this.sendEnableMessage();
    }

    @Override
    public void onDisable() {
        if (this.versionControl != null) {
            versionControl.stop();
        }

        if (this.eventManager != null) {
            eventManager.unregisterEvents();
        }

        this.sendDisableMessage();
    }

    public void stop() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void reloadMessageFile(final String oldFile, final String file) {
        if (oldFile.equalsIgnoreCase(file)) {
            this.messageFile.load();
            return;
        }

        this.messageFile = loadMessageFile(file.concat(".yml"));
    }

    private ConfigFile loadConfig() {
        return (ConfigFile) ConfigManager.create(ConfigFile.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                    opt.bindFile(new File(this.getDataFolder(), "config.yml"));
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }

    private MessageFile loadMessageFile(final String file) {
        final File messageDir = new File(this.getDataFolder(), "messages");
        final File messageFile = new File(messageDir, file);

        if (!messageFile.exists()) {
            messageFile.getParentFile().mkdirs();
            this.saveResource("messages/".concat(file), false);
        }

        return (MessageFile) ConfigManager.create(MessageFile.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer());
                    opt.bindFile(messageFile);
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }

    private boolean checkVersion() {
        final VersionData versionData = this.versionManager.getVersionData();

        if (versionData == null) {
            return true;
        }

        final VersionType versionType = this.versionManager.getVersionType();
        final String latestVersion = versionData.getLatest().getVersion();
        final String latestDownloadLink = versionData.getLatest().getDownloadUrl();

        return switch (versionType) {
            case LATEST -> {
                this.pluginLogger.info(
                        this.consoleMessage.getMessage("version.message.latest", PLUGIN_VERSION)
                );
                yield true;
            }
            case SUPPORTED -> {
                this.pluginLogger.warn(
                        this.consoleMessage.getMessage("version.message.supported", PLUGIN_VERSION, latestVersion)
                );
                this.pluginLogger.warn(
                        this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                );
                yield true;
            }
            case OUTDATED -> {
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.outdated", PLUGIN_VERSION, this.consoleMessage.getMessage("version.message.action.impossible_launch"), latestVersion)
                );
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                );
                yield false;
            }
            case BLACKLIST -> {
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.blacklist", PLUGIN_VERSION, this.consoleMessage.getMessage("version.message.action.impossible_launch"), latestVersion)
                );
                this.pluginLogger.error(
                        this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                );
                yield false;
            }
            case UNREACHABLE -> true;
        };
    }

    private void loadMetrics() {
        this.metrics = new Metrics(this, BuildConfig.BSTATS_ID);
        this.metrics.addCustomChart(
                new SimplePie(BSTATS_LANGUAGE_CHART_ID, () -> this.configFile.language)
        );
    }

    private void sendEnableMessage() {
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.1")
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.2", NAME, PLUGIN_VERSION)
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.3")
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.worldguard", supportWorldGuard ?
                        this.consoleMessage.getMessage("worldguard.status.enable") :
                        this.consoleMessage.getMessage("worldguard.status.disable"))
        );
        sendFooter();
    }

    private void sendFooter() {
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.4", String.join(", ", this.getDescription().getAuthors()))
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.5", WEBSITE)
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.6", this.configFile.language)
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.7")
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.8", ISSUE_URL)
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.9")
        );
    }

    private void loadWorldGuard() {
        if (!this.configFile.supportWorldGuard) {
            return;
        }

        final Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null) {
            return;
        }

        this.worldGuard = WorldGuard.getInstance();
        this.supportWorldGuard = true;
    }

    private void sendDisableMessage() {
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.1")
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.disable.banner.2", NAME, PLUGIN_VERSION)
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.3")
        );
        sendFooter();
    }
}
