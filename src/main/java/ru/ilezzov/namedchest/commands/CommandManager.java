package ru.ilezzov.namedchest.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.commands.executors.*;
import ru.ilezzov.namedchest.logging.Logger;

import java.util.Map;

import static ru.ilezzov.namedchest.messages.ConsoleMessages.errorOccurred;

public class CommandManager {
    private static final Logger logger = Main.getPluginLogger();
    private static final JavaPlugin plugin = Main.getInstance();

    public static void loadCommands() {
        final Map<String, CommandExecutor> commands = getCommands();

        for (final String commandName : commands.keySet()) {
            final PluginCommand command = plugin.getCommand(commandName);

            if (command != null) {
                final CommandExecutor commandExecutor = commands.get(commandName);
                command.setExecutor(commandExecutor);

                if (commandExecutor instanceof TabCompleter completer) {
                    command.setTabCompleter(completer);
                }
            } else {
                logger.info(errorOccurred(String.format("The command %s was not found in plugin.yml", commandName)));
            }
        }
    }

    private static Map<String, CommandExecutor> getCommands() {
        return Map.ofEntries(
                Map.entry("namedchest", new MainCommand())
        );
    }
}
