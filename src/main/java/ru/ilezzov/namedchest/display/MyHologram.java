package ru.ilezzov.namedchest.display;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public abstract class MyHologram {
    private static final BlockFace[] RADIAL = {
            BlockFace.SOUTH,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.EAST,
            BlockFace.SOUTH_EAST
    };

    @Getter
    protected final Location blockLocation;
    @Getter
    @Setter
    protected float yaw;
    protected final Component name;

    public MyHologram(final Component name, final Location blockLocation, final float yaw) {
        this.blockLocation = blockLocation;
        this.yaw = yaw;
        this.name = name;
    }

    public abstract void spawn(final Location playerLocation);

    public abstract void kill();

    public abstract void move(final Location playerLocation);

    protected Location createHologramLocation(final Location playerLocation, final double y) {
        final BlockFace blockFace = this.getCardinalDirection(playerLocation);
        final Location blockLocation = this.blockLocation.clone();

        return switch (blockFace) {
            case NORTH -> blockLocation.add(0.5, y, 1);
            case WEST -> blockLocation.add(1, y, 0.5);
            case EAST -> blockLocation.add(0, y, 0.5);
            case SOUTH -> blockLocation.add(0.5, y, 0);
            case SOUTH_WEST -> blockLocation.add(1, y, 0);
            case SOUTH_EAST -> blockLocation.add(0, y, 0);
            case NORTH_WEST -> blockLocation.add(1, y, 1);
            case NORTH_EAST -> blockLocation.add(0, y, 1);
            default -> blockLocation.add(0.5, y,0.5);
        };
    }

    protected BlockFace getCardinalDirection(final Location playerLocation) {
        final float yaw = (playerLocation.getYaw() % 360 + 360) % 360;
        final int index = Math.round(yaw / 45.0f) % 8;

        return RADIAL[index];
    }
}
