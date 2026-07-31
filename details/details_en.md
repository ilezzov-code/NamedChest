## <a id="hologram-names">**Display names as holograms on hover**</a>

This is the unique and main feature of the plugin. Names are displayed only when hovered over — this allows creating holograms only when needed, which significantly reduces the amount of `Entity` objects and lowers the server load.

The names appear next to the block on the side where the player is standing.

<img src="..\img\gifs\cursor-hover.gif" width="300"/>

You can also install **[DecentHolograms](https://github.com/DecentSoftware-eu/DecentHolograms)** on your server, then the plugin will create holograms using its more performant API.

## <a id="worldguard">WorldGuard support</a>

If you have **[WorldGuard](https://github.com/EngineHub/WorldGuard)** installed on your server, you can enable support for it in the plugin. To do this, change the `support-worldguard` parameter in `config.yml` to `true`.

```yaml
support-worldguard: true
```

After this, players will not be able to set names in regions they do not own.

<img src="..\img\gifs\worldguard-example.gif" width="300"/>

## <a id="dynamic-position">Dynamic hologram repositioning based on player movement</a>

If a player looks at a block and simultaneously moves around it, the hologram will follow their movement, trying to always stay in the field of view.

<img src="..\img\gifs\dynamic-hologram-movement.gif" width="300"/>

## <a id="renaming-methods">Renaming using name tags, commands, and anvils</a>

The plugin supports several methods for setting block names:
1) Using the `/namedchest set` and `/namedchest clear` commands

<img src="..\img\gifs\set-name-with-command.gif" width="300"/>

2) Using name tags

<img src="..\img\gifs\set-name-with-name-tag.gif" width="300"/>

3) Using an anvil

<img src="..\img\gifs\set-name-with-anvil.gif" width="300"/>

You can enable / disable each method in `config.yml` by changing the following parameters:

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

## <a id="supported-blocks">Support for 15 blocks, ignoring vegetation and fluids</a>

The plugin supports 15 blocks. These blocks include:
* CHEST (chest)
* TRAPPED_CHEST (trapped chest)
* BARREL (barrel)
* FURNACE (furnace)
* SMOKER (smoker)
* BLAST_FURNACE (blast furnace)
* HOPPER (hopper)
* DISPENSER (dispenser)
* DROPPER (dropper)
* BREWING_STAND (brewing stand)
* SHULKER_BOX (shulker box)

and all types of copper chests:
* WAXED_COPPER_CHEST
* WAXED_EXPOSED_COPPER_CHEST
* WAXED_WEATHERED_COPPER_CHEST
* WAXED_OXIDIZED_COPPER_CHEST

<img src="..\img\gifs\supported-blocks.gif" width="300"/>

You can also ignore vegetation, water, and other passable blocks on hover. To do this, change the `ignore-passable-blocks` parameter in `config.yml` to `true`.

```yaml
ignore-passable-blocks: true
```

## <a id="color-format">Any Minecraft color code format</a>

The plugin supports all types of text formatting in Minecraft.

* **LEGACY** — Color via & / § and HEX via &#rrggbb / §#rrggbb or &x&r&r&g&g&b&b / §x§r§r§g§g§b§b
* **LEGACY_ADVANCED** — Color and HEX via &##rrggbb / §##rrggbb
* **MINI_MESSAGE** — Color via <color>. Read more — https://docs.advntr.dev/minimessage/index.html
* And all formats available at https://www.birdflop.com/resources/rgb/

You can use all formats simultaneously when setting a name.

<img src="..\img\gifs\color-format.gif" width="300"/>