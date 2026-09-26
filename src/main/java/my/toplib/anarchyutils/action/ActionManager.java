package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs a list of actions as a sequence, respecting [WAIT] delays.
 * All actions are executed on the main thread (Bukkit-safe).
 */
public class ActionManager {

    private static final long TICK = 50L; // ms per tick

    /**
     * Parses raw config lines into an action list, logging unknown types.
     */
    public static List<Action> parse(List<String> lines) {
        List<Action> actions = new ArrayList<>();
        if (lines == null) return actions;
        for (String line : lines) {
            Action action = ActionType.parse(line);
            if (action != null) {
                actions.add(action);
            } else {
                AnarchyUtils.instance.getLogger()
                        .warning("AnarchyUtils | Unknown or malformed action: '" + line + "'");
            }
        }
        return actions;
    }

    /**
     * Executes actions sequentially for a player. [WAIT] pauses the chain.
     */
    public static void run(Player player, List<Action> actions) {
        run(player, actions, 0);
    }

    private static void run(final Player player, final List<Action> actions, final int index) {
        if (index >= actions.size()) return;
        if (player == null || !player.isOnline()) return;

        Action action = actions.get(index);

        if (action instanceof WaitAction) {
            long delayTicks = Math.round(((WaitAction) action).getSeconds() * 1000.0 / TICK);
            new BukkitRunnable() {
                @Override
                public void run() {
                    run(player, actions, index + 1);
                }
            }.runTaskLater(AnarchyUtils.instance, Math.max(1, delayTicks));
            return;
        }

        try {
            action.execute(player);
        } catch (Exception e) {
            AnarchyUtils.instance.getLogger().warning(
                    "AnarchyUtils | Action " + action.getClass().getSimpleName() + " failed: " + e.getMessage());
        }
        run(player, actions, index + 1);
    }
}
