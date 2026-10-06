package my.toplib.anarchyutils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Built-in trap/plast building behaviour (legacy module).
 */
public class Buildings {

    /** Player UUID -> timestamp when the effect was placed. */
    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    /**
     * Plast: replaces blocks around the player with the configured material,
     * then restores them after despawn_delay ticks.
     */
    public static void createPlast(Player player) {
        int radius = AnarchyUtils.itemsConfig.getConfig().getInt("Items.Plast.radius", 3);
        String matName = AnarchyUtils.itemsConfig.getConfig().getString("Items.Plast.material", "OBSIDIAN");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.OBSIDIAN;

        Location center = player.getLocation().clone();
        Map<Location, Material> previous = new HashMap<>();
        for (Block block : World.getCubeBlocks(center, radius)) {
            previous.put(block.getLocation(), block.getType());
            block.setType(material);
        }

        long delay = AnarchyUtils.itemsConfig.getConfig().getInt("Items.Plast.despawn_delay", 6000);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Location, Material> entry : previous.entrySet()) {
                    entry.getKey().getBlock().setType(entry.getValue());
                }
            }
        }.runTaskLater(AnarchyUtils.instance, delay);
    }

    /**
     * Trap: creates an obsidian box around the target location,
     * removing it after despawn_delay ticks.
     */
    public static void createBox(Player player) {
        int radius = AnarchyUtils.itemsConfig.getConfig().getInt("Items.Trap.radius", 2);
        Location center = player.getLocation().clone().add(0, 1, 0);

        List<Block> blocks = World.getCubeBlocks(center, radius);
        for (Block block : blocks) {
            boolean shell = Math.abs(block.getX() - center.getBlockX()) == radius
                    || Math.abs(block.getY() - center.getBlockY()) == radius
                    || Math.abs(block.getZ() - center.getBlockZ()) == radius;
            if (shell) block.setType(Material.OBSIDIAN);
        }

        long delay = AnarchyUtils.itemsConfig.getConfig().getInt("Items.Trap.despawn_delay", 6000);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : blocks) {
                    if (block.getType() == Material.OBSIDIAN) block.setType(Material.AIR);
                }
            }
        }.runTaskLater(AnarchyUtils.instance, delay);
    }
}
