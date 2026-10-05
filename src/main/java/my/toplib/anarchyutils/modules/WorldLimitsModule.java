package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * World limit enforcement: keeps players inside a square border centered on
 * configurable coordinates in every world (per-module setting).
 *
 * config.yml:
 *   Modules:
 *     world_limits:
 *       enabled: true
 *       radius: 5000          # blocks from center on X and Z
 *       center_x: 0
 *       center_z: 0
 *       exempt_permission: "anarchyutils.worldlimits.bypass"
 */
public class WorldLimitsModule extends Module {

    private final Map<UUID, Long> lastWarn = new ConcurrentHashMap<>();

    public WorldLimitsModule() {
        super("world_limits");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.hasChangedBlock()) return;
        Player player = e.getPlayer();
        if (player.hasPermission(cfg("exempt_permission", "anarchyutils.worldlimits.bypass"))) return;

        Location to = e.getTo();
        int radius = cfgInt("radius", 5000);
        int cx = cfgInt("center_x", 0);
        int cz = cfgInt("center_z", 0);

        if (Math.abs(to.getBlockX() - cx) <= radius && Math.abs(to.getBlockZ() - cz) <= radius) return;

        e.setCancelled(true);

        long now = System.currentTimeMillis();
        Long last = lastWarn.get(player.getUniqueId());
        if (last == null || now - last > 2000) {
            lastWarn.put(player.getUniqueId(), now);
            player.sendMessage(Utils.component(Messages.get().getString("modules.world_limits.outside",
                    "&cYou can't go further - you reached the world limit!")));
        }
    }
}
