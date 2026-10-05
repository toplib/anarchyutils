package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.event.Listener;

/**
 * A toggleable feature. Implementations are listeners; they should read their
 * own settings under Modules.<id> in config.yml.
 */
public abstract class Module implements Listener {

    private final String id;

    protected Module(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    protected boolean isEnabled() {
        return AnarchyUtils.instance.getConfig().getBoolean("Modules." + id + ".enabled", false);
    }

    protected String cfg(String path, String def) {
        return AnarchyUtils.instance.getConfig().getString("Modules." + id + "." + path, def);
    }

    protected int cfgInt(String path, int def) {
        return AnarchyUtils.instance.getConfig().getInt("Modules." + id + "." + path, def);
    }

    protected boolean cfgBool(String path, boolean def) {
        return AnarchyUtils.instance.getConfig().getBoolean("Modules." + id + "." + path, def);
    }

    protected java.util.List<String> cfgList(String path) {
        return AnarchyUtils.instance.getConfig().getStringList("Modules." + id + "." + path);
    }
}
