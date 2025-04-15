package ru.ilezzov.namedchest.api;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import ru.ilezzov.namedchest.Main;

import java.util.Collection;

public class NamedApi {
    public static void setName(final Container container, final Component name) {
        container.customName(name);
        container.update(true, true);
    }

    public static void clearName(final Container container) {
        container.customName(null);
        container.update(true, true);
    }


    private static NamespacedKey getStandName(final String key) {
        return new NamespacedKey(Main.getInstance(), "NamedChest".concat(key));

    }

}
