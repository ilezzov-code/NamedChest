package ru.ilezzov.namedchest.event.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.ilezzov.namedchest.display.manager.HologramManager;

@RequiredArgsConstructor
public class PlayerLeaveEvent implements Listener {
    private final HologramManager hologramManager;

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.hologramManager.killIfExists(player.getUniqueId());
    }
}
