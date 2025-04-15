package ru.ilezzov.namedchest.managers;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.events.models.PlayerHoverEvent;
import ru.ilezzov.namedchest.messages.PluginMessages;

public class HoverManager {
    private final BukkitTask task;

    public HoverManager() {
        this.task = getHoverRunnable().runTaskTimer(Main.getInstance(), 0, 5);
    }

    private BukkitRunnable getHoverRunnable() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final Block block = getTargetBlock(player);

                    if (block == null) {
                        continue;
                    }

                    if (!Main.getBlockManager().contains(block.getType())) {
                        continue;
                    }

                    if (!(block.getState() instanceof Container container)) {
                        continue;
                    }

                    if (container.customName() == null) {
                        continue;
                    }

                    Bukkit.getPluginManager().callEvent(new PlayerHoverEvent(player, block));
                }
            }
        };
    }

    public void stop() {
        this.task.cancel();
    }

    private Block getTargetBlock(final Player player) {
        return player.getTargetBlockExact(Main.getConfigFile().getInt("block_max_distant"));
    }
}
