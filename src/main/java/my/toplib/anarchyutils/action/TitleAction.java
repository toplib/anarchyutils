package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

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
        Component main = Utils.component(PlaceholderAPI.apply(player, title));
        Component sub = Utils.component(PlaceholderAPI.apply(player, subtitle));
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L));
        player.showTitle(Title.title(main, sub, times));
    }
}
