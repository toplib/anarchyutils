package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Sends a message to the player's action bar via the minecraft:title protocol.
 * Falls back silently on servers without ProtocolSupport (1.16.5 Paper API
 * has no native action bar).
 */
public class ActionBarAction implements Action {

    private final String message;

    public ActionBarAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        String colored = Utils.color(PlaceholderAPI.apply(player, message));
        try {
            java.lang.reflect.Method m = player.getClass().getMethod("sendActionBar", String.class);
            m.invoke(player, colored);
        } catch (NoSuchMethodException e) {
            // Older API: fall back to title with empty main line
            player.sendTitle("", colored, 5, 40, 10);
        } catch (Exception e) {
            AnarchyUtils.instance.getLogger().warning("ActionBarAction failed: " + e.getMessage());
        }
    }
}
