package ru.ilezzov.namedchest.display.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import ru.ilezzov.namedchest.display.MyHologram;

public class ArmorStandHologram extends MyHologram {
    private Entity entity;

    public ArmorStandHologram(final Component name, final Location blockLocation) {
        super(name, blockLocation);
    }

    @Override
    public void spawn(final Location playerLocation) {
        if (entity != null) {
            return;
        }

        final World world = playerLocation.getWorld();
        this.entity = world.spawn(this.createHologramLocation(playerLocation, 0.2), ArmorStand.class, armorStand -> {
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
        if (entity == null) {
            return;
        }
        entity.remove();
    }
}
