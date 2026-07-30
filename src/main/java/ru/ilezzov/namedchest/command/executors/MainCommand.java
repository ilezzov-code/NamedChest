package ru.ilezzov.namedchest.command.executors;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ilezzov.namedchest.BuildConfig;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.color.LegacySerialize;
import ru.ilezzov.namedchest.cooldown.CooldownManager;
import ru.ilezzov.namedchest.cooldown.CooldownResponse;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.logger.ConsoleMessage;
import ru.ilezzov.namedchest.logger.PluginLogger;
import ru.ilezzov.namedchest.send.SendManager;
import ru.ilezzov.namedchest.permission.PermissionManager;
import ru.ilezzov.namedchest.permission.Permissions;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.Utils;
import ru.ilezzov.namedchest.version.VersionControl;
import ru.ilezzov.namedchest.version.VersionData;
import ru.ilezzov.namedchest.version.VersionManager;
import ru.ilezzov.namedchest.version.VersionType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MainCommand implements CommandExecutor, TabExecutor {
    private final Main plugin;
    private final PluginLogger pluginLogger;
    private final VersionManager versionManager;
    private final SendManager sendManager;
    private final ConfigFile configFile;
    private final ConsoleMessage consoleMessage;
    private final CooldownManager cooldownManager;

    private static final Map<String, String> ROOT_COMMANDS = Map.of(
            "reload", Permissions.RELOAD,
            "version", Permissions.VERSION_COMMAND,
            "set", Permissions.SET_COMMAND,
            "clear", Permissions.CLEAR_COMMAND
    );

    public MainCommand(final Main plugin) {
        this.plugin = plugin;
        this.pluginLogger = plugin.getPluginLogger();
        this.versionManager = plugin.getVersionManager();
        this.sendManager = plugin.getSendManager();
        this.configFile = plugin.getConfigFile();
        this.consoleMessage = plugin.getConsoleMessage();
        this.cooldownManager = plugin.getNameCooldownManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );

        placeholder.addPlaceholder("{CONTACT}", BuildConfig.ISSUE_URL);

        if (args.length == 0) {
            handleHelp(sender, placeholder);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "version" -> handleVersion(sender);
            case "reload" -> handleReload(sender, placeholder);
            case "set" -> handleSet(sender, args, placeholder);
            case "clear" -> handleClear(sender, placeholder);
            default -> handleHelp(sender, placeholder);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final String input = args[0];
            for (Map.Entry<String, String> entry : ROOT_COMMANDS.entrySet()) {
                final String subCommand = entry.getKey();
                final String permission = entry.getValue();

                if (PermissionManager.hasPermission(sender, permission) && StringUtil.startsWithIgnoreCase(subCommand, input)) {
                    completions.add(subCommand);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private void handleHelp(final CommandSender sender, final PluginPlaceholder placeholder) {
        this.sendManager.sendMessage(
                sender, this.message().mainCommand.help, placeholder
        );
    }

    private void handleVersion(@NotNull CommandSender sender) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );

        if (!PermissionManager.hasPermission(sender, Permissions.VERSION_COMMAND)) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.noPerms, placeholder
            );
            this.sendManager.playErrorSound(sender);
            return;
        }

        this.sendManager.sendMessage(
                sender, this.message().version.loading,placeholder
        );

        CompletableFuture.runAsync(this.versionManager::loadVersionData).thenRun(() -> {
            final VersionData versionData = this.versionManager.getVersionData();

            if (versionData == null) {
                this.sendManager.sendMessageFromThread(
                       sender, this.message().version.error, placeholder
                );
                this.sendManager.playErrorSound(sender);
                return;
            }

            placeholder.addPlaceholder("{CURRENT_VERSION}", BuildConfig.PLUGIN_VERSION);
            placeholder.addPlaceholder("{LATEST_VERSION}", versionData.getLatest().getVersion());
            placeholder.addPlaceholder("{DOWNLOAD_LINK}", versionData.getLatest().getVersion());

            if (this.versionManager.getVersionType() == VersionType.LATEST) {
                this.sendManager.sendMessageFromThread(
                        sender, this.message().version.latest, placeholder
                );
                this.sendManager.playSuccessSound(sender);
                return;
            }

            switch (versionManager.getVersionType()) {
                case SUPPORTED -> this.sendManager.sendMessageFromThread(
                        sender, this.message().version.supported, placeholder
                );
                case BLACKLIST -> this.sendManager.sendMessageFromThread(
                        sender, this.message().version.blacklist, placeholder
                );
                case OUTDATED -> this.sendManager.sendMessageFromThread(
                        sender, this.message().version.outdated, placeholder
                );
            }

            this.sendManager.sendMessageFromThread(
                    sender, this.message().version.download, placeholder
            );
            this.sendManager.playSuccessSound(sender);
        });
    }

    private void handleReload(final CommandSender sender, final PluginPlaceholder placeholder) {
        if (!PermissionManager.hasPermission(sender, Permissions.RELOAD)) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.noPerms, placeholder
            );
            this.sendManager.playErrorSound(sender);
            return;
        }

        final String oldMessageFile = this.configFile.language;

        this.configFile.load();
        if (this.configFile.debug) {
            this.pluginLogger.setDebug(true);
            this.pluginLogger.debug(this.consoleMessage.getMessage("debug.enabled"));
        }

        final String messageFile = this.configFile.language;
        this.plugin.reloadMessageFile(oldMessageFile, messageFile);

        placeholder.addPlaceholder("{P}", this.message().plugin.prefix);
        placeholder.addPlaceholder("{P_E}", this.message().plugin.prefixError);

        this.plugin.getEventManager().reloadEvents();
        
        final VersionControl versionControl = this.plugin.getVersionControl();
        versionControl.stop();
        versionControl.startBackgroundCheckTask();
        versionControl.startCriticalNotifyTask();

        this.sendManager.sendMessage(
                sender, this.message().plugin.reload, placeholder
        );
        this.sendManager.playSuccessSound(sender);
    }

    public void handleSet(final CommandSender sender, final String[] args, final PluginPlaceholder placeholder) {
        if (!this.configFile.commandSettings.enableSet) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.commandDisable, placeholder
            );
            this.sendManager.playErrorSound(sender);
            return;
        }

        if (!(sender instanceof Player player)) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.noConsole, placeholder
            );
            this.sendManager.playErrorSound(sender);
            return;
        }

        if (!PermissionManager.hasPermission(sender, Permissions.SET_COMMAND, Permissions.COLOR_NAME)) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.noPerms, placeholder
            );
            this.sendManager.playErrorSound(player);
            return;
        }

        if (!PermissionManager.hasPermission(sender, Permissions.NO_COOLDOWN)) {
            final CooldownResponse response = this.cooldownManager.getCooldown(player.getUniqueId());

            if (response.hasCooldown()) {
                placeholder.addPlaceholder("{COOLDOWN}", TimeUnit.MILLISECONDS.toSeconds(response.remaining()));

                this.sendManager.sendMessage(
                        sender, this.message().plugin.cooldown, placeholder
                );
                this.sendManager.playErrorSound(player);

                return;
            }
        }

        final MessageFile.MainCommandSubConfig commandSubConfig = this.message().mainCommand;
        if (args.length < 2) {
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.emptyName, placeholder
            );
            this.sendManager.playErrorSound(player);
            return;
        }

        final Block block = Utils.getTargetBlock(
                player, this.configFile.maxDistance, this.configFile.ignoredBlocks
        );

        if (block == null) {
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.nullBlock, placeholder
            );
            this.sendManager.playErrorSound(player);
            return;
        }

        final Material material = block.getType();
        if (!this.configFile.supportedBlocks.contains(material)) {
            final String stringMaterials = Utils.getMaterialsString(this.configFile.supportedBlocks);

            placeholder.addPlaceholder("{SUPPORTED_BLOCKS}", stringMaterials);

            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.unsupportedBlock, placeholder
            );
            this.sendManager.playErrorSound(player);

            return;
        }

        final BlockState state = block.getState();

        if (!(state instanceof Container container)) {
            final String stringMaterials = Utils.getMaterialsString(this.configFile.supportedBlocks);

            placeholder.addPlaceholder("{SUPPORTED_BLOCKS}", stringMaterials);

            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.unsupportedBlock, placeholder
            );
            this.sendManager.playErrorSound(player);

            return;
        }

        if (this.plugin.isSupportWorldGuard()) {
            if (!PermissionManager.hasPermission(sender, Permissions.IGNORE_REGIONS)) {
                if (!Utils.canModify(player, block.getLocation(), this.plugin.getWorldGuard())) {
                    this.sendManager.sendMessage(
                            sender, commandSubConfig.error.notYourRegion, placeholder
                    );
                    this.sendManager.playErrorSound(player);
                    return;
                }
            }
        }

        final String textName = Utils.getTextNameFromList(Arrays.copyOfRange(args, 1, args.length), this.configFile.nameSettings.supportSpaces);
        final String serializedName = LegacySerialize.serializeToString(textName);
        Component name = LegacySerialize.serializeFromString(serializedName);
        final String nameWithoutColors = LegacySerialize.serializeWithoutColors(name);

        if (nameWithoutColors.isBlank()) {
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.emptyName, placeholder
            );
            this.sendManager.playErrorSound(player);
            return;
        }

        if (!PermissionManager.hasPermission(sender, Permissions.IGNORE_MAX_LENGTH)) {
            if (nameWithoutColors.length() > this.configFile.nameSettings.maxLength) {
                placeholder.addPlaceholder("{MAX_LENGTH}", this.configFile.nameSettings.maxLength);

                this.sendManager.sendMessage(
                        sender, commandSubConfig.error.exceededNameLength, placeholder
                );
                this.sendManager.playErrorSound(player);

                return;
            }
        }

        placeholder.addPlaceholder("{NAME}", serializedName);
        if (!this.configFile.nameSettings.supportColor) {
            name = Component.text(nameWithoutColors);
            placeholder.addPlaceholder("{NAME}", nameWithoutColors);
        }

        if (!PermissionManager.hasPermission(sender, Permissions.COLOR_NAME)) {
            name = Component.text(nameWithoutColors);
            placeholder.addPlaceholder("{NAME}", nameWithoutColors);
        }

        Utils.setContainerName(container, name);
        placeholder.addPlaceholder("{BLOCK}", Utils.getMaterialLang(material));

        this.sendManager.sendMessage(
                sender, commandSubConfig.set, placeholder
        );
        this.sendManager.playSuccessSound(sender);
        this.cooldownManager.newCooldown(player.getUniqueId(), TimeUnit.SECONDS.toMillis(
                this.configFile.commandSettings.cooldown
        ));
    }

    public void handleClear(final CommandSender sender, final PluginPlaceholder placeholder) {
        if (!this.configFile.commandSettings.enableSet) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.commandDisable, placeholder
            );
            return;
        }

        if (!(sender instanceof Player player)) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.noConsole, placeholder
            );
            return;
        }

        if (!PermissionManager.hasPermission(sender, Permissions.CLEAR_COMMAND)) {
            this.sendManager.sendMessage(
                    sender, this.message().plugin.noPerms, placeholder
            );
            return;
        }

        if (!PermissionManager.hasPermission(sender, Permissions.NO_COOLDOWN)) {
            final CooldownResponse response = this.cooldownManager.getCooldown(player.getUniqueId());

            if (response.hasCooldown()) {
                placeholder.addPlaceholder("{COOLDOWN}", TimeUnit.MILLISECONDS.toSeconds(response.remaining()));
                this.sendManager.sendMessage(
                        sender, this.message().plugin.cooldown, placeholder
                );
                return;
            }
        }

        final MessageFile.MainCommandSubConfig commandSubConfig = this.message().mainCommand;
        final Block block = Utils.getTargetBlock(
                player, this.configFile.maxDistance, this.configFile.ignoredBlocks
        );

        if (block == null) {
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.nullBlock, placeholder
            );
            return;
        }

        final Material material = block.getType();
        if (!this.configFile.supportedBlocks.contains(material)) {
            final String stringMaterials = Utils.getMaterialsString(this.configFile.supportedBlocks);

            placeholder.addPlaceholder("{SUPPORTED_BLOCKS}", stringMaterials);
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.unsupportedBlock, placeholder
            );
            return;
        }

        final BlockState state = block.getState();

        if (!(state instanceof Container container)) {
            final String stringMaterials = Utils.getMaterialsString(this.configFile.supportedBlocks);

            placeholder.addPlaceholder("{SUPPORTED_BLOCKS}", stringMaterials);
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.unsupportedBlock, placeholder
            );
            return;
        }

        if (this.plugin.isSupportWorldGuard()) {
            if (!PermissionManager.hasPermission(sender, Permissions.IGNORE_REGIONS)) {
                if (!Utils.canModify(player, block.getLocation(), this.plugin.getWorldGuard())) {
                    this.sendManager.sendMessage(
                            sender, commandSubConfig.error.notYourRegion, placeholder
                    );
                    return;
                }
            }
        }

        final Component name = container.customName();

        if (name == null) {
            this.sendManager.sendMessage(
                    sender, commandSubConfig.error.alreadyClear, placeholder
            );
            return;
        }

        Utils.clearContainerName(container);
        placeholder.addPlaceholder("{BLOCK}", Utils.getMaterialLang(material));

        this.sendManager.sendMessage(
                sender, commandSubConfig.clear, placeholder
        );
        this.sendManager.playSuccessSound(sender);
        this.cooldownManager.newCooldown(player.getUniqueId(), TimeUnit.SECONDS.toMillis(
                this.configFile.commandSettings.cooldown
        ));
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
