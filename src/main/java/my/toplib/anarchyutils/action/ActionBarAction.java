package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Sends a message to the player's action bar (Adventure API).
 */
public class ActionBarAction implements Action {

    private final String message;

    public ActionBarAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        player.sendActionBar(Utils.component(PlaceholderAPI.apply(player, message)));
    }
}
