package my.toplib.anarchyutils;

import my.toplib.anarchyutils.action.Action;
import my.toplib.anarchyutils.action.ActionManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import my.toplib.anarchyutils.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and owns the custom items defined in items.yml.
 *
 * Each item supports:
 *   DisplayName, Material, Glow, Lore, CustomModelData, Enchantments,
 *   cooldown_seconds  - per-player cooldown before onUse can trigger again
 *   consume           - consume one of the stack when used (default true)
 *   despawn_delay     - ticks until placed structures revert (legacy plast/trap)
 *   onUse             - actions on right-click
 *   onConsume         - actions when the item is eaten/drunk
 *
 * Items are tagged with a persistent NBT id ("anarchyutils:item_id") so they
 * cannot be faked by renaming a vanilla item.
 */
public class ItemManager {
    public static HashMap<String, ItemStack> items = new HashMap<>();
    public static HashMap<String, List<Action>> itemActions = new HashMap<>();
    public static HashMap<String, List<Action>> itemConsumeActions = new HashMap<>();
    public static ItemStack trap;
    public static ItemStack plast;

    public static List<String> totalItems = new ArrayList<>();

    private static NamespacedKey itemIdKey;

    public static NamespacedKey getItemIdKey() {
        if (itemIdKey == null) {
            itemIdKey = new NamespacedKey(AnarchyUtils.instance, "item_id");
        }
        return itemIdKey;
    }

    public static ItemStack getItem(String key){
        return items.get(key);
    }

    /**
     * Returns the custom-item id for a stack, or null if it isn't one of ours.
     * Prefers the persistent NBT tag, falling back to visual similarity for
     * items created before the tag existed.
     */
    public static String getItemId(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        String tagged = stack.getItemMeta().getPersistentDataContainer()
                .get(getItemIdKey(), PersistentDataType.STRING);
        if (tagged != null) return tagged;
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            if (entry.getValue().isSimilar(stack)) return entry.getKey();
        }
        return null;
    }

    /**
     * Returns the parsed onUse action list for an item (may be empty, never null).
     */
    public static List<Action> getActions(String key){
        return itemActions.getOrDefault(key.toLowerCase(), new ArrayList<>());
    }

    /**
     * Returns the parsed onConsume action list for an item (may be empty, never null).
     */
    public static List<Action> getConsumeActions(String key){
        return itemConsumeActions.getOrDefault(key.toLowerCase(), new ArrayList<>());
    }

    /**
     * Reads a per-item scalar setting from items.yml (key is case-insensitive).
     */
    public static String getItemKeyForId(String id) {
        org.bukkit.configuration.ConfigurationSection section =
                AnarchyUtils.itemsConfig.getConfig().getConfigurationSection("Items");
        if (section == null) return null;
        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase(id)) return key;
        }
        return null;
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
        itemConsumeActions.clear();
        totalItems.clear();
    }

    public static boolean containsItem(String key){ return items.containsKey(key); }

    public static Boolean itemEquals(ItemStack item){
        return getItemId(item) != null;
    }

    public static void takeItem(int takeAmount, Player player){
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (getItemId(main) != null) {
            main.setAmount(main.getAmount() - takeAmount);
        } else if (getItemId(off) != null) {
            off.setAmount(off.getAmount() - takeAmount);
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
     */
    private static void loadFromConfig() {
        org.bukkit.configuration.ConfigurationSection section =
                AnarchyUtils.itemsConfig.getConfig().getConfigurationSection("Items");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            createItem(key);
        }
    }

    public static String giveItemToPlayer(Player p, String item, int amount){
        ItemStack source = getItem(item);
        if (source == null) return "error";
        ItemStack stack = new ItemStack(source);
        stack.setAmount(amount);
        p.getInventory().addItem(stack).forEach((slot, leftover) ->
                p.getWorld().dropItemNaturally(p.getLocation(), leftover));
        return "success";
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
        if (display != null) meta.displayName(Utils.component(display));

        List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
        for (String line : AnarchyUtils.itemsConfig.getConfig().getStringList(base + "Lore")) {
            lore.add(Utils.component(line));
        }
        meta.lore(lore);

        if (AnarchyUtils.itemsConfig.getConfig().getBoolean(base + "Glow", false)) {
            meta.setEnchantmentGlintOverride(true);
        }

        for (String ench : AnarchyUtils.itemsConfig.getConfig().getStringList(base + "Enchantments")) {
            String[] parts = ench.split(";");
            try {
                Enchantment enchantment = org.bukkit.Registry.ENCHANTMENT.get(
                        NamespacedKey.minecraft(parts[0].trim().toLowerCase()));
                if (enchantment == null) continue;
                int level = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 1;
                meta.addEnchant(enchantment, level, true);
            } catch (IllegalArgumentException e) {
                AnarchyUtils.instance.getLogger().warning(
                        "AnarchyUtils | Item '" + name + "' has bad enchantment '" + ench + "'");
            }
        }

        int customModelData = AnarchyUtils.itemsConfig.getConfig().getInt(base + "CustomModelData", 0);
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }

        String id = name.toLowerCase();
        meta.getPersistentDataContainer().set(getItemIdKey(), PersistentDataType.STRING, id);

        item.setItemMeta(meta);

        items.put(id, item);
        totalItems.add(id);
        itemActions.put(id, ActionManager.parse(AnarchyUtils.itemsConfig.getConfig().getStringList(base + "onUse")));
        itemConsumeActions.put(id, ActionManager.parse(AnarchyUtils.itemsConfig.getConfig().getStringList(base + "onConsume")));

        // legacy static references
        if (id.equals("trap")) trap = item;
        if (id.equals("plast")) plast = item;
    }
}
