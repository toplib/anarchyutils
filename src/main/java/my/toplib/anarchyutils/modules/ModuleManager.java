package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.AnarchyUtils;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers/unregisters modules based on Modules.<id>.enabled in config.yml.
 */
public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final List<Module> active = new ArrayList<>();

    public ModuleManager() {
        modules.add(new ChatFilterModule());
        modules.add(new CombatLogModule());
        modules.add(new WorldLimitsModule());
        modules.add(new DupeDetectModule());
        modules.add(new AntiCheatModule());
    }

    /** Registers listeners for every enabled module. */
    public void enableAll() {
        for (Module module : modules) {
            if (!module.isEnabled() || active.contains(module)) continue;
            AnarchyUtils.instance.getServer().getPluginManager()
                    .registerEvents(module, AnarchyUtils.instance);
            active.add(module);
            AnarchyUtils.instance.getLogger().info("AnarchyUtils | Module enabled: " + module.getId());
        }
    }

    /** Unregisters all module listeners (called before enableAll on reload). */
    public void disableAll() {
        for (Module module : active) {
            HandlerList.unregisterAll(module);
        }
        active.clear();
    }

    public void reload() {
        disableAll();
        enableAll();
    }
}
