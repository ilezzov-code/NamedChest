package ru.ilezzov.namedchest.event;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.event.listeners.PlayerJoinEvent;
import ru.ilezzov.namedchest.event.listeners.PlayerUseAnvilEvent;
import ru.ilezzov.namedchest.event.listeners.PlayerUseNameTagEvent;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager {
    private final Main plugin;
    private final ConfigFile configFile;
    private final PluginLogger pluginLogger;
    private final ConsoleMessage consoleMessage;

    private final Set<Listener> registeredListener = new HashSet<>();

    public EventManager(final Main plugin) {
        this.plugin = plugin;
        this.configFile = this.plugin.getConfigFile();
        this.pluginLogger = plugin.getPluginLogger();
        this.consoleMessage = plugin.getConsoleMessage();
    }

    public void registerEvents() {
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("event.registration.start")
        );

        final Map<Listener, Boolean> listeners = loadListeners();

        for (final Listener listener : listeners.keySet()) {
            if (listeners.get(listener)) {
                Bukkit.getPluginManager().registerEvents(listener, plugin);

                this.registeredListener.add(listener);
                this.pluginLogger.debug(
                        this.consoleMessage.getMessage("event.registration.success.single", listener.getClass().getSimpleName())
                );
            }
        }

        this.pluginLogger.debug(
                this.consoleMessage.getMessage("event.registration.success.multiple", this.registeredListener.size())
        );
    }

    public void unregisterEvents() {
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("event.unregistration.start")
        );

        final int eventsCount = this.registeredListener.size();
        for (final Listener listener : this.registeredListener) {
            HandlerList.unregisterAll(listener);
            this.pluginLogger.debug(
                    this.consoleMessage.getMessage("event.unregistration.success.single", listener.getClass().getSimpleName())
            );
        }

        this.registeredListener.clear();

        pluginLogger.debug(
                this.consoleMessage.getMessage("event.unregistration.success.multiple", eventsCount)
        );
    }

    public void reloadEvents() {
        this.unregisterEvents();
        this.registerEvents();
        pluginLogger.debug(
                this.consoleMessage.getMessage("event.reload.success")
        );
    }

    private Map<Listener, Boolean> loadListeners() {
        return Map.ofEntries(
                Map.entry(new PlayerJoinEvent(plugin),
                        this.configFile.versionControl.notifyAdminsOnJoin
                ),
                Map.entry(new PlayerUseAnvilEvent(plugin),
                        this.configFile.nameSettings.cancelAnvil
                ),
                Map.entry(new PlayerUseNameTagEvent(plugin),
                    this.configFile.nameSettings.supportNameTag
                )
        );
    }
}
