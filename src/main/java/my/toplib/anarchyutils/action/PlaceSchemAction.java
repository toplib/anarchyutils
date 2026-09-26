package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.World;
import org.bukkit.entity.Player;

/**
 * [PLACESCHEM] schemName;despawnDelaySeconds
 * Places the built-in schematic (trap/plast style) at the player's location
 * and restores the terrain after the given delay.
 */
public class PlaceSchemAction implements Action {

    private final String schemName;
    private final long despawnTicks;

    public PlaceSchemAction(String schemName, long despawnSeconds) {
        this.schemName = schemName == null ? "" : schemName.trim();
        this.despawnTicks = Math.max(1, despawnSeconds * 20);
    }

    public String getSchemName() {
        return schemName;
    }

    public long getDespawnTicks() {
        return despawnTicks;
    }

    @Override
    public void execute(Player player) {
        if (player == null || !player.isOnline()) return;
        String name = schemName.toLowerCase();
        try {
            if (name.startsWith("plast")) {
                World.createPlast(player, despawnTicks);
            } else if (name.startsWith("trap")) {
                World.createTrap(player, despawnTicks);
            } else {
                AnarchyUtils.instance.getLogger().warning(
                        "AnarchyUtils | Unknown schematic '" + schemName + "' in [PLACESCHEM] action.");
            }
        } catch (Exception e) {
            AnarchyUtils.instance.getLogger().warning(
                    "AnarchyUtils | Failed to place schematic '" + schemName + "': " + e.getMessage());
        }
    }
}
