package ru.ilezzov.namedchest.display.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import ru.ilezzov.namedchest.display.MyHologram;

import java.util.List;

public class DecentHologram extends MyHologram {
    private Hologram hologram;

    private static final String HOLOGRAM_NAME_PATTERN = "nc-hologram-%d-%d-%d-%d";
    private static final double BLOCK_Y = 0.7;

    public DecentHologram(final Component name, final Location blockLocation, final float yaw) {
        super(name, blockLocation,  yaw);
    }

    @Override
    public void spawn(final Location playerLocation) {
        if (this.hologram != null) {
            return;
        }

        final String hName = this.createHologramName(playerLocation);
        if (DHAPI.getHologram(hName) != null)
            return;

        this.hologram = DHAPI.createHologram(
                hName,
                this.createHologramLocation(playerLocation, BLOCK_Y),
                List.of(LegacyComponentSerializer.legacySection().serialize(this.name))
        );
    }

    @Override
    public void kill() {
        if (this.hologram == null) {
            return;
        }
        this.hologram.delete();
    }

    @Override
    public void move(final Location playerLocation) {
        if (this.hologram == null) {
            return;
        }

        DHAPI.moveHologram(this.hologram, this.createHologramLocation(playerLocation, BLOCK_Y));
    }

    private String createHologramName(final Location location) {
        return HOLOGRAM_NAME_PATTERN.formatted(
                location.getBlockX(), location.getBlockY(), location.getBlockZ(), System.currentTimeMillis()
        );
    }
}
