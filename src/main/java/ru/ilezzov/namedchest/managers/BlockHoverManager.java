package ru.ilezzov.namedchest.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedChestAPI;
import ru.ilezzov.namedchest.api.Response;
import ru.ilezzov.namedchest.api.Status;
import ru.ilezzov.namedchest.events.model.PlayerHoverEvent;

import java.util.HashMap;

public class BlockHoverManager {
    private final NamedChestAPI api = Main.getApi();
    private final Plugin plugin = Main.getInstance();

    private final BukkitTask task;
    private final int maxDistance = Main.getConfigFile().getInt("max_distance");

    private final HashMap<Location, Entity> hoverLocation = new HashMap<>();

    public BlockHoverManager() {
        this.task = startBlockHoverController();
    }

    public void stopTask() {
        this.task.cancel();
    }

    public void killAll() {
        hoverLocation.values().forEach(Entity::remove);
    }

    public void add(final Location location, final Entity entity) {
        this.hoverLocation.put(location, entity);
    }

    public boolean contains(final Location location) {
        return this.hoverLocation.containsKey(location);
    }

    public void remove(final Location location) {
        this.hoverLocation.remove(location);
    }

    public void kill(final Location location) {
        this.hoverLocation.get(location).remove();
    }

    private BukkitTask startBlockHoverController() {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                final Block block = player.getTargetBlock(maxDistance);
                final Response response = api.checkBlock(block, false);

                if (response.status() != Status.ACCESS) {
                    continue;
                }

                final Container container = (Container) response.data();
                if (container.customName() == null) {
                    continue;
                }

               final PlayerHoverEvent event = new PlayerHoverEvent(player, block, container);
               callEvent(event);
            }
        }, 0, 5);
    }
   
    private void callEvent(final Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

}
