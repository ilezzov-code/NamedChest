package ru.ilezzov.namedchest.display.manager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.namedchest.Main;
import ru.ilezzov.namedchest.display.MyHologram;
import ru.ilezzov.namedchest.display.hologram.ArmorStandHologram;
import ru.ilezzov.namedchest.display.hologram.DecentHologram;
import ru.ilezzov.namedchest.file.ConfigFile;
import ru.ilezzov.namedchest.model.Response;
import ru.ilezzov.namedchest.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {
    private final Main plugin;
    private final ConfigFile configFile;
    private final boolean supportDHAPI;

    private final Map<UUID, MyHologram> activeHolograms = new HashMap<>();

    private BukkitTask tracerTask;

    public HologramManager(final Main plugin) {
        this.plugin = plugin;
        this.supportDHAPI = plugin.isSupportDecentHolograms();
        configFile = plugin.getConfigFile();
    }

    public void start() {
        tracerTask = Bukkit.getScheduler().runTaskTimer(
                this.plugin, () -> {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        final Block block = Utils.getTargetBlock(player, this.configFile.maxDistance, this.configFile.ignoredBlocks);
                        final Response<Component> acceptedBlock = this.checkBlock(block);

                        final UUID playerUUID = player.getUniqueId();
                        final MyHologram hologram = this.activeHolograms.get(playerUUID);

                        if (!acceptedBlock.success()) {
                            if (hologram != null) {
                                this.killIfExists(playerUUID, hologram);
                            }
                            continue;
                        }

                        final Location blockLocation = block.getLocation();

                        if (hologram != null && hologram.getBlockLocation().equals(blockLocation)) {
                            continue;
                        }

                        if (hologram != null) {
                            this.killIfExists(playerUUID, hologram);
                        }

                        final Component name = acceptedBlock.data();
                        final MyHologram newHologram = this.createHologram(name, blockLocation);

                        this.activeHolograms.put(playerUUID, newHologram);
                        newHologram.spawn(player.getLocation());
                    }
                }, 0L, 3L
        );
    }

    public void stop() {
        if (tracerTask != null && !tracerTask.isCancelled()) {
            tracerTask.cancel();
        }
    }

    public void killIfExists(final UUID uuid) {
        final MyHologram hologram = this.activeHolograms.get(uuid);
        if (hologram != null) {
            hologram.kill();
        }
    }

    public void killIfExists(final UUID uuid, final MyHologram hologram) {
        hologram.kill();
        this.activeHolograms.remove(uuid);
    }

    public void killAll() {
        for (final MyHologram hologram : this.activeHolograms.values()) {
            hologram.kill();
        }
    }

    private Response<Component> checkBlock(final Block block) {
        if (block == null) {
            return Response.error("");
        }

        final Material material = block.getType();
        if (!this.configFile.supportedBlocks.contains(material)) {
            return Response.error("");
        }

        final BlockState state = block.getState();
        if (!(state instanceof Container container)) {
            return Response.error("");
        }

        final Component name = container.customName();
        return name == null ? Response.error("") : Response.ok(name);
    }

    private MyHologram createHologram(final Component name, final Location blockLocation) {
        if (supportDHAPI) {
            return new DecentHologram(name, blockLocation);
        }
        return new ArmorStandHologram(name, blockLocation);
    }
}
