package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Teleports the player to a location.
 * Config format: [TELEPORT] x;y;z[;world[;yaw;pitch]]
 * Each coordinate may be a number or "~" (relative: keeps the player's current value).
 * "~<offset>" is also supported, e.g. [TELEPORT] ~;~5;~ teleports 5 blocks up.
 */
public class TeleportAction implements Action {

    private final String xExpr, yExpr, zExpr, worldName;
    private final Float yaw, pitch;

    public TeleportAction(String xExpr, String yExpr, String zExpr, String worldName, Float yaw, Float pitch) {
        this.xExpr = xExpr;
        this.yExpr = yExpr;
        this.zExpr = zExpr;
        this.worldName = worldName;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (worldName != null && !worldName.isEmpty()) {
            World w = Bukkit.getWorld(PlaceholderAPI.apply(player, worldName));
            if (w != null) world = w;
            else AnarchyUtils.instance.getLogger().warning("[TELEPORT] Unknown world: " + worldName);
        }
        try {
            Location target = new Location(world,
                    resolve(xExpr, loc.getX()),
                    resolve(yExpr, loc.getY()),
                    resolve(zExpr, loc.getZ()),
                    yaw != null ? yaw : loc.getYaw(),
                    pitch != null ? pitch : loc.getPitch());
            player.teleport(target);
        } catch (NumberFormatException e) {
            AnarchyUtils.instance.getLogger().warning("[TELEPORT] Bad coordinates: " + xExpr + ";" + yExpr + ";" + zExpr);
        }
    }

    private static double resolve(String expr, double current) {
        expr = PlaceholderAPI.apply(null, expr);
        if (expr.startsWith("~")) {
            String offset = expr.substring(1).trim();
            return current + (offset.isEmpty() ? 0 : Double.parseDouble(offset));
        }
        return Double.parseDouble(expr.trim());
    }
}
