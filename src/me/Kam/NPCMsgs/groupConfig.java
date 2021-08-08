package me.Kam.NPCMsgs;

import java.io.File;
import java.io.IOException;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class groupConfig {

    private static NPCMsgs plugin = NPCMsgs.getPlugin(NPCMsgs.class);

    // Files & File Configs Here
    public static FileConfiguration groupcfg;
    public static File groupsfile;
    // --------------------------

    public static void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        groupsfile = new File(plugin.getDataFolder(), "groups.yml");

        if (!groupsfile.exists()) {
            try {
                groupsfile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender()
                        .sendMessage(ChatColor.RED + "Could not create the groups.yml file");
            }
        }

        groupcfg = YamlConfiguration.loadConfiguration(groupsfile);
    }

    public static FileConfiguration getGroupcfg() {
        return groupcfg;
    }

    public static void saveGroups() {
        try {
            groupcfg.save(groupsfile);

        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the groups.yml file");
        }
    }

    public static void reloadGroups() {
        groupcfg = YamlConfiguration.loadConfiguration(groupsfile);
    }

}