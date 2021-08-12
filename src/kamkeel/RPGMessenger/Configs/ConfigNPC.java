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

    private static RPGMessenger plugin = RPGMessenger.getPlugin(RPGMessenger.class);

    // Files & File Configs Here
    public static FileConfiguration npccfg;
    public static  File npcfile;
    // --------------------------

    public static void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        npcfile = new File(plugin.getDataFolder(), "npc.yml");
        if (!npcfile.exists()) {
            plugin.saveResource("npc.yml", false);
//            try {
//                npcfile.createNewFile();
//            } catch (IOException e) {
//                Bukkit.getServer().getConsoleSender()
//                        .sendMessage(NPCStringHelper.TAG + "Could not create the npc.yml file");
//            }
        }

        reloadNPC();
    }

    public static FileConfiguration getNPCcfg() {
        return npccfg;
    }

    public static void saveNPC() {
        try {
            npccfg.save(npcfile);
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + ChatColor.RED + "Could not save the npc.yml file");
        }
    }

    public static void reloadNPC() {
        npccfg = YamlConfiguration.loadConfiguration(npcfile);
    }

}