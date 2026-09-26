package my.toplib.anarchyutils.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Runs a command from the console. Alias of ConsoleAction,
 * kept for clearer config readability: [SERVER_COMMAND] ...
 */
public class ServerCommandAction implements Action {

    private final String command;

    public ServerCommandAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                PlaceholderAPI.apply(player, command));
    }
}
