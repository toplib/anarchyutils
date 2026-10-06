package my.toplib.anarchyutils.utils;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal schematic loader. Schematics live in
 * plugins/AnarchyUtils/schematics/<name>.yml and contain a "blocks" list of
 * "dx,dy,dz,MATERIAL" strings. The paste origin is the block the player is
 * standing on.
 *
 * Example file (trap1.yml):
 *   blocks:
 *     - "0,0,0,OBSIDIAN"
 *     - "1,0,0,OBSIDIAN"
 *     - "0,1,0,IRON_BARS"
 */
public final class SchematicLoader {

    private SchematicLoader() {}

    public static File getSchematicsFolder() {
        File folder = new File(AnarchyUtils.instance.getDataFolder(), "schematics");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    /**
     * Pastes schematic {@code name} at the player's location.
     *
     * @param revertAfterSeconds if > 0, restores the original blocks after this delay
     * @return false if the file is missing or has no blocks
     */
    public static boolean paste(Player player, String name, int revertAfterSeconds) {
        File file = new File(getSchematicsFolder(), name + ".yml");
        if (!file.exists()) return false;

        YamlConfiguration schem = YamlConfiguration.loadConfiguration(file);
        List<String> lines = schem.getStringList("blocks");
        if (lines.isEmpty()) return false;

        Location origin = player.getLocation().getBlock().getLocation();
        List<BlockState> previousStates = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length < 4) continue;
            try {
                int dx = Integer.parseInt(parts[0].trim());
                int dy = Integer.parseInt(parts[1].trim());
                int dz = Integer.parseInt(parts[2].trim());
                Material mat = Material.valueOf(parts[3].trim().toUpperCase());
                Block block = origin.clone().add(dx, dy, dz).getBlock();
                previousStates.add(block.getState());
                block.setType(mat, false);
            } catch (IllegalArgumentException e) {
                AnarchyUtils.instance.getLogger()
                        .warning("[PLACESCHEM] Bad block entry '" + line + "' in " + name + ".yml");
            }
        }

        if (previousStates.isEmpty()) return false;

        if (revertAfterSeconds > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (BlockState state : previousStates) {
                        state.update(true, false);
                    }
                }
            }.runTaskLater(AnarchyUtils.instance, revertAfterSeconds * 20L);
        }
        return true;
    }
}
