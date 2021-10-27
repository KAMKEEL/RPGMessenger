package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kamkeel.RPGMessenger.Util.MessageUtil.*;
import static kamkeel.RPGMessenger.Util.MessageUtil.layoutString;
import static kamkeel.RPGMessenger.RPGCommands.*;

public class CommandTemp implements CommandDefault {

    // -------------------------------------------------| Temp Commands
    public void TempMSG(CommandSender sender, String label, String[] args){
        // /tmpmsg Player(0) CustomName(1) MSG(2)
        if(args.length > 2){
            Player target = findPlayer(args[0]);
            if(target != null){
                NPC tempNPC = new NPC(args[1]);

                int tempIndex = tempControl.npcExactIndex( tempNPC );

                if(tempIndex != -1){
                    tempNPC = tempControl.getNPC(tempIndex);
                }
                else{
                    tempControl.npcAdd(tempNPC);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            tempControl.npcRemove(args[1]);
                        }
                    }, 1200 * plugin.getConfig().getInt("Time"));
                }

                String allArgs = layoutString(2, args);

                Bukkit.getConsoleSender().sendMessage(getSpyFormat(1, tempNPC.getDisplayName(), target.getDisplayName(), allArgs));
                formMessage(target, tempNPC.getDisplayName(), true, allArgs);
                sendSpyMessage(sender, 1, true, tempNPC.getDisplayName(), target.getDisplayName(), allArgs);

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
    public void TempMSGLegacy(CommandSender sender, String label, String[] args){
        // /tmpmsg CustomName(0) Player(1) MSG(2)
        if(args.length > 2){
            Player target = findPlayer(args[1]);
            if(target != null){
                NPC tempNPC = new NPC(args[0]);

                int tempIndex = tempControl.npcExactIndex( tempNPC );

                if(tempIndex != -1){
                    tempNPC = tempControl.getNPC(tempIndex);
                }
                else{
                    tempControl.npcAdd(tempNPC);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            tempControl.npcRemove(args[0]);
                        }
                    }, 1200 * plugin.getConfig().getInt("Time"));
                }

                String allArgs = layoutString(2, args);

                Bukkit.getConsoleSender().sendMessage(getSpyFormat(1, tempNPC.getDisplayName(), target.getDisplayName(), allArgs));
                formMessage(target, tempNPC.getDisplayName(), true, allArgs);
                sendSpyMessage(sender, 1, true, tempNPC.getDisplayName(), target.getDisplayName(), allArgs);

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
    public void TempLocal(CommandSender sender, String label, String[] args){
        // /tmplocal Player(0) CustomName(1) MSG(2)
        if(args.length > 2){
            Player target = findPlayer(args[0]);
            if(target != null){
                NPC tempNPC = new NPC(args[1]);

                int tempIndex = tempControl.npcExactIndex( tempNPC );

                if(tempIndex != -1){
                    tempNPC = tempControl.getNPC(tempIndex);
                }
                else{
                    tempControl.npcAdd(tempNPC);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            tempControl.npcRemove(args[1]);
                        }
                    }, 1200 * plugin.getConfig().getInt("Time"));
                }

                String allArgs = layoutString(2, args);

                Bukkit.getConsoleSender().sendMessage(getSpyLocalFormat(true, target.getDisplayName(), tempNPC.getDisplayName(), allArgs));
                sendSpyLocalMessage(sender, true, target.getDisplayName(), tempNPC.getDisplayName(), allArgs);

                formLocalMessage(target, tempNPC.getDisplayName(), true, allArgs);

                setReply(target.getName(), tempNPC.getDisplayName(), 1);
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find player!");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/" + label + " §6NewName §ePlayer §7Message");
        }
    }
    public void TempGroup(CommandSender sender, String label, String[] args){
        // /tmpgroup Group(0) CustomName(1) MSG(2)
        if(args.length > 2) {
            int groupIndex;
            try {
                groupIndex = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException iobe) {
                groupIndex = groupControl.groupIndex(args[0]);
            }

            if (groupIndex > -1 && groupControl.validIndex(groupIndex)) {

                String allArgs = layoutString(2, args);
                NPC tempNPC = new NPC(args[1]);

                int tempIndex = tempControl.npcExactIndex( tempNPC );

                if(tempIndex != -1){
                    tempNPC = tempControl.getNPC(tempIndex);
                }
                else{
                    tempControl.npcAdd(tempNPC);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            tempControl.npcRemove(args[1]);
                        }
                    }, 1200 * plugin.getConfig().getInt("Time"));
                }

                Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(1, groupControl.getGroupOpDisplayTag(groupIndex), tempNPC.getDisplayName(), allArgs));
                sendGroupMessage(sender, groupIndex, formGroupMessage(tempNPC.getDisplayName(), groupControl.getGroupDisplayTag(groupIndex), true, allArgs));
                sendSpyGroupMessage(sender, groupIndex, 1, true, groupControl.getGroupOpDisplayTag(groupIndex), tempNPC.getDisplayName(), allArgs);

                setGroupReply(groupControl.getGroup(groupIndex), tempNPC.getDisplayName(),  true);

            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/" + label + " §6NewName §eGroup §7Message");
        }
    }
    public void TempSay(CommandSender sender, String label, String[] args){
        // /tmpsay CustomName(0) MSG(1)
        if(args.length > 1){
            NPC tempNPC = new NPC(args[0]);

            int tempIndex = tempControl.npcExactIndex( tempNPC );

            if(tempIndex != -1){
                tempNPC = tempControl.getNPC(tempIndex);
            }
            else{
                tempControl.npcAdd(tempNPC);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        tempControl.npcRemove(args[0]);
                    }
                }, 1200 * plugin.getConfig().getInt("Time"));
            }

            String allArgs = layoutString(1, args);

            Bukkit.broadcastMessage("§8<§6"+ tempNPC.getDisplayName() + "§8> §f" + allArgs);

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
        sender.sendMessage("§8| §e/tmpmsg        §8>> §7Send a MSG as a Temporary NPC");
        sender.sendMessage("§8| §e/tmpgroup      §8>> §7Send a MSG as a Temporary NPC to Group");
        sender.sendMessage("§8| §e/tmplocal      §8>> §7Send a MSG as a Temporary NPC to Local");
        sender.sendMessage("§8| §e/tmpsay        §8>> §7Send a CHAT as a Temporary NPC");
        sender.sendMessage("§8| §e/tmplist       §8>> §7List All Temporary NPCs");
        sender.sendMessage("§8|--------------------------------------|");
    }
    // -------------------------------------------------|

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        if(label.equalsIgnoreCase("tmpmsg") || label.equalsIgnoreCase("tm")
                || label.equalsIgnoreCase("fma") || label.equalsIgnoreCase("tma")
                || label.equalsIgnoreCase("tidm")) {
            if(label.equalsIgnoreCase("fma")){
                TempMSGLegacy(sender, label, args);
            }
            else {
                TempMSG(sender, label, args);
            }
        }
        else if(label.equalsIgnoreCase("tmpgroup") || label.equalsIgnoreCase("tidg")
                || label.equalsIgnoreCase("tga")) {
            TempGroup(sender, label, args);
        }
        else if(label.equalsIgnoreCase("tmplocal") || label.equalsIgnoreCase("tl")
                || label.equalsIgnoreCase("lma") || label.equalsIgnoreCase("tla")
                || label.equalsIgnoreCase("tidl") || label.equalsIgnoreCase("tidp")
                || label.equalsIgnoreCase("tmppublic")) {
            TempLocal(sender, label, args);
        }
        else if(label.equalsIgnoreCase("tmpsay") || label.equalsIgnoreCase("ts")
                || label.equalsIgnoreCase("tsa")){
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

}
