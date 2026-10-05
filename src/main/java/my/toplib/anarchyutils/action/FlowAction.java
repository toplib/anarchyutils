package my.toplib.anarchyutils.action;

import org.bukkit.entity.Player;

/**
 * Flow markers [ELSE] and [ENDIF] used with [IF].
 * They do nothing by themselves; ActionManager interprets them.
 */
public class FlowAction implements Action {

    public enum Kind { ELSE, ENDIF }

    private final Kind kind;

    public FlowAction(Kind kind) {
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public void execute(Player player) {
        // no-op
    }
}
