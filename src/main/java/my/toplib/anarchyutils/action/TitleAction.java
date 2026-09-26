package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.entity.Player;

public class TitleAction implements Action {

    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleAction(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        player.sendTitle(
                Utils.color(PlaceholderAPI.apply(player, title)),
                Utils.color(PlaceholderAPI.apply(player, subtitle)),
                fadeIn, stay, fadeOut);
    }
}
