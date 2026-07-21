package ru.ilezzov.namedchest.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Cooldown> cooldownHashMap = new HashMap<>();

    public void newCooldown(final UUID uuid, final long delay) {
        this.cooldownHashMap.put(
                uuid, new Cooldown(delay, System.currentTimeMillis())
        );
    }

    public CooldownResponse getCooldown(final UUID uuid) {
        if (!check(uuid)) {
            return new CooldownResponse(false, 0);
        }

        final Cooldown cooldown = this.cooldownHashMap.get(uuid);
        final long currentTime = System.currentTimeMillis();

        if (cooldown.endOfDelay() <= currentTime) {
            return new CooldownResponse(false, 0);
        }

        return new CooldownResponse(true, cooldown.endOfDelay() - currentTime);
    }

    private boolean check(final UUID uuid) {
        return this.cooldownHashMap.containsKey(uuid);
    }

    private record Cooldown(long delay, long created_at) {
        public long endOfDelay() {
            return created_at + delay;
        }
    }
}