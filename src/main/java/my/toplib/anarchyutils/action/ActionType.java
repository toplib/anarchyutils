package my.toplib.anarchyutils.action;

import java.util.Locale;

/**
 * All supported action types that can be used in configs (items.yml onUse, etc.)
 * Usage in config: [TYPE] arguments
 */
public enum ActionType {

    MESSAGE,
    TITLE,
    ACTIONBAR,
    SOUND,
    CONSOLE,
    PLAYER_COMMAND,
    SERVER_COMMAND,
    WAIT,
    CLOSE,
    TELEPORT,
    POTION,
    GIVEITEM,
    BROADCAST,
    IF,
    ELSE,
    ENDIF,
    PLACESCHEM;

    /**
     * Parses a raw config string like "[MESSAGE] hello" into an Action.
     *
     * @param line  the raw line from the config
     * @return parsed Action, or null if the line is malformed / type unknown
     */
    public static Action parse(String line) {
        if (line == null) return null;
        line = line.trim();
        int start = line.indexOf('[');
        int end = line.indexOf(']');
        if (start != 0 || end < 0) return null;

        String typeName = line.substring(1, end).toUpperCase(Locale.ROOT);
        String args = line.substring(end + 1).trim();

        ActionType type;
        try {
            type = ActionType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null; // unknown type -> skip
        }

        switch (type) {
            case MESSAGE:
                return new MessageAction(args);
            case TITLE: {
                // [TITLE] mainTitle;subtitle;fadeIn;stay;fadeOut  (; parts are optional)
                String[] parts = args.split(";", -1);
                String title = parts.length > 0 ? parts[0] : "";
                String subtitle = parts.length > 1 ? parts[1] : "";
                int fadeIn = parseInt(parts.length > 2 ? parts[2] : null, 10);
                int stay = parseInt(parts.length > 3 ? parts[3] : null, 70);
                int fadeOut = parseInt(parts.length > 4 ? parts[4] : null, 20);
                return new TitleAction(title, subtitle, fadeIn, stay, fadeOut);
            }
            case ACTIONBAR:
                return new ActionBarAction(args);
            case SOUND: {
                // [SOUND] ENTITY_PLAYER_LEVELUP;VOLUME;PITCH  (; parts are optional)
                String[] parts = args.split(";", -1);
                String sound = parts.length > 0 ? parts[0] : "";
                float volume = parseFloat(parts.length > 1 ? parts[1] : null, 1.0f);
                float pitch = parseFloat(parts.length > 2 ? parts[2] : null, 1.0f);
                return new SoundAction(sound, volume, pitch);
            }
            case CONSOLE:
                return new ConsoleAction(args);
            case PLAYER_COMMAND:
                return new PlayerCommandAction(args);
            case SERVER_COMMAND:
                return new ServerCommandAction(args);
            case WAIT:
                return new WaitAction(parseDouble(args, 0));
            case CLOSE:
                return new CloseInventoryAction();
            case TELEPORT: {
                // [TELEPORT] x;y;z[;world[;yaw;pitch]]  ("~" = keep current)
                String[] parts = args.split(";", -1);
                if (parts.length < 3) return null;
                String world = parts.length > 3 ? parts[3].trim() : null;
                Float yaw = parts.length > 4 ? parseFloatOrNull(parts[4]) : null;
                Float pitch = parts.length > 5 ? parseFloatOrNull(parts[5]) : null;
                return new TeleportAction(parts[0], parts[1], parts[2], world, yaw, pitch);
            }
            case POTION: {
                // [POTION] EFFECT_NAME;seconds;amplifier
                String[] parts = args.split(";", -1);
                String effect = parts.length > 0 ? parts[0].trim() : "";
                if (effect.isEmpty()) return null;
                double seconds = parseDouble(parts.length > 1 ? parts[1] : null, 30);
                int amplifier = parseInt(parts.length > 2 ? parts[2] : null, 0);
                return new PotionEffectAction(effect, seconds, amplifier);
            }
            case GIVEITEM: {
                // [GIVEITEM] itemKeyOrMaterial;amount
                String[] parts = args.split(";", -1);
                String item = parts.length > 0 ? parts[0].trim() : "";
                if (item.isEmpty()) return null;
                int amount = parseInt(parts.length > 1 ? parts[1] : null, 1);
                return new GiveItemAction(item, amount);
            }
            case BROADCAST:
                return new BroadcastAction(args);
            case IF:
                return new IfAction(args);
            case ELSE:
                return new FlowAction(FlowAction.Kind.ELSE);
            case ENDIF:
                return new FlowAction(FlowAction.Kind.ENDIF);
            case PLACESCHEM: {
                // [PLACESCHEM] name[;revertAfterSeconds]
                String[] parts = args.split(";", -1);
                String name = parts.length > 0 ? parts[0].trim() : "";
                if (name.isEmpty()) return null;
                int revert = parseInt(parts.length > 1 ? parts[1] : null, 0);
                return new PlaceSchematicAction(name, revert);
            }
            default:
                return null;
        }
    }

    private static int parseInt(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static float parseFloat(String s, float def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static Float parseFloatOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static double parseDouble(String s, double def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
