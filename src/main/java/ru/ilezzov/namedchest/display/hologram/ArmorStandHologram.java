package ru.ilezzov.namedchest.display.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import ru.ilezzov.namedchest.display.MyHologram;

public class ArmorStandHologram extends MyHologram {
    private static final double BLOCK_Y = 0.2;

    private Entity entity;

    public ArmorStandHologram(final Component name, final Location blockLocation, final float yaw) {
        super(name, blockLocation, yaw);
    }

    @Override
    public void spawn(final Location playerLocation) {
        if (this.entity != null) {
            return;
        }

        final World world = playerLocation.getWorld();
        this.entity = world.spawn(this.createHologramLocation(playerLocation, BLOCK_Y), ArmorStand.class, armorStand -> {
            armorStand.customName(this.name);
            armorStand.setCustomNameVisible(true);

            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setBasePlate(false);

            armorStand.setPersistent(false);
            armorStand.setCanPickupItems(false);
            armorStand.setDisabledSlots(EquipmentSlot.values());
            armorStand.setSmall(true);
        });
    }

    @Override
    public void kill() {
        if (this.entity == null) {
            return;
        }
        this.entity.remove();
    }

    @Override
    public void move(final Location playerLocation) {
        if (this.entity == null) {
            return;
        }
        this.entity.teleport(this.createHologramLocation(playerLocation, BLOCK_Y));
    }
}
