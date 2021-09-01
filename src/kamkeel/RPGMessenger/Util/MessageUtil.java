package kamkeel.RPGMessenger.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static kamkeel.RPGMessenger.Util.ColorConvert.convertColor;

public class MessageUtil {

    // -------------------------------------------------| Form Messages
    // Format Regular Message
    public static void formMessage(CommandSender sendTo, String name, boolean isReceiving, String args){
        if (isReceiving){
            sendTo.sendMessage("§8[§c< §6" + name + "§8] §r"  + args);
        }
        else{
            sendTo.sendMessage("§8[§a> §6" + name + "§8] §r" + args);
        }
    }
    public static String formGroupMessage(String name, String groupTag, boolean isReceiving, String args){
        if(isReceiving){
            return "§8[§9<§6 " + groupTag + "§8] " + name + "§7: §r"  + args;
        }
        return "§8[§b>§6 " + groupTag + "§8]§7: §r"  + args;
    }
    public static void formLocalMessage(CommandSender sendTo, String name, boolean isReceiving, String args){
        if(isReceiving){
            sendTo.sendMessage("§8[§c<§6 " + "§e#" + "§8] " + name + "§7: §r"  + args);
        }
        else {
            sendTo.sendMessage("§8[§a>§6 " + "§e#" + "§8]§7: §r"  + args);
        }
    }
    public static void formPublicMessage(String name, String args){
        Bukkit.broadcastMessage("§8<§6"+ name + "§8> §f" + args);
    }
    // -------------------------------------------------|


    // 0: Message, 1: NPC, 2: Player, 3: Public, 4: Group, 5: Console
    public static String[] spyTags = {RPGStringHelper.MESSAGE, RPGStringHelper.NPC,
            RPGStringHelper.PLAYER, RPGStringHelper.PUBLIC, RPGStringHelper.GROUP,
            RPGStringHelper.CONSOLE};

    // Format Spy Message
    public static String getSpyFormat(int tag, String from, String to, String chat){
        return spyTags[tag] + "§8[§6" + from + " §7> §6" + to + "§8] §r" + chat;
    }
    public static String getSpyLocalFormat(boolean NPCSend, String playerDisplay, String from, String chat){
        if(NPCSend){
            return spyTags[3] + "§8[§6" + playerDisplay + "§8] §6" + from + "§7: §r" + chat;
        }
        return spyTags[3] + "§8[§6" + playerDisplay + "§8]§7: §r" + chat;
    }
    public static String getSpyGroupFormat(int tag, String groupOPTag, String from, String chat){
        return spyTags[tag] + "§8[§6" + groupOPTag + "§8] §6" + from + "§7: §r" + chat;
    }

    // Format Chat Message
    public static String layoutString(int index, String[] args){

        StringBuilder sb = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return convertColor(sb.toString().trim());
    }

}
