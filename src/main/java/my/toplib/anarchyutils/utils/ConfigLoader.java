package my.toplib.anarchyutils.utils;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigLoader {

    private FileConfiguration config;

    private final JavaPlugin plugin;
    private final String name;
    private final File configFile;

    public static ConfigLoader of(JavaPlugin plugin, String name) {
        ConfigLoader configLoader = new ConfigLoader(plugin, name);
        configLoader.saveDefault();
        return configLoader;
    }

    public ConfigLoader(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.configFile = new File(plugin.getDataFolder(), name);
    }

    public void saveDefault() {
        if(!configFile.exists()) {
            plugin.saveResource(name, false);
        }
    }

    public FileConfiguration getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }

        return this.config;
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defConfigStream = plugin.getResource(name);
        if (defConfigStream != null) {
            this.config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }
}
