package my.toplib.anarchyutils.action;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.utils.SchematicLoader;
import org.bukkit.entity.Player;

/**
 * Pastes a schematic file from plugins/AnarchyUtils/schematics/ at the
 * player's location, optionally reverting the paste after a delay.
 *
 * Config format: [PLACESCHEM] name[;revertAfterSeconds]
 * Schematics are YAML files: schematics/<name>.yml with a "blocks" list of
 * "dx,dy,dz,MATERIAL" entries (origin = the block the player is standing on).
 */
public class PlaceSchematicAction implements Action {

    private final String schematicName;
    private final int revertAfterSeconds;

    public PlaceSchematicAction(String schematicName, int revertAfterSeconds) {
        this.schematicName = schematicName;
        this.revertAfterSeconds = revertAfterSeconds;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        boolean ok = SchematicLoader.paste(player, schematicName, revertAfterSeconds);
        if (!ok) {
            AnarchyUtils.instance.getLogger().warning("[PLACESCHEM] Schematic not found or empty: " + schematicName);
        }
    }
}
