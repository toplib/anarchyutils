package my.toplib.anarchyutils.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerAction implements Action {

    private final String command;

    public PlayerAction(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Bukkit.getServer().dispatchCommand(player, PlaceholderAPI.apply(player, command));
    }
}
