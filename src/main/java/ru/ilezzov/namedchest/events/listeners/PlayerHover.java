package ru.ilezzov.namedchest.events.listeners;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.events.models.PlayerHoverEvent;

public class PlayerHover implements Listener {
    @EventHandler
    public void onPlayerHove(final PlayerHoverEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (Main.getBlockHoverManager().contains(block)) {
            return;
        }

        startTextDisplayRunnable(player, block);
    }

    private void startTextDisplayRunnable(final Player player, final Block block) {
        final Entity textDisplay = spawnTextDisplay(player, block);
        Main.getBlockHoverManager().add(block, textDisplay);
        getTextDisplayRunnable(player, textDisplay, block).runTaskTimer(Main.getInstance(), 0, 5);
    }

    private Entity spawnTextDisplay(final Player player, final Block block) {
        final Container container = (Container) block.getState();
        final BlockFace playerBlockFace = getCardinalDirection(player);

        final Location location = switch (playerBlockFace) {
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
//        return block.getWorld().spawn(location, TextDisplay.class, entity -> {
//            entity.text(container.customName());
//            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
//            entity.setSeeThrough(true);
//            entity.setBillboard(Display.Billboard.CENTER);
//            entity.setDefaultBackground(false);
//            entity.setViewRange(0.5f);
//            entity.setBrightness(new Display.Brightness(15, 15));
//            entity.setGlowColorOverride(Color.fromRGB(255, 255, 255));
//            entity.setGlowing(true);
//        });
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
            return BlockFace.NORTH;
        } else if (yaw >= 225) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH; // По умолчанию (на всякий случай)
        }
    }

    private BukkitRunnable getTextDisplayRunnable(final Player player, final Entity textDisplay, final Block block) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                final Block targetBlock = player.getTargetBlockExact(Main.getConfigFile().getInt("block_max_distant"));

                if (!player.isOnline()) {
                    Main.getBlockHoverManager().remove(block);
                    textDisplay.remove();
                    cancel();
                }

                if (targetBlock == null) {
                    Main.getBlockHoverManager().remove(block);
                    textDisplay.remove();
                    cancel();
                    return;
                }

                if (targetBlock.getLocation().equals(block.getLocation())) {
                    return;
                }

                Main.getBlockHoverManager().remove(block);
                textDisplay.remove();
                cancel();
            }
        };
    }
}
