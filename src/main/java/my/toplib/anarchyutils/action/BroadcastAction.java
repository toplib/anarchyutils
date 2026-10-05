package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Broadcasts a message to the whole server.
 * Config format: [BROADCAST] some message
 */
public class BroadcastAction implements Action {

    private final String message;

    public BroadcastAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(Player player) {
        Bukkit.getServer().sendMessage(Utils.component(PlaceholderAPI.apply(player, message)));
    }
}
