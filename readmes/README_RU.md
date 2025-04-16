<div align="center">
    <img src="../img/logo/namedchest.png"> 
</div>

<div align="center">
    <h1>Дайте название вашему сундуку</h1>
    <p>Поддерживаемые версии: Paper 1.18.2 — 1.21.x</p>
</div>

### <img src="https://raw.githubusercontent.com/ilezzov-code/CoolLobby/refs/heads/main/img/flags/en.svg" width="15"> [Select English Version](readmes/README_RU.md)

## Страницы плагина:
* **Modrinth** — soon
* **Hangar** — soon
* **Spigot** — soon
* **Spigot RU** — soon

## Описание: 
NamedChest — плагин, который позволит вам дать название разным блокам. Теперь Вы легко сможете различать ваши сундуки без использования рамок

## Возможности: 
* Установить имя блоку через бирку
* Поддержка 2-х языков
* Поддержка HEX и MiniMessage
* Включить / Отключить поддержку цветных имен
* Установить максимальную длину имени
* Поддерживаемые блоки: CHEST (сундук), TRAPPED_CHEST (сундук-ловушка),
BARREL (бочка), FURNACE (печка), SMOKER (коптильня), BLAST_FURNACE (плавильня), HOPPER (воронка),
DISPENSER (раздатчик), DROPPER (выбрасыватель), BREWING_STAND (варочная стойка), SHULKER_BOX (шалкер бокс)
* Система прав
* Установить / Очистить имя с помощью команды `/nc`

#### -=-=-= REALESE 1.1 =-=-=-
* Сохранять или нет имя при разрушении блока
* Запретить давать имена через наковальню

## Команды

### /nc set <имя>
* Установить имя
* Право: `namedchest.name.set`

### /nc clear
* Очистить имя
* Право: `namedchest.name.clear`

## Все права плагина

| Право                      | Описание                                     |
|----------------------------|----------------------------------------------|
| namedchest.*               | Дать доступ ко всем возможностям             |
| namedchest.reload          | Дать доступ к `/nc reload`                   |
| namedchest.name.set        | Разрешить устанавливать имя                  |
| namedchest.name.set.color  | Разрешить устанавливать цветное имя          |
| namedchest.name.clear      | Разрешить очищать имя `/nc clear`            |
| namedchest.name.max.lenght | Разрешить превышать максимальную длину имени |

## Поддерживаемые языки
* ru-RU → Русский (Россия)
* en-US → Английский (США)

## Конфиг файл: `config.yml`

<details>
  <summary>config.yml</summary>

  ```yml
  # ███╗░░██╗░█████╗░███╗░░░███╗███████╗██████╗░░░░█████╗░██╗░░██╗███████╗░██████╗████████╗
  # ████╗░██║██╔══██╗████╗░████║██╔════╝██╔══██╗░░██╔══██╗██║░░██║██╔════╝██╔════╝╚══██╔══╝
  # ██╔██╗██║███████║██╔████╔██║█████╗░░██║░░██║░░██║░░╚═╝███████║█████╗░░╚█████╗░░░░██║░░░
  # ██║╚████║██╔══██║██║╚██╔╝██║██╔══╝░░██║░░██║░░██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░░░██║░░░
  # ██║░╚███║██║░░██║██║░╚═╝░██║███████╗██████╔╝░░╚█████╔╝██║░░██║███████╗██████╔╝░░░██║░░░
  # ╚═╝░░╚══╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░░░░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░░░░╚═╝░░░

  # Developer / Разработчик: ILeZzoV

  # Socials / Ссылки:
  # • Contact with me / Связаться: https://t.me/ilezovofficial
  # • Telegram Channel / Телеграм канал:
  #    | RUS: https://t.me/ilezzov
  #    | EN: https://t.me/ilezzov_en
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
  language: "ru-RU"

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

  # Maximum distance to the block / Максимальная дистанция до нужного блока
  block_max_distant: 5

  # Item's name settings / Настроить название предмета:
  name_settings:
    # Enable support color name / Включить поддержку цветных имен
    support_color: true
    # Max name length / Максимальная длинна имени
    max_name_length: 25
    

  # Don't edit this / Не редактируйте это
  config_version: 1.0
  ```
</details>

## Скриншоты: 

### Пример имени
<img src="../img/screenshots/name_example.png" width="200">

### Появление имени при наведении 
<img src="../img/screenshots/show_name.gif" width="200">

### Установка имени биркой
<img src="../img/screenshots/set_name.gif" width="200">

### Очистка имени командой
<img src="../img/screenshots/clear_name.gif" width="200">

## Ссылки:
* Связаться: https://t.me/ilezovofficial
* Телеграмм канал: https://t.me/ilezzov
* GitHub: https://github.com/ilezzov-code

## Поддержать разработчика:
* DA: https://www.donationalerts.com/r/ilezzov_dev
* YooMoney: https://yoomoney.ru/to/4100118180919675
* Telegram Gift: https://t.me/ilezovofficial
* TON: UQCInXoHOJAlMpZ-8GIHqv1k0dg2E4pglKAIxOf3ia5xHmKV
* BTC: 1KCM1QN9TNYRevvQD63UF81oBRSK67vCon
* Card: 5536914188326494

## Возникла ошибка или вопрос? Создайте новую тему — https://github.com/ilezzov-code/NamedChest/issues/new



