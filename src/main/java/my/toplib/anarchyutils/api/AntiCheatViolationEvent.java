package my.toplib.anarchyutils.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the anti-cheat module flags a player. Cancelling suppresses the
 * module's response (alerts / setback) for this violation.
 */
public class AntiCheatViolationEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final String check;
    private final int violationCount;

    public AntiCheatViolationEvent(Player player, String check, int violationCount) {
        super(player);
        this.check = check;
        this.violationCount = violationCount;
    }

    /** Name of the check that flagged, e.g. "fly". */
    public String getCheck() {
        return check;
    }

    /** Number of times this player has been flagged for this check. */
    public int getViolationCount() {
        return violationCount;
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
