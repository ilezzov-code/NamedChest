package ru.ilezzov.namedchest.events.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.messages.PluginMessages;
import ru.ilezzov.namedchest.models.PluginPlaceholder;

public class PlayerUseAnvilEvent implements Listener {
    final PluginPlaceholder placeholder = new PluginPlaceholder();

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

        if (!Main.getBlockManager().contains(itemStack.getType())) {
            return;
        }

        event.setCancelled(true);
        event.getWhoClicked().sendMessage(PluginMessages.nameUseAnvilError(placeholder));
    }
}
