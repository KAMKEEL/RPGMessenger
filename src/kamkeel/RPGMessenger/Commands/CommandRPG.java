package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kamkeel.RPGMessenger.RPGCommands.*;
import static kamkeel.RPGMessenger.Util.ColorConvert.convertColor;
import static kamkeel.RPGMessenger.Util.ColorConvert.convertSpace;
import static kamkeel.RPGMessenger.Util.MessageUtil.*;

public class CommandRPG implements CommandDefault {

    // -------------------------------------------------| RPG Commands
    public void RpgHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §7RPG Help §8--------------|");
        if(AdminPermission(sender)) {
            sender.sendMessage("§8| §4/tmp      §8>> §7Temp Commands");
            sender.sendMessage("§8| §4/role      §8>> §7Role Commands");
            sender.sendMessage("§8| §4/npc      §8>> §7NPC Commands");
        }
        sender.sendMessage("§8| §4/msg      §8>> §7Starts a Private Message");
        sender.sendMessage("§8| §4/lmsg      §8>> §7Starts a Local Message");
        sender.sendMessage("§8| §4/gmsg      §8>> §7Starts a Group Message");
        sender.sendMessage("§8| §4/reply      §8>> §7Quickly Reply to a Recent Message");
        sender.sendMessage("§8| §4/group      §8>> §7View Group Management");
        sender.sendMessage("§8| §4/request          §8>> §7Request NPCs");
        if(AdminPermission(sender)) {
            sender.sendMessage("§8| §4/rpg msg      §8>> §7View NPC Msg Commands");
        }
        sender.sendMessage("§8|--------------------------------------|");
    }
    public void RpgMsg(CommandSender sender){
        if(AdminPermission(sender)) {
            sender.sendMessage("§8|-------------- §7RPG MSG §8--------------|");
            sender.sendMessage("§8| §4/idm, nm,   §8>> §7Starts a Private Message");
            sender.sendMessage("§8| §4/nr     §8>> §7Quick Reply as NPC");
            sender.sendMessage("§8| §4/idl, idp, lm, pm   §8>> §7Starts a Public Message");
            sender.sendMessage("§8| §4/idg, gid      §8>> §7Starts a Group Message");
            sender.sendMessage("§8| §4/gr     §8>> §7Quick Reply to a group as NPC");
            sender.sendMessage("§8|--------------------------------------|");
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
    }
    // -------------------------------------------------|

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        if (args.length >= 1) {
            if(args[0].equalsIgnoreCase("help")){
                RpgHelp(sender);
            }
            else if(args[0].equalsIgnoreCase("msg")){
                RpgMsg(sender);
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, check /rpg help.");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, check /rpg help.");
        }
    }

}
