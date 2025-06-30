package ru.ilezzov.namedchest.permission;

import lombok.Getter;

@Getter
public enum Permission {
    MAIN("namedchest.*"),
    NO_COOLDOWN("namedchest.no_cooldown"),
    RELOAD("namedchest.reload"),
    NAME_SET("namedchest.name.set"),
    NAME_SET_COLOR("namedchest.name.set.color"),
    NAME_CLEAR("namedchest.name.clear"),
    NAME_MAX_LENGTH("namedchest.name.max.length");


    private final String permission;

    Permission(final String permission) {
        this.permission = permission;
    }
}
