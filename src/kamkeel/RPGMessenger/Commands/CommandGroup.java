package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.Group;
import kamkeel.RPGMessenger.Member;
import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static kamkeel.RPGMessenger.RPGCommand.*;
import static kamkeel.RPGMessenger.Util.ColorConvert.*;
import static kamkeel.RPGMessenger.Util.ColorConvert.convertToRaw;
import static kamkeel.RPGMessenger.Util.MessageUtil.*;
import static kamkeel.RPGMessenger.Util.RPGStringHelper.hasIllegalSymbols;

public class CommandGroup implements CommandDefault {

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        boolean saveConfig = false;
        if(args.length >= 1){
            if(args[0].equalsIgnoreCase("create")){
                saveConfig = GroupCreate(sender, args);
            }
            else if(args[0].equalsIgnoreCase("tag")){
                saveConfig = GroupTag(sender, args);
            }
            else if(args[0].equalsIgnoreCase("leave")){
                saveConfig = GroupLeave(sender, args);
            }
            else if(args[0].equalsIgnoreCase("swap")){
                saveConfig = GroupSwap(sender, args);
            }
            else if(args[0].equalsIgnoreCase("add")){
                saveConfig = GroupAdd(sender, args);
            }
            else if(args[0].equalsIgnoreCase("remove")){
                saveConfig = GroupRemove(sender, args);
            }
            else if(args[0].equalsIgnoreCase("rename")){
                saveConfig = GroupRename(sender, args);
            }
            else if(args[0].equalsIgnoreCase("disband")){
                GroupDisband(sender, args);
            }
            else if(args[0].equalsIgnoreCase("confirmdisband")){
                saveConfig = GroupConfirmDisband(sender, args);
            }
            else if(args[0].equalsIgnoreCase("list")){
                GroupList(sender, args);
            }
            else if(args[0].equalsIgnoreCase("view")){
                GroupView(sender, args);
            }
            else if(args[0].equalsIgnoreCase("help")){
                GroupHelp(sender);
            }

            else if(args[0].equalsIgnoreCase("switch")){
                saveConfig = GroupSwitch(sender, args);
            }
            else if(args[0].equalsIgnoreCase("optag")){
                saveConfig = GroupOpTag(sender, args);
            }
            else if(args[0].equalsIgnoreCase("id")){
                GroupID(sender, args);
            }
            else if(args[0].equalsIgnoreCase("clear")){
                GroupClear(sender, args);
            }
            else if(args[0].equalsIgnoreCase("confirmclear")){
                saveConfig = GroupConfirmClear(sender, args);
            }
            else if(args[0].equalsIgnoreCase("admin")){
                GroupAdmin(sender);
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/group help");
            }

            if(saveConfig){
                groupConfig.saveGroups();
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/group help");
        }
    }

    public boolean GroupCreate(CommandSender sender, String[] args){
        // group create NAME
        if(args.length == 2){
            Group tempGroup = new Group(args[1]);
            if(!(groupControl.nameAlreadyExists(tempGroup))){
                if(!hasIllegalSymbols(tempGroup.getName())){
                    if(groupControl.groupAdd(tempGroup)){

                        // If Sender is Player
                        if(sender instanceof Player){
                            if(!((Player)sender).hasPermission("rpg.admin")){
                                groupControl.groups.get(groupControl.listLength() - 1).addPlayer(((Player)sender));
                            }
                            sendDebugMessage(sender, RPGStringHelper.GROUP + "§7Group: §c" + groupControl.groups.get(groupControl.listLength() - 1).getDisplayName() + "§7 with id §c" + groupControl.listLength() + "§7 created by" + ((Player)sender).getDisplayName());
                        }
                        groupConfig.createConfigFile(groupControl.groups.get(groupControl.listLength() - 1).getName());
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Group: §c" + groupControl.groups.get(groupControl.listLength() - 1).getDisplayName() + "§7 with id §c" + groupControl.listLength() + "§7 created!");

                        updateGroupConfig(groupControl.listLength() - 1);
                        groupConfig.SaveGroupList();
                        return true;
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred during the adding process.");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cIllegal Symbols Found");
                }
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cGroup already exists!");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group create §cNAME.");
        }
        return false;
    }

    public boolean GroupTag(CommandSender sender, String[] args){
        // group tag GroupName/ID NewTag (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender)){
                    if(!(groupControl.tagAlreadyExists(args[2]))){
                        if(!hasIllegalSymbols(args[2])){
                            groupControl.setGroupTag(index, args[2]);
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Group: §c" + groupControl.getGroupDisplayName(index) + "§7 tag changed to: §c" + groupControl.getGroupDisplayTag(index));

                            updateGroupConfig(index);
                            sendGroupMessage(sender, index, RPGStringHelper.COLOR_TAG + "§7Group: §c" + groupControl.getGroupDisplayName(index) + "§7 tag changed to: §c" + groupControl.getGroupDisplayTag(index));
                            return true;
                        }
                        else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cIllegal Symbols Found");
                        }
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4That tag is already being used by another group.");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group tag §eGroup_Name/ID §cNew_Tag.");
        }
        return false;
    }

    public boolean GroupLeave(CommandSender sender, String[] args){
        // group leave GroupName/ID
        if (args.length == 2) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if(!(sender instanceof Player)){
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Console cannot be added or leave a group");
            }
            else if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.getGroup(index).hasMember(sender.getName(), true)){
                    if(!(groupControl.getGroup(index).isGroupOwner((Player)sender))){
                        groupControl.getGroup(index).removeMember(sender.getName(), true);
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7You have left the Group: §c" + groupControl.getGroupDisplayName(index));

                        updateGroupConfig(index);
                        groupConfig.SaveGroupList();
                        sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + ((Player)sender).getDisplayName() + "§7 has left the Group: §c" + groupControl.getGroupDisplayName(index) );
                        return true;
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot leave the group you are the owner of.");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You are not a member of that group.");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group leave §eGroup_Name/ID");
        }
        return false;
    }

    public boolean GroupSwap(CommandSender sender, String[] args){
        // group swap NAME ID ID
        if (args.length == 4) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender)){
                    try{
                        int swapOne = Integer.parseInt(args[2]) - 1;
                        try{
                            int swapTwo = Integer.parseInt(args[3]) - 1;
                            if(swapOne != 0 && swapTwo != 0){
                                if( groupControl.getGroup(index).memberSwap(swapOne, swapTwo)){
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Group: §c" + groupControl.getGroupDisplayName(index) +
                                            "§7 swapped positions: §c" + groupControl.getGroup(index).getMember(swapTwo).getDisplayName() + "§7<->"
                                            + groupControl.getGroup(index).getMember(swapOne).getDisplayName());

                                    updateGroupConfig(index);
                                    return true;
                                }
                                else{
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Unable to swap positions. Please check your IDs are correct.");
                                }
                            }
                            else{
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot swap with the first position.");
                            }
                        }
                        catch (NumberFormatException iobe) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't parse int" + args[3]);
                        }
                    }
                    catch (NumberFormatException iobe) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't parse int" + args[2]);
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group swap  §eGroup_Name/ID §cPos1 Pos2");
        }
        return false;
    }

    public boolean GroupAdd(CommandSender sender, String[] args){
        // group add GroupName/ID Player (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender)){
                    String member = args[2];

                    Player target = findPlayer(member);

                    if(target != null){
                        if(target.getName().equals(sender.getName())){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot add yourself to your own group.");
                        }
                        else if(groupControl.getGroup(index).addPlayer(target)){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Added §cPlayer: " + target.getDisplayName() + "§7 to " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                            target.sendMessage(RPGStringHelper.COLOR_TAG + "§6You"+ "§7 have been added to " + "§7Group: §c" + groupControl.getGroupDisplayName(index));

                            updateGroupConfig(index);
                            return true;
                        }
                        else{
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Attempting to add §cPlayer: " + target.getDisplayName() + "§4, but they are already in the group.");
                        }
                    }
                    else if (npcControl.toString().toLowerCase().contains( convertToRaw(member).toLowerCase()) ) {
                        int findNPC = npcControl.npcIndex(member);
                        if(findNPC > -1){
                            if(groupControl.getGroup(index).addNPC(npcControl.getNPCDisplayName(findNPC))){
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Added §2NPC: " + npcControl.getNPCDisplayName(findNPC) + "§7 to " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                                updateGroupConfig(index);
                                return true;
                            }
                            else{
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Attempting to add §2NPC: " + npcControl.getNPCDisplayName(findNPC) + "§4, but they are already in the group.");
                            }
                        }
                        else{
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Failed adding an NPC. Please double check spelling.");
                        }
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find NPC or Player to Add");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group add §eGroup_Name/ID §cNPC§7/§6Player");
        }
        return false;
    }

    public boolean GroupRemove(CommandSender sender, String[] args){
        // group remove GroupName/ID Player (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender)){
                    String member = args[2];

                    Player target = findPlayer(member);

                    if(target != null){
                        if(target.getName().equals(sender.getName())){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot add yourself to your own group.");
                        }
                        else if(groupControl.getGroup(index).removeMember(target.getName(), true)){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Removed §cPlayer: " + target.getDisplayName() + "§7 from " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                            target.sendMessage(RPGStringHelper.COLOR_TAG + "§6You"+ "§7 have been removed from " + "§7Group: §c" + groupControl.getGroupDisplayName(index));

                            updateGroupConfig(index);
                            sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + target.getDisplayName() + "§7 has been removed from " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );
                            return true;
                        }
                        else{
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Attempting to remove §cPlayer: " + target.getDisplayName() + "§4, but they are not in the group.");
                        }
                    }
                    else if (npcControl.toString().toLowerCase().contains( convertToRaw(member).toLowerCase()) ) {
                        int findNPC = npcControl.npcIndex(member);
                        if(findNPC > -1){
                            if(groupControl.getGroup(index).removeMember(npcControl.getNPCDisplayName(findNPC), false)){
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Removed §2NPC: " + npcControl.getNPCDisplayName(findNPC) + "§7 from " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                                updateGroupConfig(index);
                                return true;
                            }
                            else{
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Attempting to remove §2NPC: " + npcControl.getNPCDisplayName(findNPC) + "§4, but they are not in the group.");
                            }
                        }
                        else{
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Failed removing an NPC. Please double check spelling.");
                        }
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find NPC or Player to Add");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group remove §eGroup_Name/ID §cNPC§7/§6Player");
        }
        return false;
    }

    public boolean GroupRename(CommandSender sender, String[] args){
        // group rename GroupName/ID New_Name (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender)){
                    Group tempGroup = new Group(args[2]);
                    String oldName =  groupControl.groups.get(index).getName();
                    String oldDisplayName =  groupControl.groups.get(index).getDisplayName();
                    if(!(groupControl.nameAlreadyExists(tempGroup))){
                        if(!hasIllegalSymbols(args[2])){
                            if(groupControl.renameGroup(index, args[2])){
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Group: §c" + oldDisplayName + "§7 renamed to §c" + groupControl.getGroupDisplayName(index));

                                groupConfig.renameGroupFile(index, oldName, groupControl.getGroupName(index));
                                sendGroupMessage(sender, index, RPGStringHelper.COLOR_TAG + "§7Group: §c" + oldDisplayName + "§7 renamed to §c" + groupControl.getGroupDisplayName(index));
                                updateGroupConfig(index);
                                return true;
                            }
                            else{
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred during the renaming process.");
                            }
                        }
                        else{
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cIllegal Symbols Found");
                        }
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cGroup already exists!");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group remove §eGroup_Name/ID §cNew_Name");
        }
        return false;
    }

    public boolean GroupConfirmDisband(CommandSender sender, String[] args){
        // group confirmdisband
        if (args.length == 1) {
            String disband = deleteConfirm.get((sender).getName());
            if(disband != null){
                int index = groupControl.groupIndex(disband);
                if (index > -1 && groupControl.validIndex(index)) {
                    if(groupControl.hasEditPermission(index, sender)){
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou have disbanded group: " + groupControl.groups.get(index).getDisplayName());
                        // If Sender is Player
                        if(sender instanceof Player){
                            sendDebugMessage(sender, RPGStringHelper.GROUP + "§7Group: §c" + groupControl.groups.get(index).getDisplayName() + "§7 disbanded by " + ((Player)sender).getDisplayName());
                        }
                        deleteConfirm.remove(sender.getName());
                        groupConfig.deleteConfigFile(groupControl.getGroupName(index));

                        sendGroupMessage(sender, index,RPGStringHelper.GROUP + "§7Group: §c" + groupControl.groups.get(index).getDisplayName() + "§7 has been disbanded");
                        groupControl.groupRemove(disband);
                        groupConfig.SaveGroupList();
                        return true;
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
                }
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§4Time Ran Out or Nothing to Disband");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group confirmdisband");
        }
        return false;
    }

    public boolean GroupSwitch(CommandSender sender, String[] args){
        if(groupControl.hasAdminPermission(sender)){
            if(args.length == 3){
                try {
                    int first = Integer.parseInt(args[1]) - 1;
                    try{
                        int second = Integer.parseInt(args[2]) - 1;
                        // Perform Swap
                        if (groupControl.groupSwap(first, second)){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Switched: " + groupControl.getGroupDisplayName(second) + " §7<-> " + groupControl.getGroupDisplayName(first)   );

                            groupConfig.SaveGroupList();
                            return true;
                        }
                        else{
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred using those IDs. Please check if they are valid.");
                        }
                    } catch (NumberFormatException nfe){
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't parse number§4 " + args[2]);
                    }
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't parse number§4 " + args[1]);
                }
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/group switch §aID ID");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
        return false;
    }

    public boolean GroupOpTag(CommandSender sender, String[] args){
        // group optag GroupName/ID NewOpTag (2)
        if(groupControl.hasAdminPermission(sender)){
            if (args.length == 3) {
                int index;
                try {
                    index = Integer.parseInt(args[1]) - 1;
                } catch (NumberFormatException iobe) {
                    index = groupControl.groupIndex(args[1]);
                }
                if (index > -1 && groupControl.validIndex(index)) {
                    if(!hasIllegalSymbols(args[2])){
                        groupControl.setGroupOpTag(index, args[2]);
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Group: §c" + groupControl.getGroupDisplayName(index) + "§7 op tag changed to: §c" + groupControl.getGroupOpDisplayTag(index));

                        updateGroupConfig(index);
                        groupConfig.SaveGroupList();
                        return true;
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cIllegal Symbols Found");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group optag §eGroup_Name/ID §cNew_Tag.");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
        return false;
    }

    public boolean GroupConfirmClear(CommandSender sender, String[] args){
        // group clear
        if(groupControl.hasAdminPermission(sender)){
            if (args.length == 1) {
                String disband = deleteConfirm.get(sender.getName());
                if(disband != null){
                    if(disband.equals("ClearGroup")){
                        deleteConfirm.remove(sender.getName());
                        groupControl.groups.clear();
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cGroups cleared!");

                        groupConfig.SaveGroupList();
                        return true;
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§4You did not request to Clear Groups");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§4Time Ran Out");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group id §eGroup_Name/ID");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
        return false;
    }



    public void GroupDisband(CommandSender sender, String[] args){
        // group disband GroupName/ID
        if (args.length == 2) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender)){
                    deleteConfirm.put((sender).getName(), groupControl.groups.get(index).getName());
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Warning: §cYou are about to disband group: " + groupControl.groups.get(index).getDisplayName());
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§410s §cto type §7/group confirmdisband");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> deleteConfirm.remove((sender.getName())), 200);
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have permission to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group disband §eGroup_Name/ID");
        }
    }

    public void GroupList(CommandSender sender, String[] args){
        List<Group> activeGroups = new java.util.ArrayList<>();
        List<Integer> groupIDs =  new java.util.ArrayList<>();
        for(int z = 0; z < groupControl.listLength(); z++){
            if(sender instanceof Player){
                if( groupControl.getGroup(z).hasMember(sender.getName(), true) || groupControl.hasEditPermission(z, sender)){
                    activeGroups.add(groupControl.getGroup(z));
                    groupIDs.add(z);
                }
            }
            else {
                activeGroups.add(groupControl.getGroup(z));
                groupIDs.add(z);
            }
        }

        if(args.length == 2){
            try {
                int i = Integer.parseInt(args[1]);
                sender.sendMessage("§8>--------- §aGroup List§8 ---------<");

                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                    try {
                        try {
                            if (o <= activeGroups.size()) {
                                sender.sendMessage("§c" + (groupIDs.get(o - 1) + 1) + ".§7 " + activeGroups.get(o - 1).getDisplayName());
                            }
                        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                        }
                        o++;
                    } catch (NullPointerException localNullPointerException1) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                    }
                }
                int pages = activeGroups.size() / 15 + (activeGroups.size() % 15 == 0 ? 0 : 1);
                sender.sendMessage("§8>------- §7Page §a" + i + "§6 §7of §a" + pages + "§8 --------<");
            } catch (NumberFormatException nfe) {
                try {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't resolve number §4" + args[1]);
                } catch (ArrayIndexOutOfBoundsException aofe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe argument contains invalid characters!");
                }
            }
        }
        else {
            sender.sendMessage("§8>--------- §aGroup List§8 ---------<");
            for (int i = 1; 16 > i; i++) {
                if (( activeGroups.size() > 0) && (i <= activeGroups.size() )) {
                    sender.sendMessage("§c" + (groupIDs.get(i - 1) + 1) + ".§7 " + activeGroups.get(i - 1).getDisplayName());
                }
            }
            int pages = activeGroups.size() / 15 + (activeGroups.size() % 15 == 0 ? 0 : 1);
            sender.sendMessage("§8>------- §7Page §a" + 1 + "§6 §7of §a" + pages + "§8 --------<");
        }
    }

    public void GroupView(CommandSender sender, String[] args){
        // group view GroupName/ID
        if (args.length == 2) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.getGroup(index).hasMember(sender.getName(), true) || groupControl.hasEditPermission(index, sender)){
                    List<Member> now = groupControl.getGroup(index).getMembers();
                    try {
                        sender.sendMessage("§8>--------------------------------<");
                        sender.sendMessage("§7  Name: " + groupControl.getGroupDisplayName(index));
                        sender.sendMessage("§7  Tag: " + groupControl.getGroupDisplayTag(index));
                        if(groupControl.hasAdminPermission(sender)){
                            sender.sendMessage("§7  Op Tag: " + groupControl.getGroupOpDisplayTag(index));
                        }
                        sender.sendMessage("§7  Members: §e" + groupControl.getGroup(index).listLength());
                        sender.sendMessage("§8>--------------------------------<");

                        StringBuilder fullList = new StringBuilder();
                        fullList.append("   §7");
                        int rowCount = 0;

                        for (Member member : now) {
                            try {
                                if(rowCount == 4){
                                    rowCount = 0;
                                    fullList.append("\n   §7");
                                }

                                fullList.append(member.getDisplayName()).append("   §7");
                                rowCount++;
                            } catch (NullPointerException localNullPointerException1) {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                            }
                        }

                        sender.sendMessage(fullList.toString());
                        sender.sendMessage("§8>--------------------------------<");
                    } catch (NumberFormatException nfe) {
                        try {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't resolve number §4" + args[1]);
                        } catch (ArrayIndexOutOfBoundsException aofe) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe argument contains invalid characters!");
                        }
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You are not a member of that group.");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group view §eGroup_Name/ID");
        }
    }

    public void GroupHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §cGroup Help §8--------------|");
        sender.sendMessage("§8| §c/group create   §8>> §7Create a new group");
        sender.sendMessage("§8| §c/group tag         §8>> §7Change group tag");
        sender.sendMessage("§8| §c/group leave     §8>> §7Leave a group");
        sender.sendMessage("§8| §c/group swap      §8>> §7Swap member positions");
        sender.sendMessage("§8| §c/group add         §8>> §7Add a member");
        sender.sendMessage("§8| §c/group remove  §8>> §7Remove a member");
        sender.sendMessage("§8| §c/group rename  §8>> §7Rename an owned group");
        sender.sendMessage("§8| §c/group disband §8>> §7Disband an owned group");
        sender.sendMessage("§8| §c/group list         §8>> §7List all your groups");
        sender.sendMessage("§8| §c/group view       §8>> §7View group details");
        sender.sendMessage("§8| §c/group help       §8>> §7this help");
        if(groupControl.hasAdminPermission(sender)){
            sender.sendMessage("§8| §4/group admin     §8>> §7Admin Commands for Groups");
        }
        sender.sendMessage("§8|--------------------------------------|");
    }

    public void GroupID(CommandSender sender, String[] args){
        // group id GroupName
        if(groupControl.hasAdminPermission(sender)){
            if (args.length == 2) {
                String givenName = convertToRaw(args[0]).toLowerCase();
                if (groupControl.toString().toLowerCase().contains(givenName))
                {
                    for(int i = 0; i < groupControl.listLength(); i++){
                        if ((groupControl.getGroupName(i).toLowerCase()).startsWith(givenName)) {
                            sender.sendMessage("§6ID for §c" + groupControl.getGroupDisplayName(i) + " §6is §c" + (i+1));
                        }
                    }
                } else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't find Group§4 " + convertToRaw(args[0]));
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group id §eGroup_Name/ID");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
    }

    public void GroupClear(CommandSender sender, String[] args){
        // group clear
        if(groupControl.hasAdminPermission(sender)){
            if (args.length == 1) {
                deleteConfirm.put((sender).getName(), "ClearGroup");
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Warning: §cYou are about to delete all Groups");
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§410s §cto type §7/group confirmclear");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> deleteConfirm.remove((sender.getName())), 200);
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group clear");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
    }

    public void GroupAdmin(CommandSender sender){
        // group admin
        if(groupControl.hasAdminPermission(sender)){
            sender.sendMessage("§8|-------------- §4Group Admin §8--------------|");
            sender.sendMessage("§8| §c/group switch    §8>> §7Switch group IDs");
            sender.sendMessage("§8| §c/group optag     §8>> §7Switch a groups op tag");
            sender.sendMessage("§8| §c/group id            §8>> §7List all group IDs");
            sender.sendMessage("§8| §c/group clear     §8>> §7Delete all groups");
            sender.sendMessage("§8| §c/group admin     §8>> §7this admin help");
            sender.sendMessage("§8|---------------------------------------|");
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cYou do not have admin permissions");
        }
    }

}
