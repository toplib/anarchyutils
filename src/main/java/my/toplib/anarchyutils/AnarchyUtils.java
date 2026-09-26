package my.toplib.anarchyutils;

import my.toplib.anarchyutils.commands.MainCommands;
import my.toplib.anarchyutils.commands.MainTabComplete;
import my.toplib.anarchyutils.utils.ConfigLoader;
import my.toplib.anarchyutils.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class AnarchyUtils extends JavaPlugin {


    public static AnarchyUtils instance;
    public static ConfigLoader messagesConfig;
    public static ConfigLoader itemsConfig;


    @Override
    public void onEnable() {

        instance = this;
        saveConfigs();

        saveDefaultConfig();
        messagesConfig.saveDefault();
        itemsConfig.saveDefault();

        ItemManager.init();

        Bukkit.getLogger().info(" ");
        Bukkit.getLogger().info("             | ");
        Bukkit.getLogger().info("AnarchyUtils | AnarchyUtils - Version: " + getDescription().getVersion());
        Bukkit.getLogger().info("             | ");
        Bukkit.getLogger().info(" ");

        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("anarchy_utils").setExecutor(new MainCommands());
        getCommand("anarchy_utils").setTabCompleter(new MainTabComplete());

        Bukkit.getLogger().info("AnarchyUtils | Plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {

        Bukkit.getLogger().info(Utils.color("AnarchyUtils | Plugin has been successfully disabled!"));

    }

    private void saveConfigs(){

        messagesConfig = ConfigLoader.of(this, "messages.yml");
        itemsConfig = ConfigLoader.of(this, "items.yml");

    }


    public static void reloadConfigs(){
        instance.saveDefaultConfig();
        messagesConfig.saveDefault();
        itemsConfig.saveDefault();

        instance.reloadConfig();
        messagesConfig.reloadConfig();
        itemsConfig.reloadConfig();
    }
}
