package ru.ilezzov.namedchest.enums;

import lombok.Getter;

@Getter
public enum Permission {
    MAIN("namedchest.*"),
    RELOAD("namedchest.reload"),
    SET_NAME("namedchest.name.set"),
    SET_COLOR_NAME("namedchest.name.set.color"),
    MAX_LENGHT("namedchest.name.max.lenght"),
    CLEAR_NAME("namedchest.name.clear");

    private final String permission;

    Permission(final String permission) {
        this.permission = permission;
    }
}
