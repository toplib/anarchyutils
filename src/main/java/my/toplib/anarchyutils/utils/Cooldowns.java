package my.toplib.anarchyutils.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player, per-item cooldown tracking with millisecond expiry times.
 */
public final class Cooldowns {

    private static final Map<String, Long> EXPIRIES = new ConcurrentHashMap<>();

    private Cooldowns() {}

    private static String key(UUID playerId, String itemId) {
        return playerId + ":" + itemId;
    }

    /** @return remaining seconds (fractional) left on cooldown, 0 if none */
    public static double remainingSeconds(UUID playerId, String itemId) {
        Long expiry = EXPIRIES.get(key(playerId, itemId));
        if (expiry == null) return 0;
        long left = expiry - System.currentTimeMillis();
        return left <= 0 ? 0 : left / 1000.0;
    }

    public static boolean isOnCooldown(UUID playerId, String itemId) {
        return remainingSeconds(playerId, itemId) > 0;
    }

    public static void set(UUID playerId, String itemId, double seconds) {
        if (seconds <= 0) return;
        EXPIRIES.put(key(playerId, itemId), System.currentTimeMillis() + (long) (seconds * 1000));
    }

    /** Drops expired entries; call occasionally (e.g. on reload) to bound map size. */
    public static void purgeExpired() {
        long now = System.currentTimeMillis();
        Iterator<Long> it = EXPIRIES.values().iterator();
        while (it.hasNext()) {
            if (it.next() <= now) it.remove();
        }
    }
}
