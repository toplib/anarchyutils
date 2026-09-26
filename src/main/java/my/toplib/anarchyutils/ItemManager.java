package my.toplib.anarchyutils;

import my.toplib.anarchyutils.action.Action;
import my.toplib.anarchyutils.action.ActionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import my.toplib.anarchyutils.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
    public static HashMap<String, ItemStack> items = new HashMap<>();
    public static HashMap<String, List<Action>> itemActions = new HashMap<>();
    public static ItemStack trap;
    public static ItemStack plast;
    public static File scmSchematicsFolder;

    public static List<String> totalItems = new ArrayList<>();

    public static ItemStack getItem(String key){
        if(items.containsKey(key)){
            return items.get(key);
        }
        return null;
    }

    /**
     * Returns the parsed onUse action list for an item (may be empty, never null).
     */
    public static List<Action> getActions(String key){
        return itemActions.getOrDefault(key.toLowerCase(), new ArrayList<>());
    }

    public static String addItem(String key, ItemStack item){
        if(items.containsKey(key)){
            return "status-already_contains_key";
        } else if(items.containsValue(item)){
            return "status-already_contains_value";
        }
        items.put(key, item);
        return "status-successfully";
    }

    public static void clearItems() {
        items.clear();
        itemActions.clear();
        totalItems.clear();
    }

    public static boolean containsItem(String key){ return items.containsKey(key); }

    public static Boolean itemEquals(ItemStack item){
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            ItemStack storedItem = entry.getValue();
            if (storedItem.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }

    public static void takeItem(int takeAmount, Player player){
        for(Map.Entry<String, ItemStack> entry : items.entrySet()){
            if(player.getInventory().getItemInMainHand().isSimilar(entry.getValue())){
                if(player.getItemInHand().isSimilar(entry.getValue())){
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - takeAmount);
                }
            } else if(player.getInventory().getItemInOffHand().isSimilar(entry.getValue())){
                player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount() - takeAmount);
            }
        }
    }
    public static void init(){
        loadFromConfig();
    }


    public static void reload() {
        clearItems();
        loadFromConfig();
    }

    /**
     * Loads all custom items from items.yml (section "Items.<Name>").
     * Each item supports: DisplayName, Material, Glow, Lore, onUse actions.
     */
    private static void loadFromConfig() {
        for (String key : AnarchyUtils.itemsConfig.getConfig().getConfigurationSection("Items").getKeys(false)) {
            createItem(key);
        }
    }

    public static String giveItemToPlayer(Player p, String item, int amount){
        ItemStack source = getItem(item);
        if (source == null) return "error";
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack slotItem = p.getInventory().getItem(i);
            if (slotItem == null) {
                ItemStack newItem = new ItemStack(source);
                newItem.setAmount(amount);
                p.getInventory().addItem(newItem);
                return "success";
            }
        }
        return "error";
    }

    private static void createItem(String name) {
        String base = "Items." + name + ".";
        String materialName = AnarchyUtils.itemsConfig.getConfig().getString(base + "Material");
        if (materialName == null) {
            AnarchyUtils.instance.getLogger().warning("AnarchyUtils | Item '" + name + "' has no Material, skipping.");
            return;
        }
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            AnarchyUtils.instance.getLogger().warning("AnarchyUtils | Item '" + name + "' has invalid material '" + materialName + "', skipping.");
            return;
        }

        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        String display = AnarchyUtils.itemsConfig.getConfig().getString(base + "DisplayName");
        if (display != null) meta.setDisplayName(Utils.color(display));

        List<String> lore = new ArrayList<>();
        for (String line : AnarchyUtils.itemsConfig.getConfig().getStringList(base + "Lore")) {
            lore.add(Utils.color(line));
        }
        meta.setLore(lore);

        if (AnarchyUtils.itemsConfig.getConfig().getBoolean(base + "Glow", false)) {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS, org.bukkit.inventory.ItemFlag.ENCHANT_GLINT);
        }

        item.setItemMeta(meta);

        String id = name.toLowerCase();
        items.put(id, item);
        totalItems.add(id);
        itemActions.put(id, ActionManager.parse(AnarchyUtils.itemsConfig.getConfig().getStringList(base + "onUse")));

        // legacy static references
        if (id.equals("trap")) trap = item;
        if (id.equals("plast")) plast = item;
    }
}