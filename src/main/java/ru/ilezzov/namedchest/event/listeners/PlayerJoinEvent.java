package ru.ilezzov.namedchest.event.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.ilezzov.namedchest.BuildConfig;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.color.Colorizer;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.permission.PermissionManager;
import ru.ilezzov.namedchest.permission.Permissions;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;
import ru.ilezzov.namedchest.version.VersionManager;

public class PlayerJoinEvent implements Listener {
    private final Main plugin;
    private final VersionManager versionManager;

    public PlayerJoinEvent(final Main plugin) {
        this.plugin = plugin;
        this.versionManager = plugin.getVersionManager();
    }

    @EventHandler
    public void onPlayerJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!PermissionManager.hasPermission(player, Permissions.VERSION_NOTIFY)) {
            return;
        }

        if (this.versionManager.getVersionData() == null) {
            return;
        }

        final Colorizer colorizer = this.message().colorizer;
        final PluginPlaceholder placeholder = getPlaceholder();

        final Component message = switch (versionManager.getVersionType()) {
            case BLACKLIST -> colorizer.parse(
                    this.message().version.blacklist, placeholder
            );
            case OUTDATED -> colorizer.parse(
                    this.message().version.outdated, placeholder
            );
            case SUPPORTED -> colorizer.parse(
                    this.message().version.supported, placeholder
            );
            default -> null;
        };

        if (message == null) {
            return;
        }

        player.sendMessage(message);
        player.sendMessage(
                colorizer.parse(
                        this.message().version.download, placeholder
                )
        );
    }

    private PluginPlaceholder getPlaceholder() {
        final PluginPlaceholder placeholder = new PluginPlaceholder(this.message().plugin.prefix, this.message().plugin.prefixError);

        placeholder.addPlaceholder("{CURRENT_VERSION}", BuildConfig.PLUGIN_VERSION);
        placeholder.addPlaceholder("{DOWNLOAD_LINK}", this.versionManager.getVersionData().getLatest().getDownloadUrl());
        placeholder.addPlaceholder("{LATEST_VERSION}", this.versionManager.getVersionData().getLatest().getVersion());

        return placeholder;
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
