package ru.ilezzov.namedchest.event.listeners;

import com.google.common.util.concurrent.UncheckedTimeoutException;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.color.LegacySerialize;
import ru.ilezzov.namedchest.cooldown.CooldownManager;
import ru.ilezzov.namedchest.cooldown.CooldownResponse;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.message.MessageManager;
import ru.ilezzov.namedchest.permission.PermissionManager;
import ru.ilezzov.namedchest.permission.Permissions;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.Utils;

import java.util.concurrent.TimeUnit;

public class PlayerUseNameTagEvent implements Listener {
    private final Main plugin;
    private final ConfigFile configFile;
    private final MessageManager messageManager;
    private final CooldownManager cooldownManager;
    private final PluginPlaceholder placeholder;

    public PlayerUseNameTagEvent(final Main plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.messageManager = plugin.getMessageManager();
        this.cooldownManager = plugin.getNameCooldownManager();
        this.placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );
    }

    @EventHandler
    public void onPlayerUseNameTagEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!PermissionManager.hasPermission(player, Permissions.SET_COMMAND, Permissions.COLOR_NAME)) {
            return;
        }

        if (!PermissionManager.hasPermission(player, Permissions.NO_COOLDOWN)) {
            final CooldownResponse response = this.cooldownManager.getCooldown(player.getUniqueId());

            if (response.hasCooldown()) {
                placeholder.addPlaceholder("{COOLDOWN}", TimeUnit.MILLISECONDS.toSeconds(response.remaining()));

                this.messageManager.send(
                        player, this.message().plugin.cooldown, placeholder
                );
                this.playErrorSound(player);

                return;
            }
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.getType() != Material.NAME_TAG) {
            return;
        }

        final Block block = Utils.getTargetBlock(
                player, this.configFile.maxDistance, this.configFile.ignoredBlocks
        );
        final MessageFile.MainCommandSubConfig commandSubConfig = this.message().mainCommand;

        if (block == null) {
            return;
        }

        final Material material = block.getType();
        if (!this.configFile.supportedBlocks.contains(material)) {
            return;
        }

        final BlockState state = block.getState();

        if (!(state instanceof Container container)) {
            return;
        }

        if (this.plugin.isSupportWorldGuard()) {
            if (!PermissionManager.hasPermission(player, Permissions.IGNORE_REGIONS)) {
                if (!Utils.canModify(player, block.getLocation(), this.plugin.getWorldGuard())) {
                    this.messageManager.send(
                            player, commandSubConfig.error.notYourRegion, placeholder
                    );
                    this.playErrorSound(player);
                    return;
                }
            }
        }

        event.setCancelled(true);

        final ItemMeta nameTagMeta = itemStack.getItemMeta();
        if (!nameTagMeta.hasDisplayName()) {
            this.messageManager.send(
                    player, commandSubConfig.error.emptyName, placeholder
            );
            this.playErrorSound(player);
            return;
        }

        String textName = Utils.getTextNameFromList(
                nameTagMeta.getDisplayName().split("\\s+"), this.configFile.nameSettings.supportSpaces
        );
        final String serializedName = LegacySerialize.serializeToString(textName);
        Component name = LegacySerialize.serializeFromString(serializedName);
        final String nameWithoutColors = LegacySerialize.serializeWithoutColors(name);

        if (nameWithoutColors.isBlank()) {
            this.messageManager.send(
                    player, commandSubConfig.error.emptyName, placeholder
            );
            this.playErrorSound(player);
            return;
        }

        if (!PermissionManager.hasPermission(player, Permissions.IGNORE_MAX_LENGTH)) {
            if (nameWithoutColors.length() > this.configFile.nameSettings.maxLength) {
                placeholder.addPlaceholder("{MAX_LENGTH}", this.configFile.nameSettings.maxLength);
                this.messageManager.send(
                        player, commandSubConfig.error.exceededNameLength, placeholder
                );
                this.playErrorSound(player);
                return;
            }
        }

        placeholder.addPlaceholder("{NAME}", serializedName);
        if (!this.configFile.nameSettings.supportColor) {
            name = Component.text(nameWithoutColors);
            placeholder.addPlaceholder("{NAME}", nameWithoutColors);
        }

        if (!PermissionManager.hasPermission(player, Permissions.COLOR_NAME)) {
            name = Component.text(nameWithoutColors);
            placeholder.addPlaceholder("{NAME}", nameWithoutColors);
        }

        Utils.setContainerName(container, name);

        if (player.getGameMode() != GameMode.CREATIVE) {
            itemStack.subtract(1);
        }

        placeholder.addPlaceholder("{BLOCK}", Utils.getMaterialLang(material));

        this.messageManager.send(
                player, commandSubConfig.set, placeholder
        );
        this.cooldownManager.newCooldown(player.getUniqueId(), TimeUnit.SECONDS.toMillis(
                this.configFile.commandSettings.cooldown
        ));
    }

    private MessageFile message() {
        return this.plugin.getMessageFile();
    }

    private void playErrorSound(final Player player) {
        Utils.playErrorSound(player, this.configFile.errorSound);
    }
}
