package ru.ilezzov.namedchest.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import ru.ilezzov.namedchest.file.ConfigFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {
    private static final String LANG_TAG = "<lang:%s>";

    public static void setContainerName(final Container container, final Component name) {
        container.customName(name);
        container.update(true, true);
    }

    public static void clearContainerName(final Container container) {
        setContainerName(container, null);
    }

    public static String getMaterialLang(final Material material) {
        return LANG_TAG.formatted(
                material.translationKey()
        );
    }

    public static String getMaterialsString(final Set<Material> materialSet) {
        final List<String> materialList = new ArrayList<>();

        for (final Material material : materialSet) {
            materialList.add(getMaterialLang(material));
        }

        return String.join(", ", materialList);
    }

    public static boolean canModify(final Player player, final Location location, final WorldGuard worldGuard) {
        final RegionQuery regionQuery = worldGuard.getPlatform().getRegionContainer().createQuery();

        return regionQuery.testState(
                BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD
        );
    }

    public static Block getTargetBlock(final Player player, final int maxDistance, final boolean ignorePassableBlocks) {
        if (ignorePassableBlocks) {
            final RayTraceResult result = player.getWorld().rayTraceBlocks(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection(),
                    maxDistance,
                    FluidCollisionMode.NEVER,
                    true
            );

            if (result != null && result.getHitBlock() != null) {
                return result.getHitBlock();
            }
            return null;
        }
        return player.getTargetBlock(maxDistance);
    }

    public static String getTextNameFromList(final String[] args, final boolean supportSpaces) {
        final String firstWorld = args[0];

        if (firstWorld == null || firstWorld.isBlank()) {
            return null;
        }

        if (!supportSpaces) {
            return firstWorld;
        }

        return String.join(" ", args);
    }
}
