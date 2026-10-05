package my.toplib.anarchyutils;

import my.toplib.anarchyutils.commands.MainCommands;
import my.toplib.anarchyutils.commands.MainTabComplete;
import my.toplib.anarchyutils.hooks.AnarchyUtilsExpansion;
import my.toplib.anarchyutils.modules.ModuleManager;
import my.toplib.anarchyutils.utils.ConfigLoader;
import my.toplib.anarchyutils.utils.Cooldowns;
import my.toplib.anarchyutils.utils.SchematicLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class AnarchyUtils extends JavaPlugin {


    public static AnarchyUtils instance;
    public static ConfigLoader messagesConfig;
    public static ConfigLoader itemsConfig;

    private ModuleManager moduleManager;


    @Override
    public void onEnable() {

        instance = this;
        saveConfigs();

        saveDefaultConfig();
        messagesConfig.saveDefault();
        itemsConfig.saveDefault();
        SchematicLoader.getSchematicsFolder();

        ItemManager.init();

        moduleManager = new ModuleManager();
        moduleManager.enableAll();

        Bukkit.getLogger().info(" ");
        Bukkit.getLogger().info("             | ");
        Bukkit.getLogger().info("AnarchyUtils | AnarchyUtils - Version: " + getDescription().getVersion());
        Bukkit.getLogger().info("             | ");
        Bukkit.getLogger().info(" ");

        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("anarchy_utils").setExecutor(new MainCommands());
        getCommand("anarchy_utils").setTabCompleter(new MainTabComplete());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new AnarchyUtilsExpansion().register();
            Bukkit.getLogger().info("AnarchyUtils | PlaceholderAPI hook registered (%anarchyutils_...%).");
        }

        Bukkit.getLogger().info("AnarchyUtils | Plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {

        if (moduleManager != null) moduleManager.disableAll();
        Bukkit.getLogger().info("AnarchyUtils | Plugin has been successfully disabled!");

    }

    private void saveConfigs(){

        messagesConfig = ConfigLoader.of(this, "messages.yml");
        itemsConfig = ConfigLoader.of(this, "items.yml");

    }


    public static void reloadConfigs(){
        instance.reloadConfig();
        messagesConfig.reloadConfig();
        itemsConfig.reloadConfig();

        ItemManager.reload();
        Cooldowns.purgeExpired();
        if (instance.moduleManager != null) {
            instance.moduleManager.reload();
        }
    }
}
