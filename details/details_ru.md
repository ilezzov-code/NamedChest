## <a id="hologram-names">**Отображение имен в виде голограмм при наведении курсора**</a>

Это уникальная и главная фишка плагина. Названия отображаются только при наведении курсора — это позволяет создавать голограммы только в нужный момент, что значительно сокращает количество `Entity` и снижает нагрузку на сервер.

Названия появляются рядом с блоком с той стороны, с которой стоит игрок.

<img src="..\img\gifs\cursor-hover.gif" width="300"/>

Также вы можете установить **[DecentHolograms](https://github.com/DecentSoftware-eu/DecentHolograms)** на ваш сервер, тогда плагин будет создавать голограммы, используя его более производительный API

## <a id="worldguard">Поддержка WorldGuard</a>

Если у вас установлен **[WorldGuard](https://github.com/EngineHub/WorldGuard)** на сервере, Вы можете включить его поддержку в плагине. Для этого в `config.yml` измените параметр `support-worldguard` на `true`

```yaml
support-worldguard: true
```

После этого игроки не смогут устанавливать имена в чужих регионах

<img src="..\img\gifs\worldguard-example.gif" width="300"/>

## <a id="dynamic-position">Изменение положения голограммы при перемещении игрока</a>

Если игрок смотрит на блок и одновременно перемещается вокруг него, голограмма будет следовать за его движением, стараясь всегда оставаться в поле зрения

<img src="..\img\gifs\dynamic-hologram-movement.gif" width="300"/>

## <a id="renaming-methods">Переименования при помощи бирок, команд и наковальни</a>

Плагин поддерживает несколько способов установки имен блокам:
1) С помощью команды `/namedchest set` и `/namedchest clear`

<img src="..\img\gifs\set-name-with-command.gif" width="300"/>

2) При помощи бирок

<img src="..\img\gifs\set-name-with-name-tag.gif" width="300"/>

3) При помощи наковальни

<img src="..\img\gifs\set-name-with-anvil.gif" width="300"/>

Включить / Отключить тот или иной способ можно в `config.yml`, изменяя следующие параметры:

```yaml
# Forbid giving a name through an anvil / Запретить давать имя через наковальню
cancel-anvil: true
# Giving names through a name tag / Давать имена через бирку
support-name-tag: true
# Enable command /nc set | Включить команду /nc set
enable-set: true
# Enable command /nc clear | Включить команду /nc clear
enable-clear: true
```

## <a id="supported-blocks">Поддержка 15 блоков, игнорирование растительности и жидкостей</a>

Плагин поддерживает 15 блоков. К этим блокам относятся: 
* CHEST (сундук)
* TRAPPED_CHEST (сундук-ловушка)
* BARREL (бочка)
* FURNACE (печка)
* SMOKER (коптильня)
* BLAST_FURNACE (плавильня)
* HOPPER (воронка)
* DISPENSER (раздатчик)
* DROPPER (выбрасыватель)
* BREWING_STAND (варочная стойка)
* SHULKER_BOX (шалкер)

и все виды медных сундуков:
* WAXED_COPPER_CHEST
* WAXED_EXPOSED_COPPER_CHEST
* WAXED_WEATHERED_COPPER_CHEST
* WAXED_OXIDIZED_COPPER_CHEST

<img src="..\img\gifs\supported-blocks.gif" width="300"/>

Также вы можете игнорировать растительность, воду и другие проходимые блоки при наведении курсора. Для этого в `config.yml` измените параметр `ignore-passable-blocks` на `true`

```yaml
ignore-passable-blocks: true
```

## <a id="color-format">Любой формат цветовых кодов Minecraft</a>

Плагин поддерживает любые виды форматирования текста в Minecraft

* **LEGACY** — Цвет через & / § и HEX через &#rrggbb / §#rrggbb или &x&r&r&g&g&b&b / §x§r§r§g§g§b§b
* **LEGACY_ADVANCED** — Цвет и HEX через &##rrggbb / §##rrggbb
* **MINI_MESSAGE** — Цвет через <цвет> Подробнее — https://docs.advntr.dev/minimessage/index.html
* И все форматы доступные на https://www.birdflop.com/resources/rgb/ 

Вы можете использовать все форматы одновременно при установке имени

<img src="..\img\gifs\color-format.gif" width="300"/>