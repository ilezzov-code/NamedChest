package ru.ilezzov.namedchest.api;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;

public interface NamedChestAPI {
    Response checkBlock(final Block block);

    void setName(final Component name, final Container container);

    void removeName(final Container container);

    int componentLength(final Component component);

    boolean isPlayerInRegion(final Player player, final Location location);
}
