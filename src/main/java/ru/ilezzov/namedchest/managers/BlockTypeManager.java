package ru.ilezzov.namedchest.managers;

import org.bukkit.Material;
import ru.ilezzov.namedchest.Main;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockTypeManager {
    private final HashSet<Material> materials = new HashSet<>();

    public BlockTypeManager() {
        materials.addAll(loadMaterials());
    }

    public boolean contains(final Material material) {
        return materials.contains(material);
    }

    public void reload() {
        materials.clear();
        materials.addAll(loadMaterials());
    }

    private Set<Material> loadMaterials() {
        return Main.getConfigFile().getList("supporting_block", String.class).stream()
                .map(Material::valueOf)
                .collect(Collectors.toSet());
    }
}
