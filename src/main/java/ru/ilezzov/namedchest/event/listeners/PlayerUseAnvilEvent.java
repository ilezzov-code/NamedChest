package ru.ilezzov.namedchest.event.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.color.LegacySerialize;
import ru.ilezzov.namedchest.cooldown.CooldownManager;
import ru.ilezzov.namedchest.cooldown.CooldownResponse;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.permission.PermissionManager;
import ru.ilezzov.namedchest.permission.Permissions;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;
import ru.ilezzov.namedchest.send.SendManager;
import ru.ilezzov.namedchest.utils.Utils;

import java.util.concurrent.TimeUnit;

public class PlayerUseAnvilEvent implements Listener {
    private final Main plugin;
    private final ConfigFile configFile;
    private final SendManager sendManager;
    private final PluginPlaceholder placeholder;
    private final CooldownManager cooldownManager;

    public PlayerUseAnvilEvent(final Main plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.sendManager = plugin.getSendManager();
        this.placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );
        this.cooldownManager = plugin.getNameCooldownManager();
    }

    @EventHandler
    public void onPlayerUseAnvilEvent(final InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory)) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) {
            return;
        }

        if (!itemStack.hasItemMeta()) {
            return;
        }

        if (!this.configFile.supportedBlocks.contains(itemStack.getType())) {
            return;
        }

        final Entity entity = event.getWhoClicked();
        if (this.configFile.nameSettings.cancelAnvil) {
            event.setCancelled(true);

            this.sendManager.sendMessage(
                    entity, this.message().mainCommand.error.cancelAnvil, placeholder
            );
            this.sendManager.playErrorSound(entity);
            return;
        }

        if (!PermissionManager.hasPermission(entity, Permissions.SET_COMMAND, Permissions.COLOR_NAME)) {
            this.sendManager.sendMessage(
                    entity, this.message().plugin.noPerms, placeholder
            );
            this.sendManager.playErrorSound(entity);
            event.setCancelled(true);
            return;
        }

        if (!PermissionManager.hasPermission(entity, Permissions.NO_COOLDOWN)) {
            final CooldownResponse response = this.cooldownManager.getCooldown(entity.getUniqueId());

            if (response.hasCooldown()) {
                placeholder.addPlaceholder("{COOLDOWN}", TimeUnit.MILLISECONDS.toSeconds(response.remaining()));

                this.sendManager.sendMessage(
                        entity, this.message().plugin.cooldown, placeholder
                );
                this.sendManager.playErrorSound(entity);
                event.setCancelled(true);
                return;
            }
        }

        final ItemMeta meta = itemStack.getItemMeta();
        final MessageFile.MainCommandSubConfig commandSubConfig = this.message().mainCommand;

        final String textName = Utils.getTextNameFromList(
                meta.getDisplayName().split("\\s+"), this.configFile.nameSettings.supportSpaces
        );
        final String serializedName = LegacySerialize.serializeToString(textName);
        Component name = LegacySerialize.serializeFromString(serializedName);
        final String nameWithoutColors = LegacySerialize.serializeWithoutColors(name);

        if (nameWithoutColors.isBlank()) {
            this.sendManager.sendMessage(
                    entity, commandSubConfig.error.emptyName, placeholder
            );
            this.sendManager.playErrorSound(entity);
            event.setCancelled(true);
            return;
        }

        if (!PermissionManager.hasPermission(entity, Permissions.IGNORE_MAX_LENGTH)) {
            if (nameWithoutColors.length() > this.configFile.nameSettings.maxLength) {
                placeholder.addPlaceholder("{MAX_LENGTH}", this.configFile.nameSettings.maxLength);

                this.sendManager.sendMessage(
                        entity, commandSubConfig.error.exceededNameLength, placeholder
                );
                this.sendManager.playErrorSound(entity);
                event.setCancelled(true);
                return;
            }
        }

        if (!this.configFile.nameSettings.supportColor) {
            name = Component.text(nameWithoutColors);
        }

        if (!PermissionManager.hasPermission(entity, Permissions.COLOR_NAME)) {
            name = Component.text(nameWithoutColors);
        }

        meta.displayName(name);
        itemStack.setItemMeta(meta);
    }

    public MessageFile message() {
        return this.plugin.getMessageFile();
    }
}
