package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kamkeel.RPGMessenger.RPGCommands.*;
import static kamkeel.RPGMessenger.Util.MessageUtil.*;

public class CommandAction implements CommandDefault {

    // -------------------------------------------------| Action Commands
    public void ActionSay(String[] args){
        if(args.length > 0){
            String allArgs = layoutString(0, args);
            Bukkit.broadcastMessage(" " + allArgs);
        }
    }
    public void ActionMsg(CommandSender sender, String[] args){
        // amsg, ida
    }
    public void ActionHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §cAction Help §8--------------|");
        sender.sendMessage("§8| §6/actionsay (cbm)   §8>> §7Broadcast Global Message");
        sender.sendMessage("§8|--------------------------------------|");
    }
    // -------------------------------------------------|

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        if(label.equalsIgnoreCase("actionsay") || label.equalsIgnoreCase("as") || label.equalsIgnoreCase("cbm")){
            ActionSay(args);
        }
        else if(label.equalsIgnoreCase("help")){
            ActionHelp(sender);
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4/action help");
        }
    }

}
