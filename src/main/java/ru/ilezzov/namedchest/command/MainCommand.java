package ru.ilezzov.namedchest.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedApi;
import ru.ilezzov.namedchest.enums.Permission;
import ru.ilezzov.namedchest.messages.PluginMessages;
import ru.ilezzov.namedchest.models.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.LegacySerialize;
import ru.ilezzov.namedchest.utils.ListUtils;
import ru.ilezzov.namedchest.utils.PermissionsChecker;

import java.util.ArrayList;
import java.util.List;

import static ru.ilezzov.namedchest.utils.PermissionsChecker.hasPermission;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final PluginPlaceholder commandPlaceholders = new PluginPlaceholder();

    private final int nameMaxLength = Main.getConfigFile().getInt("name_settings.max_name_length");
    private final boolean nameSupportColor = Main.getConfigFile().getBoolean("name_settings.support_color");
    private final int blockMaxDistant = Main.getConfigFile().getInt("block_max_distant");

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        commandPlaceholders.addPlaceholder("{DEVELOPER}", ListUtils.listToString(Main.getPluginDevelopers()));
        commandPlaceholders.addPlaceholder("{CONTACT_LINK}", Main.getPluginContactLink());

        if (args.length == 0) {
            sender.sendMessage(PluginMessages.commandMainCommandMessage(commandPlaceholders));
            return true;
        }

        switch (args[0]) {
            case "set" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(PluginMessages.pluginNoConsoleMessage(commandPlaceholders));
                    return true;
                }

                if (!PermissionsChecker.hasPermission(sender, Permission.SET_NAME, Permission.SET_COLOR_NAME)){
                    player.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(PluginMessages.nameEmptyMessage(commandPlaceholders));
                    return true;
                }

                final String name = args[1];

                if (!PermissionsChecker.hasPermission(player, Permission.MAX_LENGHT)) {
                    if (name.length() > nameMaxLength) {
                        commandPlaceholders.addPlaceholder("{MAX_LENGTH}", nameMaxLength);
                        player.sendMessage(PluginMessages.nameMaxLength(commandPlaceholders));
                        return true;
                    }
                }

                final Block block = player.getTargetBlockExact(blockMaxDistant);

                if (block == null) {
                    player.sendMessage(PluginMessages.nameBlockError(commandPlaceholders));
                    return true;
                }

                if (!Main.getBlockManager().contains(block.getType())) {
                    player.sendMessage(PluginMessages.nameBlockError(commandPlaceholders));
                    return true;
                }

                final BlockState blockState = block.getState();

                if (!(blockState instanceof final Container container)) {
                    player.sendMessage(PluginMessages.nameBlockError(commandPlaceholders));
                    return true;
                }

                Component nameComponent;

                if (nameSupportColor) {
                    if (PermissionsChecker.hasPermission(player, Permission.SET_COLOR_NAME)) {
                        nameComponent = LegacySerialize.serialize(name);
                    }
                    else {
                        nameComponent = Component.text(name);
                    }
                } else {
                    nameComponent = Component.text(name);
                }

                NamedApi.setName(container, nameComponent);

                commandPlaceholders.addPlaceholder("{NAME}", LegacyComponentSerializer.legacySection().serialize(nameComponent));
                commandPlaceholders.addPlaceholder("{BLOCK}", block.getType());
                player.sendMessage(PluginMessages.nameSetMessage(commandPlaceholders));

                return true;
            }
            case "clear" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(PluginMessages.pluginNoConsoleMessage(commandPlaceholders));
                    return true;
                }

                if (!PermissionsChecker.hasPermission(sender, Permission.CLEAR_NAME)){
                    player.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                    return true;
                }

                final Block block = player.getTargetBlockExact(blockMaxDistant);

                if (block == null) {
                    player.sendMessage(PluginMessages.nameBlockError(commandPlaceholders));
                    return true;
                }

                if (!Main.getBlockManager().contains(block.getType())) {
                    player.sendMessage(PluginMessages.nameBlockError(commandPlaceholders));
                    return true;
                }

                final BlockState blockState = block.getState();

                if (!(blockState instanceof final Container container)) {
                    player.sendMessage(PluginMessages.nameBlockError(commandPlaceholders));
                    return true;
                }

                NamedApi.clearName(container);
                Main.getBlockHoverManager().kill(block);

                commandPlaceholders.addPlaceholder("{BLOCK}", block.getType());
                player.sendMessage(PluginMessages.nameClearMessage(commandPlaceholders));

                return true;
            }
            case "reload" -> {
                if (hasPermission(sender, Permission.RELOAD)) {
                    Main.loadFiles();
                    Main.reloadPrefix();
                    Main.registerCommands();
                    Main.reloadEvents();
                    Main.getBlockManager().reload();

                    commandPlaceholders.addPlaceholder("{P}", Main.getPrefix());
                    sender.sendMessage(PluginMessages.pluginReloadMessage(commandPlaceholders));

                    return true;
                }

                sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                return true;
            }
            default -> sender.sendMessage(PluginMessages.commandMainCommandMessage(commandPlaceholders));
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            if (hasPermission(sender)) {
                completions.add("reload");
            }
            if (hasPermission(sender, Permission.SET_NAME, Permission.SET_COLOR_NAME)) {
                completions.add("set");
            }
            if (hasPermission(sender, Permission.CLEAR_NAME)) {
                completions.add("clear");
            }
        }

        return completions;
    }
}
