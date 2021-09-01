package kamkeel.RPGMessenger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RPGMessenger extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener, org.bukkit.command.CommandExecutor
{

    public static RPGCommand rpgCMD;

    public RPGMessenger() {}

    public void onEnable() {
        System.out.println("------By Kam------");
        System.out.println("[RPG] V 1.0");
        System.out.println("------------------");
        getServer().getPluginManager().registerEvents(this, this);

        // Initialize Command Runner
        rpgCMD = new RPGCommand();

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        System.out.println("[RPG] Successfully Enabled!");
    }

    public void onDisable() {
        System.out.println("[RPG] Disabled!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args)
    {
        return rpgCMD.runCMD(sender, cmd, label, args);
    }
}


