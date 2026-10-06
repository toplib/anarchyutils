package my.toplib.anarchyutils.commands;

import my.toplib.anarchyutils.AnarchyUtils;
import my.toplib.anarchyutils.ItemManager;
import my.toplib.anarchyutils.utils.ConfigLoader;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MainCommands implements CommandExecutor {

    private ConfigLoader messages = AnarchyUtils.messagesConfig;

    private String msg(String key, String def) {
        return messages.getConfig().getString(key, def);
    }

    private void send(CommandSender sender, String key, String def) {
        sender.sendMessage(Utils.component(msg(key, def)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                if(sender.hasPermission("anarchyutils.reload")){
                    AnarchyUtils.reloadConfigs();
                    send(sender, "system.reload", "&aPlugin reloaded");
                    return true;
                } else {
                    send(sender, "system.no_permissions", "&cYou don't have permission to do this!");
                    return true;
                }
            case "info":
                sender.sendMessage(Utils.component("&9Anarchy Utils &fversion &6" + AnarchyUtils.instance.getDescription().getVersion()));
                return true;
            case "give":
                if(!sender.hasPermission("anarchyutils.giveCMD")) {
                    send(sender, "system.no_permissions", "&cYou don't have permission to do this!");
                    return true;
                }
                if (args.length < 2 || args.length > 4) {
                    sender.sendMessage(Utils.component("&cUsage: /au give <item> [player] [amount]"));
                    return true;
                }
                String itemId = args[1].toLowerCase();
                if (!ItemManager.containsItem(itemId)) {
                    sender.sendMessage(Utils.component(msg("modules.items.itemNotFounded", "&c%item% doesn't exists!")
                            .replace("%item%", itemId)));
                    return true;
                }
                Player target = args.length >= 3 ? Bukkit.getPlayer(args[2]) : null;
                if (args.length >= 3 && target == null) {
                    send(sender, "system.player_offline", "&cPlayer offline");
                    return true;
                }
                if (target == null) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Utils.component("&cConsole must specify a player: /au give <item> <player>"));
                        return true;
                    }
                    target = (Player) sender;
                }
                int amount = 1;
                if (args.length == 4) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Utils.component("&cInvalid amount: " + args[3]));
                        return true;
                    }
                }
                switch (ItemManager.giveItemToPlayer(target, itemId, amount)) {
                    case "success":
                        String display = ItemManager.getItem(itemId).getItemMeta().getDisplayName();
                        target.sendMessage(Utils.component(msg("modules.items.receive_item", "&fYou receive item: &6%item%")
                                .replace("%item%", display)));
                        return true;
                    case "error":
                        send(sender, "modules.items.noEnoughSpace", "&cNot enough space in your inventory!");
                        return true;
                    default:
                        send(sender, "system.error", "&cERROR! Please contact with administrator.");
                        return true;
                }
            default:
                return false;
        }
    }
}
