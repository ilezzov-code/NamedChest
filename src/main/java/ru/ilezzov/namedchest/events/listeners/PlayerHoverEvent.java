package ru.ilezzov.namedchest.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.api.NamedChestAPI;
import ru.ilezzov.namedchest.managers.BlockHoverManager;

import static org.bukkit.block.BlockFace.NORTH;

public class PlayerHoverEvent implements Listener {
    private final NamedChestAPI api = Main.getApi();
    private final BlockHoverManager blockHoverManager = Main.getBlockHoverManager();
    private final int maxDistance = Main.getConfigFile().getInt("max_distance");

    @EventHandler
    public void onPLayerHoverEvent(final ru.ilezzov.namedchest.events.model.PlayerHoverEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final Container container = event.getContainer();

        if (blockHoverManager.contains(block.getLocation())) {
            return;
        }

        showTextDisplay(player, block, container);
    }

    private void showTextDisplay(final Player player, final Block block, final Container container) {
        final Entity textEntity = spawnTextEntity(player, block, container);
        blockHoverManager.add(block.getLocation(), textEntity);
        startShowTextController(player, textEntity, block.getLocation());
    }

    private Entity spawnTextEntity(final Player player, final Block block, final Container container) {
        final BlockFace blockFace = getCardinalDirection(player);

        final Location location = switch (blockFace) {
            case NORTH -> block.getLocation().add(0.5, 0.2, 1);
            case WEST -> block.getLocation().add(1, 0.2, 0.5);
            case EAST -> block.getLocation().add(-0.2, 0.2, 0.5);
            case SOUTH -> block.getLocation().add(0.5, 0.2, -0.2);
            default -> block.getLocation().add(0.5, 0.5,0.5);
        };

        return block.getWorld().spawn(location, ArmorStand.class, entity -> {
            entity.customName(container.customName());
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setCustomNameVisible(true);
            entity.setMarker(true);
            entity.setSmall(true);
        });
    }

    private BlockFace getCardinalDirection(final Player player) {
        float yaw = player.getLocation().getYaw();

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return NORTH;
        } else if (yaw >= 225) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    private void startShowTextController(final Player player, final Entity textDisplay, final Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Block targetBlock = player.getTargetBlockExact(maxDistance);

                if (!player.isOnline()) {
                    Main.getBlockHoverManager().remove(location);
                    textDisplay.remove();
                    cancel();
                    return;
                }

                if (targetBlock == null) {
                    Main.getBlockHoverManager().remove(location);
                    textDisplay.remove();
                    cancel();
                    return;
                }

                if (targetBlock.getLocation().equals(location)) {
                    return;
                }

                blockHoverManager.remove(location);
                textDisplay.remove();
                cancel();
            }
        }.runTaskTimer(Main.getInstance(), 0, 5);
    }
}
