package my.toplib.anarchyutils.api;

import my.toplib.anarchyutils.ItemManager;
import my.toplib.anarchyutils.action.Action;
import my.toplib.anarchyutils.action.ActionManager;
import my.toplib.anarchyutils.utils.Cooldowns;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Public entry points for other plugins integrating with AnarchyUtils.
 */
public final class AnarchyUtilsAPI {

    private AnarchyUtilsAPI() {}

    /** @return true if the stack is one of the configured custom items */
    public static boolean isCustomItem(ItemStack stack) {
        return ItemManager.getItemId(stack) != null;
    }

    /** @return the custom-item id from items.yml, or null */
    public static String getCustomItemId(ItemStack stack) {
        return ItemManager.getItemId(stack);
    }

    /** Gives the player {@code amount} of the custom item {@code id}. */
    public static boolean giveItem(Player player, String id, int amount) {
        return "success".equals(ItemManager.giveItemToPlayer(player, id.toLowerCase(), amount));
    }

    /** @return remaining cooldown seconds for the player on the item, 0 if usable */
    public static double cooldownRemaining(Player player, String itemId) {
        return Cooldowns.remainingSeconds(player.getUniqueId(), itemId.toLowerCase());
    }

    /** Runs the item's onUse action chain for the player. */
    public static void runItemActions(Player player, String itemId) {
        List<Action> actions = ItemManager.getActions(itemId);
        if (!actions.isEmpty()) ActionManager.run(player, actions);
    }

    /** Parses and runs raw action lines ("[MESSAGE] hi", ...) for the player. */
    public static void runActions(Player player, List<String> actionLines) {
        ActionManager.run(player, ActionManager.parse(actionLines));
    }
}
