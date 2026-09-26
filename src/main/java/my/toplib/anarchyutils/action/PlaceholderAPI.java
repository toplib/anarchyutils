package my.toplib.anarchyutils.action;

import org.bukkit.entity.Player;

/**
 * Simple placeholder engine for action strings.
 * Supported placeholders:
 *   %player% - player name
 *   %world%  - player world name
 *   %x% %y% %z% - player coordinates (rounded)
 *   %health% - player health
 *   %item%   - amount of the item in main hand (if any)
 */
public final class PlaceholderAPI {

    private PlaceholderAPI() {}

    public static String apply(Player player, String text) {
        if (player == null || text == null) return text;
        text = text.replace("%player%", player.getName());
        text = text.replace("%world%", player.getWorld().getName());
        text = text.replace("%x%", String.valueOf(player.getLocation().getBlockX()));
        text = text.replace("%y%", String.valueOf(player.getLocation().getBlockY()));
        text = text.replace("%z%", String.valueOf(player.getLocation().getBlockZ()));
        text = text.replace("%health%", String.valueOf((int) player.getHealth()));
        return text;
    }
}
