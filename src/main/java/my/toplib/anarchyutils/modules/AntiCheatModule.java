package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.api.AntiCheatViolationEvent;
import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic anti-cheat hooks. This is intentionally light: it detects players who
 * stay airborne beyond a threshold while not gliding, swimming, riding or
 * levitating (the classic fly signature), fires AntiCheatViolationEvent for
 * other plugins, warns staff, and optionally teleports the player back down.
 *
 * For real protection, pair with a dedicated anti-cheat and listen for
 * AntiCheatViolationEvent.
 *
 * config.yml:
 *   Modules:
 *     anti_cheat:
 *       enabled: true
 *       max_air_ticks: 80        # ~4s airborne before flagging
 *       setback: true            # teleport player back to from-location
 */
public class AntiCheatModule extends Module {

    private final Map<UUID, Integer> airTicks = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> violations = new ConcurrentHashMap<>();

    public AntiCheatModule() {
        super("anti_cheat");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("anarchyutils.anticheat.bypass")) return;

        if (isLegitimatelyAirborne(player)) {
            airTicks.remove(player.getUniqueId());
            return;
        }

        int ticks = airTicks.merge(player.getUniqueId(), 1, Integer::sum);
        if (ticks < cfgInt("max_air_ticks", 80)) return;

        int count = violations.merge(player.getUniqueId(), 1, Integer::sum);
        airTicks.put(player.getUniqueId(), 0);

        AntiCheatViolationEvent event = new AntiCheatViolationEvent(player, "fly", count);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        String msg = Messages.get().getString("modules.anti_cheat.alert",
                "&c[AntiCheat] &f%player% &7flagged for &f%check% &7(x%count%)")
                .replace("%player%", player.getName())
                .replace("%check%", "fly")
                .replace("%count%", String.valueOf(count));
        AnarchyUtils.instance.getLogger().warning("[AntiCheat] " + player.getName()
                + " flagged for fly (x" + count + ")");
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("anarchyutils.alerts")) {
                online.sendMessage(Utils.component(msg));
            }
        }

        if (cfgBool("setback", true) && e.getFrom() != null) {
            e.setCancelled(true);
        }
    }

    private boolean isLegitimatelyAirborne(Player player) {
        if (player.isOnGround() || player.isFlying() || player.isGliding()
                || player.isSwimming() || player.isInsideVehicle()
                || player.getAllowFlight()
                || player.hasPotionEffect(PotionEffectType.LEVITATION)
                || player.getLocation().getBlock().isLiquid()) {
            return true;
        }
        // falling with velocity is fine - only hovering/climbing counts
        return player.getVelocity().getY() < -0.08;
    }
}
