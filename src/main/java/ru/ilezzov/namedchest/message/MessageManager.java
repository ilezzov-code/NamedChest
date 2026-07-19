package ru.ilezzov.namedchest.message;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;

@AllArgsConstructor
public class MessageManager {
    private final Main plugin;

    public void send(final CommandSender commandSender, final String text, final PluginPlaceholder placeholder) {
        commandSender.sendMessage(
                this.parseComponent(text, placeholder)
        );
    }

    public void sendFromThread(final CommandSender sender, final String text ,final PluginPlaceholder placeholder) {
        Bukkit.getScheduler().runTask(plugin, () -> this.send(sender, text, placeholder));
    }

    private Component parseComponent(final String text, final PluginPlaceholder placeholder) {
        return this.message().colorizer.parse(text, placeholder);
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
