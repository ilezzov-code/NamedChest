package ru.ilezzov.namedchest.utils;

import org.bukkit.command.CommandSender;
import ru.ilezzov.namedchest.enums.Permission;

public class PermissionsChecker {
    public static boolean hasPermission(final CommandSender sender, final Permission... permissions) {
        boolean hasPerms = false;

        for (final Permission permission : permissions) {
            if (hasPerms) {
                return hasPerms;
            }
            hasPerms = sender.hasPermission(permission.getPermission());
        }
        return hasPerms;
    }

    public static boolean hasPermission(final CommandSender sender) {
        return sender.hasPermission(Permission.MAIN.getPermission());
    }
}
