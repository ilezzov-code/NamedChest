package ru.ilezzov.namedchest.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.events.listeners.*;
import ru.ilezzov.namedchest.events.listeners.VersionCheckEvent;
import ru.ilezzov.namedchest.logging.Logger;

import java.util.HashMap;
import java.util.Map;

import static ru.ilezzov.namedchest.messages.ConsoleMessages.*;

public class EventManager {
    private final Logger logger = Main.getPluginLogger();
    private final JavaPlugin plugin;
    private Map<Class<? extends Listener>, Boolean> listenerClasses;
    private Map<String, Object> events;

    public EventManager(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.events = new HashMap<>();

        listenerClasses = loadListenerClasses();
    }

    public Object get(final String eventName) {
        return events.get(eventName);
    }

    public void loadEvents() {
        final Map<String, Object> events = new HashMap<>();

        for (Class<? extends Listener> listenerClass : listenerClasses.keySet()) {
            try {
                if (listenerClasses.get(listenerClass)) {
                    final Listener listener = listenerClass.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(listener, plugin);
                    events.put(listenerClass.getSimpleName(), listener);
                    logger.info(eventLoaded(listenerClass.getSimpleName()));
                }
            } catch (Exception e) {
                logger.info(errorOccurred(String.format("Couldn't load event %s", listenerClass.getSimpleName())));
            }
        }

        this.events = events;
    }

    public void reloadEvents() {
        HandlerList.unregisterAll(plugin);
        listenerClasses = loadListenerClasses();
        loadEvents();
    }

    private Map<Class<? extends Listener>, Boolean> loadListenerClasses() {
        final FileConfiguration config = Main.getConfigFile().getConfig();
        final ConfigurationSection nameSettings = config.getConfigurationSection("name_settings");

        return Map.ofEntries(
                eventEntry(VersionCheckEvent.class, true),
                eventEntry(PlayerClickEvent.class, true),
                eventEntry(PlayerHoverEvent.class, true),
                eventEntry(PlayerBlockBreakEvent.class, !nameSettings.getBoolean("save_for_breaking")),
                eventEntry(PlayerUseAnvilEvent.class, nameSettings.getBoolean("cancel_anvil"))
        );
    }

    private Map.Entry<Class<? extends Listener>, Boolean> eventEntry(final Class<? extends  Listener> eventClass, Boolean enable) {
        return Map.entry(eventClass, enable);
    }
}
