package my.toplib.anarchyutils;

import my.toplib.anarchyutils.action.Action;
import my.toplib.anarchyutils.action.ActionManager;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class Events implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack used = e.getItem();
        if (used == null) return;
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                && e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        for (Map.Entry<String, ItemStack> entry : ItemManager.items.entrySet()) {
            if (!entry.getValue().isSimilar(used)) continue;

            String id = entry.getKey();
            Player pl = e.getPlayer();
            e.setCancelled(true);

            // per-item cooldown from items.yml: Items.<Name>.cooldown_seconds
            int cdSeconds = AnarchyUtils.itemsConfig.getConfig()
                    .getInt("Items." + id.substring(0, 1).toUpperCase() + id.substring(1) + ".cooldown_seconds", 0);
            if (cdSeconds > 0) {
                long lastUse = Buildings.cooldown.getOrDefault(pl.getUniqueId(), 0L);
                long elapsedMillis = System.currentTimeMillis() - lastUse;
                // Cooldown is global per player: while any effect of this player is active,
                // placing a new one is blocked. Effects live for despawn_delay milliseconds.
                long maxEffectMillis = Math.max(cdSeconds * 1000L,
                        AnarchyUtils.itemsConfig.getConfig().getInt("Items.Plast.despawn_delay", 6000));
                if (Buildings.cooldown.containsKey(pl.getUniqueId()) && elapsedMillis < maxEffectMillis) {
                    pl.sendMessage(Utils.color(my.toplib.anarchyutils.configs.Messages.get()
                            .getString("modules.items.cooldown", "&cPlease wait!")));
                    return;
                }
                pl.setCooldown(entry.getValue().getType(), cdSeconds * 20);
                Buildings.cooldown.put(pl.getUniqueId(), System.currentTimeMillis());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Buildings.cooldown.remove(pl.getUniqueId());
                    }
                }.runTaskLater(AnarchyUtils.instance, maxEffectMillis / 50L);
            }

            // legacy built-in behaviour for trap/plast (schematic building)
            if (id.equals("plast")) {
                Buildings.createPlast(pl);
            } else if (id.equals("trap")) {
                Buildings.createBox(pl);
            }

            // config-driven onUse actions for ANY item (including trap/plast)
            List<Action> actions = ItemManager.getActions(id);
            if (!actions.isEmpty()) {
                ActionManager.run(pl, actions);
            }
            return;
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) throws URISyntaxException, IOException, InterruptedException {
        if(e.getPlayer().hasPermission("anarchyutils.updateCheck")){
            Utils.checkUpdate(e.getPlayer());
        }
    }
}
