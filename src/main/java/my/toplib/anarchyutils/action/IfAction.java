package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Conditional gate for action chains. Evaluated by ActionManager:
 * if the condition is false, all actions until the matching [ELSE] or [ENDIF]
 * are skipped.
 *
 * Config format: [IF] [!]<condition>
 * Supported conditions (prefix with ! to negate):
 *   permission:<node>          - player has permission
 *   item:<MATERIAL>            - player has at least one of the material
 *   customitem:<id>            - player has the custom item (items.yml id)
 *   world:<name>               - player is in world <name>
 *   health:>|<|>=|<=|= <num>   - health comparison, e.g. health:>10
 *   sneaking                   - player is sneaking
 *   op                         - player is a server operator
 */
public class IfAction implements Action {

    private final String condition;

    public IfAction(String condition) {
        this.condition = condition == null ? "" : condition.trim();
    }

    public boolean test(Player player) {
        if (player == null) return false;
        String cond = condition;
        boolean negate = cond.startsWith("!");
        if (negate) cond = cond.substring(1).trim();

        boolean result;
        int colon = cond.indexOf(':');
        String key = colon >= 0 ? cond.substring(0, colon).toLowerCase() : cond.toLowerCase();
        String value = colon >= 0 ? cond.substring(colon + 1).trim() : "";

        switch (key) {
            case "permission":
            case "perm":
                result = player.hasPermission(value);
                break;
            case "item":
                result = hasItem(player, value);
                break;
            case "customitem":
                result = hasCustomItem(player, value.toLowerCase());
                break;
            case "world":
                result = player.getWorld().getName().equalsIgnoreCase(value);
                break;
            case "health":
                result = compare(player.getHealth(), value);
                break;
            case "sneaking":
                result = player.isSneaking();
                break;
            case "op":
                result = player.isOp();
                break;
            default:
                AnarchyUtils.instance.getLogger().warning("[IF] Unknown condition: " + cond);
                result = false;
        }
        return negate != result;
    }

    private static boolean hasItem(Player player, String materialName) {
        try {
            Material m = Material.valueOf(materialName.toUpperCase());
            return player.getInventory().contains(m);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean hasCustomItem(Player player, String id) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && id.equals(ItemManager.getItemId(stack))) return true;
        }
        return false;
    }

    private static boolean compare(double actual, String expr) {
        expr = expr.trim();
        try {
            if (expr.startsWith(">=")) return actual >= Double.parseDouble(expr.substring(2));
            if (expr.startsWith("<=")) return actual <= Double.parseDouble(expr.substring(2));
            if (expr.startsWith(">")) return actual > Double.parseDouble(expr.substring(1));
            if (expr.startsWith("<")) return actual < Double.parseDouble(expr.substring(1));
            if (expr.startsWith("=") || expr.startsWith("=="))
                return actual == Double.parseDouble(expr.replaceFirst("^==?", ""));
            return actual == Double.parseDouble(expr);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * IF never executes anything by itself; ActionManager interprets it.
     */
    @Override
    public void execute(Player player) {
        // no-op
    }
}
