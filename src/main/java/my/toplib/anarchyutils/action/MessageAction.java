package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.entity.Player;

public class MessageAction implements Action {

    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        player.sendMessage(Utils.color(PlaceholderAPI.apply(player, message)));
    }
}
