package my.toplib.anarchyutils;

import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy built-in structures for the Trap and Plast items.
 * Both place obsidian around/under the player and revert after the item's
 * configured despawn_delay (ticks) in items.yml.
 */
public class Buildings {

    /** Places a 3x3 obsidian platform ("plast") under the player. */
    public static void createPlast(Player player) {
        Location base = player.getLocation().getBlock().getLocation();
        List<BlockState> previous = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                org.bukkit.block.Block b = base.clone().add(dx, -1, dz).getBlock();
                previous.add(b.getState());
                b.setType(Material.OBSIDIAN, false);
            }
        }
        logPlace(player, "player_setPlast", "player_setPlastLog");
        scheduleRevert(previous, despawnDelay("Plast"), player, "player_plastDisable");
    }

    /** Places a 3x3x2 obsidian box ("trap") around the player. */
    public static void createBox(Player player) {
        Location base = player.getLocation().getBlock().getLocation();
        List<BlockState> previous = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    boolean shell = Math.abs(dx) == 1 || Math.abs(dz) == 1 || dy == -1 || dy == 1;
                    if (!shell) continue;
                    org.bukkit.block.Block b = base.clone().add(dx, dy, dz).getBlock();
                    previous.add(b.getState());
                    b.setType(Material.OBSIDIAN, false);
                }
            }
        }
        logPlace(player, "player_setTrap", "player_setTrapLog");
        scheduleRevert(previous, despawnDelay("Trap"), player, "player_TrapDisable");
    }

    private static int despawnDelay(String itemKey) {
        return AnarchyUtils.itemsConfig.getConfig().getInt("Items." + itemKey + ".despawn_delay", 6000);
    }

    private static void logPlace(Player player, String playerMsgKey, String logMsgKey) {
        String playerMsg = Messages.get().getString("modules.items." + playerMsgKey,
                Messages.get().getString(playerMsgKey));
        if (playerMsg != null) {
            player.sendMessage(Utils.component(playerMsg.replace("%player%", player.getName())));
        }
        String logMsg = Messages.get().getString("modules.items." + logMsgKey,
                Messages.get().getString(logMsgKey));
        if (logMsg != null) {
            AnarchyUtils.instance.getLogger().info(logMsg
                    .replace("%player%", player.getName())
                    .replace("%location_x%", String.valueOf(player.getLocation().getBlockX()))
                    .replace("%location_y%", String.valueOf(player.getLocation().getBlockY()))
                    .replace("%location_z%", String.valueOf(player.getLocation().getBlockZ())));
        }
    }

    private static void scheduleRevert(List<BlockState> previous, long delayTicks, Player player, String msgKey) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BlockState state : previous) {
                    state.update(true, false);
                }
                String msg = Messages.get().getString("modules.items." + msgKey, Messages.get().getString(msgKey));
                if (msg != null && player.isOnline()) {
                    player.sendMessage(Utils.component(msg.replace("%player%", player.getName())));
                }
            }
        }.runTaskLater(AnarchyUtils.instance, delayTicks);
    }
}
