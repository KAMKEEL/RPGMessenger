package kamkeel.RPGMessenger.Configs;

import kamkeel.RPGMessenger.RPGMessenger;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigNPC {

    private static final RPGMessenger plugin = RPGMessenger.getPlugin(RPGMessenger.class);

    private final String configName;

    public ConfigNPC(String name){
        configName = name;
    }

    // Files & File Configs Here
    public FileConfiguration config;
    public File file;

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        file = new File(plugin.getDataFolder(), (configName + ".yml"));
        if (!file.exists()) {
            plugin.saveResource((configName + ".yml"), false);
        }

        reloadConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + ChatColor.RED + "Could not save the " + configName + ".yml file");
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

}