package my.toplib.anarchyutils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Simple world helpers used by the built-in trap/plast behaviour
 * and by the [PLACESCHEM] action.
 */
public class World {

    /**
     * Returns all blocks in a cube with the given radius around the location (inclusive).
     */
    public static List<Block> getCubeBlocks(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(center.getWorld().getBlockAt(
                            center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z));
                }
            }
        }
        return blocks;
    }

    /**
     * Plast schematic: fills a cube around the player with obsidian,
     * restoring the previous blocks after {@code despawnTicks}.
     */
    public static void createPlast(Player player, long despawnTicks) {
        int radius = AnarchyUtils.itemsConfig.getConfig().getInt("Items.Plast.radius", 3);
        Location center = player.getLocation().clone();
        java.util.Map<Location, Material> previous = new java.util.HashMap<>();
        for (Block block : getCubeBlocks(center, radius)) {
            previous.put(block.getLocation(), block.getType());
            block.setType(Material.OBSIDIAN);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (java.util.Map.Entry<Location, Material> entry : previous.entrySet()) {
                    entry.getKey().getBlock().setType(entry.getValue());
                }
            }
        }.runTaskLater(AnarchyUtils.instance, Math.max(1, despawnTicks));
    }

    /**
     * Trap schematic: builds an obsidian shell box around the player,
     * clearing it after {@code despawnTicks}.
     */
    public static void createTrap(Player player, long despawnTicks) {
        int radius = AnarchyUtils.itemsConfig.getConfig().getInt("Items.Trap.radius", 2);
        Location center = player.getLocation().clone().add(0, 1, 0);
        List<Block> blocks = getCubeBlocks(center, radius);
        for (Block block : blocks) {
            boolean shell = Math.abs(block.getX() - center.getBlockX()) == radius
                    || Math.abs(block.getY() - center.getBlockY()) == radius
                    || Math.abs(block.getZ() - center.getBlockZ()) == radius;
            if (shell) block.setType(Material.OBSIDIAN);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : blocks) {
                    if (block.getType() == Material.OBSIDIAN) block.setType(Material.AIR);
                }
            }
        }.runTaskLater(AnarchyUtils.instance, Math.max(1, despawnTicks));
    }
}
