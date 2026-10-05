package my.toplib.anarchyutils.modules;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.configs.Messages;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Combat log protection: tags players who fight; if a tagged player quits
 * before the tag expires they are killed and a broadcast is sent.
 * Optionally blocks configured commands while tagged.
 *
 * config.yml:
 *   Modules:
 *     combat_log:
 *       enabled: true
 *       tag_seconds: 15
 *       block_commands: true
 *       blocked_commands: [tpa, spawn, home, warp]
 */
public class CombatLogModule extends Module {

    private static final Map<UUID, Long> TAGS = new ConcurrentHashMap<>();

    public CombatLogModule() {
        super("combat_log");
    }

    public static boolean isTagged(UUID playerId) {
        Long expiry = TAGS.get(playerId);
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public static double remainingSeconds(UUID playerId) {
        Long expiry = TAGS.get(playerId);
        if (expiry == null) return 0;
        long left = expiry - System.currentTimeMillis();
        return left <= 0 ? 0 : left / 1000.0;
    }

    private void tag(Player player) {
        if (player == null || player.hasPermission("anarchyutils.combatlog.bypass")) return;
        TAGS.put(player.getUniqueId(),
                System.currentTimeMillis() + cfgInt("tag_seconds", 15) * 1000L);
    }

    private Player asPlayer(Object entity) {
        if (entity instanceof Player) return (Player) entity;
        if (entity instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Player) return (Player) shooter;
        }
        return null;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Player victim = asPlayer(e.getEntity());
        Player attacker = asPlayer(e.getDamager());
        if (victim == null || attacker == null || victim.equals(attacker)) return;
        tag(victim);
        tag(attacker);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!isTagged(player.getUniqueId())) {
            TAGS.remove(player.getUniqueId());
            return;
        }
        TAGS.remove(player.getUniqueId());
        player.setHealth(0);
        String msg = Messages.get().getString("modules.combat_log.logout",
                "&c%player% logged out during combat and was killed!");
        Bukkit.getServer().sendMessage(Utils.component(msg.replace("%player%", player.getName())));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!cfgBool("block_commands", true)) return;
        if (!isTagged(e.getPlayer().getUniqueId())) return;
        String cmd = e.getMessage().substring(1).split(" ")[0].toLowerCase();
        List<String> blocked = cfgList("blocked_commands");
        if (blocked.stream().noneMatch(c -> c.equalsIgnoreCase(cmd))) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(Utils.component(Messages.get().getString("modules.combat_log.blocked_command",
                "&cYou can't use that command in combat!")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        TAGS.remove(e.getEntity().getUniqueId());
    }
}
