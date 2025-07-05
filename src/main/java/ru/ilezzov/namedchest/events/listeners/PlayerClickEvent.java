package ru.ilezzov.namedchest.events.listeners;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedChestAPI;
import ru.ilezzov.namedchest.api.Response;
import ru.ilezzov.namedchest.api.Status;
import ru.ilezzov.namedchest.messages.PluginMessages;
import ru.ilezzov.namedchest.permission.Permission;
import ru.ilezzov.namedchest.permission.PermissionsChecker;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.LegacySerialize;

public class PlayerClickEvent implements Listener {
    private final NamedChestAPI api = Main.getApi();
    private final PluginPlaceholder placeholder = new PluginPlaceholder();

    private final FileConfiguration configuration = Main.getConfigFile().getConfig();
    private final ConfigurationSection nameSettings = configuration.getConfigurationSection("name_settings");

    private final boolean supportColor = nameSettings.getBoolean("support_color");
    private final boolean supportSpaces = nameSettings.getBoolean("support_spaces");

    private final int maxNameLength = nameSettings.getInt("max_name_length");

    @EventHandler
    public void onPlayerClickEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if(!PermissionsChecker.hasPermission(player, Permission.NAME_SET, Permission.NAME_SET_COLOR)) {
            return;
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

        final Block block = event.getClickedBlock();
        final Response response = api.checkBlock(block);

        if (Main.isSupportWorldGuard()) {
            if (!api.isPlayerInRegion(player, block.getLocation())) {
                player.sendMessage(PluginMessages.commandNotOwnedRegion(placeholder));
                return;
            }
        }


        if (response.status() != Status.ACCESS) {
            return;
        }

        event.setCancelled(true);

        final ItemMeta nameTagMeta = itemStack.getItemMeta();

        if (!nameTagMeta.hasDisplayName()) {
            player.sendMessage(PluginMessages.commandNameEmpty(placeholder));
            return;
        }

        String textName = nameTagMeta.getDisplayName();
        final String[] textNameSplit = textName.split("\\s+");
        final Component name;

        if (!supportSpaces) {
            textName = textNameSplit[0];
        }

        if (supportColor) {
            if (PermissionsChecker.hasPermission(player, Permission.NAME_SET_COLOR)) {
                name = LegacySerialize.serialize(textName);
            } else {
                name = Component.text(textName);
            }
        } else {
            name = Component.text(textName);
        }

        if (PermissionsChecker.hasPermission(player, Permission.NAME_MAX_LENGTH)) {
            api.setName(name, (Container) response.data());

            final Material material = block.getType();

            placeholder.addPlaceholder("{BLOCK}", material);
            placeholder.addPlaceholder("{NAME}", LegacyComponentSerializer.legacySection().serialize(name));

            player.sendMessage(PluginMessages.commandNameSet(placeholder));
        } else {
            final int componentLength = api.componentLength(name);

            if (componentLength > maxNameLength) {
                placeholder.addPlaceholder("{MAX_LENGTH}", maxNameLength);
                player.sendMessage(PluginMessages.commandNameMaxLength(placeholder));
            } else {
                api.setName(name, (Container) response.data());

                final Material material = block.getType();

                placeholder.addPlaceholder("{BLOCK}", material);
                placeholder.addPlaceholder("{NAME}", LegacyComponentSerializer.legacySection().serialize(name));

                player.sendMessage(PluginMessages.commandNameSet(placeholder));
            }
        }
        removeTagFromInventory(player, itemStack);
    }

    private void removeTagFromInventory(final Player player, final ItemStack nameTag) {
        final String displayName = nameTag.getItemMeta().getDisplayName();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack itemStack = player.getInventory().getItem(i);

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getAmount() < 1) {
                continue;
            }

            if (itemStack.getType() != nameTag.getType()) {
                continue;
            }

            if (!itemStack.hasItemMeta()) {
                continue;
            }

            final ItemMeta itemMeta = itemStack.getItemMeta();

            if (!itemMeta.hasDisplayName()) {
                continue;
            }

            if (itemMeta.getDisplayName().equals(displayName)) {
                itemStack.setAmount(itemStack.getAmount() -1);
                player.getInventory().setItem(i, itemStack);
                break;
            }
        }
    }
}
