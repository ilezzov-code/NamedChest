package ru.ilezzov.namedchest.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.models.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.LegacySerialize;
import ru.ilezzov.namedchest.utils.PlaceholderReplacer;

public class PluginMessages {
    public static Component pluginReloadMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-reload", placeholder);
    }

    public static Component pluginNoPermsMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-permission-error", placeholder);
    }

    public static Component pluginNoConsoleMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-no-console-error", placeholder);
    }

    public static Component commandMainCommandMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.command-main-message", placeholder);
    }

    public static @NotNull Component nameEmptyMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.name-empty", placeholder);
    }

    public static @NotNull Component nameMaxLength(final PluginPlaceholder placeholder) {
        return getComponent("Messages.name-max-length", placeholder);
    }

    public static @NotNull Component nameSetMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.name-set", placeholder);
    }

    public static @NotNull Component nameClearMessage(final PluginPlaceholder placeholder) {
        return getComponent("Messages.name-clear", placeholder);
    }

    public static @NotNull Component nameBlockError(final PluginPlaceholder placeholder) {
        return getComponent("Messages.name-block-error", placeholder);
    }

    public static Component pluginOutdatedVersionMessage(final PluginPlaceholder placeholder) {
        return getComponent("Plugin.plugin-use-outdated-version", placeholder);
    }
    private static Component getComponent(final String key) {
        final String message = Main.getMessagesFile().getConfig().getString(key);

        return LegacySerialize.serialize(message);
    }

    private static Component getComponent(final String key, final PluginPlaceholder placeholder) {
        String message = Main.getMessagesFile().getString(key);
        message = replacePlaceholder(message, placeholder);

        return LegacySerialize.serialize(message);
    }

    private static String replacePlaceholder(final String message, final PluginPlaceholder placeholder) {
        return PlaceholderReplacer.replacePlaceholder(message, placeholder);
    }

}
