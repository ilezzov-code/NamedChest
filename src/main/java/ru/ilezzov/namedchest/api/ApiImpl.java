package ru.ilezzov.namedchest.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
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

    private static final class Accumulator implements FlattenerListener {

        private int length = 0;

        @Override
        public void component(String text) {
            this.length += text.length();
        }

    }
}
