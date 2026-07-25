package ru.ilezzov.namedchest.event.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.file.MessageFile;
import ru.ilezzov.namedchest.message.MessageManager;
import ru.ilezzov.namedchest.placeholder.PluginPlaceholder;

public class PlayerUseAnvilEvent implements Listener {
    private final Main plugin;
    private final ConfigFile configFile;
    private final MessageManager messageManager;
    private final PluginPlaceholder placeholder;

    public PlayerUseAnvilEvent(final Main plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.messageManager = plugin.getMessageManager();
        this.placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );
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

        event.setCancelled(true);
        this.messageManager.send(
                event.getWhoClicked(), this.message().mainCommand.error.cancelAnvil, placeholder
        );
    }

    public MessageFile message() {
        return this.plugin.getMessageFile();
    }

}
