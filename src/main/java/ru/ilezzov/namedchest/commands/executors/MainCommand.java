package ru.ilezzov.namedchest.commands.executors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedChestAPI;
import ru.ilezzov.namedchest.api.Response;
import ru.ilezzov.namedchest.commands.CommandManager;
import ru.ilezzov.namedchest.managers.CooldownManager;
import ru.ilezzov.namedchest.managers.VersionManager;
import ru.ilezzov.namedchest.messages.PluginMessages;
import ru.ilezzov.namedchest.permission.Permission;
import ru.ilezzov.namedchest.permission.PermissionsChecker;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.LegacySerialize;
import ru.ilezzov.namedchest.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final FileConfiguration configuration = Main.getConfigFile().getConfig();
    private final ConfigurationSection nameSettings = configuration.getConfigurationSection("name_settings");
    private final ConfigurationSection commandSettings = configuration.getConfigurationSection("command_settings");

    private final PluginPlaceholder commandPlaceholders = new PluginPlaceholder();
    private final CooldownManager cooldownManager = new CooldownManager(commandSettings.getLong("cooldown"));

    private final boolean enableSetCommand = commandSettings.getBoolean("enable_set");
    private final boolean enableRemoveCommand = commandSettings.getBoolean("enable_clear");

    private final boolean supportColor = nameSettings.getBoolean("support_color");
    private final boolean supportSpaces = nameSettings.getBoolean("support_spaces");

    private final int maxNameLength = nameSettings.getInt("max_name_length");
    private final int maxDistance = configuration.getInt("max_distance");

    private final NamedChestAPI api = Main.getApi();

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String[] args) {
        commandPlaceholders.addPlaceholder("{DEVELOPER}", ListUtils.listToString(Main.getPluginDevelopers()));
        commandPlaceholders.addPlaceholder("{CONTACT_LINK}", Main.getPluginContactLink());

        if (args.length == 0) {
            return handleHelp(sender);
        }

        return switch (args[0]) {
            case "reload" -> handleReload(sender);
            case "version" -> handleVersion(sender);
            case "set" -> handleSet(sender, args);
            case "clear" -> handleClear(sender);
            default -> handleHelp(sender);
        };
    }

    private boolean handleHelp(final @NotNull CommandSender sender) {
        sender.sendMessage(PluginMessages.commandMainCommandMessage(commandPlaceholders));
        return true;
    }

    private boolean handleReload(final CommandSender sender) {
        if (!PermissionsChecker.hasPermission(sender, Permission.RELOAD)) {
            sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
            return true;
        }

        Main.loadFiles();
        Main.reloadPluginInfo();

        Main.checkPluginVersion();
        Main.loadWorldGuard();

        Main.getBlockTypeManager().reload();
        Main.getBlockHoverManager().killAll();

        CommandManager.loadCommands();
        Main.getEventManager().reloadEvents();

        commandPlaceholders.addPlaceholder("{P}", Main.getPrefix());
        sender.sendMessage(PluginMessages.pluginReloadMessage(commandPlaceholders));

        return true;
    }

    public boolean handleVersion(final CommandSender sender) {
        final VersionManager versionManager = Main.getVersionManager();

        if (versionManager == null) {
            sender.sendMessage(PluginMessages.pluginHasErrorCheckVersionMessage(commandPlaceholders));
            return true;
        }

        commandPlaceholders.addPlaceholder("{LATEST_VERS}", versionManager.getCurrentPluginVersion());

        if (Main.isOutdatedVersion()) {
            commandPlaceholders.addPlaceholder("{DOWNLOAD_LINK}", Main.getPluginSettings().getUrlToDownloadLatestVersion());
            commandPlaceholders.addPlaceholder("{OUTDATED_VERS}", Main.getPluginVersion());

            sender.sendMessage(PluginMessages.pluginUseOutdatedVersionMessage(commandPlaceholders));
            return true;
        }

        sender.sendMessage(PluginMessages.pluginUseLatestVersionMessage(commandPlaceholders));
        return true;
    }

    private boolean handleSet(final CommandSender sender, final String[] args) {
        if (!enableSetCommand) {
            sender.sendMessage(PluginMessages.commandDisableCommandMessage(commandPlaceholders));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PluginMessages.pluginNoConsoleMessage(commandPlaceholders));
            return true;
        }

        if (!PermissionsChecker.hasPermission(sender, Permission.NAME_SET, Permission.NAME_SET_COLOR)) {
            sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
            return true;
        }

        if (cooldownManager.checkCooldown(player.getUniqueId())) {
            commandPlaceholders.addPlaceholder("{TIME}", cooldownManager.getRemainingTime(player.getUniqueId()));
            sender.sendMessage(PluginMessages.pluginCommandCooldownMessage(commandPlaceholders));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PluginMessages.commandNameEmpty(commandPlaceholders));
            return true;
        }

        final Block block = player.getTargetBlock(maxDistance);
        final Response response = api.checkBlock(block);

        if (Main.isSupportWorldGuard()) {
            if (!api.isPlayerInRegion(player, block.getLocation())) {
                player.sendMessage(PluginMessages.commandNotOwnedRegion(commandPlaceholders));
                return true;
            }
        }

        switch (response.status()) {
            case NULL_BLOCK -> sender.sendMessage(PluginMessages.commandNameBlockNull(commandPlaceholders));
            case INVALUABLE_BLOCK -> sender.sendMessage(PluginMessages.commandNameBlockError(commandPlaceholders));
            case ACCESS -> {
                final String textName = getTextName(args);

                if (textName == null) {
                    sender.sendMessage(PluginMessages.commandNameEmpty(commandPlaceholders));
                    return true;
                }

                Component name;

                if (supportColor) {
                    if (PermissionsChecker.hasPermission(sender, Permission.NAME_SET_COLOR)) {
                        name = LegacySerialize.serialize(textName);
                    } else {
                        name = Component.text(textName);
                    }
                } else {
                    name = Component.text(textName);
                }

                if (PermissionsChecker.hasPermission(sender, Permission.NAME_MAX_LENGTH)) {
                    api.setName(name, (Container) response.data());

                    final Material material = block.getType();

                    commandPlaceholders.addPlaceholder("{BLOCK}", material);
                    commandPlaceholders.addPlaceholder("{NAME}", LegacyComponentSerializer.legacySection().serialize(name));

                    sender.sendMessage(PluginMessages.commandNameSet(commandPlaceholders));
                } else {
                    final int componentLength = api.componentLength(name);

                    if (componentLength > maxNameLength) {
                        commandPlaceholders.addPlaceholder("{MAX_LENGTH}", maxNameLength);
                        sender.sendMessage(PluginMessages.commandNameMaxLength(commandPlaceholders));
                    } else {
                        api.setName(name, (Container) response.data());

                        final Material material = block.getType();

                        commandPlaceholders.addPlaceholder("{BLOCK}", material);
                        commandPlaceholders.addPlaceholder("{NAME}", LegacyComponentSerializer.legacySection().serialize(name));

                        sender.sendMessage(PluginMessages.commandNameSet(commandPlaceholders));
                    }
                }
                newCooldown(player);
                return true;
            }
        }
        return true;
    }

    private String getTextName(final String[] args) {
        final String firstWorld = args[1];

        if (firstWorld == null || firstWorld.isBlank()) {
            return null;
        }

        if (!supportSpaces) {
            return firstWorld;
        }

        return String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
    }

    private boolean handleClear(final CommandSender sender) {
        if (!enableRemoveCommand) {
            sender.sendMessage(PluginMessages.commandDisableCommandMessage(commandPlaceholders));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PluginMessages.pluginNoConsoleMessage(commandPlaceholders));
            return true;
        }

        if (!PermissionsChecker.hasPermission(sender, Permission.NAME_CLEAR)) {
            sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
            return true;
        }

        if (cooldownManager.checkCooldown(player.getUniqueId())) {
            commandPlaceholders.addPlaceholder("{TIME}", cooldownManager.getRemainingTime(player.getUniqueId()));
            sender.sendMessage(PluginMessages.pluginCommandCooldownMessage(commandPlaceholders));
            return true;
        }

        final Block block = player.getTargetBlock(maxDistance);
        final Response response = api.checkBlock(block);

        if (Main.isSupportWorldGuard()) {
            if (!api.isPlayerInRegion(player, block.getLocation())) {
                player.sendMessage(PluginMessages.commandNotOwnedRegion(commandPlaceholders));
                return true;
            }
        }

        switch (response.status()) {
            case NULL_BLOCK -> sender.sendMessage(PluginMessages.commandNameBlockNull(commandPlaceholders));
            case INVALUABLE_BLOCK -> sender.sendMessage(PluginMessages.commandNameBlockError(commandPlaceholders));
            case NULL_DISPLAY_NAME -> sender.sendMessage(PluginMessages.commandNameAlreadyClear(commandPlaceholders));
            case ACCESS -> {
                api.removeName((Container) response.data());
                Main.getBlockHoverManager().kill(block.getLocation());

                final Material material = block.getType();

                commandPlaceholders.addPlaceholder("{BLOCK}", material);
                sender.sendMessage(PluginMessages.commandNameClear(commandPlaceholders));

                newCooldown(player);
                return true;
            }
        }
        return true;
    }

    private void newCooldown(final CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return;
        }
        if (PermissionsChecker.hasPermission(player, Permission.NO_COOLDOWN)) {
            return;
        }

        cooldownManager.newCooldown(player.getUniqueId());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final String partialName = args[0];

            if ("version".startsWith(partialName)) {
                completions.add("version");
            }

            if (PermissionsChecker.hasPermission(sender, Permission.RELOAD)) {
                if ("reload".startsWith(partialName)) {
                    completions.add("reload");
                }
            }

            if (PermissionsChecker.hasPermission(sender, Permission.NAME_SET, Permission.NAME_SET_COLOR)) {
                if ("set".startsWith(partialName)) {
                    completions.add("set");
                }
            }

            if (PermissionsChecker.hasPermission(sender, Permission.NAME_CLEAR)) {
                if ("clear".startsWith(partialName)) {
                    completions.add("clear");
                }
            }
        }

        return completions;
    }
}

