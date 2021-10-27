package kamkeel.RPGMessenger;

import kamkeel.RPGMessenger.Commands.*;
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

public class RPGCommands {

    public static final RPGMessenger plugin = RPGMessenger.getPlugin(RPGMessenger.class);

    private final CommandGroup groupCMD = new CommandGroup();
    private final CommandNPC npcCMD = new CommandNPC();
    private final CommandRole roleCMD = new CommandRole();
    private final CommandTemp tempCMD = new CommandTemp();
    private final CommandRPG rpgCMD = new CommandRPG();
    private final CommandRequest requestCMD = new CommandRequest();
    private final CommandAction actionCMD = new CommandAction();

    // -------------------------------------------------| Hash Map Control
    // Type of Reply
    // String to Byte Map >> 0 Private, 1 Public, 2 Group
    public static Map<String, Integer> replyType = new HashMap<>();
    // Reply Name Keeper
    public static Map<String, String> reply = new HashMap<>();
    public static Map<String, String> groupReply = new HashMap<>();
    public static Map<String, String> groupNPC = new HashMap<>();
    // RPG Names
    public static Map<String, NPC> roleNames = new HashMap<>();
    // Confirm Delete
    public static Map<String, String> deleteConfirm = new HashMap<>();
    public static Map<String, String> requestConfirm = new HashMap<>();
    // -------------------------------------------------|

    // -------------------------------------------------| List Control
    // NPC Lists
    public static NPCControl npcControl;
    public static NPCControl requestControl;
    public static NPCControl roleControl = new NPCControl(new java.util.ArrayList<>());
    public static NPCControl tempControl = new NPCControl(new java.util.ArrayList<>());
    // Group Lists
    public static GroupControl groupControl = new GroupControl();
    // -------------------------------------------------|

    // -------------------------------------------------| Config Control
    public static List<String> npcString = new java.util.ArrayList<>();
    public static List<String> requestString = new java.util.ArrayList<>();

    public static ConfigNPC npcConfig;
    public static ConfigNPC requestConfig;
    public static ConfigGroup groupConfig;

    // -------------------------------------------------| Config Control
    public static void loadConfigs(){

        // Process NPCs Config
        npcConfig.setup();
        npcString = npcConfig.getConfig().getStringList("NPCs");
        npcConfig.saveConfig();
        npcConfig.reloadConfig();
        npcControl = new NPCControl(npcString);

        // Process Requests Config
        requestConfig.setup();
        requestString = requestConfig.getConfig().getStringList("NPCs");
        requestConfig.saveConfig();
        requestConfig.reloadConfig();
        if(requestString != null){
            requestControl = new NPCControl(requestString);
        }
        else{
            requestControl = new NPCControl(new java.util.ArrayList<>());
        }

        // Process Group Configs
        groupConfig.createFolder();
        groupConfig.setup();
        groupConfig.saveGroupYaml();
        groupConfig.reloadGroupYaml();
        groupControl = groupConfig.loadGroups();
    }
    // Update Group
    public static void updateGroupConfig(int index){
        groupConfig.updateGroup(index, groupControl.getGroup(index));
    }
    // -------------------------------------------------|

    // Find Player
    public static Player findPlayer(String name){
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
    public static void setGroupReply(Group group, String who, boolean fromNPC){

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
    public static void setReply(String sender, String target, int type){
        reply.remove(sender);
        replyType.remove(sender);

        reply.put(sender, target);
        replyType.put(sender, type);
    }
    // -------------------------------------------------|

    // -------------------------------------------------| Send Messages
    // Spy Message
    public static void sendSpyMessage(CommandSender sender, int tag, boolean NPCSend, String from, String to, String chat){
        String spyMessage = getSpyFormat(tag, from, to, chat);

        for (Player player : plugin.getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) && (!((player.getName()).equalsIgnoreCase(sender.getName())) || NPCSend) && !(player.getDisplayName().equals(to))) {
                player.sendMessage(spyMessage);
            }
        }
    }
    public static void sendSpyLocalMessage(CommandSender sender, boolean NPCSend, String playerName, String from, String chat){
        String spyMessage = getSpyLocalFormat(NPCSend, playerName, from, chat);

        for (Player player : plugin.getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) && (!((player.getName()).equalsIgnoreCase(sender.getName())) || NPCSend)) {
                player.sendMessage(spyMessage);
            }
        }
    }
    public static void sendSpyGroupMessage(CommandSender sender, int group, int tag, boolean NPCSend, String groupOPTag, String from, String chat){
        String spyMessage = getSpyGroupFormat(tag, groupOPTag, from, chat);

        for (Player player : plugin.getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) &&
                    (!((player.getName()).equalsIgnoreCase(sender.getName())) || NPCSend) &&
                    !groupControl.getGroup(group).hasMember(player.getName(), true)) {
                player.sendMessage(spyMessage);
            }
        }
    }
    public static void sendDebugMessage(CommandSender sender, String message){
        for (Player player : plugin.getServer().getOnlinePlayers()) { // Loop Through Players
            if ((player.hasPermission("rpg.spy") || player.hasPermission("rpg.admin")) && !((player.getName()).equalsIgnoreCase(sender.getName()))) {
                player.sendMessage(message);
            }
        }
    }
    // Group Messages
    public static void sendGroupMessage(CommandSender sender, int index, String text){
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

    public static boolean AdminPermission(CommandSender sender) {
        if(!(sender instanceof Player)){
            return true;
        }
        else return ((Player) sender).hasPermission("rpg.admin");
    }

    public RPGCommands(){
        npcConfig = new ConfigNPC("npc");
        requestConfig = new ConfigNPC("request");
        groupConfig = new ConfigGroup();

        loadConfigs();
    }

    public boolean runCMD(CommandSender sender, Command cmd, String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("rpg")) {
            rpgCMD.runCMD(sender, label, args);
        }
        else if (cmd.getName().equalsIgnoreCase("request")) {
            requestCMD.runCMD(sender, label, args);
        }
        else if (cmd.getName().equalsIgnoreCase("action")) {
            actionCMD.runCMD(sender, label, args);
        }
        else if (cmd.getName().equalsIgnoreCase("role")) {
            roleCMD.runCMD(sender, label, args);
        }
        else if (cmd.getName().equalsIgnoreCase("tmp")) {
            tempCMD.runCMD(sender, label, args);
        }
        else if (cmd.getName().equalsIgnoreCase("npc")) {
            npcCMD.runCMD(sender, label, args);
        }
        else if (cmd.getName().equalsIgnoreCase("group")) {
            groupCMD.runCMD(sender, label, args);
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
                                String baseName = convertToRawPlayer(reply.get(sender.getName())).toLowerCase();

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
                                String baseName = convertToRawPlayer(npcName).toLowerCase();

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
            else {
                // npcmsg Player (0) NPC/ID (1) chat (2)
                if (args.length > 2) {

                    Player target = findPlayer(args[0]);

                    if (target != null) {
                        int index;
                        try {
                            index = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException iobe) {
                            index = npcControl.npcIndex(args[1]);
                        }

                        if (npcControl.validIndex(index)) {

                            NPC npc = npcControl.getNPC(index);
                            String allArgs = layoutString(2, args);

                            Bukkit.getConsoleSender().sendMessage(getSpyFormat(1, npc.getDisplayName(), target.getDisplayName(), allArgs));
                            formMessage(target, npc.getDisplayName(), true, allArgs);
                            sendSpyMessage(sender, 1, true, npc.getDisplayName(), target.getDisplayName(), allArgs);

                            setReply(target.getName(), npc.getDisplayName(), 0);
                        }
                        else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find player!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/npcmsg §ePlayer §6ID/Name §7Message");
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("localmsg")) {
            // /localmsg Player (0) NPC (1) Msg (2)
            if (args.length > 2) {
                Player target = findPlayer(args[0]);
                if (target != null) {
                    int index;
                    try {
                        index = Integer.parseInt(args[1]) - 1;
                    } catch (NumberFormatException iobe) {
                        index = npcControl.npcIndex(args[1]);
                    }

                    if (npcControl.validIndex(index)) {

                        NPC npc = npcControl.getNPC(index);
                        String allArgs = layoutString(2, args);

                        Bukkit.getConsoleSender().sendMessage(getSpyLocalFormat(true, target.getDisplayName(), npcControl.getNPCDisplayName(index), allArgs));
                        sendSpyLocalMessage(sender, true, target.getDisplayName(), npcControl.getNPCDisplayName(index), allArgs);

                        formLocalMessage(target, npcControl.getNPCDisplayName(index), true, allArgs);

                        setReply(target.getName(), npcControl.getNPCDisplayName(index), 1);

                    } else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Could not find player!");
                }
            }
            else {
                sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/localmsg §ePlayer §6ID/Name §7Message");
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
                                String baseName = convertToRawPlayer(groupReplyName).toLowerCase();

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
                // groupmsg GroupName (0) NPC/ID (1) chat (2)
                if (args.length > 2) {
                    int groupIndex;
                    try {
                        groupIndex = Integer.parseInt(args[0]) - 1;
                    } catch (NumberFormatException iobe) {
                        groupIndex = groupControl.groupIndex(args[0]);
                    }
                    if (groupIndex > -1 && groupControl.validIndex(groupIndex)) {
                        int npcIndex;
                        try {
                            npcIndex = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException iobe) {
                            npcIndex = npcControl.npcIndex(args[1]);
                        }
                        if (npcIndex > -1 && npcControl.validIndex(npcIndex)) {
                            NPC npc = npcControl.getNPC(npcIndex);
                            String allArgs = layoutString(2, args);

                            Bukkit.getConsoleSender().sendMessage(getSpyGroupFormat(1, groupControl.getGroupOpDisplayTag(groupIndex), npc.getDisplayName(), allArgs));
                            sendGroupMessage(sender, groupIndex, formGroupMessage(npc.getDisplayName(), groupControl.getGroupDisplayTag(groupIndex), true, allArgs));
                            sendSpyGroupMessage(sender, groupIndex, 1, true, groupControl.getGroupOpDisplayTag(groupIndex), npc.getDisplayName(), allArgs);

                            setGroupReply(groupControl.getGroup(groupIndex), npc.getDisplayName(),  true);

                        } else {
                            sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find NPC!");
                        }
                    }
                    else {
                        sender.sendMessage(RPGStringHelper.COLOR_TAG + "§4Couldn't find group!");
                    }
                }
                else {
                    sender.sendMessage(RPGStringHelper.COLOR_TAG + "§c/groupmsg §dGroup/ID §6NPC/ID §7Message");
                }
            }
        }
        return true;
    }
}
