package ru.ilezzov.namedchest.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import ru.ilezzov.namedchest.color.Colorizer;

public class MessageFile extends OkaeriConfig {
    @Comment({
            "The plugin supports all types of message formatting.",
            "LEGACY — Color via & / § and HEX via &#rrggbb / §#rrggbb or &x&r&r&g&g&b&b / §x§r§r§g§g§b§b",
            "LEGACY_ADVANCED — Color and HEX via &##rrggbb / §##rrggbb",
            "MINI_MESSAGE — Color via <color> More details — https://docs.advntr.dev/minimessage/index.html",
            "And all formats available at https://www.birdflop.com/resources/rgb/",
            "",
            "MINI_MESSAGE (RECOMMENDED) — color via <color> More details — https://docs.advntr.dev/minimessage/index.html",
            "ALL — support for all formats simultaneously. Slower, not all MINI_MESSAGE tags are supported",
            "",
            "Плагин поддерживает все виды форматирования сообщений.",
            "# LEGACY — Цвет через & / § и HEX через &#rrggbb / §#rrggbb или &x&r&r&g&g&b&b / §x§r§r§g§g§b§b",
            "# LEGACY_ADVANCED — Цвет и HEX через &##rrggbb / §##rrggbb",
            "# MINI_MESSAGE — Цвет через <цвет> Подробнее — https://docs.advntr.dev/minimessage/index.html",
            "# И все форматы доступные на https://www.birdflop.com/resources/rgb/",
            "",
            "# MINI_MESSAGE (РЕКОМЕНДУЕТСЯ) — цвет через <цвет> Подробнее — https://docs.advntr.dev/minimessage/index.html",
            "# ALL — поддержка всех форматов одновременно. Медленнее, поддерживаются не все теги MINI_MESSAGE"
    })
    public Colorizer colorizer = Colorizer.MINI_MESSAGE;

    @Comment("")
    public PluginSubConfig plugin = new PluginSubConfig();

    @Comment("")
    public VersionSubConfig version = new VersionSubConfig();

    @Comment("")
    @CustomKey("main-command")
    public MainCommandSubConfig mainCommand = new MainCommandSubConfig();

    public static class PluginSubConfig extends OkaeriConfig {
        @Comment({
                "Plugin prefix for regular messages. Use {P} in messages",
                "Префикс плагина для обычных сообщений. Используйте {P} в сообщениях"
        })
        public String prefix = "<gold>PluginBlank</gold> <dark_gray>|</dark_gray>";

        @Comment({
                "Plugin prefix for error messages. Use {P_E} in messages",
                "Префикс плагина для сообщений об ошибке. Используйте {P_E} в сообщения"
        })
        @CustomKey("prefix-error")
        public String prefixError = "<red>PluginBlank</red> <dark_gray>|</dark_gray>";

        public String reload = "{P} <gray>The plugin has been successfully <green>reloaded</gray>";

        @CustomKey("no-console")
        public String noConsole = "{P_E} <red>This command can only be executed by players</red>";

        @CustomKey("no-perms")
        public String noPerms = "{P_E} <red>You do not have permission to use this command</red>";

        @CustomKey("command-disable")
        public String commandDisable = "{P_E} <red>This command has been disabled by the server administrator</red>";

        public String cooldown = "{P} <gray>Please wait another <yellow>{COOLDOWN}s</yellow> before using this command again";
    }

    public static class VersionSubConfig extends OkaeriConfig {
        public String latest = "{P} <gray>You are running the latest version of the plugin: <green>{CURRENT_VERSION}</green></gray>";
        public String supported = "{P} <gray>You are running a supported version of the plugin (<yellow>{CURRENT_VERSION}</yellow>), however we recommend updating to the latest version (<green>{LATEST_VERSION}</green>)";
        public String outdated = "{P} <gray>You are running an unsupported version of the plugin (<red>{CURRENT_VERSION}). <bold>{ACTION}</bold></red> Please immediately install the latest version (<green>{LATEST_VERSION}</green>)";
        public String blacklist = "{P} <gray>You are running a blacklisted version of the plugin (<red>{CURRENT_VERSION}). <bold>{ACTION}.</bold></red> Please immediately install the latest version (<green>{LATEST_VERSION}</green>)";
        public String download = "{P} Download here — <yellow><click:open_url:{DOWNLOAD_LINK}>{DOWNLOAD_LINK}</click></yellow>";
        public String error = "{P_E} <red>An error occurred while fetching the plugin version. Learn more in the console";
        public String loading = "{P} <gray>Loading the latest version of the plugin</gray>";
    }

    public static class MainCommandSubConfig extends OkaeriConfig {
        public String help = """
                {P} <gray>Plugin command help:
                 <yellow>• <click:suggest_command:/$command set>/$command set</click></yellow> — set a name
                 <yellow>• <click:suggest_command:/$command clear>/$command clear</click></yellow> — clear a name
                 <yellow>• <click:suggest_command:/$command reload>/$command reload</click></yellow> — reload the plugin
                 <yellow>• <click:suggest_command:/$command version>/$command version</click></yellow> — get the latest plugin version
                Contact the developer — <blue><click:open_url:{CONTACT}>{CONTACT}</click></gray>
                """;
        public String set = "{P} <gray>You have successfully set the name '<yellow>{NAME}</yellow>' on block <yellow>{BLOCK}</yellow></gray>";
        public String clear = "{P} <gray>You have successfully cleared the name of block <yellow>{BLOCK}</yellow></gray>";
        public ErrorSection error = new ErrorSection();

        public static class ErrorSection extends OkaeriConfig {
            @CustomKey("not-your-region")
            public String notYourRegion = "{P_E} <red>You are not in your region</red>";

            @CustomKey("exceeded-name-length")
            public String exceededNameLength = "{P_E} <red>The name must be no longer than <yellow>{MAX_LENGTH}</yellow> characters</red>";

            @CustomKey("empty-name")
            public String emptyName = "{P_E} <red>The name cannot be empty</red>";

            @CustomKey("null-block")
            public String nullBlock = "{P_E} <red>The block cannot be air</red>";

            @CustomKey("unsupported-block")
            public String unsupportedBlock = "{P_E} <red>Unsupported block. Use supported blocks:</red> <yellow>{SUPPORTED_BLOCKS}</yellow>";

            @CustomKey("cancel-anvil")
            public String cancelAnvil = "{P_E} <red>You cannot rename this block using anvil</red>";

            @CustomKey("already-clear")
            public String alreadyClear = "{P_E} <red>There is no name for this block</red>";
        }
    }
}
