package ru.ilezzov.namedchest.events.model;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerHoverEvent extends Event implements Cancellable {
    @Getter
    private final Player player;

    @Getter
    private final Block block;
    @Getter
    private final Container container;
    private boolean isCancel;

    private static final HandlerList handlerList = new HandlerList();

    public PlayerHoverEvent(final Player player, final Block block, final Container container) {
        this.player = player;
        this.block = block;
        this.container = container;
        this.isCancel = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancel;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancel = cancel;
    }
}