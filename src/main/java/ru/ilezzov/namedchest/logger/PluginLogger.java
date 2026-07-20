package ru.ilezzov.namedchest.logger;

import lombok.Setter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.Plugin;
import ru.ilezzov.namedchest.color.LegacySerialize;

public class PluginLogger {
    private final ComponentLogger console;

    private static final String DEBUG_PREFIX = "<dark_gray>[<aqua>DEBUG</aqua>]</dark_gray> ";

    @Setter
    private boolean debug = false;

    public PluginLogger(final Plugin plugin) {
        this.console = plugin.getComponentLogger();
    }

    public PluginLogger(final Plugin plugin, final boolean debug) {
        this.console = plugin.getComponentLogger();
        this.debug = debug;
    }

    public void info(final String text) {
        console.info(
                LegacySerialize.serialize(text)
        );
    }

    public void warn(final String text) {
        console.warn(
                LegacySerialize.serialize(text)
        );
    }

    public void warn(final String text, final Throwable throwable) {
        console.warn(
                LegacySerialize.serialize(text),
                throwable
        );
    }

    public void error(final String text) {
        console.error(
                LegacySerialize.serialize(text)
        );
    }

    public void error(final String text, final Throwable throwable) {
        console.error(
                LegacySerialize.serialize(text),
                throwable
        );
    }

    public void debug(final String text) {
        if (debug) {
            console.info(
                    LegacySerialize.serialize(DEBUG_PREFIX.concat(text))
            );
        }
    }
}
