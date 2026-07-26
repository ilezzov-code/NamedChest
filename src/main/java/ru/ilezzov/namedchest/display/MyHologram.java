package ru.ilezzov.namedchest.display;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public abstract class MyHologram {
    @Getter
    protected final Location blockLocation;
    protected final Component name;

    public MyHologram(final Component name, final Location blockLocation) {
        this.name = name;
        this.blockLocation = blockLocation;
    }

    public abstract void spawn(final Location playerLocation);

    public abstract void kill();

    protected Location createHologramLocation(final Location playerLocation, final double y) {
        final BlockFace blockFace = this.getCardinalDirection(playerLocation);
        final Location blockLocation = this.blockLocation.clone();

        return switch (blockFace) {
            case NORTH -> blockLocation.add(0.5, y, 1);
            case WEST -> blockLocation.add(1, y, 0.5);
            case EAST -> blockLocation.add(0, y, 0.5);
            case SOUTH -> blockLocation.add(0.5, y, 0);
            default -> blockLocation.add(0.5, y,0.5);
        };
    }

    protected BlockFace getCardinalDirection(final Location playerLocation) {
        float yaw = playerLocation.getYaw();

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
            return BlockFace.SOUTH;
        }
    }

}
