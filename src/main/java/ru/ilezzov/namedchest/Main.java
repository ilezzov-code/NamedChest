package ru.ilezzov.namedchest;

import com.sk89q.worldguard.WorldGuard;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimpleBarChart;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.namedchest.color.Colorizer;
import ru.ilezzov.namedchest.command.CommandManager;
import ru.ilezzov.namedchest.cooldown.CooldownManager;
import ru.ilezzov.namedchest.display.manager.HologramManager;
import ru.ilezzov.namedchest.event.EventManager;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;
import ru.ilezzov.namedchest.send.SendManager;
import ru.ilezzov.namedchest.version.VersionControl;
import ru.ilezzov.namedchest.version.VersionData;
import ru.ilezzov.namedchest.version.VersionManager;
import ru.ilezzov.namedchest.version.VersionType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private SendManager sendManager;
    @Getter
    private VersionManager versionManager;
    @Getter
    private VersionControl versionControl;
    @Getter
    private EventManager eventManager;
    @Getter
    private HologramManager hologramManager;

    @Getter
    private WorldGuard worldGuard;
    @Getter
    private boolean supportWorldGuard = false;

    @Getter
    private boolean supportDecentHolograms = false;

    // Cooldowns
    @Getter
    private final CooldownManager nameCooldownManager = new CooldownManager();

    // Metrics
    @Getter
    private Metrics metrics;

    @Override
    public void onEnable() {
        final boolean isDebug = Boolean.getBoolean("ilezzov.debug");

        this.pluginLogger = new PluginLogger(this, isDebug);
        this.consoleMessage = new ConsoleMessage("messages");

        final YamlConfiguration oldConfig = this.dumpFile(
                Path.of(this.getDataFolder().getPath(), "config.yml")
        );

        this.configFile = this.loadConfig();
        if (configFile.debug) {
            this.pluginLogger.setDebug(true);
            this.pluginLogger.debug(this.consoleMessage.getMessage("debug.enabled"));
        }

        if (oldConfig != null) {
            this.migrateConfig(oldConfig);
        }

        final String messageFileName = this.configFile.language.concat(".yml");
        final YamlConfiguration oldMessage = this.dumpFile(
                Path.of(this.getDataFolder().getPath(), "messages", messageFileName)
        );

        this.messageFile = loadMessageFile(messageFileName);
        if (oldMessage != null) {
            this.migrateMessage(oldMessage);
        }

        this.sendManager = new SendManager(this);

        this.pluginLogger.debug(this.consoleMessage.getMessage("file.message.loaded", messageFileName));

        this.versionManager = new VersionManager(this.pluginLogger, this.consoleMessage);
        if (this.configFile.versionControl.checkOnStartup) {
            this.versionManager.loadVersionData();
            if (!this.checkVersion()) {
                this.stop();
                return;
            }
        }

        this.versionControl = new VersionControl(this);
        this.versionControl.startBackgroundCheckTask();
        this.versionControl.startCriticalNotifyTask();

        this.loadWorldGuard();
        this.loadDecentHolograms();

        this.hologramManager = new HologramManager(this);
        this.hologramManager.start();

        this.eventManager = new EventManager(this);
        eventManager.registerEvents();

        new CommandManager(this).loadCommands();

        this.loadMetrics();

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

        if (this.hologramManager != null) {
            this.hologramManager.killAll();
            this.hologramManager.stop();
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

    private YamlConfiguration dumpFile(final Path sourcePath) {
        final String fileName = sourcePath.getFileName().toString();
        this.pluginLogger.debug(this.consoleMessage.getMessage("file.dump.started", fileName));

        final long start = System.currentTimeMillis();

        if (Files.notExists(sourcePath)) {
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("file.dump.old-file.not-found", fileName)
            );
            return null;
        }

        final YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(sourcePath.toFile());

        if (!oldConfig.contains("config_version")) {
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("file.dump.old-file.missing-keys", fileName)
            );
            return null;
        }

        final Path dumpPath = sourcePath.resolveSibling(fileName + ".dump");

        try {
            Files.copy(sourcePath, dumpPath, StandardCopyOption.REPLACE_EXISTING);

            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("file.dump.success", fileName, System.currentTimeMillis() - start)
            );
            return oldConfig;
        } catch (final IOException e) {
            this.pluginLogger.error(
                    this.consoleMessage.getMessage("file.dump.failed", e.getMessage(), e)
            );
            return null;
        }
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

    private void migrateConfig(final YamlConfiguration oldConfig) {
        final String fileName = oldConfig.getName();
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("migration.file.started", fileName)
        );
        final long start = System.currentTimeMillis();

        if (oldConfig.contains("check_updates")) {
            this.configFile.versionControl.checkOnStartup = oldConfig.getBoolean("check_updates");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "check_updates", "version-control.check-on-startup")
            );
        }
        if (oldConfig.contains("supporting_block")) {
            this.configFile.supportedBlocks = parseMaterial(oldConfig.getStringList("supporting_block"));
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "supporting_block", "supported-blocks")
            );
        }

        if (oldConfig.contains("max_distance")) {
            this.configFile.maxDistance = oldConfig.getInt("max_distance");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "max_distance", "max-distance")
            );
        }

        if (oldConfig.contains("support_worldguard")) {
            this.configFile.supportWorldGuard = oldConfig.getBoolean("support_worldguard");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "support_worldguard", "support-worldguard")
            );
        }

        if (oldConfig.contains("name_settings.support_color")) {
            this.configFile.nameSettings.supportColor = oldConfig.getBoolean("name_settings.support_color");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "name_settings.support_color", "name-settings.support-color")
            );
        }

        if (oldConfig.contains("name_settings.max_name_length")) {
            this.configFile.nameSettings.maxLength = oldConfig.getInt("name_settings.max_name_length");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "name_settings.max_name_length", "name-settings.max-length")
            );
        }

        if (oldConfig.contains("name_settings.save_for_breaking")) {
            this.configFile.nameSettings.saveWhenBreaking = oldConfig.getBoolean("name_settings.save_for_breaking");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "name_settings.save_for_breaking", "name-settings.save-when-breaking")
            );
        }

        if (oldConfig.contains("name_settings.cancel_anvil")) {
            this.configFile.nameSettings.cancelAnvil = oldConfig.getBoolean("name_settings.cancel_anvil");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "name_settings.cancel_anvil", "name-settings.cancel-anvil")
            );
        }

        if (oldConfig.contains("name_settings.support_spaces")) {
            this.configFile.nameSettings.supportSpaces = oldConfig.getBoolean("name_settings.support_spaces");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "name_settings.support_spaces", "name-settings.support-spaces")
            );
        }

        if (oldConfig.contains("command_settings.enable_set")) {
            this.configFile.commandSettings.enableSet = oldConfig.getBoolean("command_settings.enable_set");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "command_settings.enable_set", "command-settings.enable-set")
            );
        }

        if (oldConfig.contains("command_settings.enable_clear")) {
            this.configFile.commandSettings.enableClear = oldConfig.getBoolean("command_settings.enable_clear");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "command_settings.enable_clear", "command-settings.enable-clear")
            );
        }

        if (oldConfig.contains("command_settings.cooldown")) {
            this.configFile.commandSettings.cooldown = oldConfig.getInt("command_settings.cooldown");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "command_settings.cooldown", "command-settings.cooldown")
            );
        }

        this.configFile.save();
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("migration.file.completed", fileName, System.currentTimeMillis() - start)
        );
    }

    private Set<Material> parseMaterial(final List<String> materialList) {
        final Set<Material> materialSet = new HashSet<>();

        for (final String materialString : materialList) {
            final Material material = Material.matchMaterial(materialString);

            if (material != null) {
                materialSet.add(material);
            } else {
                this.pluginLogger.debug(
                        this.consoleMessage.getMessage("migration.bukkit.material-parse.failed", materialString)
                );
            }
        }

        return materialSet;
    }

    private void migrateMessage(final YamlConfiguration oldMessage) {
        final String fileName = oldMessage.getName();
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("migration.file.started", fileName)
        );
        final long start = System.currentTimeMillis();

        boolean migrated = false;

        if (oldMessage.contains("Plugin.plugin-prefix")) {
            this.messageFile.plugin.prefix = oldMessage.getString("Plugin.plugin-prefix");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Plugin.plugin-prefix", "plugin.prefix")
            );
            this.messageFile.plugin.prefixError = oldMessage.getString("Plugin.plugin-prefix");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Plugin.plugin-prefix", "plugin.prefix-error")
            );
            migrated = true;
        }
        if (oldMessage.contains("Plugin.plugin-reload")) {
            this.messageFile.plugin.reload = oldMessage.getString("Plugin.plugin-reload");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Plugin.plugin-reload", "plugin.reload")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-no-console-error")) {
            this.messageFile.plugin.noConsole = oldMessage.getString("Messages.command-no-console-error");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-no-console-error", "plugin.no-console")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-permission-error")) {
            this.messageFile.plugin.noPerms = oldMessage.getString("Messages.command-permission-error");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-permission-error", "plugin.no-perms")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-disable")) {
            this.messageFile.plugin.commandDisable = oldMessage.getString("Messages.command-disable");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-disable", "plugin.command-disable")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-cooldown")) {
            String cooldownMsg = oldMessage.getString("Messages.command-cooldown");
            if (cooldownMsg != null) {
                cooldownMsg = cooldownMsg.replace("{TIME}", "{COOLDOWN}");
            }
            this.messageFile.plugin.cooldown = cooldownMsg;
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-cooldown", "plugin.cooldown")
            );
            migrated = true;
        }

        if (oldMessage.contains("Plugin.plugin-use-latest-version")) {
            this.messageFile.version.latest = oldMessage.getString("Plugin.plugin-use-latest-version");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Plugin.plugin-use-latest-version", "version.latest")
            );
            migrated = true;
        }

        if (oldMessage.contains("Plugin.plugin-use-outdated-version")) {
            this.messageFile.version.outdated = oldMessage.getString("Plugin.plugin-use-outdated-version");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Plugin.plugin-use-outdated-version", "version.outdated")
            );
            migrated = true;
        }

        if (oldMessage.contains("Plugin.plugin-has-error-check-version")) {
            this.messageFile.version.error = oldMessage.getString("Plugin.plugin-has-error-check-version");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Plugin.plugin-has-error-check-version", "version.error")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-main-message")) {
            this.messageFile.mainCommand.help = oldMessage.getString("Messages.command-main-message");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-main-message", "main-command.help")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-set")) {
            this.messageFile.mainCommand.set = oldMessage.getString("Messages.command-name-set");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-set", "main-command.set")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-clear")) {
            this.messageFile.mainCommand.clear = oldMessage.getString("Messages.command-name-clear");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-clear", "main-command.clear")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-not-owned-region")) {
            this.messageFile.mainCommand.error.notYourRegion = oldMessage.getString("Messages.command-not-owned-region");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-not-owned-region", "main-command.error.not-your-region")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-max-length")) {
            this.messageFile.mainCommand.error.exceededNameLength = oldMessage.getString("Messages.command-name-max-length");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-max-length", "main-command.error.exceeded-name-length")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-empty")) {
            this.messageFile.mainCommand.error.emptyName = oldMessage.getString("Messages.command-name-empty");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-empty", "main-command.error.empty-name")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-block-null")) {
            this.messageFile.mainCommand.error.nullBlock = oldMessage.getString("Messages.command-name-block-null");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-block-null", "main-command.error.null-block")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-block-error")) {
            this.messageFile.mainCommand.error.unsupportedBlock = oldMessage.getString("Messages.command-name-block-error");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-block-error", "main-command.error.unsupported-block")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-use-anvil-error")) {
            this.messageFile.mainCommand.error.cancelAnvil = oldMessage.getString("Messages.command-name-use-anvil-error");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-use-anvil-error", "main-command.error.cancel-anvil")
            );
            migrated = true;
        }

        if (oldMessage.contains("Messages.command-name-already-clear")) {
            this.messageFile.mainCommand.error.alreadyClear = oldMessage.getString("Messages.command-name-already-clear");
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("migration.file.key-migrated", "Messages.command-name-already-clear", "main-command.error.already-clear")
            );
            migrated = true;
        }

        if (migrated) {
            this.messageFile.colorizer = Colorizer.ALL;
        }

        this.messageFile.save();
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("migration.file.completed", fileName, System.currentTimeMillis() - start)
        );
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
        this.metrics.addCustomChart(
                new SimpleBarChart("soft_depend", () -> Map.of(
                        "WorldGuard", this.supportWorldGuard ? 1 : 0)
                )
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
                this.consoleMessage.getMessage("plugin.enable.banner.line")
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.depend", "WorldGuard", supportWorldGuard ?
                        this.consoleMessage.getMessage("depend.status.enable") :
                        this.consoleMessage.getMessage("depend.status.disable"))
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.depend", "DecentHolograms", supportDecentHolograms ?
                        this.consoleMessage.getMessage("depend.status.enable") :
                        this.consoleMessage.getMessage("depend.status.disable"))
        );
        sendFooter();
    }

    private void sendFooter() {
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.line")
        );
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
                this.consoleMessage.getMessage("plugin.enable.banner.line")
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

    private void loadDecentHolograms() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("DecentHolograms");

        if (plugin == null) {
            return;
        }

        this.supportDecentHolograms = true;
    }

    private void sendDisableMessage() {
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.1")
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.disable.banner.2", NAME, PLUGIN_VERSION)
        );
        this.pluginLogger.info(
                this.consoleMessage.getMessage("plugin.enable.banner.line")
        );
        sendFooter();
    }
}
