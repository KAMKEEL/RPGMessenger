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

public class CommandRole implements CommandDefault {

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        if (args.length >= 1) {
            if(args[0].equalsIgnoreCase("name")){
                RoleName(sender, args);
            }
            else if(args[0].equalsIgnoreCase("id")){
                RoleID(sender, args);
            }
            else if(args[0].equalsIgnoreCase("say")){
                RoleSay(sender, args);
            }
            // NPC to Player: Type 1
            else if (args[0].equalsIgnoreCase("msg")) {
                RoleMSG(sender, args);
            }
            else if(args[0].equalsIgnoreCase("list")){
                RoleList(sender, args);
            }
            else if(args[0].equalsIgnoreCase("help")){
                RoleHelp(sender);
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, check /role help.");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, check /role help.");
        }
    }

    public void RoleName(CommandSender sender, String[] args){
        NPC current = roleNames.get(sender.getName());
        if(args.length == 2){
            NPC create = new NPC(args[1]);
            if(current != null){
                roleControl.npcRemove(current.getName());
            }
            roleNames.put(sender.getName(), create);
            roleControl.npcAdd(create);
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Your role name has been set to:§c " + convertSpace(convertColor(args[1])));
        }
        else{
            if(current == null){
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/role name §eNAME");
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§6current name set to: " + current.getDisplayName());
            }
        }
    }

    public void RoleID(CommandSender sender, String[] args){
        NPC current = roleNames.get(sender.getName());
        if(args.length == 2){
            try
            {
                int index = Integer.parseInt(args[1]) - 1;
                try {
                    if(npcControl.validIndex(index)){
                        if(current != null){
                            roleControl.npcRemove(current.getName());
                        }
                        roleNames.put(sender.getName(), npcControl.getNPC(index));
                        roleControl.npcAdd(npcControl.getNPC(index));
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Your role name has been set to§c " + npcControl.getNPC(index).getDisplayName());
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't find an NPC with the id§4 " + args[1]);
                    }
                }
                catch (IndexOutOfBoundsException iobe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4" + args[1] + "§c is not a valid ID");
                }
            }
            catch (NumberFormatException nfe)
            {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe specified argument is not a number:§4 " + args[1]);
            }
        }
        else{
            if(current == null){
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/role id §eNPC_ID");
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§6current name set to: " + current.getDisplayName());
            }
        }
    }

    public void RoleSay(CommandSender sender, String[] args){
        if (args.length > 1)
        {
            String allArgs;
            if (roleNames.get(sender.getName()) != null){
                allArgs = layoutString(1, args);
                formPublicMessage(roleNames.get(sender.getName()).getDisplayName(), allArgs);
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "You don't have a role name. Set it with " + ChatColor.DARK_RED + "/role name §cNAME");
            }
        }
        else
        {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /role say §cMSG.");
        }
    }

    public void RoleMSG(CommandSender sender, String[] args){
        if (args.length > 2)
        {
            Player target = findPlayer(args[1]);
            String allArgs;
            if (roleNames.get(sender.getName()) != null){

                if (target != null) {

                    allArgs = layoutString(2, args);

                    Bukkit.getConsoleSender().sendMessage(spyTags[1] + "§8[§6" + roleNames.get(sender.getName()).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + allArgs);
                    sendSpyMessage(sender, 1, true, roleNames.get(sender.getName()).getDisplayName(), target.getDisplayName(), allArgs);
                    formMessage(target, roleNames.get(sender.getName()).getDisplayName(), true, allArgs);

                    setReply(target.getName(), roleNames.get(sender.getName()).getDisplayName(), 0);
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "Could not find player " + ChatColor.DARK_RED + args[2]);

                }
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "You don't have a role name. Set it with " + ChatColor.DARK_RED + "/role name §cNAME §7or " + ChatColor.DARK_RED + "/rpg id §cID");
            }
        }
        else
        {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /role msg target §cMSG.");
        }
    }

    public void RoleList(CommandSender sender, String[] args){
        if(args.length == 2){
            try {
                int i = Integer.parseInt(args[1]);
                sender.sendMessage("§8>--------- §9Role List§8 ---------<");

                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                    try {
                        try {
                            if (o <= roleControl.listLength()) {
                                sender.sendMessage("§c" + o + ".§7 " + roleControl.getNPCDisplayName(o - 1));
                            }
                        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                        }
                        o++;
                    } catch (NullPointerException localNullPointerException1) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                    }
                }
                int pages = roleControl.listLength() / 15 + (roleControl.listLength() % 15 == 0 ? 0 : 1);
                sender.sendMessage("§8>------- §7Page §c" + i + "§6 §7of §c" + pages + "§8 --------<");
            } catch (NumberFormatException nfe) {
                try {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't resolve number §4" + args[1]);
                } catch (ArrayIndexOutOfBoundsException aofe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe argument contains invalid characters!");
                }
            }
        }
        else {
            sender.sendMessage("§8>--------- §9Role List§8 ---------<");
            for (int i = 1; 16 > i; i++) {
                if (( roleControl.listLength() > 0) && (i <= roleControl.listLength() )) {
                    sender.sendMessage("§c" + i + ".§7 " + roleControl.getNPCDisplayName(i - 1));
                }
            }
            int pages = roleControl.listLength() / 15 + (roleControl.listLength() % 15 == 0 ? 0 : 1);
            sender.sendMessage("§8>------- §7Page §c" + 1 + "§6 §7of §c" + pages + "§8 --------<");
        }
    }

    public void RoleHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §bRole Help §8--------------|");
        sender.sendMessage("§8| §9/role name      §8>> §7Set and Check your Role Name");
        sender.sendMessage("§8| §9/role id          §8>> §7Chane Role Name to NPC ID");
        sender.sendMessage("§8| §9/role msg         §8>> §7Send a MSG from Role");
        sender.sendMessage("§8| §9/role say         §8>> §7Talk to Open Chat from Role");
        sender.sendMessage("§8| §9/role list         §8>> §7List all Role Names");
        sender.sendMessage("§8|--------------------------------------|");
    }

}
