package ru.ilezzov.namedchest.events.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedApi;
import ru.ilezzov.namedchest.messages.PluginMessages;

public class PlayerBreakBlockEvent implements Listener {
    @EventHandler
    public void onPlayerBreakBlockEvent(final BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (!Main.getBlockManager().contains(block.getType())) {
            return;
        }

        final BlockState blockState = block.getState();

        if (!(blockState instanceof final Container container)) {
            return;
        }

        NamedApi.clearName(container);
    }
}
