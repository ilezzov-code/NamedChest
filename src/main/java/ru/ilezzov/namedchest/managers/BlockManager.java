package ru.ilezzov.namedchest.managers;

import org.bukkit.Material;
import ru.ilezzov.namedchest.Main;

import java.util.HashSet;
import java.util.List;

public class BlockManager {
    final HashSet<Material> materials;

    public BlockManager() {
        this.materials = new HashSet<>();
        setMaterials();
    }

    public boolean contains(final Material material) {
        return this.materials.contains(material);
    }

    public void reload() {
        setMaterials();
    }

    private void setMaterials() {
        final List<String> materialList = Main.getConfigFile().getList("supporting_block", String.class);

        for(String material : materialList) {
            this.materials.add(Material.valueOf(material));
        }
    }
}
