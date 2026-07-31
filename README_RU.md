<div align="center">
    <img src="img/logo/BannerRoundedRu.png" alt="">
    <h4>Поддерживаемые версии: Paper: 1.18.2 — 1.21.x</h4>
    <h1>Назови свой сундук уникальным именем</h1>
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

### <a href="https://github.com/ilezzov-code/NamedChest/tree/main"><img src="img/flags/en.svg" width=15px> Select English README.md</a>

## <a>[📋] Оглавление</a>

- [Описание](#about)
- [Особенности](#features)
- [Требования и зависимости](#requirements)
- [Конфигурация ](#config)
- [Команды и права](#commands-permissions)
- [Ссылки](#links)
- [Поддержать разработчика](#donate)
- [Сообщить об ошибке](https://github.com/ilezzov-code/NamedChest/issues/new)
- [Лицензия](LICENSE)


## <a id="about">[🐲] Описание</a>

**NamedChest** — это уникальный плагин, который позволяет давать имена разным блокам в Minecraft с помощью команд, бирок и наковален! Название будет отображаться при наведении курсора в виде голограммы

## <a id="features">[⭐] Особенности</a>
* **[🔥] Отображение имен в виде голограмм при наведении курсора** → [подробнее](details/details_ru.md#hologram-names)
* [🆕] Поддержка WorldGuard → [подробнее](details/details_ru.md#worldguard)
* [🆕] Поддержка DecentHologram
* [🆕] Изменение положения голограммы при перемещении игрока → [подробнее](details/details_ru.md#dynamic-position)
* Переименования при помощи бирок, команд и наковальни → [подробнее](details/details_ru.md#renaming-methods)
* Поддержка 15 блоков, игнорирование растительности и жидкостей → [подробнее](details/details_ru.md#supported-blocks)
* Любой формат цветовых кодов Minecraft → [подробнее](details/details_ru.md#color-format)
* Ограничение длины имени
* Настройка дистанции поиска блока
* Включение / Отключение следующих функций:
  * Сохранение имен при разрушении
  * Запрет на использование наковальни
  * Поддержка пробелов
  * Поддержка бирок
  * Команд `set` и `clear`
* Настройка задержки на использование команд и бирок
* Поддержка 2-х языков (English, Русский) + возможность добавить свой перевод
* Контроль версии и безопасность

## <a id="requirements">[⚙️] Требования и зависимости</a>
* **Ядро сервера:** Paper-1.18.2 (Purpur, Leaf и другие форки) 
* **Java:** 17 и выше
* **Зависимости**:
  * [WorldGuard (опционально)](https://github.com/EngineHub/WorldGuard) 
  * [DecentHolograms (опционально)](https://github.com/DecentSoftware-eu/DecentHolograms)  

## <a id="config">[🐍] Конфигурация (config.yml)</a>
<details>
    <summary>Посмотреть config.yml</summary>

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

## <a id="commands-permissions">[💻] Команды и права</a>
| Команда               | Описание                          | Права (Permissions)          |
|-----------------------|-----------------------------------|------------------------------|
| `/namedchest`         | Помощь по командам                |                              | 
| `/namedchest reload`  | Перезагрузить плагин              | `namedchest.reload`          |
| `/namedchest version` | Получить последнюю версию плагина | `namedchest.version.command` |
| `/namedchest set`     | Установить имя блоку              | `namedchest.name.set`        |
| `/namedchest clear`   | Очистить имя блоку                | `namedchest.name.clear`      |

### Остальные права
| Право                        | Описание                                              |
|------------------------------|-------------------------------------------------------|
| `namedchest.*`               | Все возможности плагина                               |
| `namedchest.no_cooldown`     | Отключает задержку на использование функций плагина   |
| `namedchest.name.set.color`  | Разрешить давать цветные имена                        |
| `namedchest.name.max.length` | Убрать ограничение на длину имени                     |
| `namedchest.ignore_regions`  | Игнорировать регионы WorldGuard                       |
| `namedchest.version.notify`  | Уведомлять о выходе новой версии и критического патча |

## <a id="links">[🖇️] Ссылки</a>
* Связаться [TG](https://t.me/ilezovofficial), [Email](mailto:ilezov.morozov@yandex.com)
* Телеграм канал: [клик](https://t.me/+o7693l_-Y8lmYzMy)
* Modrinth: [клик](https://modrinth.com/plugin/namedchest)
* SpigotRU: [клик](https://spigotmc.ru/resources/namedchest-gologrammnye-imena-pri-pomoschi-birok-dlja-vashix-xranilisch-1-18-2-1-21-x.3927/updates?ysclid=ms6l7m0y7b134784836)
* Hangar: soon

## <a id="donate">[❤️] Поддержать разработчика</a>
* DA: https://www.donationalerts.com/r/ilezov  
* YooMoney: https://yoomoney.ru/to/4100118180919675
* Telegram Gift: https://t.me/ilezovofficial
* TON: UQBENFaqzv19GBR1EnDKad_nAOB--Ofsdk1FeRU7W3xprRLY
* BTC: bc1qn0f9yvcsacvedjk6qzu9cvagchkcz28gqrg2eh
* ETH: 0xbE7f260b952b87578079C0D6E03d3641425718c9
* Card: 2200700733487101

## <a>[🌋] Сообщить об ошибке</a>
Нашли ошибку, есть вопрос или хотите дать совет — https://github.com/ilezzov-code/NamedChest/issues/new

## <a>[🪪] Лицензия</a>
Этот проект распространяется под лицензией `GPL-3.0 License`. Подробнее см. в файле [LICENSE](LICENSE).