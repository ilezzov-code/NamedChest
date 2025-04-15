package ru.ilezzov.namedchest.managers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class BlockHoverManager {
    private final HashMap<Location, Entity> playerHover;

    public BlockHoverManager() {
        this.playerHover = new HashMap<>();
    }

    public boolean contains(final Block block) {
        return playerHover.containsKey(block.getLocation());
    }

    public void add(final Block block, final Entity entity) {
        this.playerHover.put(block.getLocation(), entity);
    }

    public void remove(final Block block) {
        this.playerHover.remove(block.getLocation());
    }

    public void kill(final Block block) {
        this.playerHover.get(block.getLocation()).remove();
    }

    public void killEntities() {
        for (final Entity entity : playerHover.values()) {
            entity.remove();
        }
    }
}
