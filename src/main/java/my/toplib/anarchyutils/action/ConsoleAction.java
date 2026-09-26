package my.toplib.anarchyutils.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleAction implements Action{

    private final String command;

    public ConsoleAction(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void execute(Player player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                PlaceholderAPI.apply(player, command));
    }
}
