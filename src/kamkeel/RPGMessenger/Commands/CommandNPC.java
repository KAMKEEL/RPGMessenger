package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.Group;
import kamkeel.RPGMessenger.Member;
import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.RPGCommands;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static kamkeel.RPGMessenger.RPGCommands.*;
import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class CommandNPC implements CommandDefault {

    // -------------------------------------------------| NPC Commands
    public void NpcID(CommandSender sender, String label, String[] args){
        if(args.length == 2 && !label.equalsIgnoreCase("npcid")){
            String givenName = convertToRaw(args[1]).toLowerCase();
            if (npcControl.toString().toLowerCase().contains(givenName))
            {
                for(int i = 0; i < npcControl.listLength(); i++){
                    if ((npcControl.getNPCName(i).toLowerCase()).startsWith(givenName)) {
                        sender.sendMessage("§6ID for §c" + npcControl.getNPCDisplayName(i) + " §6is §c" + (i+1));
                    }
                }
            } else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't find NPC§4 " + convertToRaw(args[1]));
            }
        }
        else if(args.length == 1 && label.equalsIgnoreCase("npcid")){
            String givenName = convertToRaw(args[0]).toLowerCase();
            if (npcControl.toString().toLowerCase().contains(givenName))
            {
                for(int i = 0; i < npcControl.listLength(); i++){
                    if ((npcControl.getNPCName(i).toLowerCase()).startsWith(givenName)) {
                        sender.sendMessage("§6ID for §c" + npcControl.getNPCDisplayName(i) + " §6is §c" + (i+1));
                    }
                }
            } else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't find NPC§4 " + convertToRaw(args[0]));
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npc id §bName §7or §c/npcid §bName ");
        }
    }
    public boolean NpcSwap(CommandSender sender, String[] args){
        if(args.length == 3){
            try {
                int first = Integer.parseInt(args[1]) - 1;
                try{
                    int second = Integer.parseInt(args[2]) - 1;
                    // Perform Swap
                    if (npcControl.npcSwap(first, second)){
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Swapped: " + npcControl.getNPCDisplayName(second) + " §7<-> " + npcControl.getNPCDisplayName(first)   );
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
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npc swap §aID ID");
        }
        return false;
    }
    public boolean NpcRename(CommandSender sender, String[] args){
        if(args.length == 3){
            try {
                int index = Integer.parseInt(args[1]) - 1;
                if (npcControl.validIndex(index)){
                    String before = npcControl.getNPCDisplayName(index);
                    if(npcControl.renameNPC(index, args[2])){
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Changed: " + before + " §7-> " + npcControl.getNPCDisplayName(index) );
                        return true;
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred during the renaming process.");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred using this ID. Please check if it is valid.");
                }
            } catch (NumberFormatException nfe) {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't parse number§4 " + args[1]);
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npc rename §aID §bNew_Name");
        }
        return false;
    }
    public boolean NpcAdd(CommandSender sender, String[] args){
        if(args.length == 2){
            NPC tempNPC = new NPC(args[1]);
            if(!(npcControl.alreadyExists(tempNPC))){
                if(npcControl.npcAdd(tempNPC)){
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "NPC §c" + npcControl.npcs.get(npcControl.listLength() - 1).getDisplayName() + "§7 with id §c" + npcControl.listLength() + "§7 added!");
                    return true;
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred during the adding process.");
                }
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThat NPC already exists.");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npc add §bName");
        }
        return false;
    }
    public boolean NpcRemove(CommandSender sender,  String[] args){
        if(args.length == 2){
            String removeName = npcControl.npcRemove(args[1].toLowerCase());
            if(removeName != null){
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "NPC §c" + removeName + "§7 removed!");
                return true;
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCould not find NPC §4" + convertToRaw(args[1]));
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npc remove §bName");
        }
        return false;
    }
    public void NpcList(CommandSender sender, String[] args){
        if(args.length == 2){
            try {
                int i = Integer.parseInt(args[1]);
                sender.sendMessage("§8>--------- §cNPC List§8 ---------<");

                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                    try {
                        try {
                            if (o <= npcControl.listLength()) {
                                sender.sendMessage("§c" + o + ".§7 " + npcControl.getNPCDisplayName(o - 1));
                            }
                        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                        }
                        o++;
                    } catch (NullPointerException localNullPointerException1) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                    }
                }
                int pages = npcControl.listLength() / 15 + (npcControl.listLength() % 15 == 0 ? 0 : 1);
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
            sender.sendMessage("§8>--------- §cNPC List§8 ---------<");
            for (int i = 1; 16 > i; i++) {
                if (( npcControl.listLength() > 0) && (i <= npcControl.listLength() )) {
                    sender.sendMessage("§c" + i + ".§7 " + npcControl.getNPCDisplayName(i - 1));
                }
            }
            int pages = npcControl.listLength() / 15 + (npcControl.listLength() % 15 == 0 ? 0 : 1);
            sender.sendMessage("§8>------- §7Page §c" + 1 + "§6 §7of §c" + pages + "§8 --------<");
        }
    }
    public void NpcClear(CommandSender sender){
        boolean allow = false;

        if (!(sender instanceof Player)) {
            allow = true;
        }
        else {
            if(sender.hasPermission("rpg.admin")){
                allow = true;
            }
        }

        if(allow) {
            deleteConfirm.put((sender).getName(), "ClearNPC");
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Warning: §cYou are about to delete all NPCs");
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§410s §cto type §7/npc confirmclear");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> deleteConfirm.remove((sender.getName())), 200);
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§cYou do not have permission for this.");
        }
    }
    public boolean NpcConfirm(CommandSender sender){
        boolean allow = false;

        if (!(sender instanceof Player)) {
            allow = true;
        }
        else {
            if(sender.hasPermission("rpg.admin")){
                allow = true;
            }
        }

        if(allow){
            String disband = deleteConfirm.get(sender.getName());
            if(disband != null){
                deleteConfirm.remove(sender.getName());
                npcControl.npcs.clear();
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cNPCs cleared!");
                return true;
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§4Time Ran Out");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§cYou do not have permission for this.");
        }
        return false;
    }
    public boolean NpcSort(CommandSender sender){

        // Insertion Sort
        int n = npcControl.listLength();
        for (int i = 1; i < n; ++i) {
            NPC key = npcControl.getNPC(i);
            int j = i - 1;

            while (j >= 0 && ((npcControl.getNPCName(j)).toLowerCase()).compareTo(key.getName().toLowerCase()) > 0 ){
                npcControl.npcs.set(j+1, npcControl.getNPC(j));
                j = j - 1;
            }
            npcControl.npcs.set(j + 1, key);
        }

        sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§cSorted all NPCs");
        return true;
    }
    public void NpcHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §aNPC Help §8--------------|");
        sender.sendMessage("§8| §2/npcid    §8>> §7Checks NPC IDs");
        sender.sendMessage("§8| §2/npc swap      §8>> §7Swap IDs between NPCs");
        sender.sendMessage("§8| §2/npc rename      §8>> §7Rename an NPC");
        sender.sendMessage("§8| §2/npc add      §8>> §7Create a new NPC");
        sender.sendMessage("§8| §2/npc remove      §8>> §7Delete an NPC");
        sender.sendMessage("§8| §2/npc list      §8>> §7List all permanent NPCs");
        sender.sendMessage("§8| §2/npc clear      §8>> §7Clear all permanent NPCs");
        sender.sendMessage("§8| §2/npc sort      §8>> §7Sort All NPCs");
        sender.sendMessage("§8|--------------------------------------|");
    }
    // -------------------------------------------------|

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        boolean saveNPC = false;
        if (args.length >= 1){
            if (args[0].equalsIgnoreCase("id") || label.equalsIgnoreCase("npcid")) {
                NpcID(sender, label, args);
            }
            else if (args[0].equalsIgnoreCase("swap")) {
                saveNPC = NpcSwap(sender, args);
            }
            else if (args[0].equalsIgnoreCase("rename")) {
                saveNPC = NpcRename(sender, args);
            }
            else if (args[0].equalsIgnoreCase("add")) {
                saveNPC = NpcAdd(sender, args);
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                saveNPC = NpcRemove(sender, args);
            }
            else if (args[0].equalsIgnoreCase("list")) {
                NpcList(sender, args);
            }
            else if (args[0].equalsIgnoreCase("clear")){
                NpcClear(sender);
            }
            else if (args[0].equalsIgnoreCase("confirmclear")){
                saveNPC = NpcConfirm(sender);
            }
            else if (args[0].equalsIgnoreCase("sort")){
                saveNPC = NpcSort(sender);
            }
            else if (args[0].equalsIgnoreCase("help")){
                NpcHelp(sender);
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, /npc help");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, /npc help");
        }
        if(saveNPC){
            npcString.clear();
            for(NPC n: npcControl.getNpcs()){
                npcString.add(n.getSaveName());
            }
            npcConfig.getConfig().set("NPCs", npcString);
            npcConfig.saveConfig();
        }
    }

}
