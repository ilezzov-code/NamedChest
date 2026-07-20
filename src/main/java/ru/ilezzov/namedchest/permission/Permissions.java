package ru.ilezzov.namedchest.permission;

import static ru.ilezzov.namedchest.BuildConfig.BASE_PERMISSION;

public class Permissions {
    public static final String ALL = create("*");
    public static final String RELOAD = create("reload");

    public static final String NO_COOLDOWN = create("no_cooldown");

    public static final String SET_COMMAND = create("name.set");
    public static final String CLEAR_COMMAND = create("name.clear");

    public static final String COLOR_NAME = create("name.set.color");
    public static final String IGNORE_MAX_LENGTH = create("name.max.length");
    public static final String IGNORE_REGIONS = create("ignore_regions");

    public static final String VERSION_NOTIFY = create("version.notify");
    public static final String VERSION_COMMAND = create("version.command");

    private static String create(final String key) {
        return BASE_PERMISSION.concat(key);
    }

}
