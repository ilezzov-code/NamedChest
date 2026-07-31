<div align="center">
    <img src="img/logo/BannerRoundedEn.png" alt="">
    <h4>Supported versions: Paper: 1.18.2 — 1.21.x</h4>
    <h1>Give your chest a unique name</h1>
</div>

<div align="center" content="">
    <a href="https://modrinth.com/plugin/namedchest">
        <img src="img/places/Modrinth.svg" width="200">
    </a>
    <a href="">
        <img src="img/places/Hangar.svg" width="200">
    </a>
    <a href="https://spigotmc.ru/resources/namedchest-gologrammnye-imena-pri-pomoschi-birok-dlja-vashix-xranilisch-1-18-2-1-21-x.3927/updates?ysclid=ms6l7m0y7b134784836">
          <img src="img/places/SpigotRU.svg" width="200">
    </a>
</div>

### <a href="https://github.com/ilezzov-code/NamedChest/blob/main/README_RU.md"><img src="img/flags/ru.svg" width=15px> Выбрать русский README.md</a>

## <a>[📋] Table of Contents</a>

- [Description](#about)
- [Features](#features)
- [Requirements and dependencies](#requirements)
- [Configuration](#config)
- [Commands and permissions](#commands-permissions)
- [Links](#links)
- [Support the developer](#donate)
- [Report a bug](https://github.com/ilezzov-code/NamedChest/issues/new)
- [License](LICENSE)


## <a id="about">[🐲] Description</a>

**NamedChest** is a unique plugin that allows you to give names to various blocks in Minecraft using commands, name tags, and anvils! The name will be displayed as a hologram when hovered over.

## <a id="features">[⭐] Features</a>
* **[🔥] Display names as holograms on hover** → [read more](details/details_en.md#hologram-names)
* [🆕] WorldGuard support → [read more](details/details_en.md#worldguard)
* [🆕] DecentHolograms support
* [🆕] Dynamic hologram repositioning based on player movement → [read more](details/details_en.md#dynamic-position)
* Renaming using name tags, commands, and anvils → [read more](details/details_en.md#renaming-methods)
* Support for 15 blocks, ignoring vegetation and fluids → [read more](details/details_en.md#supported-blocks)
* Any Minecraft color code format → [read more](details/details_en.md#color-format)
* Name length limits
* Configurable block search distance
* Enable / Disable the following features:
    * Saving names when blocks are broken
    * Restricting anvil usage
    * Space support
    * Name tag support
    * `set` and `clear` commands
* Configurable cooldown for commands and name tags
* Support for 2 languages (English, Russian) + ability to add custom translations
* Version control and security

## <a id="requirements">[⚙️] Requirements and dependencies</a>
* **Server Core:** Paper-1.18.2 (Purpur, Leaf, and other forks)
* **Java:** 17 or higher
* **Dependencies**:
    * [WorldGuard (optional)](https://github.com/EngineHub/WorldGuard)
    * [DecentHolograms (optional)](https://github.com/DecentSoftware-eu/DecentHolograms)

## <a id="config">[🐍] Configuration (config.yml)</a>
<details>
    <summary>View config.yml</summary>

```yml
# ███╗░░██╗░█████╗░███╗░░░███╗███████╗██████╗░░░░█████╗░██╗░░██╗███████╗░██████╗████████╗
# ████╗░██║██╔══██╗████╗░████║██╔════╝██╔══██╗░░██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝
# ██╔██╗██║███████║██╔████╔██║█████╗░░██║░░██║░░██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░
# ██║╚████║██╔══██║██║╚██╔╝██║██╔══╝░░██║░░██║░░██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░
# ██║░╚███║██║░░██║██║░╚═╝░██║███████╗██████╔╝░░╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░
# ╚═╝░░╚══╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░░░░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░

# Developer / Разработчик: ILeZzoV
# More plugins / Больше плагинов: https://t.me/ilezzov_plugins

# Socials / Ссылки:
# • Contact with me / Связаться: https://t.me/ilezovofficial
# • Telegram Channel / Телеграм канал: https://t.me/ilezzov_plugins
# • GitHub: https://github.com/ilezzov-code

# By me coffee / Поддержать разработчика:
# • DA: https://www.donationalerts.com/r/ilezov
# • YooMoney: https://yoomoney.ru/to/4100118180919675
# • Telegram Gift: https://t.me/ilezovofficial
# • TON: UQBENFaqzv19GBR1EnDKad_nAOB--Ofsdk1FeRU7W3xprRLY
# • BTC: bc1qn0f9yvcsacvedjk6qzu9cvagchkcz28gqrg2eh
# • ETH: 0xbE7f260b952b87578079C0D6E03d3641425718c9
# • Card: 2200700733487101

# Supporting messages languages / Доступные языки сообщений:
# en-US, ru-RU
language: en-US

version-control:
  # Check for updates on server startup
  # Проверять ли наличие обновлений при запуске сервера
  check-on-startup: true

  # Background update check interval
  # Интервал автоматической проверки обновлений в фоне
  check-interval:
    enable: true
    # TimeUnit: SECONDS, MINUTES, HOURS, DAYS
    unit: HOURS
    value: 6

  # Notify administrators about new versions on join
  # Отправлять ли администраторам уведомление о наличии новой версии при входе на сервер
  notify-admins-on-join: true

  security:
    # Automatically disable the plugin if the current version is no longer supported or if a critical vulnerability is found
    # Warning: Setting true may disable important functionality

    # Автоматическое отключение плагина, если текущая версия больше не поддерживается или если обнаружена критическая уязвимость
    # Внимание: Установка значения true может привести к отключению важной функциональности
    lockdown-on-critical: false

    # How often to spam online admins in chat
    # about the need to urgently install a critical patch
    # Set to 0 to notify only once on join

    # Как часто спамить администраторам онлайн в чат
    # о необходимости срочно установить критический патч
    # Установите 0, чтобы уведомлять только один раз при входе
    critical-notify-interval:
      enable: true
      # TimeUnit: SECONDS, MINUTES, HOURS, DAYS
      unit: MINUTES
      value: 10

# Blocks that can be named / Блоки, которым можно будет дать имя
# Supporting blocks / Поддерживаемые блоки: CHEST (сундук), TRAPPED_CHEST (сундук-ловушка),
# BARREL (бочка), FURNACE (печка), SMOKER (коптильня), BLAST_FURNACE (плавильня), HOPPER (воронка),
# DISPENSER (раздатчик), DROPPER (выбрасыватель), BREWING_STAND (варочная стойка), SHULKER_BOX (шалкер бокс)
supported-blocks:
  - BARREL
  - BLAST_FURNACE
  - BREWING_STAND
  - CHEST
  - DISPENSER
  - DROPPER
  - FURNACE
  - HOPPER
  - SHULKER_BOX
  - SMOKER
  - TRAPPED_CHEST
  - WAXED_COPPER_CHEST
  - WAXED_EXPOSED_COPPER_CHEST
  - WAXED_WEATHERED_COPPER_CHEST
  - WAXED_OXIDIZED_COPPER_CHEST

# Whether to ignore passable obstacles (such as tall grass, signs, fluids, etc.) when hovering over a block
# Следует ли игнорировать проходимые, но встречающиеся препятствия (например, высокую траву, знаки, жидкости, ..) при наведение курсора на блок
ignore-passable-blocks: true

# Maximum distance to the block / Максимальная дистанция до блока
max-distance: 5

# Enable supporting WorldGuard / Включить поддержку WorldGuard
# WorldGuard is a Minecraft plugin — https://github.com/EngineHub/WorldGuard
support-worldguard: true

# Item's name settings / Настройки имени блока
name-settings:
  # Enable support color name / Включить поддержку цветных имен
  support-color: true
  # Max name length / Максимальная длинна имени
  max-length: 25
  # Save the name when the block is destroyed / Сохранять имя при разрушении блока
  save-when-breaking: false
  # Forbid giving a name through an anvil / Запретить давать имя через наковальню
  cancel-anvil: false
  # Support for spaces in the name / Поддержка пробелов в имени
  support-spaces: true
  # Giving names through a name tag / Давать имена через бирку
  support-name-tag: true

# Command settings / Настройки команд
command-settings:
  # Enable command /nc set | Включить команду /nc set
  enable-set: true
  # Enable command /nc clear | Включить команду /nc clear
  enable-clear: true
  # Cooldown in using commands in seconds | Задержка на использование команд в секундах
  cooldown: 5

# The sound of a rejected action / Звук отклоненного действия
error-sound:
  enable: true
  # Key sound / Ключ звука
  sound: entity.villager.no
  # Volume / Громкость
  volume: 1.0
  # Tone / Тон
  pitch: 1.0

# The sound of success action / Звук успешного действия
success-sound:
  enable: true
  # Key sound / Ключ звука
  sound: entity.experience_orb.pickup
  # Volume / Громкость
  volume: 1.0
  # Tone / Тон
  pitch: 2.0

debug: false
```
</details>

## <a id="commands-permissions">[💻] Commands and permissions</a>
| Command               | Description                       | Permissions                  |
|-----------------------|-----------------------------------|------------------------------|
| `/namedchest`         | Command help                      |                              | 
| `/namedchest reload`  | Reload the plugin                 | `namedchest.reload`          |
| `/namedchest version` | Get the latest plugin version     | `namedchest.version.command` |
| `/namedchest set`     | Set a block name                  | `namedchest.name.set`        |
| `/namedchest clear`   | Clear a block name                | `namedchest.name.clear`      |

### Other permissions
| Permission                   | Description                                           |
|------------------------------|-------------------------------------------------------|
| `namedchest.*`               | All plugin features                                   |
| `namedchest.no_cooldown`     | Disables cooldown for plugin functions                |
| `namedchest.name.set.color`  | Allow setting colored names                           |
| `namedchest.name.max.length` | Bypass name length limit                              |
| `namedchest.ignore_regions`  | Ignore WorldGuard regions                             |
| `namedchest.version.notify`  | Notify about new versions and critical patches        |

## <a id="links">[🖇️] Links</a>
* Contact: [TG](https://t.me/ilezovofficial), [Email](mailto:ilezov.morozov@yandex.com)
* Telegram channel: [click](https://t.me/+o7693l_-Y8lmYzMy)
* Modrinth: [click](https://modrinth.com/plugin/namedchest)
* SpigotRU: [click](https://spigotmc.ru/resources/namedchest-gologrammnye-imena-pri-pomoschi-birok-dlja-vashix-xranilisch-1-18-2-1-21-x.3927/updates?ysclid=ms6l7m0y7b134784836)
* Hangar: soon

## <a id="donate">[❤️] Support the developer</a>
* DA: https://www.donationalerts.com/r/ilezov
* YooMoney: https://yoomoney.ru/to/4100118180919675
* Telegram Gift: https://t.me/ilezovofficial
* TON: UQBENFaqzv19GBR1EnDKad_nAOB--Ofsdk1FeRU7W3xprRLY
* BTC: bc1qn0f9yvcsacvedjk6qzu9cvagchkcz28gqrg2eh
* ETH: 0xbE7f260b952b87578079C0D6E03d3641425718c9
* Card: 2200700733487101

## <a>[🌋] Report a bug</a>
Found a bug, have a question, or want to give advice — https://github.com/ilezzov-code/NamedChest/issues/new

## <a>[🪪] License</a>
This project is distributed under the `GPL-3.0 License`. For more details, see the [LICENSE](LICENSE) file.