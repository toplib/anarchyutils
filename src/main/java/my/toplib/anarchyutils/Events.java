package my.toplib.anarchyutils;

import my.toplib.anarchyutils.action.Action;
import my.toplib.anarchyutils.action.ActionManager;
import my.toplib.anarchyutils.api.CustomItemUseEvent;
import my.toplib.anarchyutils.utils.Cooldowns;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Events implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack used = e.getItem();
        if (used == null) return;
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                && e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        String id = ItemManager.getItemId(used);
        if (id == null) return;

        Player pl = e.getPlayer();
        String configKey = ItemManager.getItemKeyForId(id);
        String base = configKey == null ? null : "Items." + configKey + ".";

        CustomItemUseEvent apiEvent = new CustomItemUseEvent(pl, used, id);
        Bukkit.getPluginManager().callEvent(apiEvent);
        if (apiEvent.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        // per-item cooldown from items.yml: Items.<Name>.cooldown_seconds
        int cdSeconds = base == null ? 0
                : AnarchyUtils.itemsConfig.getConfig().getInt(base + "cooldown_seconds", 0);
        if (cdSeconds > 0 && Cooldowns.isOnCooldown(pl.getUniqueId(), id)) {
            pl.sendMessage(Utils.component(my.toplib.anarchyutils.configs.Messages.get()
                    .getString("modules.items.cooldown", "&cPlease wait!")));
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        if (cdSeconds > 0) {
            Cooldowns.set(pl.getUniqueId(), id, cdSeconds);
            pl.setCooldown(used.getType(), cdSeconds * 20);
        }

        // legacy built-in behaviour for trap/plast (schematic building)
        if (id.equals("plast")) {
            Buildings.createPlast(pl);
        } else if (id.equals("trap")) {
            Buildings.createBox(pl);
        }

        // consume one of the stack unless consume: false
        boolean consume = base == null
                || AnarchyUtils.itemsConfig.getConfig().getBoolean(base + "consume", true);
        if (consume) {
            used.setAmount(used.getAmount() - 1);
        }

        List<Action> actions = ItemManager.getActions(id);
        if (!actions.isEmpty()) {
            ActionManager.run(pl, actions);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        String id = ItemManager.getItemId(e.getItem());
        if (id == null) return;
        List<Action> actions = ItemManager.getConsumeActions(id);
        if (!actions.isEmpty()) {
            ActionManager.run(e.getPlayer(), actions);
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) throws URISyntaxException, IOException, InterruptedException {
        if(e.getPlayer().hasPermission("anarchyutils.updateCheck")){
            Utils.checkUpdate(e.getPlayer());
        }
    }
}
