package ru.ilezzov.namedchest.version;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.color.Colorizer;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;
import ru.ilezzov.namedchest.permission.PermissionManager;
import ru.ilezzov.namedchest.permission.Permissions;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;

import static ru.ilezzov.namedchest.BuildConfig.PLUGIN_VERSION;

public class VersionControl {
    private final Main plugin;
    private final PluginLogger pluginLogger;
    private final VersionManager versionManager;
    private final ConsoleMessage consoleMessage;
    private final ConfigFile configFile;

    public VersionControl(final Main main) {
        this.plugin = main;
        this.pluginLogger = main.getPluginLogger();
        this.versionManager = main.getVersionManager();
        this.consoleMessage = main.getConsoleMessage();
        this.configFile = main.getConfigFile();
    }

    private BukkitTask backgroundCheckTask;
    private BukkitTask criticalNotifyTask;

    public void startBackgroundCheckTask() {
        if (this.backgroundCheckTask != null && !this.backgroundCheckTask.isCancelled()) {
            this.backgroundCheckTask.cancel();
        }

        final ConfigFile.VersionControl versionControlSection = this.configFile.versionControl;
        final ConfigFile.Interval interval = versionControlSection.checkInterval;
        final ConfigFile.Security versionSecuritySection = versionControlSection.security;

        final long periodInSeconds = interval.unit.toSeconds(interval.value);
        final long period = periodInSeconds * 20L;

        if (!interval.enable) {
            return;
        }

        this.pluginLogger.debug(
                this.consoleMessage.getMessage("version.control.task.background_check.started", periodInSeconds)
        );
        this.backgroundCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this.plugin,
                () -> {
                    this.pluginLogger.debug(
                            this.consoleMessage.getMessage("version.control.task.background_check.working")
                    );

                    this.versionManager.loadVersionData();
                    final VersionData versionData = this.versionManager.getVersionData();

                    if (versionData == null) {
                        return;
                    }

                    final VersionType versionType = this.versionManager.getVersionType();
                    final String latestVersion = versionData.getLatest().getVersion();
                    final String latestDownloadLink = versionData.getLatest().getDownloadUrl();
                    final String action = versionSecuritySection.lockdownOnCritical ?
                            this.consoleMessage.getMessage("version.message.action.auto_stopping") : this.consoleMessage.getMessage("version.message.action.no_recommended");

                    final boolean accepted = switch (versionType) {
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
                                    this.consoleMessage.getMessage("version.message.outdated", PLUGIN_VERSION, action, latestVersion)
                            );
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                            );
                            yield false;
                        }
                        case BLACKLIST -> {
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.blacklist", PLUGIN_VERSION, action, latestVersion)
                            );
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                            );
                            yield false;
                        }
                        case UNREACHABLE -> true;
                    };

                    if (!accepted) {
                        if (versionSecuritySection.lockdownOnCritical) {
                            this.plugin.stop();
                        }
                    }

                }, period, period
        );
    }

    public void startCriticalNotifyTask() {
        if (this.criticalNotifyTask != null && !this.criticalNotifyTask.isCancelled()) {
            this.criticalNotifyTask.cancel();
        }

        final ConfigFile.Interval interval = this.configFile.versionControl.security.criticalNotifyInterval;
        final long periodInSeconds = interval.unit.toSeconds(interval.value);
        final long period = periodInSeconds * 20L;

        if (!interval.enable) {
            return;
        }

        this.pluginLogger.debug(
                this.consoleMessage.getMessage("version.control.task.critical_notify.started", periodInSeconds)
        );
        this.criticalNotifyTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this.plugin,
                () -> {
                    this.pluginLogger.debug(
                            this.consoleMessage.getMessage("version.control.task.critical_notify.working")
                    );

                    final VersionData versionData = this.versionManager.getVersionData();
                    if (versionData == null) {
                        return;
                    }

                    final String latestVersion = versionData.getLatest().getVersion();
                    final String latestDownloadLink = versionData.getLatest().getDownloadUrl();

                    final PluginPlaceholder placeholder = new PluginPlaceholder(this.message().plugin.prefix, this.message().plugin.prefixError
                    );

                    placeholder.addPlaceholder("{CURRENT_VERSION}", PLUGIN_VERSION);
                    placeholder.addPlaceholder("{LATEST_VERSION}", latestVersion);
                    placeholder.addPlaceholder("{DOWNLOAD_LINK}", latestDownloadLink);

                    final Colorizer colorizer = this.message().colorizer;

                    Component message = switch (versionManager.getVersionType()) {
                        case BLACKLIST -> {
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.blacklist", PLUGIN_VERSION, this.consoleMessage.getMessage("version.message.action.no_recommended"), latestVersion)
                            );
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                            );

                            yield colorizer.parse(
                                    this.message().version.blacklist, placeholder
                            );
                        }
                        case OUTDATED -> {
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.outdated", PLUGIN_VERSION, this.consoleMessage.getMessage("version.message.action.no_recommended"), latestVersion)
                            );
                            this.pluginLogger.error(
                                    this.consoleMessage.getMessage("version.message.download", latestDownloadLink)
                            );

                            yield colorizer.parse(
                                    this.message().version.outdated, placeholder
                            );
                        }
                        default -> null;
                    };

                    if (message == null) {
                        return;
                    }

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                            if (PermissionManager.hasPermission(player, Permissions.VERSION_NOTIFY)) {
                                player.sendMessage(message);
                                player.sendMessage(
                                        colorizer.parse(
                                                this.message().version.download, placeholder
                                        )
                                );
                            }
                        }
                    });
                },
                period, period
        );
    }

    public void stop() {
        if (this.backgroundCheckTask != null && !this.backgroundCheckTask.isCancelled()) {
            this.backgroundCheckTask.cancel();
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("version.control.task.background_check.stopped")
            );
        }

        if (this.criticalNotifyTask != null && !this.criticalNotifyTask.isCancelled()) {
            this.criticalNotifyTask.cancel();
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("version.control.task.critical_notify.stopped")
            );
        }

    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
