package my.toplib.anarchyutils.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player right-clicks a configured AnarchyUtils custom item,
 * before cooldowns and actions are processed. Cancelling prevents the use.
 */
public class CustomItemUseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final ItemStack item;
    private final String itemId;

    public CustomItemUseEvent(Player player, ItemStack item, String itemId) {
        super(player);
        this.item = item;
        this.itemId = itemId;
    }

    public ItemStack getItem() {
        return item;
    }

    /** The items.yml id of the custom item (lowercase). */
    public String getItemId() {
        return itemId;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
