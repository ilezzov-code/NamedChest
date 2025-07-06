package ru.ilezzov.namedchest.events.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedChestAPI;
import ru.ilezzov.namedchest.api.Response;
import ru.ilezzov.namedchest.api.Status;

public class PlayerBlockBreakEvent implements Listener {
    private final NamedChestAPI api = Main.getApi();

    @EventHandler
    public void onBlockBreakEvent(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Response response = api.checkBlock(block, false);

        if (response.status() != Status.ACCESS) {
            return;
        }

        final Container container = (Container) response.data();
        api.removeName(container);
    }
}
