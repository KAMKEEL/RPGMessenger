package kamkeel.RPGMessenger.Configs;

import kamkeel.RPGMessenger.RPGMessenger;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigGroup {

    private static RPGMessenger plugin = RPGMessenger.getPlugin(RPGMessenger.class);

    // Files & File Configs Here
    public static List<FileConfiguration> groupcfgs = new java.util.ArrayList();;
    public static  List<File> groupsfiles = new java.util.ArrayList();;
    // --------------------------

    private static void createFolder() {
        File folder = new File(plugin.getDataFolder(), "groups");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    private static void createConfigFile(String fileName) {
        File groupsfile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + fileName + ".yml");
        if ( !groupsfile.exists() ) {
            try {
                groupsfile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender()
                        .sendMessage(RPGStringHelper.TAG + "Could not create the " + fileName + ".yml file");
            }
        }
    }

    public static void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        createFolder();
    }

    public static List<FileConfiguration> getGroupcfgs() {
        return groupcfgs;
    }

    public static void saveGroups() {
        for(int i = 0; i < groupcfgs.size(); i++){
            try {
                groupcfgs.get(i).save(groupsfiles.get(i));
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + "Could not save a groups file");
            }
        }
    }

    public static void reloadGroups() {
        for(int i = 0; i < groupcfgs.size(); i++){
            groupcfgs.set(i, YamlConfiguration.loadConfiguration(groupsfiles.get(i)));
        }
    }

}