package kamkeel.RPGMessenger.Commands;

import kamkeel.RPGMessenger.NPC;
import kamkeel.RPGMessenger.Util.CommandDefault;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kamkeel.RPGMessenger.RPGCommands.*;
import static kamkeel.RPGMessenger.Util.ColorConvert.convertToRaw;

public class CommandRequest implements CommandDefault {


    // -------------------------------------------------| Request Commands
    public boolean RequestAdd(CommandSender sender, String[] args){
        if(args.length == 2){
            NPC tempNPC = new NPC(args[1]);
            if(!(npcControl.alreadyExists(tempNPC))) {
                if (!(requestControl.alreadyExists(tempNPC))) {
                    if (requestControl.npcAdd(tempNPC)) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "NPC §c" + requestControl.npcs.get(requestControl.listLength() - 1).getDisplayName() + "§7 with id §c" + requestControl.listLength() + "§7 added to requests");
                        return true;
                    } else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAn error occurred during the adding process.");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThat Request already exists.");

                }
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThat NPC already exists.");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/request add §bName");
        }
        return false;
    }
    public boolean RequestRemove(CommandSender sender,  String[] args){
        if(args.length == 2){
            String removeName = requestControl.npcRemove(args[1].toLowerCase());
            if(removeName != null){
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "NPC §c" + removeName + "§7 removed from Requests!");
                return true;
            }
            else{
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCould not find NPC §4" + convertToRaw(args[1]));
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/request remove §bName");
        }
        return false;
    }
    public boolean RequestRename(CommandSender sender, String[] args){
        if(args.length == 3){
            try {
                int index = Integer.parseInt(args[1]) - 1;
                if (requestControl.validIndex(index)){
                    String before = requestControl.getNPCDisplayName(index);
                    if(requestControl.renameNPC(index, args[2])){
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Changed: " + before + " §7-> " + requestControl.getNPCDisplayName(index) );
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
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/request rename §aID §bNew_Name");
        }
        return false;
    }
    public void RequestList(CommandSender sender, String[] args){
        if(args.length == 2){
            try {
                int i = Integer.parseInt(args[1]);
                sender.sendMessage("§8>--------- §6Request List§8 ---------<");

                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                    try {
                        try {
                            if (o <= requestControl.listLength()) {
                                sender.sendMessage("§c" + o + ".§7 " + requestControl.getNPCDisplayName(o - 1));
                            }
                        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                        }
                        o++;
                    } catch (NullPointerException localNullPointerException1) {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cList Out of Bounds");
                    }
                }
                int pages = requestControl.listLength() / 15 + (requestControl.listLength() % 15 == 0 ? 0 : 1);
                sender.sendMessage("§8>------- §7Page §6" + i + "§6 §7of §6" + pages + "§8 --------<");
            } catch (NumberFormatException nfe) {
                try {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCouldn't resolve number §4" + args[1]);
                } catch (ArrayIndexOutOfBoundsException aofe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe argument contains invalid characters!");
                }
            }
        }
        else {
            sender.sendMessage("§8>--------- §6Request List§8 ---------<");
            for (int i = 1; 16 > i; i++) {
                if (( requestControl.listLength() > 0) && (i <= requestControl.listLength() )) {
                    sender.sendMessage("§c" + i + ".§7 " + requestControl.getNPCDisplayName(i - 1));
                }
            }
            int pages = requestControl.listLength() / 15 + (requestControl.listLength() % 15 == 0 ? 0 : 1);
            sender.sendMessage("§8>------- §7Page §6" + 1 + "§6 §7of §6" + pages + "§8 --------<");
        }
    }
    public void RequestClear(CommandSender sender){
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
            requestConfirm.put((sender).getName(), "ClearRequest");
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Warning: §cYou are about to delete all Requests");
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§410s §cto type §7/request clearconfirm");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> requestConfirm.remove((sender.getName())), 200);
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG +  "§cYou do not have permission for this.");
        }
    }
    public boolean RequestConfirmClear(CommandSender sender){
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
            String disband = requestConfirm.get(sender.getName());
            if(disband != null){
                requestConfirm.remove(sender.getName());
                requestControl.npcs.clear();
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cRequests cleared!");
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
    public int RequestConfirm(CommandSender sender, String[] args){
        // request confirm Name/Num
        if(args.length == 2){
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = requestControl.npcIndex(args[1]);
            }

            boolean added = false;

            if(requestControl.validIndex(index)){
                NPC npc = requestControl.getNPC(index);
                if(!(npcControl.alreadyExists(npc))) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cAdding Request " + requestControl.getNPCDisplayName(index) + "§c to the NPC");
                    npcControl.npcAdd(npc);
                    added = true;
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe NPC: " + requestControl.getNPCDisplayName(index) + "§c already exists... Removing Request");
                }
                requestControl.npcRemove(npc.getName());
                if(added){
                    return 1;
                }
                return 0;
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cCould not find that request.");
            }
        }
        else{
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/request add §bName");
        }
        return -1;
    }
    public void RequestConfirmAll(CommandSender sender){
        // request confirmall
        int totalAdded = 0;
        int totalFailed = 0;

        for(NPC npc : requestControl.getNpcs()) {
            if(!(npcControl.alreadyExists(npc))) {
                npcControl.npcAdd(npc);
                totalAdded++;
            }
            else {
                totalFailed++;
            }
        }
        requestControl.npcs.clear();
        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cSuccess: §e" + totalAdded + "§c, §4Failed: §e" + totalFailed);
    }
    public void RequestHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §dRPG Help §8--------------|");
        sender.sendMessage("§8| §5/request add      §8>> §7Add a Request");
        sender.sendMessage("§8| §5/request remove    §8>> §7Remove a Request");
        sender.sendMessage("§8| §5/request list    §8>> §7List all Requests");
        sender.sendMessage("§8| §5/request rename    §8>> §7Rename a Request");
        if(AdminPermission(sender)) {
            sender.sendMessage("§8| §5/request confirm    §8>> §7Confirm a Request");
            sender.sendMessage("§8| §5/request all    §8>> §7Confirm all Requests");
            sender.sendMessage("§8| §5/request clear    §8>> §7Clear Requests");
        }
        sender.sendMessage("§8|--------------------------------------|");
    }
    // -------------------------------------------------|

    @Override
    public void runCMD(CommandSender sender, String label, String[] args) {
        boolean saveRequest = false;
        boolean saveNPC = false;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                RequestClear(sender);
            }
            else if (args[0].equalsIgnoreCase("confirmclear")) {
                saveRequest = RequestConfirmClear(sender);
            }
            else if (args[0].equalsIgnoreCase("add")) {
                saveRequest = RequestAdd(sender, args);
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                saveRequest = RequestRemove(sender, args);
            }
            else if (args[0].equalsIgnoreCase("rename")) {
                saveRequest = RequestRename(sender, args);
            }
            else if (args[0].equalsIgnoreCase("confirm")) {
                // -1: No --- 0: Save Request --- 1: Save NPCs
                int confirmNum = RequestConfirm(sender, args);
                if(confirmNum == 0){
                    saveRequest = true;
                }
                else if(confirmNum == 1){
                    saveRequest = true;
                    saveNPC = true;
                }
            }
            else if (args[0].equalsIgnoreCase("all")) {
                RequestConfirmAll(sender);
                saveRequest = true;
                saveNPC = true;
            }
            else if (args[0].equalsIgnoreCase("list")) {
                RequestList(sender, args);
            }
            else if(args[0].equalsIgnoreCase("help")){
                RequestHelp(sender);
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, check /request help.");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Invalid Syntax, check /request help.");
        }

        if(saveRequest){
            requestString.clear();
            for(NPC n: requestControl.getNpcs()){
                requestString.add(n.getSaveName());
            }
            requestConfig.getConfig().set("NPCs", requestString);
            requestConfig.saveConfig();
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
