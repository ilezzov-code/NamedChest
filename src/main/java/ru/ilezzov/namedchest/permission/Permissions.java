package ru.ilezzov.namedchest.permission;

import static ru.ilezzov.namedchest.BuildConfig.BASE_PERMISSION;

public class Permissions {
    public static final String ALL = create("*");
    public static final String RELOAD = create("reload");

    public static final String VERSION_NOTIFY = create("version.notify");
    public static final String VERSION_COMMAND = create("version.command");

    private static String create(final String key) {
        return BASE_PERMISSION.concat(key);
    }

}
