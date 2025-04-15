package ru.ilezzov.namedchest.messages;

import net.kyori.adventure.text.Component;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.utils.LegacySerialize;

import java.util.ArrayList;
import java.util.List;

public class ConsoleMessages {
    public static Component newFileCreateMessage(final String fileName) {
        return getComponent(Main.getPluginSettings().getString("Messages.create-new-file"), fileName);
    }

    public static Component addNewKeysToFile(final String fileName) {
        return getComponent(Main.getPluginSettings().getString("Messages.add-new-keys-to-file"), fileName);
    }

    public static List<Component> enablePlugin(final String pluginDeveloper, final String pluginVersion, final String pluginContactLink) {
        return getComponents(Main.getPluginSettings().getString("Messages.plugin-enable"), pluginDeveloper, pluginVersion, pluginContactLink);
    }

    public static List<Component> disablePlugin(final String pluginDeveloper, final String pluginVersion, final String pluginContactLink) {
        return getComponents(Main.getPluginSettings().getString("Messages.plugin-disable"), pluginDeveloper, pluginVersion, pluginContactLink);
    }

    private static Component getComponent(final String message, final Object... keys) {
        return LegacySerialize.serialize(message.formatted(keys));
    }

    private static List<Component> getComponents(final String message, final Object... keys) {
        final String[] messageSplit = message.formatted(keys).split("\n");
        List<Component> components = new ArrayList<>();

        for (final String text : messageSplit) {
            components.add(LegacySerialize.serialize(text));
        }

        return components;
    }

}
