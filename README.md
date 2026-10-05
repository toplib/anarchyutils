# Anarchy Utils

Paper plugin (1.21+, Java 21) with utilities for anarchy servers.

# Commands:
* ##### /anarhy_utils, /au, /autils - Root command
* ##### /au give <item> [player] [amount] - give a custom item
* ##### /au reload - reload config, items and modules
* ##### /au info - plugin version

# Build
* Maven: `mvn package` (JDK 21)
* Gradle: `gradle build` — `build/libs/AnarchyUtils-<version>.jar`

# Modules (toggle in config.yml)

1. **Items** — create custom items in items.yml: DisplayName, Material, Glow, Lore, CustomModelData, Enchantments, per-item cooldown_seconds, consume flag, onUse / onConsume action chains. Items are NBT-tagged so they can't be faked by renaming.
2. **chat_filter** — censor or cancel messages matching blocked words/regex.
3. **combat_log** — kill players who log out while combat-tagged; optional command blocking while tagged.
4. **world_limits** — square world border enforcement.
5. **dupe_detect** — alerts on abnormally fast item drops.
6. **anti_cheat** — basic fly detection; fires `AntiCheatViolationEvent` for other plugins.

# Action types (items.yml onUse / onConsume)

`[MESSAGE]`, `[ACTIONBAR]`, `[TITLE]`, `[SOUND]`, `[PLAYER_COMMAND]`,
`[SERVER_COMMAND]`/`[CONSOLE]`, `[BROADCAST]`, `[TELEPORT]`, `[POTION]`,
`[GIVEITEM]`, `[PLACESCHEM]`, `[WAIT]`, `[CLOSE]`, plus conditional
`[IF]` / `[ELSE]` / `[ENDIF]` blocks. See the header of items.yml for syntax.

# Schematics

`[PLACESCHEM] name;seconds` pastes `plugins/AnarchyUtils/schematics/name.yml`
(a simple `blocks:` list of `dx,dy,dz,MATERIAL`) and reverts it after the delay.

# PlaceholderAPI

Soft-depend. Provides `%anarchyutils_version%`,
`%anarchyutils_cooldown_<item>%`, `%anarchyutils_combat_tagged%`,
`%anarchyutils_combat_remaining%`.

# API for other plugins

`my.toplib.anarchyutils.api.AnarchyUtilsAPI` — item lookup/give, cooldowns,
running action chains. Events: `CustomItemUseEvent`, `AntiCheatViolationEvent`.

# Description:

_This plugin was created to cover the need for implementing anarchy servers.
Here modules will be implemented that can be configured if necessary._
