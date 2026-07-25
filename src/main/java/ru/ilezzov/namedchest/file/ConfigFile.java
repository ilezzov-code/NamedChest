package ru.ilezzov.namedchest.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


// Header generator: https://fsymbols.com/ru/generatory/
@Header({
        "███╗░░██╗░█████╗░███╗░░░███╗███████╗██████╗░░░░█████╗░██╗░░██╗███████╗░██████╗████████╗",
        "████╗░██║██╔══██╗████╗░████║██╔════╝██╔══██╗░░██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝",
        "██╔██╗██║███████║██╔████╔██║█████╗░░██║░░██║░░██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░",
        "██║╚████║██╔══██║██║╚██╔╝██║██╔══╝░░██║░░██║░░██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░",
        "██║░╚███║██║░░██║██║░╚═╝░██║███████╗██████╔╝░░╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░",
        "╚═╝░░╚══╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░░░░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░",
        "",
        "Developer / Разработчик: ILeZzoV",
        "More plugins / Больше плагинов: https://t.me/ilezzov_plugins",
        "",
        "Socials / Ссылки:",
        "• Contact with me / Связаться: https://t.me/ilezovofficial",
        "• Telegram Channel / Телеграм канал: https://t.me/ilezzov_plugins",
        "• GitHub: https://github.com/ilezzov-code",
        "",
        "By me coffee / Поддержать разработчика:",
        "• DA: https://www.donationalerts.com/r/ilezov",
        "• YooMoney: https://yoomoney.ru/to/4100118180919675",
        "• Telegram Gift: https://t.me/ilezovofficial",
        "• TON: UQBENFaqzv19GBR1EnDKad_nAOB--Ofsdk1FeRU7W3xprRLY",
        "• BTC: bc1qn0f9yvcsacvedjk6qzu9cvagchkcz28gqrg2eh",
        "• ETH: 0xbE7f260b952b87578079C0D6E03d3641425718c9",
        "• Card: 2200700733487101",
        ""
})
public class ConfigFile extends OkaeriConfig {

    @Comment({
            "Supporting messages languages / Доступные языки сообщений:",
            "en-US, ru-RU"
    })
    public String language = "en-US";

    @Comment("")
    @CustomKey("version-control")
    public VersionControl versionControl = new VersionControl();

    public static class VersionControl extends OkaeriConfig {

        @Comment({
                "Check for updates on server startup",
                "Проверять ли наличие обновлений при запуске сервера"
        })
        @CustomKey("check-on-startup")
        public boolean checkOnStartup = true;

        @Comment({
                "",
                "Background update check interval",
                "Интервал автоматической проверки обновлений в фоне"
        })
        @CustomKey("check-interval")
        public Interval checkInterval = new Interval(true, TimeUnit.HOURS, 6);

        @Comment({
                "",
                "Notify administrators about new versions on join",
                "Отправлять ли администраторам уведомление о наличии новой версии при входе на сервер",
        })
        @CustomKey("notify-admins-on-join")
        public boolean notifyAdminsOnJoin = true;


        @Comment("")
        public Security security = new Security();
    }

    public static class Interval extends OkaeriConfig {
        public boolean enable;
        @Comment("TimeUnit: SECONDS, MINUTES, HOURS, DAYS")
        public TimeUnit unit;
        public int value;

        public Interval() {}

        public Interval(boolean enable, TimeUnit unit, int value) {
            this.enable = enable;
            this.unit = unit;
            this.value = value;
        }
    }

    public static class Security extends OkaeriConfig {

        @Comment({
                "Automatically disable the plugin if the current version is no longer supported or if a critical vulnerability is found",
                "Warning: Setting true may disable important functionality",
                "",
                "Автоматическое отключение плагина, если текущая версия больше не поддерживается или если обнаружена критическая уязвимость",
                "Внимание: Установка значения true может привести к отключению важной функциональности"
        })
        @CustomKey("lockdown-on-critical")
        public boolean lockdownOnCritical = false;

        @Comment({
                "",
                "How often to spam online admins in chat",
                "about the need to urgently install a critical patch",
                "Set to 0 to notify only once on join",
                "",
                "Как часто спамить администраторам онлайн в чат",
                "о необходимости срочно установить критический патч",
                "Установите 0, чтобы уведомлять только один раз при входе",
        })
        @CustomKey("critical-notify-interval")
        public Interval criticalNotifyInterval = new Interval(true, TimeUnit.MINUTES, 10);
    }

    @Comment({
            "",
            "Blocks that can be named / Блоки, которым можно будет дать имя",
            "Supporting blocks / Поддерживаемые блоки: CHEST (сундук), TRAPPED_CHEST (сундук-ловушка),",
            "BARREL (бочка), FURNACE (печка), SMOKER (коптильня), BLAST_FURNACE (плавильня), HOPPER (воронка),",
            "DISPENSER (раздатчик), DROPPER (выбрасыватель), BREWING_STAND (варочная стойка), SHULKER_BOX (шалкер бокс)"
    })
    @CustomKey("supported-blocks")
    public Set<Material> supportedBlocks = EnumSet.of(
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL, Material.FURNACE,
            Material.SMOKER, Material.BLAST_FURNACE, Material.HOPPER, Material.DISPENSER,
            Material.DROPPER, Material.BREWING_STAND, Material.SHULKER_BOX
    );

    @Comment({
            "",
            "Список блоков, сквозь которые будет проходить взгляд (RayTrace)",
            "A list of blocks that the view will pass through (RayTrace)",
            "",
            "Air / Воздух: AIR, CAVE_AIR, VOID_AIR",
            "Grass(seagrass) / Трава(морская трава): GRASS, TALL_GRASS, SEAGRASS, TALL_SEAGRASS",
            "Fern and vine / Папоротник и лиана: FERN, LARGE_FERN, VINE",
    })
    @CustomKey("ignored-blocks")
    public Set<Material> ignoredBlocks = EnumSet.of(
            Material.AIR, Material.CAVE_AIR, Material.VOID_AIR,
            Material.GRASS, Material.TALL_GRASS, Material.SEAGRASS, Material.TALL_SEAGRASS,
            Material.FERN, Material.LARGE_FERN, Material.VINE, Material.LIGHT, Material.STRUCTURE_VOID
    );

    @Comment({
            "",
            "Maximum distance to the block / Максимальная дистанция до блока"
    })
    @CustomKey("max-distance")
    public int maxDistance = 5;

    @Comment({
            "",
            "Enable supporting WorldGuard / Включить поддержку WorldGuard",
            "WorldGuard is a Minecraft plugin — https://github.com/EngineHub/WorldGuard"
    })
    @CustomKey("support-worldguard")
    public boolean supportWorldGuard = true;

    @Comment({
            "",
            "Item's name settings / Настройки имени блока"
    })
    @CustomKey("name-settings")
    public NameSettings nameSettings = new NameSettings();

    public static class NameSettings extends OkaeriConfig {
        @Comment("Enable support color name / Включить поддержку цветных имен")
        @CustomKey("support-color")
        public boolean supportColor = true;

        @Comment("Max name length / Максимальная длинна имени")
        @CustomKey("max-length")
        public int maxLength = 25;

        @Comment("Save the name when the block is destroyed / Сохранять имя при разрушении блока")
        @CustomKey("save-when-breaking")
        public boolean saveWhenBreaking = false;

        @Comment("Forbid giving a name through an anvil / Запретить давать имя через наковальню")
        @CustomKey("cancel-anvil")
        public boolean cancelAnvil = true;

        @Comment("Support for spaces in the name / Поддержка пробелов в имени")
        @CustomKey("support-spaces")
        public boolean supportSpaces = true;

        @Comment("Giving names through a name tag / Давать имена через бирку")
        @CustomKey("support-name-tag")
        public boolean supportNameTag = true;
    }

    @Comment({
            "",
            "Command settings / Настройки команд"
    })
    @CustomKey("command-settings")
    public CommandSettings commandSettings = new CommandSettings();

    public static class CommandSettings extends OkaeriConfig {
        @Comment("Enable command /nc set | Включить команду /nc set")
        @CustomKey("enable-set")
        public boolean enableSet = true;

        @Comment("Enable command /nc clear | Включить команду /nc clear")
        @CustomKey("enable-clear")
        public boolean enableClear = true;

        @Comment("Cooldown in using commands in seconds | Задержка на использование команд в секундах")
        public int cooldown = 5;
    }

    @Comment("")
    public boolean debug = false;
}

