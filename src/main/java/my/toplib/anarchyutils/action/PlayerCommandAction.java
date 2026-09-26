package my.toplib.anarchyutils.action;

import org.bukkit.entity.Player;

/**
 * Runs a command AS the player (with permissions of the player).
 * A leading '/' is optional in configs.
 */
public class PlayerCommandAction implements Action {

    private final String command;

    public PlayerCommandAction(String command) {
        this.command = command.startsWith("/") ? command.substring(1) : command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        player.performCommand(PlaceholderAPI.apply(player, command));
    }
}
