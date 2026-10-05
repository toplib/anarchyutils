package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dupe detection (heuristic): flags players dropping items at an abnormal
 * rate, which is the common signature of duplication exploits. Alerts online
 * staff (anarchyutils.alerts) and optionally cancels further drops.
 *
 * config.yml:
 *   Modules:
 *     dupe_detect:
 *       enabled: true
 *       max_drops_per_minute: 120
 *       cancel_excess: true
 */
public class DupeDetectModule extends Module {

    private static final long WINDOW_MS = 60_000L;
    private final Map<UUID, Deque<Long>> drops = new ConcurrentHashMap<>();

    public DupeDetectModule() {
        super("dupe_detect");
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        int max = cfgInt("max_drops_per_minute", 120);

        Deque<Long> times = drops.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        while (!times.isEmpty() && now - times.peekFirst() > WINDOW_MS) times.pollFirst();
        times.addLast(now);

        if (times.size() <= max) return;

        if (cfgBool("cancel_excess", true)) {
            e.setCancelled(true);
        }

        String msg = Messages.get().getString("modules.dupe_detect.alert",
                "&c[DupeDetect] &f%player% &7dropped %count% items in 60s")
                .replace("%player%", player.getName())
                .replace("%count%", String.valueOf(times.size()));
        AnarchyUtils.instance.getLogger().warning("[DupeDetect] " + player.getName()
                + " dropped " + times.size() + " items in 60s");
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("anarchyutils.alerts")) {
                online.sendMessage(Utils.component(msg));
            }
        }
    }
}
