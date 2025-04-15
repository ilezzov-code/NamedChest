package ru.ilezzov.namedchest.events.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import ru.ilezzov.namedchest.api.NamedApi;
import ru.ilezzov.namedchest.enums.Permission;
import ru.ilezzov.namedchest.messages.PluginMessages;
import ru.ilezzov.namedchest.models.PluginPlaceholder;
import ru.ilezzov.namedchest.utils.LegacySerialize;
import ru.ilezzov.namedchest.utils.PermissionsChecker;

public class PlayerClickEvent implements Listener {
    private final PluginPlaceholder placeholder = new PluginPlaceholder();
    private final int nameMaxLength = Main.getConfigFile().getInt("name_settings.max_name_length");
    private final boolean nameSupportColor = Main.getConfigFile().getBoolean("name_settings.support_color");

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!PermissionsChecker.hasPermission(player, Permission.SET_NAME, Permission.SET_COLOR_NAME)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final ItemStack clickedItem = event.getItem();

        if (clickedItem == null) {
            return;
        }

        if (clickedItem.getType() != Material.NAME_TAG) {
            return;
        }

        final Block block = event.getClickedBlock();
        if (!Main.getBlockManager().contains(block.getType())) {
            player.sendMessage(PluginMessages.nameBlockError(placeholder));
            return;
        }

        final ItemMeta nameTagMeta = clickedItem.getItemMeta();
        if (!nameTagMeta.hasDisplayName()) {
            event.setCancelled(true);
            player.sendMessage(PluginMessages.nameEmptyMessage(placeholder));
            return;
        }

        final String name = nameTagMeta.getDisplayName();
        if (!PermissionsChecker.hasPermission(player, Permission.MAX_LENGHT)) {
            if (name.length() > nameMaxLength) {
                event.setCancelled(true);
                placeholder.addPlaceholder("{MAX_LENGTH}", nameMaxLength);
                player.sendMessage(PluginMessages.nameMaxLength(placeholder));
                return;
            }
        }

        final BlockState blockState = block.getState();
        if (!(blockState instanceof final Container container)) {
            event.setCancelled(true);
            player.sendMessage(PluginMessages.nameBlockError(placeholder));
            return;
        }

        event.setCancelled(true);
        Component nameComponent = null;

        if (nameSupportColor) {
            if (PermissionsChecker.hasPermission(player, Permission.SET_COLOR_NAME)) {
                nameComponent = LegacySerialize.serialize(name);
            }
        } else {
            nameComponent = Component.text(name).asComponent();
        }

        NamedApi.setName(container, nameComponent);
        clickedItem.setAmount(clickedItem.getAmount() - 1);

        placeholder.addPlaceholder("{NAME}", LegacyComponentSerializer.legacySection().serialize(nameComponent));
        placeholder.addPlaceholder("{BLOCK}", block.getType());
        player.sendMessage(PluginMessages.nameSetMessage(placeholder));
    }
}
