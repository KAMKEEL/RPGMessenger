package kamkeel.RPGMessenger;

import kamkeel.RPGMessenger.Configs.ConfigGroup;
import kamkeel.RPGMessenger.Configs.ConfigNPC;
import kamkeel.RPGMessenger.Control.GroupControl;
import kamkeel.RPGMessenger.Control.NPCControl;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;
import static kamkeel.RPGMessenger.Util.MessageUtil.*;
import static kamkeel.RPGMessenger.Util.RPGStringHelper.*;

public class RPGMessenger extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener, org.bukkit.command.CommandExecutor
{

    private RPGCommands rpgCommands;
    
    public RPGMessenger() {}

    public void onEnable() {
        System.out.println("------By Kam------");
        System.out.println("[RPG] V 1.0");
        System.out.println("------------------");
        getServer().getPluginManager().registerEvents(this, this);

        rpgCommands = new RPGCommands();

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        System.out.println("[RPG] Successfully Enabled!");
    }

    public void onDisable() {
        System.out.println("[RPG] Disabled!");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args)
    {
        return rpgCommands.runCMD(sender, cmd, label, args);
    }
}


