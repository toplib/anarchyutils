package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Gives the player an item - either a custom item defined in items.yml,
 * or a vanilla Material name.
 * Config format: [GIVEITEM] itemKeyOrMaterial;amount
 */
public class GiveItemAction implements Action {

    private final String itemName;
    private final int amount;

    public GiveItemAction(String itemName, int amount) {
        this.itemName = itemName;
        this.amount = Math.max(1, amount);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        ItemStack toGive = null;
        String key = PlaceholderAPI.apply(player, itemName);
        ItemStack custom = ItemManager.getItem(key.toLowerCase());
        if (custom != null) {
            toGive = new ItemStack(custom);
        } else {
            try {
                toGive = new ItemStack(Material.valueOf(key.toUpperCase()));
            } catch (IllegalArgumentException e) {
                AnarchyUtils.instance.getLogger().warning("[GIVEITEM] Unknown item/material: " + key);
                return;
            }
        }
        toGive.setAmount(Math.min(amount, toGive.getMaxStackSize()));
        player.getInventory().addItem(toGive).forEach((slot, leftover) ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }
}
