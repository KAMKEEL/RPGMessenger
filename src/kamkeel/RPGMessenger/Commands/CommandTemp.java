package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;
import static kamkeel.RPGMessenger.Util.RPGStringHelper.*;
import static kamkeel.RPGMessenger.Util.MessageUtil.*;

import static kamkeel.RPGMessenger.RPGCommand.*;

public class CommandTemp implements CommandDefault {

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        if(label.equalsIgnoreCase("tmpmsg") || label.equalsIgnoreCase("tm")
                || label.equalsIgnoreCase("fma") || label.equalsIgnoreCase("tma")) {
            TempMSG(sender, label, args);
        }
        else if(label.equalsIgnoreCase("tmpsay") || label.equalsIgnoreCase("ts")){
            TempSay(sender, label, args);
        }
        else if(label.equalsIgnoreCase("tmplist") || label.equalsIgnoreCase("tlist")){
            TempList(sender, args);
        }
        else if(label.equalsIgnoreCase("tmphelp")){
            TempHelp(sender);
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4/tmphelp");
        }
    }

    public void TempMSG(CommandSender sender, String label, String[] args){
        // /tmpmsg CustomName(0) Player(1) MSG(2)
        if(args.length > 2){
            Player target = findPlayer(args[1]);
            if(target != null){
                NPC tempNPC = new NPC(args[0]);
                if(tempControl.alreadyExists(tempNPC)){
                    tempNPC = tempControl.getNPC(tempControl.npcIndex(args[0]));
                }
                else{
                    tempControl.npcAdd(tempNPC);
                }
                String allArgs = layoutString(2, args);

                Bukkit.getConsoleSender().sendMessage(getSpyFormat(1, tempNPC.getDisplayName(), target.getDisplayName(), allArgs));
                formMessage(target, tempNPC.getDisplayName(), true, allArgs);
                sendSpyMessage(sender, 1, true, tempNPC.getDisplayName(), target.getDisplayName(), allArgs);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        tempControl.npcRemove(args[0]);
                    }
                }, 1200 * plugin.getConfig().getInt("Time"));

                setReply(target.getName(), tempNPC.getDisplayName(), 0);
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find player!");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/" + label + " §6NewName §ePlayer §7Message");
        }
    }

    public void TempSay(CommandSender sender, String label, String[] args){
        // /tmpsay CustomName(0) MSG(1)
        if(args.length > 1){
            NPC tempNPC = new NPC(args[0]);
            if(tempControl.alreadyExists(tempNPC)){
                tempNPC = tempControl.getNPC(tempControl.npcIndex(args[0]));
            }
            else{
                tempControl.npcAdd(tempNPC);
            }
            String allArgs = layoutString(1, args);

            Bukkit.broadcastMessage("§8<§6"+ tempNPC.getDisplayName() + "§8> §f" + allArgs);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    tempControl.npcRemove(args[0]);
                }
            }, 1200 * plugin.getConfig().getInt("Time"));

        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/" + label + " §6NewName §7Message");
        }
    }

    public void TempList(CommandSender sender, String[] args){
        if(args.length == 1){
            try {
                int i = Integer.parseInt(args[0]);
                sender.sendMessage("§8>--------- §6Temp List§8 ---------<");

                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                    try {
                        try {
                            if (o <= tempControl.listLength()) {
                                sender.sendMessage("§c" + o + ".§7 " + tempControl.getNPCDisplayName(o - 1));
                            }
                        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                        }
                        o++;
                    } catch (NullPointerException localNullPointerException1) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                    }
                }
                int pages = tempControl.listLength() / 15 + (tempControl.listLength() % 15 == 0 ? 0 : 1);
                sender.sendMessage("§8>------- §7Page §e" + i + "§6 §7of §e" + pages + "§8 --------<");
            } catch (NumberFormatException nfe) {
                try {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't resolve number §4" + args[0]);
                } catch (ArrayIndexOutOfBoundsException aofe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe argument contains invalid characters!");
                }
            }
        }
        else {
            sender.sendMessage("§8>--------- §6Temp List§8 ---------<");
            for (int i = 1; 16 > i; i++) {
                if (( tempControl.listLength() > 0) && (i <= tempControl.listLength() )) {
                    sender.sendMessage("§c" + i + ".§7 " + tempControl.getNPCDisplayName(i - 1));
                }
            }
            int pages = tempControl.listLength() / 15 + (tempControl.listLength() % 15 == 0 ? 0 : 1);
            sender.sendMessage("§8>------- §7Page §e" + 1 + "§6 §7of §e" + pages + "§8 --------<");
        }
    }

    public void TempHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §6Temp Help §8--------------|");
        sender.sendMessage("§8| §e/tempmsg         §8>> §7Send a MSG as a Temporary NPC");
        sender.sendMessage("§8| §e/tempsay         §8>> §7Send a CHAT as a Temporary NPC");
        sender.sendMessage("§8| §e/templist      §8>> §7List All Temporary NPCs");
        sender.sendMessage("§8|--------------------------------------|");
    }

}
