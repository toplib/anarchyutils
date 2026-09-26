package my.toplib.anarchyutils.action;

import org.bukkit.entity.Player;

/**
 * Closes the player's current inventory/screen.
 */
public class CloseInventoryAction implements Action {

    @Override
    public void execute(Player player) {
        if (player == null) return;
        player.closeInventory();
    }
}
