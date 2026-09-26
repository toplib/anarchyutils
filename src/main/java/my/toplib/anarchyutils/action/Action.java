package my.toplib.anarchyutils.action;

import org.bukkit.entity.Player;

public interface Action {

    /**
     * Executes this action for the given player.
     * Actions that need a player context (message, sound, command)
     * should check {@code player == null} or rely on setPlayer().
     */
    void execute(Player player);
}
