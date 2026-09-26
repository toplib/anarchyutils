package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Plays a sound for the player.
 * Config format: [SOUND] SOUND_NAME;VOLUME;PITCH (volume/pitch optional)
 */
public class SoundAction implements Action {

    private final String soundName;
    private final float volume;
    private final float pitch;

    public SoundAction(String soundName, float volume, float pitch) {
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        try {
            player.playSound(player.getLocation(), Sound.valueOf(soundName.toUpperCase()), volume, pitch);
        } catch (IllegalArgumentException e) {
            AnarchyUtils.instance.getLogger().warning("Unknown sound in action config: " + soundName);
        }
    }
}
