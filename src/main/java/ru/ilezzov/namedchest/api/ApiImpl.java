package ru.ilezzov.namedchest.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import ru.ilezzov.namedchest.Main;

public class ApiImpl implements NamedChestAPI{
    @Override
    public Response checkBlock(final Block block) {
        if (block == null) {
            return new Response(Status.NULL_BLOCK, null);
        }

        if (!Main.getBlockTypeManager().contains(block.getType())) {
            return new Response(Status.INVALUABLE_BLOCK, null);
        }

        final BlockState blockState = block.getState();

        if (!(blockState instanceof final Container container)) {
            return new Response(Status.INVALUABLE_BLOCK, null);
        }

        return new Response(Status.ACCESS, container);
    }

    @Override
    public void setName(final Component name, final Container container) {
        container.customName(name);
        container.update(true, true);
    }

    @Override
    public void removeName(final Container container) {
        setName(null, container);
    }

    @Override
    public int componentLength(Component component) {
        Accumulator accumulator = new Accumulator();
        ComponentFlattener.basic().flatten(component, accumulator);
        return accumulator.length;
    }

    @Override
    public boolean isPlayerInRegion(Player player, Location location) {
        final WorldGuard worldGuard = Main.getWorldGuard();

        if (worldGuard == null) {
            return false;
        }

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        final RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager == null) {
            return false;
        }

        final ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        if (regions.size() == 0) {
            return true;
        }

        for (final ProtectedRegion region : regions) {
            if (region.isOwner(localPlayer) || region.isMember(localPlayer)) {
                return true;
            }
        }
        return false;
    }

    private static final class Accumulator implements FlattenerListener {

        private int length = 0;

        @Override
        public void component(String text) {
            this.length += text.length();
        }

    }
}
