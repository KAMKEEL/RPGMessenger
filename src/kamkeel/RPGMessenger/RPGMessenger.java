package kamkeel.RPGMessenger;

import kamkeel.RPGMessenger.Configs.ConfigGroup;
import kamkeel.RPGMessenger.Configs.ConfigNPC;
import kamkeel.RPGMessenger.Control.GroupControl;
import kamkeel.RPGMessenger.Control.NPCControl;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;
import static kamkeel.RPGMessenger.Util.ColorConvert.convertToRaw;
import static kamkeel.RPGMessenger.Util.MessageUtil.*;
import static kamkeel.RPGMessenger.Util.RPGStringHelper.hasIllegalSymbols;

public class RPGMessenger extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener, org.bukkit.command.CommandExecutor
{

    // -------------------------------------------------| Hash Map Control
    // Type of Reply
    // String to Byte Map >> 0 Private, 1 Public, 2 Group
    public Map<String, Integer> replyType = new HashMap<>();
    // Reply Name Keeper
    public Map<String, String> reply = new HashMap<>();
    public Map<String, String> groupReply = new HashMap<>();
    public Map<String, String> groupNPC = new HashMap<>();
    // RPG Names
    public Map<String, NPC> roleNames = new HashMap<>();
    // Confirm Delete
    public Map<String, String> deleteConfirm = new HashMap<>();
    // -------------------------------------------------|

    // -------------------------------------------------| List Control
    // NPC Lists
    public NPCControl npcControl;
    public NPCControl requestControl;
    public NPCControl roleControl = new NPCControl(new java.util.ArrayList<>());
    public NPCControl tempControl = new NPCControl(new java.util.ArrayList<>());
    // Group Lists
    public GroupControl groupControl = new GroupControl();
    // -------------------------------------------------|

    // -------------------------------------------------| Config Control
    public List<String> npcString = new java.util.ArrayList<>();
    public List<String> requestString = new java.util.ArrayList<>();

    public ConfigNPC npcConfig;
    public ConfigNPC requestConfig;
    public ConfigGroup groupConfig;

    // -------------------------------------------------| Config Control
    public void loadConfigs(){

        // Process NPCs Config
        npcConfig.setup();
        npcString = npcConfig.getConfig().getStringList("NPCs");
        npcConfig.saveConfig();
        npcConfig.reloadConfig();
        this.npcControl = new NPCControl(npcString);

        // Process Requests Config
        requestConfig.setup();
        requestString = requestConfig.getConfig().getStringList("NPCs");
        requestConfig.saveConfig();
        requestConfig.reloadConfig();
        if(requestString != null){
            this.requestControl = new NPCControl(requestString);
        }
        else{
            this.requestControl = new NPCControl(new java.util.ArrayList<>());
        }

        // Process Group Configs
        groupConfig.createFolder();
        groupConfig.setup();
        groupConfig.saveGroupYaml();
        groupConfig.reloadGroupYaml();
        this.groupControl = groupConfig.loadGroups();
    }
    // Update Group
    public void updateGroupConfig(int index){
        groupConfig.updateGroup(index, groupControl.getGroup(index));
    }
    // -------------------------------------------------|

    // Find Player
    public Player findPlayer(String name){
        Player target = Bukkit.getPlayer(name);
        if(target == null){
            for (Player player : Bukkit.getServer().getOnlinePlayers()) { // Loop through players Names
                if ( (player.getName().toLowerCase()).startsWith( name.toLowerCase() )) {
                    return player;
                }
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) { // Loop through players Display Name
                if ( (convertToRawPlayer(player.getDisplayName()).toLowerCase()).startsWith( name.toLowerCase() )) {
                    return player;
                }
            }
        }

        return target;
    }

    // -------------------------------------------------| Reply Set
    // Set Reply Type
    public void setGroupReply(Group group, String who, boolean fromNPC){

        if(fromNPC){
            groupNPC.remove(group.getName());
            groupNPC.put(group.getName(), who);
        }

        for (Member member : group.getMembers()){
            if(member.getIsPlayer()){
                Player target = findPlayer(member.getName());
                if(target != null){
                    if(fromNPC){
                        reply.remove(target.getName());
                        reply.put(target.getName(), who);
                    }

                    replyType.remove(target.getName());
                    replyType.put(target.getName(), 2);

                    groupReply.remove(target.getName());
                    groupReply.put(target.getName(), group.getName());
                }
            }
        }
    }
    public void setReply(String sender, String target, int type){
        reply.remove(sender);
        replyType.remove(sender);

        reply.put(sender, target);
        replyType.put(sender, type);
    }
    // -------------------------------------------------|

    // -------------------------------------------------| Send Messages
    // Spy Message
    public void sendSpyMessage(CommandSender sender, int tag, boolean NPCSend, String from, String to, String chat){
        String spyMessage = getSpyFormat(tag, from, to, chat);

        for (Player player : getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) && (!((player.getName()).equalsIgnoreCase(sender.getName())) || NPCSend) && !(player.getDisplayName().equals(to))) {
                player.sendMessage(spyMessage);
            }
        }
    }
    public void sendSpyLocalMessage(CommandSender sender, boolean NPCSend, String playerName, String from, String chat){
        String spyMessage = getSpyLocalFormat(NPCSend, playerName, from, chat);

        for (Player player : getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) && (!((player.getName()).equalsIgnoreCase(sender.getName())) || NPCSend)) {
                player.sendMessage(spyMessage);
            }
        }
    }
    public void sendSpyGroupMessage(CommandSender sender, int group, int tag, boolean NPCSend, String groupOPTag, String from, String chat){
        String spyMessage = getSpyGroupFormat(tag, groupOPTag, from, chat);

        for (Player player : getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) &&
                    (!((player.getName()).equalsIgnoreCase(sender.getName())) || NPCSend) &&
                    !groupControl.getGroup(group).hasMember(player.getName(), true)) {
                player.sendMessage(spyMessage);
            }
        }
    }
    public void sendDebugMessage(CommandSender sender, String message){
        for (Player player : getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) && !((player.getName()).equalsIgnoreCase(sender.getName()))) {
                player.sendMessage(message);
            }
        }
    }
    // Group Messages
    public void sendGroupMessage(CommandSender sender, int index, String text){
        Group group = groupControl.getGroup(index);
        for (Member member : group.getMembers()){
            if(member.getIsPlayer() && !member.getName().equals(sender.getName())){
                Player target = findPlayer(member.getName());
                if(target != null){
                    target.sendMessage(text);
                }
            }
        }
    }
    // -------------------------------------------------|
    
    public RPGMessenger() {}

    public void onEnable() {
        System.out.println("------By Kam------");
        System.out.println("[RPG] V 1.0");
        System.out.println("------------------");
        getServer().getPluginManager().registerEvents(this, this);

        npcConfig = new ConfigNPC("npc");
        requestConfig = new ConfigNPC("request");
        groupConfig = new ConfigGroup();

        loadConfigs();

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        System.out.println("[RPG] Successfully Enabled!");
    }

    public void onDisable() {
        System.out.println("[RPG] Disabled!");
    }


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
        if(args.length == 1 && label.equalsIgnoreCase("npcid")){
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> deleteConfirm.remove((sender.getName())), 200);
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
    public void NpcHelp(CommandSender sender){
        sender.sendMessage("§8|-------------- §aNPC Help §8--------------|");
        sender.sendMessage("§8| §2/npcid    §8>> §7Checks NPC IDs");
        sender.sendMessage("§8| §2/npc swap      §8>> §7Swap IDs between NPCs");
        sender.sendMessage("§8| §2/npc rename      §8>> §7Rename an NPC");
        sender.sendMessage("§8| §2/npc add      §8>> §7Create a new NPC");
        sender.sendMessage("§8| §2/npc remove      §8>> §7Delete an NPC");
        sender.sendMessage("§8| §2/npc list      §8>> §7List all permanent NPCs");
        sender.sendMessage("§8| §2/npc clear      §8>> §7Clear all permanent NPCs");
        sender.sendMessage("§8|--------------------------------------|");
    }
    // -------------------------------------------------|

    // -------------------------------------------------| Group Commands
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
                            sendDebugMessage(sender, RPGStringHelper.GROUP + "§7Group: §c" + groupControl.groups.get(groupControl.listLength() - 1).getDisplayName() + "§7 with id §c" + groupControl.listLength() + "§7 created by " + ((Player)sender).getDisplayName());
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
                if(groupControl.hasEditPermission(index, sender, false)){
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
                if(groupControl.hasEditPermission(index, sender, false)){
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
    public boolean GroupOwner(CommandSender sender, String[] args){
        // group owner (0) Group (1) Name (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender, true)){
                    int memberID = groupControl.getGroup(index).getIndex(args[2], true);
                    if(memberID != -1){
                        Member member = groupControl.getGroup(index).getMember(memberID);
                        groupControl.getGroup(index).setOwner(member.getName());

                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You have lost ownership to " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                        sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + member.getDisplayName() + "§7 has been promoted to Owner for " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );
                        return true;
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4No player in group found");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have owner permissions to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group owner §eGroup_Name/ID §eMemberName");
        }
        return false;
    }
    public boolean GroupPromote(CommandSender sender, String[] args){
        // group owner (0) Group (1) Name (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender, true)){
                    int memberID = groupControl.getGroup(index).getIndex(args[2], true);
                    if(memberID != -1){
                        Member member = groupControl.getGroup(index).getMember(memberID);
                        if(member.getType() == 0){
                            if(groupControl.getGroup(index).PromoteMember(member.getName())){
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You have promoted " + member.getDisplayName() + " to Mod in " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                                sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + member.getDisplayName() + "§7 has been promoted to Mod in " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );
                                return true;
                            }
                            else {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error with Promoting Player");
                            }
                        }
                        else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot promote a Player with Owner or Mod");
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4No player in group found");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have owner permissions to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group promote §eGroup_Name/ID §eMemberName");        }
        return false;
    }
    public boolean GroupDemote(CommandSender sender, String[] args){
        // group owner (0) Group (1) Name (2)
        if (args.length == 3) {
            int index;
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException iobe) {
                index = groupControl.groupIndex(args[1]);
            }
            if (index > -1 && groupControl.validIndex(index)) {
                if(groupControl.hasEditPermission(index, sender, true)){
                    int memberID = groupControl.getGroup(index).getIndex(args[2], true);
                    if(memberID != -1){
                        Member member = groupControl.getGroup(index).getMember(memberID);
                        if(member.getType() == 1){
                            if(groupControl.getGroup(index).DemoteMember(member.getName())){
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You have demoted " + member.getDisplayName() + " to Mod in " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                                sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + member.getDisplayName() + "§7 has been demoted to Member in " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );
                            }
                            else {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error with Demoting Player");
                            }
                        }
                        else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot promote a Player without Mod or the Owner");
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4No player in group found");
                    }
                }
                else{
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not have owner permissions to edit this group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
            }
        }
        else {
            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "Invalid Syntax, check /group promote §eGroup_Name/ID §eMemberName");        }
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
                if(groupControl.hasEditPermission(index, sender, false)){
                    String member = args[2];

                    Player target = findPlayer(member);

                    if(target != null){
                        if(target.getName().equals(sender.getName())){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot add yourself to your own group.");
                        }
                        else if(groupControl.getGroup(index).addPlayer(target)){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Added §cPlayer: " + target.getDisplayName() + "§7 to " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                            target.sendMessage(RPGStringHelper.COLOR_TAG + "§6You"+ "§7 have been added to " + "§7Group: §c" + groupControl.getGroupDisplayName(index));

                            sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + target.getDisplayName() + "§7 has been added to " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );

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

                                sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + npcControl.getNPCDisplayName(findNPC) + "§7 has been added to " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );

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
                if(groupControl.hasEditPermission(index, sender, false)){
                    String member = args[2];

                    int getMemberIndex = groupControl.getGroup(index).getIndex(member, true);
                    if(getMemberIndex == -1) {
                        getMemberIndex = groupControl.getGroup(index).getIndex(member, false);
                    }

                    if(getMemberIndex != -1) {
                        boolean remove = false;
                        Member removedMember = groupControl.getGroup(index).getMember(getMemberIndex);
                        if(removedMember.getType() == 2){
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You cannot remove the Owner from a Group");
                        }
                        else if(removedMember.getType() == 1){
                            if(groupControl.hasAdminPermission(sender)){
                                remove = true;
                            }
                            else if(groupControl.getGroup(index).isGroupOwner((Player) sender)){
                                remove = true;
                            }
                            else {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Only the Owner can remove a Mod");
                            }
                        }
                        else {
                            remove = true;
                        }

                        if(remove){
                            if(removedMember.getIsPlayer()){
                                Player target = findPlayer(removedMember.getName());
                                if(groupControl.getGroup(index).removeMember(removedMember.getName(), true)){
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Removed §cPlayer: " + removedMember.getDisplayName() + "§7 from " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                                    if(target != null){
                                        target.sendMessage(RPGStringHelper.COLOR_TAG + "§6You"+ "§7 have been removed from " + "§7Group: §c" + groupControl.getGroupDisplayName(index));
                                    }

                                    updateGroupConfig(index);
                                    sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + removedMember.getDisplayName() + "§7 has been removed from " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );
                                    return true;
                                }
                                else{
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Attempting to remove §cPlayer: " + removedMember.getDisplayName() + "§4, but they are not in the group.");
                                }
                            }
                            else{
                                if(groupControl.getGroup(index).removeMember(removedMember.getName(), false)){
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§7Removed §2NPC: " + removedMember.getDisplayName() + "§7 from " + "§7Group: §c" + groupControl.getGroupDisplayName(index));

                                    updateGroupConfig(index);
                                    sendGroupMessage(sender, index,RPGStringHelper.COLOR_TAG + removedMember.getDisplayName() + "§7 has been removed from " + "§7Group: §c" + groupControl.getGroupDisplayName(index) );
                                    return true;
                                }
                                else{
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Attempting to remove §2NPC: " + removedMember.getDisplayName() + "§4, but they are not in the group.");
                                }
                            }
                        }
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You could not find that member in the group.");
                    }
                }
                else {
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
                if(groupControl.hasEditPermission(index, sender, true)){
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
                    if(groupControl.hasEditPermission(index, sender, true)){
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

                            groupConfig.groupSwap(first, second);
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
                if(groupControl.hasEditPermission(index, sender, true)){
                    deleteConfirm.put((sender).getName(), groupControl.groups.get(index).getName());
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Warning: §cYou are about to disband group: " + groupControl.groups.get(index).getDisplayName());
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§410s §cto type §7/group confirmdisband");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> deleteConfirm.remove((sender.getName())), 200);
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
                if( groupControl.getGroup(z).hasMember(sender.getName(), true) || groupControl.hasEditPermission(z, sender,false)){
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
                if(groupControl.getGroup(index).hasMember(sender.getName(), true) || groupControl.hasEditPermission(index, sender, false)){
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
                                if(rowCount == 5){
                                    rowCount = 0;
                                    fullList.append("\n   §7");
                                }
                                for(int i = 0; i < member.getType(); i++){
                                    fullList.append("§f*");
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
        sender.sendMessage("§8| §c/group owner  §8>> §7Transfer Ownership to a Player");
        sender.sendMessage("§8| §c/group promote  §8>> §7Promote a member to Mod");
        sender.sendMessage("§8| §c/group demote  §8>> §7Demote a Mod to Member");
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> deleteConfirm.remove((sender.getName())), 200);
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
    // -------------------------------------------------|

    // -------------------------------------------------| Role Commands
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
    // -------------------------------------------------|

    // -------------------------------------------------| Temp Commands
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

                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        tempControl.npcRemove(args[0]);
                    }
                }, 1200 * getConfig().getInt("Time"));

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
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                    tempControl.npcRemove(args[0]);
                }
            }, 1200 * getConfig().getInt("Time"));

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
        sender.sendMessage("§8| §e/tmpmsg         §8>> §7Send a MSG as a Temporary NPC");
        sender.sendMessage("§8| §e/tmpsay         §8>> §7Send a CHAT as a Temporary NPC");
        sender.sendMessage("§8| §e/tmplist      §8>> §7List All Temporary NPCs");
        sender.sendMessage("§8|--------------------------------------|");
    }
    // -------------------------------------------------|
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("role")) {
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
        else if (cmd.getName().equalsIgnoreCase("tmp")) {
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
        else if (cmd.getName().equalsIgnoreCase("npc")) {
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
        else if (cmd.getName().equalsIgnoreCase("group")) {
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
                else if(args[0].equalsIgnoreCase("owner")){
                    saveConfig = GroupOwner(sender, args);
                }
                else if(args[0].equalsIgnoreCase("promote")){
                    saveConfig = GroupPromote(sender, args);
                }
                else if(args[0].equalsIgnoreCase("demote")){
                    saveConfig = GroupDemote(sender, args);
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

        else if (cmd.getName().equalsIgnoreCase("msg")) {
            if (args.length > 1) {

                Player target = findPlayer(args[0]);
                String allArgs;

                // If Target = Player: Type 0
                if (target != null) {

                    allArgs = layoutString(1, args);

                    // Console is Sender
                    if(!(sender instanceof Player)) {
                        formMessage(sender, target.getDisplayName(), false, allArgs);
                        sendSpyMessage(sender, 0, false, "§4§oCON", target.getDisplayName(), allArgs);
                        formMessage(target, "§4§oCON", true, allArgs);
                    }
                    else {
                        // Console Message
                        Bukkit.getConsoleSender().sendMessage(spyTags[0] + "§8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + allArgs);

                        formMessage(sender, target.getDisplayName(), false, allArgs);
                        sendSpyMessage(sender, 0, false, ((Player) sender).getDisplayName(), target.getDisplayName(), allArgs);
                        formMessage(target, ((Player) sender).getDisplayName(), true, allArgs);
                    }

                    setReply(target.getName(), sender.getName(), 0);
                    setReply(sender.getName(), target.getName(), 0);
                }
                // No Target -> NPC: Type 1
                else if (roleControl.toString().toLowerCase().contains( convertToRaw(args[0]).toLowerCase()) ) {

                    allArgs = layoutString(1, args);

                    int index = roleControl.npcIndex(convertToRaw(args[0]).toLowerCase());
                    if(index > -1){
                        NPC fms = roleControl.getNPC(index);

                        // Console is Sender
                        if(!(sender instanceof Player)) {
                            formMessage(sender, fms.getDisplayName(), false, allArgs);
                            sendSpyMessage(sender, 2, false, "§4§oCON", fms.getDisplayName(), allArgs);
                        }
                        else{
                            // Console Message
                            Bukkit.getConsoleSender().sendMessage(spyTags[2] + "§8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + fms.getDisplayName() + "§8] §r" + allArgs);

                            formMessage(sender, fms.getDisplayName(), false, allArgs);
                            sendSpyMessage(sender, 2, false, ((Player) sender).getDisplayName(), fms.getDisplayName(), allArgs);
                        }

                        setReply(sender.getName(), fms.getDisplayName(), 0);
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find NPC!");
                    }
                }
                else if (npcControl.toString().toLowerCase().contains( convertToRaw(args[0]).toLowerCase()) ) {

                    allArgs = layoutString(1, args);

                    int index = npcControl.npcIndex(convertToRaw(args[0]).toLowerCase());
                    if(index > -1){
                        NPC npc = npcControl.getNPC(index);

                        // Console is Sender
                        if(!(sender instanceof Player)) {
                            formMessage(sender, npc.getDisplayName(), false, allArgs);
                            sendSpyMessage(sender, 2, false, "§4§oCON", npc.getDisplayName(), allArgs);
                        }
                        else{
                            // Console Message
                            Bukkit.getConsoleSender().sendMessage(spyTags[2] + "§8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + npc.getDisplayName() + "§8] §r" + allArgs);

                            formMessage(sender, npc.getDisplayName(), false, allArgs);
                            sendSpyMessage(sender, 2, false, ((Player) sender).getDisplayName(),npc.getDisplayName(), allArgs);
                        }

                        setReply(sender.getName(), npc.getDisplayName(), 0);
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find NPC!");
                    }
                }
                else if (tempControl.toString().toLowerCase().contains( convertToRaw(args[0]).toLowerCase()) ) {

                    allArgs = layoutString(1, args);

                    int index = tempControl.npcIndex(convertToRaw(args[0]).toLowerCase());
                    if(index > -1){
                        NPC npc = tempControl.getNPC(index);

                        // Console is Sender
                        if(!(sender instanceof Player)) {
                            formMessage(sender, npc.getDisplayName(), false, allArgs);
                            sendSpyMessage(sender, 2, false, "§4§oCON", npc.getDisplayName(), allArgs);
                        }
                        else{
                            // Console Message
                            Bukkit.getConsoleSender().sendMessage(spyTags[2] + "§8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + npc.getDisplayName() + "§8] §r" + allArgs);

                            formMessage(sender, npc.getDisplayName(), false, allArgs);
                            sendSpyMessage(sender, 2, false, ((Player) sender).getDisplayName(),npc.getDisplayName(), allArgs);
                        }

                        setReply(sender.getName(), npc.getDisplayName(), 0);
                    }
                    else{
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find NPC!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find Player or NPC!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "/" + label + " <player/npc> <message>");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("pmsg")) {
            // /pmsg chat
            if (args.length > 0) {

                String allArgs = layoutString(0, args);

                // Console is Sender
                if(!(sender instanceof Player)) {
                    formLocalMessage(sender, "§4§oCON", false, allArgs);
                    sendSpyLocalMessage(sender, false, "§4§oCON", "§4§oCON", allArgs);
                }
                else{
                    // Console Message
                    Bukkit.getConsoleSender().sendMessage(getSpyLocalFormat(false, ((Player) sender).getDisplayName(), ((Player) sender).getDisplayName(), allArgs));

                    formLocalMessage(sender, ((Player) sender).getDisplayName(), false, allArgs);
                    sendSpyLocalMessage(sender, false, ((Player) sender).getDisplayName(), ((Player) sender).getDisplayName(), allArgs);
                }

                replyType.remove(sender.getName());
                replyType.put(sender.getName(), 1);
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "/" + label + " <message>");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("gmsg")) {
            if (args.length > 1) {

                int index;
                try {
                    index = Integer.parseInt(args[0]) - 1;
                } catch (NumberFormatException iobe) {
                    index = groupControl.groupIndex(args[0]);
                }
                if (index > -1 && groupControl.validIndex(index)) {
                    if(groupControl.hasAdminPermission(sender) || groupControl.getGroup(index).hasMember(sender.getName(), true)){
                        String allArgs = layoutString(1, args);

                        // Console is Sender
                        if(!(sender instanceof Player)) {
                            sender.sendMessage(formGroupMessage("§4§oCON", groupControl.getGroupDisplayTag(index), false, allArgs));
                            sendGroupMessage(sender, index, formGroupMessage("§4§oCON", groupControl.getGroupDisplayTag(index), true, allArgs));

                            sendSpyGroupMessage(sender, index, 5, false, groupControl.getGroupOpDisplayTag(index), "§4§oCON", allArgs);

                        }
                        else{
                            // Console Message
                            Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(2, groupControl.getGroupOpDisplayTag(index), ((Player)sender).getDisplayName(), allArgs));

                            sender.sendMessage(formGroupMessage(((Player)sender).getDisplayName(), groupControl.getGroupDisplayTag(index), false, allArgs));
                            sendGroupMessage(sender, index, formGroupMessage(((Player)sender).getDisplayName(), groupControl.getGroupDisplayTag(index), true, allArgs));

                            sendSpyGroupMessage(sender, index, 2, false, groupControl.getGroupOpDisplayTag(index), ((Player)sender).getDisplayName(), allArgs);

                        }

                        setGroupReply(groupControl.getGroup(index), sender.getName(), false);
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You are not a member of that group.");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "/" + label + " <group/groupid> <message>");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("reply")) {
            if (args.length > 0) {
                String allArgs;
                try {
                    if(replyType.containsKey(sender.getName())){
                        allArgs = layoutString(0, args);
                        int type = replyType.get(sender.getName());
                        // Type = 2 : GMSG
                        if(type == 2){
                            if(groupReply.containsKey(sender.getName())) {
                                int index = groupControl.groupIndex(groupReply.get(sender.getName()));
                                if (index > -1 && groupControl.validIndex(index)) {
                                    if(groupControl.hasAdminPermission(sender) || groupControl.getGroup(index).hasMember(sender.getName(), true)){

                                        // Console is Sender
                                        if(!(sender instanceof Player)) {
                                            sender.sendMessage(formGroupMessage("§4§oCON", groupControl.getGroupDisplayTag(index), false, allArgs));
                                            sendGroupMessage(sender, index, formGroupMessage("§4§oCON", groupControl.getGroupDisplayTag(index), true, allArgs));

                                            sendSpyGroupMessage(sender, index, 5, false, groupControl.getGroupOpDisplayTag(index), "§4§oCON", allArgs);

                                        }
                                        else{
                                            // Console Message
                                            Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(2, groupControl.getGroupOpDisplayTag(index), ((Player)sender).getDisplayName(), allArgs));

                                            sender.sendMessage(formGroupMessage(((Player)sender).getDisplayName(), groupControl.getGroupDisplayTag(index), false, allArgs));
                                            sendGroupMessage(sender, index, formGroupMessage(((Player)sender).getDisplayName(), groupControl.getGroupDisplayTag(index), true, allArgs));

                                            sendSpyGroupMessage(sender, index, 2, false, groupControl.getGroupOpDisplayTag(index), ((Player)sender).getDisplayName(), allArgs);

                                        }

                                        setGroupReply(groupControl.getGroup(index), sender.getName(), false);
                                    }
                                    else {
                                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You are not a member of that group.");
                                    }
                                }
                                else {
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group to reply to!");
                                }
                            }
                            else {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You don't have a recent group to reply to");
                            }
                        }
                        // Type = 1 : PMSG
                        else if(type == 1){
                            // Console is Sender
                            if(!(sender instanceof Player)) {
                                formLocalMessage(sender, "§4§oCON", false, allArgs);
                                sendSpyLocalMessage(sender, false, "§4§oCON", "§4§oCON", allArgs);
                            }
                            else{
                                // Console Message
                                Bukkit.getConsoleSender().sendMessage(getSpyLocalFormat(false, ((Player) sender).getDisplayName(), ((Player) sender).getDisplayName(), allArgs));

                                formLocalMessage(sender, ((Player) sender).getDisplayName(), false, allArgs);
                                sendSpyLocalMessage(sender, false, ((Player) sender).getDisplayName(), ((Player) sender).getDisplayName(), allArgs);
                            }
                        }
                        // Type = 0 : MSG
                        else{
                            if (reply.containsKey(sender.getName())) {

                                String senderName;
                                // If Console
                                if(!(sender instanceof Player)) {
                                    senderName = "§4§oCON";
                                }
                                else{
                                    senderName = ((Player)sender).getDisplayName();
                                }

                                Player target = findPlayer(reply.get(sender.getName()));
                                String baseName = convertToRaw(convertToRawPlayer(reply.get(sender.getName()))).toLowerCase();

                                if(target != null){
                                    if(sender instanceof Player){
                                        Bukkit.getConsoleSender().sendMessage(getSpyFormat(0, senderName, target.getDisplayName(), allArgs));
                                    }

                                    formMessage(sender, target.getDisplayName(), false, allArgs);
                                    sendSpyMessage(sender, 0, false, senderName, target.getDisplayName(), allArgs);
                                    formMessage(target, senderName, true, allArgs);

                                    reply.remove(sender.getName());
                                    reply.put(sender.getName(), target.getName());

                                    reply.remove(target.getName());
                                    reply.put(target.getName(), sender.getName());
                                }
                                else if(   roleControl.toString().toLowerCase().contains(baseName) ||
                                        npcControl.toString().toLowerCase().contains(baseName)  ||
                                        tempControl.toString().toLowerCase().contains(baseName) ){

                                    if(sender instanceof Player){
                                        Bukkit.getConsoleSender().sendMessage(getSpyFormat(2, senderName, convertSpace(convertColor(reply.get(sender.getName()))), allArgs));
                                    }

                                    sendSpyMessage(sender, 2, false, senderName, convertSpace(convertColor(reply.get(sender.getName()))), allArgs);

                                    formMessage(sender, reply.get(sender.getName()), false, allArgs);

                                    String npcName = reply.get(sender.getName());

                                    reply.remove(sender.getName());
                                    reply.put(sender.getName(), npcName);
                                }
                                else if(reply.get(sender.getName()).equals("CONSOLE")){
                                    formMessage(sender, "§4§oCON", false, allArgs);
                                    sendSpyMessage(sender, 0, false, senderName, "§4§oCON", allArgs);
                                    Bukkit.getConsoleSender().sendMessage("§8[§c< §6" + senderName + "§8] §r"  + allArgs);

                                    reply.remove(sender.getName());
                                    reply.put(sender.getName(), "CONSOLE");

                                    reply.remove("CONSOLE");
                                    reply.put("CONSOLE", sender.getName());
                                }
                                else{
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cThe person to reply to is no longer available.");
                                }
                            }
                            else {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cError: §4There is no private message to reply to");
                            }
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cError: §4There is no one to whom you can reply");
                    }
                } catch (NullPointerException npe) {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "The player you are trying to reply to has left the game!");
                }
            }
            else {
                sender.sendMessage("Quickly reply to a private message");
                sender.sendMessage("Usage: /" + label + " <message>");
            }
        }

        else if (cmd.getName().equalsIgnoreCase("npcmsg")) {
            if(label.equalsIgnoreCase("nr") || label.equalsIgnoreCase("npcreply") ||
                    label.equalsIgnoreCase("npcr")){
                // /nr Player (0) Msg (1)
                if (args.length > 1) {
                    Player target = findPlayer(args[0]);
                    String allArgs;

                    if(target != null){
                        try {
                            if (reply.containsKey(target.getName()))
                            {

                                allArgs = layoutString(1, args);
                                String npcName = reply.get(target.getName());
                                String baseName = convertToRaw(convertToRawPlayer(npcName)).toLowerCase();

                                if (reply.get(target.getName()).equals("CONSOLE")) {
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cYou cannot reply as CONSOLE");
                                }
                                else if(    roleControl.toString().toLowerCase().contains(baseName) ||
                                        npcControl.toString().toLowerCase().contains(baseName)  ||
                                        tempControl.toString().toLowerCase().contains(baseName) ){

                                    if(replyType.containsKey(target.getName())){
                                        // Type = 2 [GMSG]
                                        if(replyType.get(target.getName()) == 2){
                                            int groupIndex = groupControl.groupIndex(groupReply.get(target.getName()));
                                            if(groupControl.validIndex(groupIndex)){
                                                if(groupControl.getGroup(groupIndex).hasMember(target.getName(), true)){

                                                    Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(2, groupControl.getGroupOpDisplayTag(groupIndex), reply.get(target.getName()), allArgs));
                                                    sendGroupMessage(sender, groupIndex, formGroupMessage(reply.get(target.getName()), groupControl.getGroupDisplayTag(groupIndex), true, allArgs));
                                                    sendSpyGroupMessage(sender, groupIndex, 2, true, groupControl.getGroupOpDisplayTag(groupIndex), reply.get(target.getName()), allArgs);

                                                    setGroupReply(groupControl.getGroup(groupIndex), reply.get(target.getName()), true);
                                                }
                                                else {
                                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cThat Player is no longer part of that group");
                                                }
                                            }
                                            else{
                                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cThat group no longer exists");
                                            }
                                        }
                                        // Type = 0 [PMSG]
                                        else if(replyType.get(target.getName()) == 1){
                                            Bukkit.getConsoleSender().sendMessage(getSpyLocalFormat(true, target.getDisplayName(), convertSpace(convertColor(reply.get(target.getName()))), allArgs));
                                            sendSpyLocalMessage(sender, true, target.getDisplayName(), convertSpace(convertColor(reply.get(target.getName()))), allArgs);

                                            formLocalMessage(target, reply.get(target.getName()), true, allArgs);
                                        }
                                        // Type = 0 [MSG]
                                        else {
                                            Bukkit.getConsoleSender().sendMessage(getSpyFormat(1, convertSpace(convertColor(reply.get(target.getName()))), target.getDisplayName(), allArgs));
                                            sendSpyMessage(sender, 1, true, convertSpace(convertColor(reply.get(target.getName()))), target.getDisplayName(), allArgs);

                                            formMessage(target, reply.get(target.getName()), true, allArgs);
                                        }
                                    }
                                    else {
                                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cThat player does not have a reply type");
                                    }
                                }
                                else {
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cYou cannot reply as another player");
                                }
                            }
                            else {
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cError: §4There is no one to whom they can reply");
                            }
                        } catch (NullPointerException npe) {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + ChatColor.DARK_RED + "The player you are trying to reply to has left the game!");
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§cError:§4 Couldn't find that player!");
                    }
                }
                else {
                    sender.sendMessage("Quickly reply using an NPC in a private message");
                    sender.sendMessage("Usage: /" + label + " <PlayerName>" +  " <message>");
                }
            }
            else if(label.equalsIgnoreCase("ns") || label.equalsIgnoreCase("npcsay") ||
                    label.equalsIgnoreCase("npcs")){
                // npcsay NPC/ID (0) chat (1)
                if (args.length > 1) {

                    int index;
                    try {
                        index = Integer.parseInt(args[0]) - 1;
                    } catch (NumberFormatException iobe) {
                        index = npcControl.npcIndex(args[0]);
                    }

                    if (npcControl.validIndex(index)) {
                        NPC npc = npcControl.getNPC(index);
                        String allArgs = layoutString(1, args);

                        formPublicMessage(npc.getDisplayName(), allArgs);
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                    }
                }
                else {
                    sender.sendMessage("Speak using an NPC");
                    sender.sendMessage("Usage: /" + label + " <NPCName/ID>" +  " <message>");
                }
            }
            else{
                // npcmsg NPC/ID (0) Player (1) chat (2)
                if (args.length > 2) {

                    int index;
                    try {
                        index = Integer.parseInt(args[0]) - 1;
                    } catch (NumberFormatException iobe) {
                        index = npcControl.npcIndex(args[0]);
                    }

                    if (npcControl.validIndex(index)) {
                        Player target = findPlayer(args[1]);
                        if (target != null) {
                            NPC npc = npcControl.getNPC(index);
                            String allArgs = layoutString(2, args);

                            Bukkit.getConsoleSender().sendMessage(getSpyFormat(1, npc.getDisplayName(), target.getDisplayName(), allArgs));
                            formMessage(target, npc.getDisplayName(), true, allArgs);
                            sendSpyMessage(sender, 1, true, npc.getDisplayName(), target.getDisplayName(), allArgs);

                            setReply(target.getName(), npc.getDisplayName(), 0);
                        } else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find player!");
                        }
                    } else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npcmsg §6ID/Name §ePlayer §7Message");
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("localmsg")) {
            // /localmsg NPC (0) Player (1) Msg (2)
            if (args.length > 2) {

                int index;
                try {
                    index = Integer.parseInt(args[0]) - 1;
                } catch (NumberFormatException iobe) {
                    index = npcControl.npcIndex(args[0]);
                }

                if (npcControl.validIndex(index)) {
                    Player target = findPlayer(args[1]);
                    if (target != null) {
                        NPC npc = npcControl.getNPC(index);
                        String allArgs = layoutString(2, args);

                        Bukkit.getConsoleSender().sendMessage(getSpyLocalFormat(true, target.getDisplayName(), npcControl.getNPCDisplayName(index), allArgs));
                        sendSpyLocalMessage(sender, true, target.getDisplayName(), npcControl.getNPCDisplayName(index), allArgs);

                        formLocalMessage(target, npcControl.getNPCDisplayName(index), true, allArgs);

                        setReply(target.getName(), npcControl.getNPCDisplayName(index), 1);
                    } else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find player!");
                    }
                } else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/localmsg §6ID/Name §ePlayer §7Message");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("groupmsg")) {
            if(label.equalsIgnoreCase("gr") || label.equalsIgnoreCase("groupreply") || label.equalsIgnoreCase("groupr")){
                // /gr GroupName (0) chat (1)
                if (args.length > 1) {

                    int index;
                    try {
                        index = Integer.parseInt(args[0]) - 1;
                    } catch (NumberFormatException iobe) {
                        index = groupControl.groupIndex(args[0]);
                    }
                    if (index > -1 && groupControl.validIndex(index)) {
                        if(groupControl.hasAdminPermission(sender)){
                            String allArgs;
                            if(groupNPC.containsKey(groupControl.getGroupName(index))){

                                allArgs = layoutString(1, args);
                                String groupReplyName = groupNPC.get(groupControl.getGroupName(index));
                                String baseName = convertToRaw(convertToRawPlayer(groupReplyName)).toLowerCase();

                                if (groupReplyName.equals("CONSOLE")) {
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cYou cannot reply as CONSOLE");
                                }
                                else if(    roleControl.toString().toLowerCase().contains(baseName) ||
                                        npcControl.toString().toLowerCase().contains(baseName)  ||
                                        tempControl.toString().toLowerCase().contains(baseName) ){

                                    Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(1, groupControl.getGroupOpDisplayTag(index), groupReplyName, allArgs));
                                    sendGroupMessage(sender, index, formGroupMessage(groupReplyName, groupControl.getGroupDisplayTag(index), true, allArgs));
                                    sendSpyGroupMessage(sender, index, 1, true, groupControl.getGroupOpDisplayTag(index), groupReplyName, allArgs);

                                    setGroupReply(groupControl.getGroup(index), groupReplyName,  true);
                                }
                                else {
                                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cYou cannot reply as another player");
                                }
                            }
                            else{
                                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Error: §cGroup does not have a recent NPC");
                            }
                        }
                        else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4You do not admin permissions");
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "/" + label + " <group/groupid> <message>");
                }
            }
            else{
                // groupmsg NPC/ID (0) GroupName (1) chat (2)
                if (args.length > 2) {

                    int npcIndex;
                    try {
                        npcIndex = Integer.parseInt(args[0]) - 1;
                    } catch (NumberFormatException iobe) {
                        npcIndex = npcControl.npcIndex(args[0]);
                    }

                    if (npcIndex > -1 && npcControl.validIndex(npcIndex)) {

                        int groupIndex;
                        try {
                            groupIndex = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException iobe) {
                            groupIndex = groupControl.groupIndex(args[1]);
                        }

                        if (groupIndex > -1 && groupControl.validIndex(groupIndex)) {

                            NPC npc = npcControl.getNPC(npcIndex);
                            String allArgs = layoutString(2, args);

                            Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(1, groupControl.getGroupOpDisplayTag(groupIndex), npc.getDisplayName(), allArgs));
                            sendGroupMessage(sender, groupIndex, formGroupMessage(npc.getDisplayName(), groupControl.getGroupDisplayTag(groupIndex), true, allArgs));
                            sendSpyGroupMessage(sender, groupIndex, 1, true, groupControl.getGroupOpDisplayTag(groupIndex), npc.getDisplayName(), allArgs);

                            setGroupReply(groupControl.getGroup(groupIndex), npc.getDisplayName(),  true);
                        }
                        else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
                        }
                    } else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/groupmsg §6ID/Name §dGroup §7Message");
                }
            }
        }
        return true;
    }
}


