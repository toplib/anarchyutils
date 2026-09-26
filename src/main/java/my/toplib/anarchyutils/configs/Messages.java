package my.toplib.anarchyutils.configs;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Convenience accessor for messages.yml.
 */
public final class Messages {

    private Messages() {}

    public static FileConfiguration get() {
        return AnarchyUtils.messagesConfig.getConfig();
    }
}
