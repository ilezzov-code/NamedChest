package ru.ilezzov.namedchest.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.ilezzov.namedchest.managers.VersionManager;
import ru.ilezzov.namedchest.permission.PermissionsChecker;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;

import static ru.ilezzov.namedchest.Main.*;
import static ru.ilezzov.namedchest.messages.PluginMessages.pluginHasErrorCheckVersionMessage;
import static ru.ilezzov.namedchest.messages.PluginMessages.pluginUseOutdatedVersionMessage;

public class VersionCheckEvent implements Listener {
    private final PluginPlaceholder eventPlaceholders = new PluginPlaceholder();
    private final boolean isEnable = (getConfigFile().getBoolean("check_updates"));

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        if (!isEnable) {
            return;
        }

        if (!isOutdatedVersion()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!PermissionsChecker.hasPermission(player)) {
            return;
        }

        final VersionManager versionManager = getVersionManager();

        if (versionManager == null) {
            player.sendMessage(pluginHasErrorCheckVersionMessage(eventPlaceholders));
            return;
        }

        eventPlaceholders.addPlaceholder("{OUTDATED_VERS}", getPluginVersion());
        eventPlaceholders.addPlaceholder("{LATEST_VERS}", versionManager.getCurrentPluginVersion());
        eventPlaceholders.addPlaceholder("{DOWNLOAD_LINK}", getPluginSettings().getUrlToDownloadLatestVersion());

        player.sendMessage(pluginUseOutdatedVersionMessage(eventPlaceholders));
    }
}
