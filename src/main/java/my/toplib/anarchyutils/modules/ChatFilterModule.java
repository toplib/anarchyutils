package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Chat filter module. Blocks or censors configured words / regex patterns.
 *
 * config.yml:
 *   Modules:
 *     chat_filter:
 *       enabled: true
 *       mode: censor            # censor | cancel
 *       blocked_words: [badword]
 *       blocked_regex: ["\\d{4}"]
 */
public class ChatFilterModule extends Module {

    private final List<Pattern> patterns = new ArrayList<>();

    public ChatFilterModule() {
        super("chat_filter");
    }

    private void compilePatterns() {
        patterns.clear();
        for (String word : cfgList("blocked_words")) {
            patterns.add(Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE));
        }
        for (String regex : cfgList("blocked_regex")) {
            try {
                patterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
            } catch (PatternSyntaxException e) {
                my.toplib.anarchyutils.AnarchyUtils.instance.getLogger()
                        .warning("ChatFilter | Bad regex: " + regex);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if (e.getPlayer().hasPermission("anarchyutils.chatfilter.bypass")) return;
        if (patterns.isEmpty()) compilePatterns();

        String text = PlainTextComponentSerializer.plainText().serialize(e.message());
        String lowered = text.toLowerCase(Locale.ROOT);
        boolean matched = false;
        for (Pattern pattern : patterns) {
            if (pattern.matcher(lowered).find()) {
                matched = true;
                break;
            }
        }
        if (!matched) return;

        String mode = cfg("mode", "censor");
        if ("cancel".equalsIgnoreCase(mode)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.component(Messages.get()
                    .getString("modules.chat_filter.blocked", "&cYour message was blocked by the chat filter.")));
        } else {
            String censored = text;
            for (Pattern pattern : patterns) {
                censored = pattern.matcher(censored).replaceAll("***");
            }
            e.message(Component.text(censored));
        }
    }
}
