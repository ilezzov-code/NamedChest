package ru.ilezzov.namedchest.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import ru.ilezzov.namedchest.BuildConfig;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.command.executors.MainCommand;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;

import java.util.Map;

public class CommandManager {
    private final Main plugin;
    private final PluginLogger pluginLogger;
    private final ConsoleMessage consoleMessage;

    public CommandManager(final Main plugin) {
        this.plugin = plugin;
        this.pluginLogger = plugin.getPluginLogger();
        this.consoleMessage = plugin.getConsoleMessage();
    }

    public void loadCommands() {
        this.pluginLogger.debug(
                this.consoleMessage.getMessage("command.registration.start")
        );

        final Map<String, CommandExecutor> commands = getCommands();

        int commandRegistered = 0;
        for (final String commandName : commands.keySet()) {
            final PluginCommand command = this.plugin.getCommand(commandName);

            if (command != null) {
                final CommandExecutor commandExecutor = commands.get(commandName);
                command.setExecutor(commandExecutor);

                if (commandExecutor instanceof TabCompleter completer) {
                    command.setTabCompleter(completer);
                }

                this.pluginLogger.debug(
                        this.consoleMessage.getMessage("command.registration.success.single", commandName)
                );
                commandRegistered++;
            } else {
                this.pluginLogger.warn(
                        this.consoleMessage.getMessage("command.registration.error.missing_declaration", commandName)
                );
            }
        }

        this.pluginLogger.debug(
                this.consoleMessage.getMessage("command.registration.success.multiple", commandRegistered)
        );
    }

    private Map<String, CommandExecutor> getCommands() {
        return Map.ofEntries(
                Map.entry(BuildConfig.MAIN_COMMAND, new MainCommand(this.plugin))
        );
    }
}
