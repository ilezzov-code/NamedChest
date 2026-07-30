package ru.ilezzov.namedchest.send;

import lombok.AllArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;

public class SendManager {
    private final Main plugin;
    private final ConfigFile configFile;

    public SendManager(final Main plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
    }

    public void sendMessage(final CommandSender commandSender, final String text, final PluginPlaceholder placeholder) {
        commandSender.sendMessage(
                this.parseComponent(text, placeholder)
        );
    }

    public void sendMessageFromThread(final CommandSender sender, final String text , final PluginPlaceholder placeholder) {
        Bukkit.getScheduler().runTask(plugin, () -> this.sendMessage(sender, text, placeholder));
    }

    public void playSound(final CommandSender sender, final Sound sound) {
        sender.playSound(sound);
    }

    public void playErrorSound(final CommandSender sender) {
        final ConfigFile.ErrorSound errorSound = this.configFile.errorSound;
        if (errorSound.enable) {
            sender.playSound(
                    Sound.sound(Key.key(errorSound.sound), Sound.Source.PLAYER, errorSound.volume, errorSound.pitch)
            );
        }
    }

    private Component parseComponent(final String text, final PluginPlaceholder placeholder) {
        return this.message().colorizer.parse(text, placeholder);
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
