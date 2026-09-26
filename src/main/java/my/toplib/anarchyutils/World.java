package my.toplib.anarchyutils;

import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class World {
    public static HashMap<UUID, Integer> cooldown = new HashMap<>();

    public static boolean placeSchem(Player player, String schemName) throws FileNotFoundException {

        Location location = player.getLocation();
        List<Block> blocksToRemove;



            cooldown.put(player.getUniqueId(), 1);
        player.sendMessage(Utils.color(Messages.get().getString("player_setPlast"))
                .replaceAll("%player%", player.getName()));
        AnarchyUtils.instance.getLogger().info(Utils.color(Messages.get().getString("player_setPlastLog"))
                .replaceAll("%player%", player.getName())
                .replaceAll("%location_x%", String.valueOf(player.getLocation().getX()))
                .replaceAll("%location_y%", String.valueOf(player.getLocation().getY()))
                .replaceAll("%location_z%", String.valueOf(player.getLocation().getZ())));
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<Location, Material> entry : blocks.entrySet()) {
                    entry.getKey().getBlock().setType(entry.getValue());
                }
                blocks.clear();
                cooldown.remove(player.getUniqueId());
                player.sendMessage(Utils.color(Messages.get().getString("player_plastDisable")).replaceAll("%player%", player.getName()));
            }
        }.runTaskLater(AnarchyUtils.instance, AnarchyUtils.itemsConfig.getConfig().getInt("Items.Plast.despawn_delay", 6000));
        return true;
    }
}
