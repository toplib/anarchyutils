package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Applies a potion effect to the player.
 * Config format: [POTION] EFFECT_NAME;seconds;amplifier
 * seconds supports fractions; amplifier defaults to 0.
 */
public class PotionEffectAction implements Action {

    private final String effectName;
    private final double seconds;
    private final int amplifier;

    public PotionEffectAction(String effectName, double seconds, int amplifier) {
        this.effectName = effectName;
        this.seconds = seconds;
        this.amplifier = Math.max(0, amplifier);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        PotionEffectType type = PotionEffectType.getByName(effectName.toUpperCase());
        if (type == null) {
            AnarchyUtils.instance.getLogger().warning("[POTION] Unknown effect: " + effectName);
            return;
        }
        player.addPotionEffect(new PotionEffect(type, (int) Math.round(seconds * 20), amplifier));
    }
}
