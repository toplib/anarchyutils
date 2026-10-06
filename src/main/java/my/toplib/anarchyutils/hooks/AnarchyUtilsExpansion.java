package my.toplib.anarchyutils.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.modules.CombatLogModule;
import my.toplib.anarchyutils.utils.Cooldowns;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI expansion. Registered only when PlaceholderAPI is present.
 *
 * Placeholders:
 *   %anarchyutils_version%                    - plugin version
 *   %anarchyutils_cooldown_<item>%            - remaining cooldown seconds (0 if ready)
 *   %anarchyutils_combat_tagged%              - true/false
 *   %anarchyutils_combat_remaining%           - remaining combat tag seconds
 */
public class AnarchyUtilsExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "anarchyutils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TOPLIB";
    }

    @Override
    public @NotNull String getVersion() {
        return AnarchyUtils.instance.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String key = params.toLowerCase();
        if (key.equals("version")) {
            return getVersion();
        }
        if (key.equals("combat_tagged")) {
            return String.valueOf(player != null && CombatLogModule.isTagged(player.getUniqueId()));
        }
        if (key.equals("combat_remaining")) {
            return String.valueOf(player == null ? 0 : (int) CombatLogModule.remainingSeconds(player.getUniqueId()));
        }
        if (key.startsWith("cooldown_")) {
            if (player == null) return "0";
            String itemId = key.substring("cooldown_".length());
            return String.valueOf((int) Cooldowns.remainingSeconds(player.getUniqueId(), itemId));
        }
        return null;
    }
}
