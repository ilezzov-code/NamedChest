package ru.ilezzov.namedchest.event.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.utils.Utils;

@RequiredArgsConstructor
public class PlayerBlockBreakEvent implements Listener {
    private final ConfigFile configFile;

    @EventHandler
    public void onPlayerBreakBlockEvent(final BlockBreakEvent event) {
        final Block block = event.getBlock();

        final Material material = block.getType();
        if (!this.configFile.supportedBlocks.contains(material)) {
            return;
        }

        final BlockState state = block.getState();

        if (!(state instanceof Container container)) {
            return;
        }

        Utils.clearContainerName(container);
    }
}
