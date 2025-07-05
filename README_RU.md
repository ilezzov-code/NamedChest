<div align="center">
    <img src="img/logo/BannerRoundedRu.png" alt="">
    <h4>Поддерживаемые версии: Paper: 1.18.2 — 1.21.x</h4>
    <h1>Назови свой сундук уникальным именем</h1>
</div>

<div align="center" content="">
    <a href="https://modrinth.com/plugin/namedchest">
        <img src="img/social/modrinth.svg" width="">
    </a>
</div>

### <a href="https://github.com/ilezzov-code/NamedChest/tree/main"><img src="img/flags/en.svg" width=15px> Select English README.md</a>

##  <a>Оглавление</a>

- [Описание](#about)
- [Особенности](#features)
- [Config.yml](#config)
- [Команды](#commands)
- [Права](#permissions)
- [Ссылки](#links)
- [Поддержать разработчика](#donate)
- [Сообщить об ошибке](https://github.com/ilezzov-code/namedchest/issues)


## <a id="about">Описание</a>

**NamedChest** — это уникальный плагин, который позволяет давать имена разным блокам в Minecraft с помощью команд, бирок и наковален! Название будет отображаться при наведении курсора в виде голограммы

## <a id="features">Особенности</a>

* **[🔥] Отображение голограмм при наведении курсора** → [подробнее](#cursor-hover)
* **[🆕] Поддержка WorldGuard**
* Поддержка 2-х языков (Russian, English) + возможность создать собственный перевод
* Поддержка 11 блоков → [подробнее](#supporting-blocks)
* Настройка максимальной дистанции до блока
* Поддержка MiniMessage в именах → [подробнее](#minimessage-support)
* Давать имена при помощи бирки → [подробнее](#support-nametag)
* Настройка максимальной длинны имени
* Сохранение / Удаление имени при ломании блока
* Разрешить / Запретить имена через наковальню 
* Разрешить / Запретить поддержку пробелов
* Полная кастомизация:
    * Отключение / Включение каждой возможности
    * Задержка на использование каждой команды
    * Подробная система прав

## <a id="config">Config.yml</a>

<details>
    <summary>Посмотреть config.yml</summary>

```yaml
# ███╗░░██╗░█████╗░███╗░░░███╗███████╗██████╗░░░░█████╗░██╗░░██╗███████╗░██████╗████████╗
# ████╗░██║██╔══██╗████╗░████║██╔════╝██╔══██╗░░██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝
# ██╔██╗██║███████║██╔████╔██║█████╗░░██║░░██║░░██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░
# ██║╚████║██╔══██║██║╚██╔╝██║██╔══╝░░██║░░██║░░██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░
# ██║░╚███║██║░░██║██║░╚═╝░██║███████╗██████╔╝░░╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░
# ╚═╝░░╚══╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░░░░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░

# Developer / Разработчик: ILeZzoV

# Socials / Ссылки:
# • Contact with me / Связаться: https://t.me/ilezovofficial
# • Telegram Channel / Телеграм канал: RUS: https://t.me/ilezzov
# • GitHub: https://github.com/ilezzov-code

# By me coffee / Поддержать разработчика:
# • DA: https://www.donationalerts.com/r/ilezzov_dev
# • YooMoney: https://yoomoney.ru/to/4100118180919675
# • Telegram Gift: https://t.me/ilezovofficial
# • TON: UQCInXoHOJAlMpZ-8GIHqv1k0dg2E4pglKAIxOf3ia5xHmKV
# • BTC: 1KCM1QN9TNYRevvQD63UF81oBRSK67vCon
# • Card: 5536914188326494

# Supporting messages languages / Доступные языки сообщений:
# ru-RU, en-US
language: "en-US"

# Check the plugin for updates
# Проверять плагин на наличие обновлений
check_updates: true

# Enable / Disable the option to name a block | Включить / Отключить возможность давать имя блоку
# Supporting blocks / Поддерживаемые блоки: CHEST (сундук), TRAPPED_CHEST (сундук-ловушка),
# BARREL (бочка), FURNACE (печка), SMOKER (коптильня), BLAST_FURNACE (плавильня), HOPPER (воронка),
# DISPENSER (раздатчик), DROPPER (выбрасыватель), BREWING_STAND (варочная стойка), SHULKER_BOX (шалкер бокс)
supporting_block:
  - CHEST
  - TRAPPED_CHEST
  - BARREL
  - FURNACE
  - SMOKER
  - BLAST_FURNACE
  - HOPPER
  - DISPENSER
  - DROPPER
  - BREWING_STAND
  - SHULKER_BOX

# Maximum distance to the block / Максимальная дистанция до блока
max_distance: 5

# Item's name settings / Настройки имени блока
name_settings:
  # Enable support color name / Включить поддержку цветных имен
  support_color: true
  # Max name length / Максимальная длинна имени
  max_name_length: 25
  # Save the name when the block is destroyed / Сохранять имя при разрушении блока
  save_for_breaking: false
  # Forbid giving a name through an anvil / Запретить давать имя через наковальню
  cancel_anvil: true
  # Support for spaces in the name / Поддержка пробелов в имени
  support_spaces: true

# Enable / Disable plugin commands
# Включить / Выключить команды плагина
command_settings:
  enable_set: true # Enable command /nc set | Включить команду /nc set
  enable_clear: true # Enable command /nc clear | Включить команду /nc clear
  cooldown: 5 # Cooldown in using commands in seconds | Задержка на использование команд в секундах

# Don't edit this / Не редактируйте это
config_version: 1.2
```

</details>

## <a id="commands">Команды (/команда → /псевдоним1, /псевдоним2, ... ※ `право`)</a>

### /namedchest reload → /nc reload ※ `namedchest.reload`

* Перезагрузить конфигурацию плагина

### /namedchest version → /nc version ※ ``

* Узнать последнюю версию плагинв

### /namedchest set → /nc set ※ `namedchest.name.set`

* Установить имя блоку

### /namedchest clear → /nc clear ※ `namedchest.name.clear`

* Очистить имя блока

## <a id="permissions">Все права плагина</a>

| Право                      | Описание                                               |
|----------------------------|--------------------------------------------------------|
| namedchest.*               | Доступ ко всем возможностям плагина                    |
| namedchest.reload          | Доступ к перезагрузке плагина /cl reload               |
| namedchest.no_cooldown     | Отключает задержку на использование какой-либо функции |
| namedchest.name.set        | Доступ к команде /nc set                               |
| namedchest.name.set.color  | Разрешить устанавливать цветные имена                  |
| namedchest.name.clear      | Доступ к команде /nc clear                             |
| namedchest.name.max.length | Убрать ограничение на длину имени                      |
| namedchest.ignore_regions  | Устанавливать имена в чужих регионах WorldGuard        |

## <a id="cursor-hover">Отображение голограмм при наведении курсора 🔥</a>

Это уникальная фишка плагина, которая делает его производительным. Названия отображаются только при наведении курсора — это позволяет спавнить голограммы только в нужный момент, что значительно сокращает количество Entity и снижает нагрузку.

Названия появляются рядом с сундуков с той стороны, с которой стоит игрок

<img src="img/gifs/cursor-hover.gif" width="400">

## <a id="supporting-blocks">Поддерживаемые блоки</a>

Плагин поддерживает 11 блоков: 

- CHEST (сундук)
- TRAPPED_CHEST (сундук-ловушка)
- BARREL (бочка)
- FURNACE (печка)
- SMOKER (коптильня)
- BLAST_FURNACE (плавильня)
- HOPPER (воронка)
- DISPENSER (раздатчик)
- DROPPER (выбрасыватель)
- BREWING_STAND (варочная стойка)
- SHULKER_BOX (шалкер)

<img src="img/gifs/supporting-blocks.gif" width="400">

Вы можете настроить убрать лишние блоки в конфигурации плагина. Раздел `supporting_block`

<details>
  <summary>config.yml → supporting_blocks</summary>

```yml
supporting_block:
  - CHEST
  - TRAPPED_CHEST
  - BARREL
  - FURNACE
  - SMOKER
  - BLAST_FURNACE
  - HOPPER
  - DISPENSER
  - DROPPER
  - BREWING_STAND
  - SHULKER_BOX   
 ```
</details>

## <a id="minimessage-support">Поддержка MiniMessage</a>

Плагин поддерживает любые виды форматирования текста в Minecraft

- **LEGACY** — Цвет через & / § и HEX через &#rrggbb / §#rrggbb или &x&r&r&g&g&b&b / §x§r§r§g§g§b§b
- **LEGACY_ADVANCED** — Цвет и HEX через &##rrggbb / §##rrggbb
- **MINI_MESSAGE** — Цвет через <цвет> Подробнее — https://docs.advntr.dev/minimessage/index.html

И все форматы доступные на https://www.birdflop.com/resources/rgb/
Вы можете использовать все форматы одновременно в одном сообщении.

## <a id="support-nametag">Давать имена через бирки</a>

Вы можете давать имена блокам при помощи бирок

<img src="img/gifs/support-nametag.gif" width="400">

## <a id="links">Ссылки</a>

* Связаться: https://t.me/ilezovofficial
* Telegram Channel: https://t.me/ilezzov
* Modrinth: https://modrinth.com/plugin/namedchest

## <a id="donate">Поддержать разработчика</a>

* DA: https://www.donationalerts.com/r/ilezov
* YooMoney: https://yoomoney.ru/to/4100118180919675
* Telegram Gift: https://t.me/ilezovofficial
* TON: UQCInXoHOJAlMpZ-8GIHqv1k0dg2E4pglKAIxOf3ia5xHmKV
* BTC: 1KCM1QN9TNYRevvQD63UF81oBRSK67vCon
* Card: 5536914188326494

## Found an issue or have a question? Create a new issue — https://github.com/ilezzov-code/NamedChest/issues/new

## <a id="license">Лицензия</a>

Этот проект распространяется под лицензией `GPL-3.0 License`. Подробнее см. в файле [LICENSE](LICENSE).
