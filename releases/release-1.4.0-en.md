# NamedChest-1.4.0 (Paper 1.18.2 — 26.2.x)

## [⭐] New Features and Improvements

* **[DecentHolograms](https://github.com/DecentSoftware-eu/DecentHolograms) Integration**: Added full support for the DecentHolograms API.
* **Visibility Settings**: Added the `ignore-passable-blocks` parameter to `config.yml`. Storage names and holograms will now be displayed through passable blocks (e.g., tall grass and fluids).
* **Audio Feedback**: Introduced an audio response system for player actions. Added `error-sound` and `success-sound` parameters to `config.yml` to configure sounds for successful and failed actions.
* **Minecraft 1.21.9 Blocks**: The default configuration has been updated and now includes all the new blocks introduced in version 1.21.9.
* **Advanced Version Control and Security**: Completely overhauled the update checker system. Server administrators will now receive notifications when critical patches are released. Also added an emergency shutdown mechanism (kill-switch), allowing the plugin to automatically deactivate if a critical bug or vulnerability is detected by the developer.

## [🛠] Fixes and Adjustments

* **Cooldown Unification**: Reworked the cooldown logic. Cooldowns are now global and synchronized across the usage of commands, name tags, and anvils.
* **Anvil Fixes**: Resolved issues related to applying colored names via the anvil, and added permission checks as well as cooldowns.

## [⚙️] Technical Changes

* **Global Refactoring:** The project has been completely rewritten from scratch. The new codebase is significantly cleaner, more transparent, and easier to maintain.
* **Migration to Okaeri Configs:** The plugin now uses the [Okaeri Configs](https://github.com/OkaeriPoland/okaeri-configs/) library for configuration management, ensuring a more robust structure and data serialization.