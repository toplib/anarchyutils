package my.toplib.anarchyutils.action;

import org.bukkit.entity.Player;

/**
 * Marker action that tells ActionManager to pause the sequence
 * before executing the NEXT action. Delay is in seconds (supports fractions).
 */
public class WaitAction implements Action {

    private final double seconds;

    public WaitAction(double seconds) {
        this.seconds = Math.max(0, seconds);
    }

    public double getSeconds() {
        return seconds;
    }

    /**
     * WAIT does nothing on its own - the delay is handled by ActionManager.
     */
    @Override
    public void execute(Player player) {
        // no-op
    }
}
