package me.Kam.NPCMsgs;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import me.Kam.NPCMsgs.Group;

public class NPCMsgs extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener, org.bukkit.command.CommandExecutor
{

    Map<String, String> who = new HashMap();
    Map<String, String> reply = new HashMap();
    Map<String, String> reply2 = new HashMap();

    Map<String, String> groupReply = new HashMap();
    Map<String, String> deleteConfirm = new HashMap();

    List<String> fms = new java.util.ArrayList();
    List<Group> groups = new java.util.ArrayList();
    List<String> groupString = new java.util.ArrayList();
    List<String> npcs = getConfig().getStringList("NPCs");
    String allArgs;
    StringBuilder sb;


    public NPCMsgs() {}

    public void onEnable() {
        System.out.println("------By Kam------");
        System.out.println("[NPCMsgs] V 4.0");
        System.out.println("------------------");
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        loadGroupConfig();
        System.out.println("[NPCMsgs] Successfully Enabled!");
    }

    public void onDisable() {
        System.out.println("[NPCMsgs] Disabled!");
    }

    public void loadGroupConfig(){
        groupConfig.setup();
        groupString = groupConfig.getGroupcfg().getStringList("Groups");
        groupConfig.saveGroups();
        groupConfig.reloadGroups();
        convertGroups();
    }

    public void convertGroups(){
        System.out.println("[NPCMsgs] Converting Group");
        for (String s : groupString) {
            groups.add(Group.deserialize(s));
        }
    }

    public int groupIndex(String group){
        String name = group.replace('&', '§');
        if (groups.toString().toLowerCase().contains(name.toLowerCase()))
        {
            for (int k = 0; k < groups.size(); k++)
            {
                String currentName = groups.get(k).returnName();
                if (currentName.toLowerCase().contains(name.toLowerCase())) {
                    return(k);
                }
            }
        }
        return(-1);
    }

    public int npcIndex(String npcName){
        if (npcs.toString().toLowerCase().contains(npcName.toLowerCase().replace(';', ' ')))
        {
            for (String npc : npcs) {
                if (npc.toLowerCase().contains(npcName.toLowerCase().replace(';', ' '))) {
                    return (npcs.indexOf(npc));
                }
            }
        }
        return(-1);
    }

    public void playerToPlayerReply(CommandSender sender, String arguments) {
        Player target = Bukkit.getPlayer((String)reply.get(sender.getName()));
        sender.sendMessage("§8[§a> §6" + target.getDisplayName() + "§8]§r " + arguments);
        Bukkit.getConsoleSender().sendMessage("§6[M] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + arguments);
        if(!(sender.hasPermission("fm.spy")) && !(target.hasPermission("fm.spy"))) {
            Bukkit.broadcast("§6[M] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + arguments, "fm.spy");
        }
        if ((sender instanceof Player)) {
            target.sendMessage("§8[§c< §6" + ((Player)sender).getDisplayName() + "§8] §r" + arguments);
        } else {
            target.sendMessage("§8[§c< §6" + sender.getName() + "§8] §r" + arguments);
        }
    }

    public void playerToNPCReply(CommandSender sender, String arguments) {
        sender.sendMessage("§8[§a> §6" + ((String) reply.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + arguments);
        if (!(sender.hasPermission("fm.spy"))) {
            Bukkit.broadcast("§9[P] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + ((String) reply.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + arguments, "fm.spy");
        }
        Bukkit.getConsoleSender().sendMessage("§9[P] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + ((String) reply.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + arguments);
    }

    public void playerToPlayerReply2(CommandSender sender, String arguments) {
        Player target = Bukkit.getPlayer((String)reply2.get(sender.getName()));
        sender.sendMessage("§8[§a> §6" + target.getDisplayName() + "§8]§r " + arguments);
        Bukkit.getConsoleSender().sendMessage("§6[M] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + arguments);
        if(!(sender.hasPermission("fm.spy")) && !(target.hasPermission("fm.spy"))) {
            Bukkit.broadcast("§6[M] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + arguments, "fm.spy");
        }
        if ((sender instanceof Player)) {
            target.sendMessage("§8[§c< §6" + ((Player)sender).getDisplayName() + "§8] §r" + arguments);
        } else {
            target.sendMessage("§8[§c< §6" + sender.getName() + "§8] §r" + arguments);
        }
    }

    public void playerToNPCReply2(CommandSender sender, String arguments) {
        sender.sendMessage("§8[§a> §6" + ((String)reply2.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + arguments);
        if(!(sender.hasPermission("fm.spy"))){
            Bukkit.broadcast("§9[P] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + ((String) reply2.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + arguments, "fm.spy");
        }
        Bukkit.getConsoleSender().sendMessage("§9[P] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + ((String)reply2.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + arguments);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("fmn")) {
            if (args.length == 1)
            {
                if ((label.equalsIgnoreCase("fmnid")) && (args.length == 1)) {
                    try
                    {
                        Integer.parseInt(args[0]);
                        try
                        {
                            who.put(sender.getName(), ((String)npcs.get(Integer.parseInt(args[0]) - 1)).replace(';', ' ').replace('&', '§'));

                            if (fms.contains(who.get(sender.getName())))
                            {
                                fms.remove(who.get(sender.getName()));
                            }

                            sender.sendMessage("§6Your fake name has been set to§c " + (String)npcs.get(Integer.parseInt(args[0]) - 1));
                        }
                        catch (IndexOutOfBoundsException iobe) {
                            sender.sendMessage("§cCouldn't find an NPC with the id§4 " + args[0]);
                        }



                        if (!fms.contains(who.get(sender.getName()))) {
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        sender.sendMessage("§cThe specified argument is not a number§4 " + args[0]);
                    }

                }
                else
                {
                    fms.remove(who.get(sender.getName()));
                }

                label296:
                who.put(sender.getName(), args[0].replace(';', ' ').replace('&', '§'));

                sender.sendMessage(ChatColor.GOLD + "Your fake name has been set to: " + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', args[0].replace(';', ' ')));

                fms.add(args[0].replace('&', '§').replace(';', ' '));

            }
            else
            {

                sender.sendMessage(ChatColor.RED + "Invalid Syntax, check /help fmn.");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("fm")) {
            if (args.length > 1)
            {
                Player target = Bukkit.getPlayer(args[0]);

                if (who.get(sender.getName()) != null) {
                    if (target != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }

                        String allArgs = sb.toString().trim();
                        allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                        allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                        allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                        if(sender.getName().equalsIgnoreCase("CONSOLE")){
                            Bukkit.broadcast("§2[N]§8 [§6" + (String)who.get(sender.getName()) + "§7 > §8" + target.getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                        }
                        else{
                            sender.sendMessage("§2[N]§8 [§6" + (String)who.get(sender.getName()) + "§7 > §8" + target.getDisplayName() + "§8] §r" + allArgs);
                        }
                        target.sendMessage("§8[§c< §6" + ChatColor.translateAlternateColorCodes('&', (String)who.get(sender.getName())) + "§8] §r" + allArgs);

                        reply.remove(target.getName());
                        reply2.remove(target.getName());

                        reply.put(target.getName(), (String)who.get(sender.getName()));
                        reply2.put((String)who.get(sender.getName()), target.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Couldn't find player " + ChatColor.DARK_RED + args[0]);
                    }

                }
                else {
                    sender.sendMessage(ChatColor.RED + "You don't have a fake message name. Set it with " + ChatColor.DARK_RED + "/fmn");
                }

            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Invalid Syntax, check /help fm.");
            }

        }
        else if (cmd.getName().equalsIgnoreCase("msg")) {
            if (args.length > 1)
            {

                Player target = Bukkit.getPlayer(args[0]);
                String allArgs;

                if (target == null){
                    List checks = Bukkit.matchPlayer(args[0]);
                    if (checks.size() == 1){
                            target = (Player)checks.get(0);
                    }
                }

                if (target != null)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }

                    allArgs = sb.toString().trim();
                    allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                    allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                    allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                    if(sender.getName().equalsIgnoreCase("CONSOLE")){
                        Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + target.getName() + "§8] §r" + allArgs);
                        target.sendMessage("§8[§c< §4§oCON§8] §r"  + allArgs);
                        Bukkit.broadcast("§6[M] §8[§4§oCON §7> §6" + target.getName() + "§8] §r" + allArgs, "fm.spy");
                    }
                    else if (!(target instanceof Player)) {
                        sender.sendMessage("§8[§a> §6" + target.getName() + "§8] §r" + allArgs);
                        if ((sender instanceof Player)) {
                            target.sendMessage("§8[§c< §6" + ((Player)sender).getDisplayName() + "§8] §r" + allArgs);
                        } else {
                            target.sendMessage("§8[§c< §6" + sender.getName() + "§8] §r" + allArgs);
                        }
                    } else {
                        sender.sendMessage("§8[§a> §6" + target.getDisplayName() + "§8] §r" + allArgs);
                        Bukkit.getConsoleSender().sendMessage("§8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + allArgs);
                        if(!(sender.hasPermission("fm.spy")) && !(target.hasPermission("fm.spy"))){
                            Bukkit.broadcast("§6[M] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + target.getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                        }
                        if ((sender instanceof Player)) {
                            target.sendMessage("§8[§c< §6"  + ((Player)sender).getDisplayName() + "§8] §r" + allArgs);
                        } else {
                            target.sendMessage("§8[§c< §6" + sender.getName() + "§8] §r" + allArgs);
                        }
                    }


                    reply.remove(target.getName());
                    reply2.remove(target.getName());

                    reply.remove(sender.getName());
                    reply2.remove(sender.getName());

                    reply.put(sender.getName(), target.getName());
                    reply2.put(target.getName(), sender.getName());
                }
                else if (fms.toString().toLowerCase().contains(args[0].toLowerCase().replace(';', ' ')))
                {
                    for (String s : fms)
                    {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        allArgs = sb.toString().trim();
                        allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                        allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                        allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                        if (s.toLowerCase().contains(args[0].toLowerCase().replace(';', ' '))) {
                            if(!(sender.getName().equals("CONSOLE"))) {
                                sender.sendMessage("§8[§a> §6" + s.replace('&', '§') + "§8] §r" + allArgs);
                            }
                            if(!(sender.hasPermission("fm.spy"))) {
                                Bukkit.broadcast("§9[P] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + s.replace('&', '§') + "§8] §r" + allArgs, "fm.spy");
                            }
                            if(sender.getName().equals("CONSOLE")){
                                Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + s.replace('&', '§') + "§8] §r" + allArgs);
                                Bukkit.broadcast("§9[P] §8[§4§oCON §7> §6" + s.replace('&', '§') + "§8] §r" + allArgs, "fm.spy");
                            }
                            else{
                                Bukkit.getConsoleSender().sendMessage("§4[CON] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + s.replace('&', '§') + "§8] §r" + allArgs);
                            }

                            reply.remove(sender.getName());
                            reply2.remove(sender.getName());
                            reply.put(((Player)sender).getName(), s);
                            reply2.put(s, ((Player)sender).getName());
                            break;
                        }

                    }
                }
                else if (npcs.toString().toLowerCase().contains(args[0].toLowerCase().replace(';', ' ')))
                {
                    for (String s : npcs)
                    {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        allArgs = sb.toString().trim();
                        allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                        allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                        allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                        if (s.toLowerCase().contains(args[0].toLowerCase().replace(';', ' '))) {
                            if(!(sender.getName().equals("CONSOLE"))) {
                                sender.sendMessage("§8[§a> §6" + s.replace('&', '§') + "§8] §r" + allArgs);
                            }
                            if(!(sender.hasPermission("fm.spy"))) {
                                Bukkit.broadcast("§9[P] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + s.replace('&', '§') + "§8] §r" + allArgs, "fm.spy");
                            }
                            if(sender.getName().equals("CONSOLE")){
                                Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + s.replace('&', '§') + "§8] §r" + allArgs);
                                Bukkit.broadcast("§9[P] §8[§4§oCON §7> §6" + s.replace('&', '§') + "§8] §r" + allArgs, "fm.spy");
                            }
                            else{
                                Bukkit.getConsoleSender().sendMessage("§4[CON] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + s.replace('&', '§') + "§8] §r" + allArgs);
                            }

                            reply.remove(sender.getName());
                            reply2.remove(sender.getName());
                            reply.put(sender.getName(), s);
                            reply2.put(s, sender.getName());
                            break;
                        }
                    }
                }
                else {
                    sender.sendMessage("§cError:§4 Couldn't find that player!");
                }
            }
            else {
                sender.sendMessage("Send a player a private message");
                sender.sendMessage("Usage: /" + label + " <player> <message>");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("gmsg")) {
            if (args.length > 1)
            {
                String name = "";
                String allArgs;
                boolean allow = false;
                if (label.equalsIgnoreCase("gid")){
                    try{
                        int num = Integer.parseInt(args[0]) - 1;
                        if(groups.size() > num && num > -1){
                            name = groups.get(num).returnName();
                            allow = true;
                        }
                    }
                    catch (NumberFormatException nfe) {
                        sender.sendMessage("§cCouldn't parse number§4 " + args[0]);
                        allow = false;
                    }
                }
                else{
                    name = args[0];
                    allow = true;
                }
                if(allow){
                    if (groups.toString().toLowerCase().contains(name.toLowerCase()))
                    {
                        for (Group g : groups)
                        {
                            String s = g.returnName();
                            List<String> view = g.allPeople();

                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }
                            allArgs = sb.toString().trim();
                            allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                            allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                            allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                            if (s.toLowerCase().contains(name.toLowerCase())) {
                                if(!(sender.getName().equals("CONSOLE"))){
                                    if (view.contains(sender.getName()) || sender.hasPermission("fm.spy")){

                                        String out = "";
                                        if(g.getTagToggle() && !(sender.hasPermission("npc.off"))){
                                            out += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                        }
                                        sender.sendMessage(out + "§8[§2>> §6" + s.replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);

                                        groupReply.remove(sender.getName());
                                        groupReply.put(sender.getName(), s);

                                        for(int j = 0; j < view.size(); j++){
                                            Player target = Bukkit.getPlayer(view.get(j));
                                            if (target == null){
                                                List checks = Bukkit.matchPlayer(view.get(j));
                                                if (checks.size() == 1){
                                                    target = (Player)checks.get(0);
                                                }
                                            }
                                            if(target != null && !(target.equals(sender))){

                                                String output = "";
                                                if(g.getTagToggle() && !(target.hasPermission("npc.off"))){
                                                    output += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                                }

                                                target.sendMessage(output + "§8[§4<< §6" + ((Player) sender).getDisplayName() + "§8] §r" + allArgs);
                                                groupReply.remove(target.getName());
                                                groupReply.put(target.getName(), s);

                                            }
                                        }
                                        if(!(sender.hasPermission("fm.spy"))) {
                                            Bukkit.broadcast("§e[G] §8[§6" + ((Player) sender).getDisplayName() + "§7 >>§6 " + g.getOpTag().replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                        }
                                    }
                                    else{
                                        sender.sendMessage("§cError:§4 You are not part of the group " + s.replace('&', '§').replace(';', ' ') + "§4!");
                                    }
                                }
                                if(sender.getName().equals("CONSOLE")){
                                    Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7>> §6" + s.replace('&', '§') + "§8] §r" + allArgs);
                                    Bukkit.broadcast("§e[G] §8[§4§oCON §7>> §6" + s.replace('&', '§') + "§8] §r" + allArgs, "fm.spy");
                                    for(int j = 0; j < view.size(); j++){
                                        Player target = Bukkit.getPlayer(view.get(j));
                                        if (target == null){
                                            List checks = Bukkit.matchPlayer(view.get(j));
                                            if (checks.size() == 1){
                                                target = (Player)checks.get(0);
                                            }
                                        }
                                        if(target != null && !(target.equals(sender))){

                                            String output2 = "";
                                            if(g.getTagToggle() && !(target.hasPermission("npc.off"))){
                                                output2 += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                            }

                                            target.sendMessage(output2 + "§8[§4<< §6" + ((Player) sender).getDisplayName() + "§8] §r" + allArgs);
                                            groupReply.remove(target.getName());
                                            groupReply.put(target.getName(), s);
                                        }
                                    }
                                }
                                else{
                                    Bukkit.getConsoleSender().sendMessage("§4[CON] §8[§6" + ((Player)sender).getDisplayName() + "§7 >§6 " + g.getOpTag().replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                }
                                break;
                            }
                        }
                    }
                }
                else {
                    sender.sendMessage("§cError:§4 Couldn't find a group with that name that you are in!");
                }

                StringBuilder sb = new StringBuilder();
            }
            else {
                sender.sendMessage("Send a group a private message");
                sender.sendMessage("Usage: /" + label + " <group> <message>");
            }
        }
        else { String allArgs;
            if (cmd.getName().equalsIgnoreCase("r")) {
                if (args.length > 0)
                {
                    try {
                        if (reply2.containsKey(sender.getName()))
                        {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }
                            allArgs = sb.toString().trim();
                            allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                            allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                            allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                            if(sender.getName().equalsIgnoreCase("CONSOLE")){
                                Player target = Bukkit.getPlayer((String)reply2.get(sender.getName()));
                                if(target != null){
                                    Bukkit.broadcast("§6[M] §8[§4§oCON §7> §6" + target.getName() + "§8] §r" + allArgs,"fm.spy");
                                    Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + target.getName() + "§8] §r" + allArgs);
                                    target.sendMessage("§8[§c< §4§oCON" + "§8] §r"  + allArgs);
                                }
                                else if(((fms.toString().toLowerCase().contains(((String)reply2.get(sender.getName())).toLowerCase()))) || ((npcs.toString().toLowerCase().contains(((String)reply2.get(sender.getName())).toLowerCase())))){
                                    Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + ((String)reply2.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + allArgs);
                                    Bukkit.broadcast("§9[P] §8[§4§oCON §7> §6" + ((String) reply2.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                }
                                else{
                                    Bukkit.getConsoleSender().sendMessage("§cNo one to reply to.");
                                }
                            }
                            else if (((String)reply2.get(sender.getName())).equals("CONSOLE")) {
                                sender.sendMessage("§8[§a> §6" + ((String)reply2.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + allArgs);
                                if ((sender instanceof Player)) {
                                    Bukkit.getConsoleSender().sendMessage("§8[§c< " + ((Player)sender).getDisplayName() + "§8] §r" + allArgs);
                                    Bukkit.broadcast("§6[M] §8[§6" + ((Player)sender).getDisplayName() + "§7 > §4§oCON§8] §r" + allArgs, "fm.spy");
                                } else {
                                    Bukkit.getConsoleSender().sendMessage("§8[§c< " + sender.getName() + "§8] §r" + allArgs);
                                }
                            }
                            else if (fms.toString().toLowerCase().contains(((String)reply2.get(sender.getName())).toLowerCase())) {
                                Player target = Bukkit.getPlayer((String)reply2.get(sender.getName()));
                                if(target != null){
                                    playerToPlayerReply2(sender, allArgs);
                                }
                                else{
                                    playerToNPCReply2(sender, allArgs);

                                }
                            }
                            else if (npcs.toString().toLowerCase().contains(((String)reply2.get(sender.getName())).toLowerCase())) {
                                Player target = Bukkit.getPlayer((String)reply2.get(sender.getName()));
                                if(target != null){
                                    playerToPlayerReply2(sender, allArgs);
                                }
                                else{
                                    playerToNPCReply2(sender, allArgs);
                                }
                            }
                            else {
                                playerToPlayerReply2(sender, allArgs);
                            }

                            reply.put(sender.getName(), (String)reply2.get(sender.getName()));
                            reply2.put((String)reply2.get(sender.getName()), sender.getName());

                        }
                        else if (reply.containsKey(sender.getName()))
                        {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }
                            allArgs = sb.toString().trim();
                            allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                            allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                            allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                            if(sender.getName().equalsIgnoreCase("CONSOLE")){
                                Player target = Bukkit.getPlayer((String)reply.get(sender.getName()));
                                if(target != null){
                                    Bukkit.broadcast("§6[M] §8[§4§oCON §7> §6" + target.getName() + "§8] §r" + allArgs,"fm.spy");
                                    Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + target.getName() + "§8] §r" + allArgs);
                                    target.sendMessage("§8[§c< §4§oCON" + "§8] §r"  + allArgs);
                                }
                                else if(((fms.toString().toLowerCase().contains(((String)reply.get(sender.getName())).toLowerCase()))) || ((npcs.toString().toLowerCase().contains(((String)reply.get(sender.getName())).toLowerCase())))){
                                    Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7> §6" + ((String)reply.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + allArgs);
                                    Bukkit.broadcast("§9[P] §8[§4§oCON §7> §6" + ((String) reply.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                    Bukkit.broadcast("Test Nuts 3", "fm.spy"); // HERE
                                }
                                else{
                                    Bukkit.getConsoleSender().sendMessage("§cNo one to reply to.");
                                }
                            }
                            else if (((String)reply.get(sender.getName())).equals("CONSOLE")) {
                                sender.sendMessage("§8[§a> §6" + ((String)reply.get(sender.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + allArgs);
                                if ((sender instanceof Player)) {
                                    Bukkit.getConsoleSender().sendMessage("§8[§c< " + ((Player)sender).getDisplayName() + "§8] §r" + allArgs);
                                    Bukkit.broadcast("§6[M] §8[§6" + ((Player)sender).getDisplayName() + "§7 > §4§oCON§8] §r" + allArgs, "fm.spy");
                                } else {
                                    Bukkit.getConsoleSender().sendMessage("§8[§c< " + sender.getName() + "§8] §r" + allArgs);
                                }
                            }
                            else if (fms.toString().toLowerCase().contains(((String)reply.get(sender.getName())).toLowerCase())) {
                                Player target = Bukkit.getPlayer((String)reply.get(sender.getName()));
                                if(target != null){
                                    playerToPlayerReply(sender, allArgs);
                                }
                                else{
                                    playerToNPCReply(sender, allArgs);
                                }
                            }
                            else if (npcs.toString().toLowerCase().contains(((String)reply.get(sender.getName())).toLowerCase())) {
                                Player target = Bukkit.getPlayer((String)reply.get(sender.getName()));
                                if(target != null){
                                    playerToPlayerReply(sender, allArgs);
                                }
                                else{
                                    playerToNPCReply(sender, allArgs);
                                }
                            }
                            else {
                                playerToPlayerReply(sender, allArgs);
                            }


                            reply.put(sender.getName(), (String)reply.get(sender.getName()));
                            reply2.put((String)reply.get(sender.getName()), sender.getName());
                        }
                        else
                        {
                            sender.sendMessage("§cError: §4There is no one to whom you can reply");
                        }
                    } catch (NullPointerException npe) {
                        sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "The player you are trying to reply to has left the game!");
                    }
                }
                else {
                    sender.sendMessage("Quickly reply to a private message");
                    sender.sendMessage("Usage: /" + label + " <message>");
                }
            }
            else if (cmd.getName().equalsIgnoreCase("gr")) {
                if (args.length > 0) {
                    try {
                        if (groupReply.containsKey(sender.getName())) {
                            String groupName = (String) groupReply.get(sender.getName());
                            if (groups.toString().toLowerCase().contains(groupName.toLowerCase())) {
                                for (Group g : groups) {
                                    String s = g.returnName();
                                    List<String> view = g.allPeople();

                                    StringBuilder sb = new StringBuilder();
                                    for (String arg : args) {
                                        sb.append(arg).append(" ");
                                    }
                                    allArgs = sb.toString().trim();
                                    allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                                    allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                                    allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                                    if (s.toLowerCase().contains(groupName.toLowerCase())) {
                                        if (!(sender.getName().equals("CONSOLE"))) {
                                            if (view.contains(sender.getName())) {

                                                String out = "";
                                                if(g.getTagToggle() && !(sender.hasPermission("npc.off"))){
                                                    out += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                                }
                                                sender.sendMessage(out + "§8[§2>> §6" + s.replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);

                                                groupReply.remove(sender.getName());
                                                groupReply.put(sender.getName(), s);

                                                for (String value : view) {
                                                    Player target = Bukkit.getPlayer(value);
                                                    if (target == null) {
                                                        List<Player> checks = Bukkit.matchPlayer(value);
                                                        if (checks.size() == 1) {
                                                            target = (Player) checks.get(0);
                                                        }
                                                    }
                                                    if (target != null && !(target.equals(sender))) {

                                                        String output = "";
                                                        if (g.getTagToggle() && !(target.hasPermission("npc.off"))) {
                                                            output += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                                        }

                                                        target.sendMessage(output + "§8[§4<< §6" + ((Player) sender).getDisplayName() + "§8] §r" + allArgs);
                                                        groupReply.remove(target.getName());
                                                        groupReply.put(target.getName(), s);
                                                    }
                                                }
                                                if (!(sender.hasPermission("fm.spy"))) {
                                                    Bukkit.broadcast("§e[G] §8[§6" + ((Player) sender).getDisplayName() + "§7 >>§6 " + g.getOpTag().replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                                }
                                            } else {
                                                sender.sendMessage("§cError:§4 You are no longer part of the group " + s.replace('&', '§').replace(';', ' ') + "§4!");
                                            }
                                        }
                                        if (sender.getName().equals("CONSOLE")) {
                                            Bukkit.getConsoleSender().sendMessage("§8[§4§oCON §7>> §6" + s.replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                            Bukkit.broadcast("§e[G] §8[§4§oCON §7>> §6" + s.replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                            for (String value : view) {
                                                Player target = Bukkit.getPlayer(value);
                                                if (target == null) {
                                                    List checks = Bukkit.matchPlayer(value);
                                                    if (checks.size() == 1) {
                                                        target = (Player) checks.get(0);
                                                    }
                                                }
                                                if (target != null && !(target.equals(sender))) {

                                                    String output2 = "";
                                                    if (g.getTagToggle() && !(target.hasPermission("npc.off"))) {
                                                        output2 += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                                    }

                                                    target.sendMessage(output2 + "§8[§4<< §6" + ((Player) sender).getDisplayName() + "§8] §r" + allArgs);
                                                    groupReply.remove(target.getName());
                                                    groupReply.put(target.getName(), s);

                                                }
                                            }
                                        } else {
                                            Bukkit.getConsoleSender().sendMessage("§4[CON] §8[§6" + ((Player) sender).getDisplayName() + "§7 >§6 " + g.getOpTag().replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                sender.sendMessage("§cError:§4 Group no longer exists!");
                            }
                            StringBuilder sb = new StringBuilder();
                        } else {
                            sender.sendMessage("§cError: §4There is no group to whom you can reply");
                        }
                    }
                    catch (NullPointerException npe) {
                        sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "The group you are trying to reply to is no longer available!");
                    }
                }
                else {
                    sender.sendMessage("Quickly reply to a group message");
                    sender.sendMessage("Usage: /" + label + " <message>");
                }
            }
            else if(cmd.getName().equalsIgnoreCase("nr")){
                if (args.length > 1)
                {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null){
                        List checks = Bukkit.matchPlayer(args[0]);
                        if (checks.size() == 1){
                            target = (Player)checks.get(0);
                        }
                    }
                    if(target != null){
                        try {
                            if (reply2.containsKey(target.getName()))
                            {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                allArgs = sb.toString().trim();
                                allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                                allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                                allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                                if((sender.getName().equalsIgnoreCase("CONSOLE")) && ((fms.toString().toLowerCase().contains(((String)reply2.get(target.getName())).toLowerCase())) || (npcs.toString().toLowerCase().contains(((String)reply2.get(target.getName())).toLowerCase())))){
                                    Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((String)reply2.get(target.getName())).replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                    target.sendMessage("§8[§c< §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    Bukkit.broadcast("§2[N] §4§o[CON] §8[" + ((String) reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 >§6 " + ((Player)target).getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                }
                                else if (((String)reply2.get(target.getName())).equals("CONSOLE")) {
                                    sender.sendMessage("§2[N] §8[§6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                    if ((sender instanceof Player)) {
                                        Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((Player)target).getDisplayName() + "§7 > §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    } else {
                                        Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + target.getName() + "§7 > §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                }
                                else if (fms.toString().toLowerCase().contains(((String)reply2.get(target.getName())).toLowerCase()))
                                {
                                    if(sender.getName().equals(target.getName())){
                                        target.sendMessage("§8[§c< §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    else{
                                        sender.sendMessage("§2[N] §8[§6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                        target.sendMessage("§8[§c< §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    if(!(sender.hasPermission("fm.spy"))){
                                        Bukkit.broadcast("§9[P] §6[§f<" + ((Player) sender).getDisplayName() + "§f>" + "§f<" + ((String) reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§f>" + "§7 >§6 " + ((Player)target).getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                    }
                                    Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                }
                                else if (npcs.toString().toLowerCase().contains(((String)reply2.get(target.getName())).toLowerCase())) {
                                    if(sender.getName().equals(target.getName())){
                                        target.sendMessage("§8[§c< §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    else{
                                        sender.sendMessage("§2[N] §8[§6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                        target.sendMessage("§8[§c< §6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    if(!(sender.hasPermission("fm.spy"))){
                                        Bukkit.broadcast("§9[P] §6[§f<" + ((Player) sender).getDisplayName() + "§f>" + "§f<" + ((String) reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§f>" + "§7 >§6 " + ((Player)target).getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                    }
                                    Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((String)reply2.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                }
                                else {
                                    sender.sendMessage("§4Error: §cYou cannot reply as another player");
                                }

                                reply.put((String)reply2.get(target.getName()), target.getName());
                                reply2.put(target.getName(), (String)reply2.get(target.getName()));
                            }
                            else if (reply.containsKey(target.getName()))
                            {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                allArgs = sb.toString().trim();
                                allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                                allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                                allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                                if((sender.getName().equalsIgnoreCase("CONSOLE")) && ((fms.toString().toLowerCase().contains(((String)reply.get(target.getName())).toLowerCase())) || (npcs.toString().toLowerCase().contains(((String)reply.get(target.getName())).toLowerCase())))){
                                    Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((String)reply.get(target.getName())).replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                    Bukkit.broadcast("§2[N] §4§o[CONS] §8[" + ((String) reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 >§6 " + ((Player)target).getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                    target.sendMessage("§8[§c< §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                }
                                else if (((String)reply.get(target.getName())).equals("CONSOLE")) {
                                    // sender.sendMessage("§8[§a> §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8]§r " + allArgs);
                                    sender.sendMessage("§2[N] §8[§6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                    if ((sender instanceof Player)) {
                                        Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((Player)target).getDisplayName() + "§7 > §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    } else {
                                        Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + target.getName() + "§7 > §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                }
                                else if (fms.toString().toLowerCase().contains(((String)reply.get(target.getName())).toLowerCase())) {
                                    if(sender.getName().equals(target.getName())){
                                        target.sendMessage("§8[§c< §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    else{
                                        sender.sendMessage("§2[N] §8[§6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                        target.sendMessage("§8[§c< §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    if(!(sender.hasPermission("fm.spy"))){
                                        Bukkit.broadcast("§9[P] §6[§f<" + ((Player) sender).getDisplayName() + "§f>" + "§f<" + ((String) reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§f>" + "§7 >§6 " + ((Player)target).getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                        Bukkit.broadcast("Test Nuts 7", "fm.spy"); // HERE
                                    }
                                    Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                }
                                else if (npcs.toString().toLowerCase().contains(((String)reply.get(target.getName())).toLowerCase())) {
                                    if(sender.getName().equals(target.getName())){
                                        target.sendMessage("§8[§c< §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    else{
                                        sender.sendMessage("§2[N] §8[§6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                        target.sendMessage("§8[§c< §6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                    }
                                    if(!(sender.hasPermission("fm.spy"))){
                                        Bukkit.broadcast("§9[P] §6[§f<" + ((Player) sender).getDisplayName() + "§f>" + "§f<" + ((String) reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§f>" + "§7 >§6 " + ((Player)target).getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                        Bukkit.broadcast("Test Nuts 8", "fm.spy"); // HERE
                                    }
                                    Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + ((String)reply.get(target.getName())).replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                }
                                else {
                                    sender.sendMessage("§4Error: §cYou cannot reply as another player");
                                }

                                reply.put((String)reply.get(target.getName()), target.getName());
                                reply2.put(target.getName(), (String)reply.get(target.getName()));
                            }
                            else
                            {
                                sender.sendMessage("§cError: §4There is no one to whom you can reply");
                            }
                        } catch (NullPointerException npe) {
                            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "The player you are trying to reply to has left the game!");
                        }
                    }
                    else {
                        sender.sendMessage("§cError:§4 Couldn't find that player!");
                    }
                }
                else {
                    sender.sendMessage("Quickly reply using an NPC in a private message");
                    sender.sendMessage("Usage: /" + label + " <PlayerName>" +  " <message>");
                }
            }
            else if (cmd.getName().equalsIgnoreCase("npc")) {
                if (args.length == 3){
                    if (args[0].equalsIgnoreCase("swap")) {
                        try {
                            int first = Integer.parseInt(args[1]) - 1;
                            int second = Integer.parseInt(args[2]) - 1;
                            if(npcs.size() > first && npcs.size() > second){
                                String firstName = npcs.get(first);
                                String secondName = npcs.get(second);
                                npcs.set(first, secondName);
                                npcs.set(second, firstName);
                                sender.sendMessage("§aSwapped: " + npcs.get(second) + " §a<-> " + npcs.get(first));
                            }
                            else{
                                sender.sendMessage("§cThose ids are not used");
                            }
                        } catch (NumberFormatException nfe) {
                            sender.sendMessage("§cCouldn't parse number§4 " + args[1] + " or " + args[2]);
                        }
                    }
                    else if (args[0].equalsIgnoreCase("rename")) {
                        try {
                            int first = Integer.parseInt(args[1]) - 1;
                            if(npcs.size() > first){
                                String firstName = args[2].replace('&', '§').replace(';', ' ');
                                String before = npcs.get(first);
                                npcs.set(first, firstName);
                                sender.sendMessage("§aChanged: " + before + " §a-> " + npcs.get(first));
                            }
                            else{
                                sender.sendMessage("§cThat ids are not used");
                            }
                        } catch (NumberFormatException nfe) {
                            sender.sendMessage("§cCouldn't parse number§4 " + args[1]);
                        }
                    }
                    else
                    {
                        sender.sendMessage("§cNot a Valid Command");
                    }
                }
                else if (args.length == 2)
                {
                    if (args[0].equalsIgnoreCase("add"))
                    {
                        npcs.add(args[1].replace('&', '§').replace(';', ' '));
                        sender.sendMessage(ChatColor.GREEN + "NPC §c" + args[1].replace('&', '§').replace(';', ' ') + ChatColor.GREEN + " with id §c" + npcs.size() + "§2 added!");
                    }
                    else if (args[0].equalsIgnoreCase("remove"))
                    {
                        if (npcs.toString().toLowerCase().contains(args[1].toLowerCase().replace(';', ' ')))
                        {
                            for (String string : npcs)
                            {
                                if (string.toLowerCase().contains(args[1].toLowerCase().replace(';', ' ')))
                                {
                                    sender.sendMessage(ChatColor.GREEN + "NPC §c" + string + ChatColor.GREEN + " removed!");
                                    npcs.remove(string);
                                    break;
                                }
                            }

                        }
                        else
                        {
                            sender.sendMessage("§cCouldn't find NPC§4 " + args[1]);
                        }

                    }
                    else if (args[0].equalsIgnoreCase("list")) {
                        try {
                            int i = Integer.parseInt(args[1]);
                            sender.sendMessage("§6>--------- §cNPC List§6 ---------<");

                            for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                                try {
                                    try {
                                        if (o <= npcs.size()) {
                                            sender.sendMessage("§c" + o + ".§6 " + (String) npcs.get(o - 1));
                                        }
                                    } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
                                    }
                                    o++;
                                } catch (NullPointerException localNullPointerException1) {
                                }
                            }


                            int pages = npcs.size() / 15 + (npcs.size() % 15 == 0 ? 0 : 1);
                            sender.sendMessage("§6>------- Page §c" + i + "§6 of §c" + pages + "§6 --------<");
                        } catch (NumberFormatException nfe) {
                            try {
                                sender.sendMessage("§cCouldn't resolve number §4" + args[2]);
                            } catch (ArrayIndexOutOfBoundsException aofe) {
                                sender.sendMessage("§cThe argument contains invalid characters!");
                            }
                        }
                    }

                    else
                    {
                        sender.sendMessage("§cInvalid Syntax. Use /help npc");
                    }
                }
                else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("deleteall")) {
                        sender.sendMessage(ChatColor.GREEN + "NPCs cleared!");
                        npcs.clear();
                    }
                    else if (args[0].equalsIgnoreCase("list"))
                    {

                        sender.sendMessage("§6>--------- §cNPC List§6 ---------<");
                        for (int i = 1; 16 > i; i++) {
                            if ((npcs.size() > 0) && (i <= npcs.size())) {
                                sender.sendMessage("§c" + i + ". §6" + (String)npcs.get(i - 1));
                            }
                        }
                        int pages = npcs.size() / 15 + (npcs.size() % 15 == 0 ? 0 : 1);
                        sender.sendMessage("§6>------- Page §c1§6 of §c" + pages + "§6 --------<");
                    }
                    else if (args[0].equalsIgnoreCase("fms"))
                    {

                        sender.sendMessage("§6>--------- §9FMS List§6 ---------<");
                        for (int i = 1; 16 > i; i++) {
                            if ((fms.size() > 0) && (i <= fms.size())) {
                                sender.sendMessage("§c" + i + ". §6" + (String)fms.get(i - 1));
                            }
                        }
                        int pages = fms.size() / 15 + (fms.size() % 15 == 0 ? 0 : 1);
                        sender.sendMessage("§6>------- Page §c1§6 of §c" + pages + "§6 --------<");
                    }
                    else if (args[0].equalsIgnoreCase("reply"))
                    {

                        sender.sendMessage("§6>--------- §aReply List§6 ---------<");
                        for (String name: reply.keySet()){
                            String key = name.toString();
                            String value = reply.get(name).toString();
                            sender.sendMessage("§6Key: " + key + ". §aValue: " + value);
                        }
                        int pages = reply.size() / 15 + (reply.size() % 15 == 0 ? 0 : 1);
                        sender.sendMessage("§6>------- Page §c1§6 of §c" + pages + "§6 --------<");
                    }
                    else if (args[0].equalsIgnoreCase("reply2"))
                    {

                        sender.sendMessage("§6>--------- §eReply 2 List§6 ---------<");
                        for (String name: reply2.keySet()){
                            String key = name.toString();
                            String value = reply2.get(name).toString();
                            sender.sendMessage("§6Key: " + key + ". §aValue: " + value);
                        }
                        int pages = reply2.size() / 15 + (reply2.size() % 15 == 0 ? 0 : 1);
                        sender.sendMessage("§6>------- Page §c1§6 of §c" + pages + "§6 --------<");
                    }
                    else if (label.equalsIgnoreCase("npcid"))
                    {
                        if (npcs.toString().toLowerCase().contains(args[0].toLowerCase().replace(';', ' ')))
                        {
                            for (String npc : npcs) {
                                if (npc.toLowerCase().contains(args[0].toLowerCase().replace(';', ' '))) {
                                    sender.sendMessage("§6ID for §c" + npc + " §6is §c" + (npcs.indexOf(npc) + 1));
                                }
                            }
                        } else {
                            sender.sendMessage("§cCouldn't find NPC§4 " + args[0]);
                        }
                    }
                    else {
                        sender.sendMessage("§cInvalid Syntax. Use /help npc");
                    }
                }
                else {
                    sender.sendMessage("§cInvalid Syntax. Use /help npc");
                }

                getConfig().set("NPCs", npcs);
                saveConfig();
            }
            else if (cmd.getName().equalsIgnoreCase("group")) {
                if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("tag") ||
                            args[0].equalsIgnoreCase("tagid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("tagID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("tag")){
                            num = groupIndex(args[1]);
                        }
                        if(num > -1){
                            try {
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        Group current = groups.get(num);
                                        boolean allow = false;
                                        boolean notOwner = false;
                                        if(((Player)sender).hasPermission("fm.group")){
                                            allow = true;
                                        }
                                        if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                            String owner = current.allPeople().get(0);
                                            String playerSender = ((Player)sender).getName();
                                            if(owner.equals(playerSender)){
                                                allow = true;
                                            }
                                            else{
                                                notOwner = true;
                                            }
                                        }
                                        if (allow) {
                                            if(args[2].equalsIgnoreCase("set")){
                                                String newTag = args[3].replace('&', '§');
                                                sender.sendMessage("§6Tag Changed for "+ current.returnName().replace(';', ' ') + "§6 from " + current.getTag().replace(';', ' ') + "§6 to " + newTag.replace(';', ' '));
                                                groups.get(num).setTag(newTag);
                                            }
                                            else{
                                                sender.sendMessage("§cError:§4 Unknown Entry: /group tag set NAME");
                                            }
                                        }
                                        else if(notOwner){
                                            sender.sendMessage("§cError: You are not the owner of that Group");
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not part of that Group");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("switchuser") ||
                            args[0].equalsIgnoreCase("switchuserid")) {
                        int groupID = -1;
                        if (args[0].equalsIgnoreCase("switchuserid")){
                            try{ groupID = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("switchuser")){
                            groupID = groupIndex(args[1]);
                        }
                        if(groupID > -1){
                            try {
                                if(groupID >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        Group current = groups.get(groupID);
                                        boolean allow = false;
                                        boolean notOwner = false;
                                        if(((Player)sender).hasPermission("fm.group")){
                                            allow = true;
                                        }
                                        if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                            String owner = current.allPeople().get(0);
                                            String playerSender = ((Player)sender).getName();
                                            if(owner.equals(playerSender)){
                                                allow = true;
                                            }
                                            else{
                                                notOwner = true;
                                            }
                                        }
                                        if (allow) {
                                            try {
                                                int userOne = Integer.parseInt(args[2]) - 1;
                                                int userTwo = Integer.parseInt(args[3]) - 1;
                                                if((userOne != 0 && userTwo != 0) || ((Player)sender).hasPermission("fm.spy")){
                                                    try {
                                                        if (groups.get(groupID).allPeople().size() > userOne && groups.get(groupID).allPeople().size() > userTwo) {
                                                            String temp = groups.get(groupID).allPeople().get(userOne);
                                                            groups.get(groupID).allPeople().set(userOne, groups.get(groupID).allPeople().get(userTwo));
                                                            groups.get(groupID).allPeople().set(userTwo, temp);
                                                            sender.sendMessage("§aSwapped " + groups.get(groupID).returnName().replace(';', ' ') + "§a Users: " + groups.get(groupID).allPeople().get(userTwo).replace(';', ' ') + " §a<-> " + groups.get(groupID).allPeople().get(userOne).replace(';', ' '));
                                                        } else {
                                                            sender.sendMessage("§cThose ids are not used");
                                                        }
                                                    }
                                                    catch (ArrayIndexOutOfBoundsException e) {
                                                        sender.sendMessage("§cError: §4 " + args[3] + " is not a valid GroupID");
                                                    }
                                                }
                                                else{
                                                    sender.sendMessage("§cError: §4You cannot transfer ownership. Ask Operator");
                                                }
                                            } catch (NumberFormatException nfe) {
                                                sender.sendMessage("§cCouldn't parse number§4 " + args[1] + " or " + args[2] + " or " + args[3]);
                                            }
                                        }
                                        else if(notOwner){
                                            sender.sendMessage("§cError: You are not the owner of that Group");
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not part of that Group");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }
                    else{
                        sender.sendMessage("§7Incorrect: §7/group help §c");
                    }
                }
                else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("add") ||
                            args[0].equalsIgnoreCase("addid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("addID")){
                            try{ num = Integer.parseInt(args[2]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("add")){
                            num = groupIndex(args[2]);
                        }
                        if(num > -1){
                            try {
                                if(groups.size() > num){
                                    Group current = groups.get(num);
                                    boolean allow = false;
                                    boolean notOwner = false;
                                    if(((Player)sender).hasPermission("fm.group")){
                                        allow = true;
                                    }
                                    if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                        String owner = current.allPeople().get(0);
                                        String playerSender = ((Player)sender).getName();
                                        if(owner.equals(playerSender)){
                                            allow = true;
                                        }
                                        else{
                                            notOwner = true;
                                        }
                                    }
                                    if(allow){
                                        Player target = Bukkit.getPlayer(args[1]);
                                        if (target == null){
                                            List<Player> checks = Bukkit.matchPlayer(args[1]);
                                            if (checks.size() == 1){
                                                target = (Player)checks.get(0);
                                            }
                                        }
                                        if(target != null){
                                            int has = current.addPlayer(target.getName());
                                            if(has == -1){
                                                sender.sendMessage(target.getDisplayName() + " §7is already in §7to Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                            }
                                            else{
                                                sender.sendMessage("§7Added §6Player§7 " + target.getDisplayName() + " §7to Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                                target.sendMessage("§7You have been added" + " §7to Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                            }
                                        }
                                        else{
                                            if(npcs.toString().toLowerCase().contains(args[1].toLowerCase().replace(';', ' '))){
                                                for (String curNPC : npcs)
                                                {
                                                    if (curNPC.toLowerCase().contains(args[1].toLowerCase().replace(';', ' ')))
                                                    {
                                                        int has = current.addPlayer(curNPC);
                                                        if(has == -1){
                                                            sender.sendMessage(curNPC + " §7is already in §7to Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                                        }
                                                        else {
                                                            sender.sendMessage("§7Added §eNPC§7 " + curNPC.replace(';', ' ') + " §7to Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num + 1));
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                            else{
                                                sender.sendMessage("§cError: No NPC or Player Found");
                                            }
                                        }
                                    }
                                    else if(notOwner){
                                        sender.sendMessage("§cError: You are not the creator of the group");
                                    }
                                    else{
                                        sender.sendMessage("§cError: You are not in this group");
                                    }
                                }
                                else{
                                    sender.sendMessage("§cError: This ID is not used");
                                }
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[2]);
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("remove") ||
                            args[0].equalsIgnoreCase("removeid")){
                        int num = -1;
                        
                        if (args[0].equalsIgnoreCase("removeID")){
                            try{ num = Integer.parseInt(args[2]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("remove")){
                            num = groupIndex(args[2]);
                        }
                        
                        if(num > -1){
                            try {
                                if(groups.size() > num){
                                    Group current = groups.get(num);
                                    boolean allow = false;
                                    boolean notOwner = false;
                                    boolean removeSelf = false;
                                    if(((Player)sender).hasPermission("fm.group")){
                                        allow = true;
                                    }
                                    if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                        String owner = current.allPeople().get(0);
                                        String playerSender = ((Player)sender).getName();
                                        if(owner.equals(playerSender)){
                                            allow = true;
                                        }
                                        else{
                                            notOwner = true;
                                        }
                                    }
                                    if(allow){
                                        Player target = Bukkit.getPlayer(args[1]);
                                        if (target == null){
                                            List<Player> checks = Bukkit.matchPlayer(args[1]);
                                            if (checks.size() == 1){
                                                target = (Player)checks.get(0);
                                            }
                                        }
                                        String playerSender = ((Player)sender).getName();
                                        if(target != null && playerSender.equals(target.getName()) && !((Player)sender).hasPermission("fm.group")){
                                            removeSelf = true;
                                        }
                                        if(target != null && !(removeSelf)){
                                            int result = current.removePlayer(target.getName());
                                            if(result == -1){
                                                sender.sendMessage("§7§6Player " + target.getDisplayName() + " §7is not in Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                            }
                                            else {
                                                sender.sendMessage("§7Removed §7§6Player " + target.getDisplayName() + " §7to Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                                target.sendMessage("§7You have been removed" + " §7from Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                            }
                                        }
                                        else if(removeSelf){
                                            sender.sendMessage("§cError: You cannot remove yourself");
                                        }
                                        else{
                                            int result = -1;
                                            if(current.allPeople().toString().toLowerCase().contains(args[1].toLowerCase().replace(';', ' '))){
                                                for (String curNPC : current.allPeople())
                                                {
                                                    if (curNPC.toLowerCase().contains(args[1].toLowerCase().replace(';', ' ')))
                                                    {
                                                        result = current.removePlayer(curNPC);
                                                        sender.sendMessage("§7Removed §eNPC§7 " + curNPC.replace(';', ' ') + " §7from Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                                        break;
                                                    }
                                                }
                                            }
                                            if(result == -1){
                                                sender.sendMessage("§7§eNPC " + args[1].replace(';', ' ').replace('&', '§') + " §7is not in Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));
                                            }
                                        }
                                    }
                                    else if(notOwner){
                                        sender.sendMessage("§cError: You are not the creator of the group");
                                    }
                                    else{
                                        sender.sendMessage("§cError: You are not in this group");
                                    }
                                }
                                else{
                                    sender.sendMessage("§cError: §cThis ID is not used");
                                }
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[2]);
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }         // W
                    else if (args[0].equalsIgnoreCase("rename") ||
                            args[0].equalsIgnoreCase("renameid")){
                        int num = -1;
                        
                        if (args[0].equalsIgnoreCase("renameID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("rename")){
                            num = groupIndex(args[1]);
                        }
                        if(num > -1){
                            try {
                                if(groups.size() > num){
                                    Group current = groups.get(num);
                                    boolean allow = false;
                                    boolean notOwner = false;
                                    if(((Player)sender).hasPermission("fm.group")){
                                        allow = true;
                                    }
                                    if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                        String owner = current.allPeople().get(0);
                                        String playerSender = ((Player)sender).getName();
                                        if(owner.equals(playerSender)){
                                            allow = true;
                                        }
                                        else{
                                            notOwner = true;
                                        }
                                    }
                                    if(allow){
                                        String newName = args[2].replace('&', '§');
                                        if(!((Player)sender).hasPermission("fm.group")){
                                            Bukkit.broadcast(((Player)sender).getDisplayName() + "§c has renamed " + current.returnName().replace(';', ' ') + " §cto " + newName, "fm.spy");
                                        }
                                        sender.sendMessage("§cYou have renamed " + current.returnName().replace(';', ' ') + " §cto " + newName);
                                        groups.get(num).setName(newName);
                                    }
                                    else if(notOwner){
                                        sender.sendMessage("§cError: You are not the creator of the group");
                                    }
                                    else{
                                        sender.sendMessage("§cError: You are not in this group");
                                    }
                                }
                                else{
                                    sender.sendMessage("§cError: §cThis ID is not used");
                                }
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[2]);
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }         // W
                    else if (args[0].equalsIgnoreCase("tag") ||
                            args[0].equalsIgnoreCase("tagid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("tagID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("tag")){
                            num = groupIndex(args[1]);
                        }
                        
                        if(num > -1){
                            try {
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        Group current = groups.get(num);
                                        boolean allow = false;
                                        boolean notOwner = false;
                                        if(((Player)sender).hasPermission("fm.group")){
                                            allow = true;
                                        }
                                        if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                            String owner = current.allPeople().get(0);
                                            String playerSender = ((Player)sender).getName();
                                            if(owner.equals(playerSender)){
                                                allow = true;
                                            }
                                            else{
                                                notOwner = true;
                                            }
                                        }
                                        if (allow) {
                                            if(args[2].equalsIgnoreCase("on")){
                                                sender.sendMessage("§6Tag Toggle for "+ current.returnName().replace(';', ' ') + "§6: §aON");
                                                groups.get(num).setTagToggle(true);
                                            }
                                            else if(args[2].equalsIgnoreCase("off")){
                                                sender.sendMessage("§6Tag Toggle for "+ current.returnName().replace(';', ' ') + "§6: §cOFF");
                                                groups.get(num).setTagToggle(false);
                                            }
                                            else{
                                                sender.sendMessage("§cError:§4 Unknown Entry. ON/OFF");
                                            }
                                        }
                                        else if(notOwner){
                                            sender.sendMessage("§cError: You are not the owner of that Group");
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not part of that Group");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }           // W
                    else if (args[0].equalsIgnoreCase("optag") ||
                            args[0].equalsIgnoreCase("optagid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("optagID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("optag")){
                            num = groupIndex(args[1]);
                        }
                        
                        if(num > -1){
                            try {
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        Group current = groups.get(num);
                                        boolean allow = false;
                                        if(((Player)sender).hasPermission("fm.group")){
                                            allow = true;
                                        }
                                        if (allow) {
                                            String newTag = args[2].replace('&', '§');
                                            sender.sendMessage("§7Op Tag Changed for "+ current.returnName().replace(';', ' ') + "§7 from " + current.getOpTag().replace(';', ' ') + "§7 to " + newTag.replace(';', ' '));
                                            groups.get(num).setOpTag(newTag);
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not an operator");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }         // W
                    else if (args[0].equalsIgnoreCase("view") ||
                            args[0].equalsIgnoreCase("viewid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("viewID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("view")){
                            num = groupIndex(args[1]);
                        }
                        
                        if(num > -1){
                            try {
                                int pageNum = Integer.parseInt(args[2]);
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        boolean allow = ((Group) groups.get(num)).allPeople().toString().toLowerCase().contains(((Player) sender).getName().toLowerCase());
                                        if (((Player) sender).hasPermission("fm.group") || allow) {
                                            List<String> now = groups.get(num).allPeople();
                                            try {
                                                sender.sendMessage("§8>------- §c" + groups.get(num).returnName().replace(';', ' ').replace('&', '§') + " §7| ID: §e" + (num + 1) + "§8 -------<");
                                                for (int o = pageNum * 15 - 14; o < pageNum * 15 + 1; ) {
                                                    try {
                                                        try {
                                                            if (o <= now.size()) {
                                                                sender.sendMessage("§c" + o + ".§6 " + (now.get(o - 1)).replace(';', ' ').replace('&', '§'));
                                                            }
                                                        } catch (ArrayIndexOutOfBoundsException ignored) {
                                                        }
                                                        o++;
                                                    } catch (NullPointerException ignored) {
                                                    }
                                                }
                                                int pages = now.size() / 15 + (now.size() % 15 == 0 ? 0 : 1);
                                                sender.sendMessage("§8>------- §7Page §c" + pageNum + "§7 of §c" + pages + "§8 --------<");
                                            }
                                            catch (ArrayIndexOutOfBoundsException ignored) {
                                            }
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not part of that Group");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }          // W
                    else if (args[0].equalsIgnoreCase("swap")) {
                        try {
                            int first = Integer.parseInt(args[1]) - 1;
                            int second = Integer.parseInt(args[2]) - 1;
                            if(groups.size() > first && groups.size() > second){
                                Group temp = groups.get(first);
                                groups.set(first, groups.get(second));
                                groups.set(second, temp);
                                sender.sendMessage("§aSwapped: " + groups.get(second).returnName().replace(';', ' ') + " §a<-> " + groups.get(first).returnName().replace(';', ' '));
                            }
                            else{
                                sender.sendMessage("§cThose ids are not used");
                            }
                        } catch (NumberFormatException nfe) {
                            sender.sendMessage("§cCouldn't parse number§4 " + args[1] + " or " + args[2]);
                        }
                    }          // COMPLETE
                    else{
                        sender.sendMessage("§7Incorrect: §7/group help §c");
                    }
                }
                else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("create")) {
                        groups.add(new Group(args[1].replace('&', '§')));
                        sender.sendMessage("§2Group §c" + args[1].replace('&', '§').replace(';', ' ') + " §2with ID: §c" + groups.size() + "§2 added!");
                        if(!((Player)sender).hasPermission("fm.group")){
                            groups.get(groups.size()-1).addPlayer(((Player)sender).getName());
                            Bukkit.broadcast(((Player)sender).getDisplayName() + "§2 made group §c" + args[1].replace('&', '§').replace(';', ' ') + " §2with ID: §c" + groups.size(), "fm.spy");
                        }
                    }             // COMPLETE
                    else if (args[0].equalsIgnoreCase("leave") ||
                            args[0].equalsIgnoreCase("leaveid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("leaveID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("leave")){
                            num = groupIndex(args[1]);
                        }
                        
                        if(num > -1){
                            try {
                                if(groups.size() > num){
                                    Group current = groups.get(num);
                                    boolean allow = false;
                                    boolean isOwner = false;
                                    if(((Player)sender).hasPermission("fm.group")){
                                        allow = true;
                                    }
                                    if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                        String owner = current.allPeople().get(0);
                                        String playerSender = ((Player)sender).getName();
                                        if(!(owner.equals(playerSender))){
                                            allow = true;
                                        }
                                        else{
                                            isOwner = true;
                                        }
                                    }
                                    if(allow){
                                        current.removePlayer(((Player)sender).getName());
                                        sender.sendMessage("§7You have left" + " §7Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num+1));

                                        if(!((Player)sender).hasPermission("fm.group")){
                                            Bukkit.broadcast(((Player) sender).getDisplayName() + "§7 has left" + "§7 Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num + 1), "fm.spy");
                                        }

                                        // Alert all Online Players
                                        List<String> users = current.allPeople();
                                        for (String name : users) {
                                            Player target = Bukkit.getPlayer(name);
                                            if (target == null) {
                                                List<Player> checks = Bukkit.matchPlayer(name);
                                                if (checks.size() == 1) {
                                                    target = (Player) checks.get(0);
                                                }
                                            }
                                            if (target != null) {
                                                target.sendMessage(((Player) sender).getDisplayName() + "§7 has left" + "§7 Group: §e" + current.returnName().replace(';', ' ') + "§7, ID: §e" + (num + 1));
                                            }
                                        }
                                    }
                                    else if(isOwner){
                                        sender.sendMessage("§cError: The owner cannot leave the group!");
                                    }
                                    else{
                                        sender.sendMessage("§cError: You are not in this group");
                                    }
                                }
                                else{
                                    sender.sendMessage("§cError: §cThis ID is not used");
                                }
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[1]);
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }              // W
                    else if (args[0].equalsIgnoreCase("disband") ||
                            args[0].equalsIgnoreCase("disbandid")){
                        int num = -1;
                        
                        if (args[0].equalsIgnoreCase("disbandID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("disband")){
                            num = groupIndex(args[1]);
                        }
                        if(num > -1){
                            try {
                                if(groups.size() > num){
                                    Group current = groups.get(num);
                                    boolean allow = false;
                                    boolean notOwner = false;
                                    if(((Player)sender).hasPermission("fm.group")){
                                        allow = true;
                                    }
                                    if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                        String owner = current.allPeople().get(0);
                                        String playerSender = ((Player)sender).getName();
                                        if(owner.equals(playerSender)){
                                            allow = true;
                                        }
                                        else{
                                            notOwner = true;
                                        }
                                    }
                                    if(allow){
                                        deleteConfirm.put(((Player)sender).getName(), groups.get(num).returnName());
                                        sender.sendMessage("§4Warning: §cYou are about to delete " + groups.get(num).returnName());
                                        sender.sendMessage("§e10s §cto type §7/group confirmdisband");
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                            public void run() {
                                                deleteConfirm.remove(((Player)sender).getName());
                                            }
                                        }, 200);
                                    }
                                    else if(notOwner){
                                        sender.sendMessage("§cError: You are not the creator of the group");
                                    }
                                    else{
                                        sender.sendMessage("§cError: You are not in this group");
                                    }
                                }
                                else{
                                    sender.sendMessage("§cError: §cThis ID is not used");
                                }
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[1]);
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("stats") ||
                            args[0].equalsIgnoreCase("statsid")) {
                        int num = -1;
                        
                        if (args[0].equalsIgnoreCase("statsID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("stats")){
                            num = groupIndex(args[1]);
                        }
                        
                        if(num > -1){
                            try {
                                if(groups.size() > num){
                                    Group current = groups.get(num);
                                    boolean allow = false;
                                    if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase()) || ((Player)sender).hasPermission("fm.group")){
                                        allow = true;
                                    }
                                    if(allow){
                                        sender.sendMessage("§8>>--- §7Group: §e" + current.returnName().replace(';', ' ') + " §8---<<");
                                        sender.sendMessage("§6>> §cMembers: §6" + current.allPeople().size());
                                        sender.sendMessage("§6>> §9Tag: §6" + current.getTag().replace(';', ' '));
                                        if(((Player)sender).hasPermission("fm.spy")){ sender.sendMessage("§6>> §4Op Tag: §6" + current.getOpTag().replace(';', ' ')); }
                                        if(current.getTagToggle()){ sender.sendMessage("§6>> §2Tag Toggle: §aON"); }
                                        else{ sender.sendMessage("§6>> §2Tag Toggle: §cOFF"); }
                                        sender.sendMessage("§6>> §eID: §6" + (num+1));
                                    }
                                    else{
                                        sender.sendMessage("§cError: You are not in this group");
                                    }
                                }
                                else{
                                    sender.sendMessage("§cError: §cThis ID is not used");
                                }
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[1]);
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }              // COMPLETE TEST
                    else if (args[0].equalsIgnoreCase("view") ||
                            args[0].equalsIgnoreCase("viewid")) {
                        int num = -1;
                        
                        if (args[0].equalsIgnoreCase("viewID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                            
                        }
                        else if(args[0].equalsIgnoreCase("view")){
                            num = groupIndex(args[1]);
                            
                        }
                        if(num > -1){
                            try {
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        boolean allow = ((Group) groups.get(num)).allPeople().toString().toLowerCase().contains(((Player) sender).getName().toLowerCase());
                                        if (((Player) sender).hasPermission("fm.group") || allow) {
                                            List<String> now = groups.get(num).allPeople();
                                            try {
                                                sender.sendMessage("§8>------- §c" + groups.get(num).returnName().replace(';', ' ').replace('&', '§') + " §7| ID: §e" + (num + 1) + "§8 -------<");
                                                for (int i = 1; 16 > i; i++) {
                                                    if ((now.size() > 0) && (i <= now.size())) {
                                                        sender.sendMessage("§c" + i + ". §6" + ((String) now.get(i - 1)).replace(';', ' ').replace('&', '§'));
                                                    }
                                                }
                                                int pages = now.size() / 15 + (now.size() % 15 == 0 ? 0 : 1);
                                                sender.sendMessage("§8>------- §7Page §c" + 1 + "§7 of §c" + pages + "§8 --------<");
                                            }
                                            catch (ArrayIndexOutOfBoundsException ignored) {
                                            }
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not part of that Group");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }               // COMPLETE TEST
                    else if (args[0].equalsIgnoreCase("tag") ||
                            args[0].equalsIgnoreCase("tagid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("tagID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                            
                        }
                        else if(args[0].equalsIgnoreCase("tag")){
                            num = groupIndex(args[1]);
                            
                        }
                        if(num > -1){
                            try {
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        boolean allow = ((Group) groups.get(num)).allPeople().toString().toLowerCase().contains(((Player) sender).getName().toLowerCase());
                                        if (((Player) sender).hasPermission("fm.group") || allow) {
                                            Group current = groups.get(num);
                                            sender.sendMessage("§6Tag of "+ current.returnName().replace(';', ' ') + "§6: " + current.getTag().replace(';', ' '));
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not part of that Group");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }
                    }                // COMPLETE TEST
                    else if (args[0].equalsIgnoreCase("optag") ||
                            args[0].equalsIgnoreCase("optagid")) {
                        int num = -1;
                        if (args[0].equalsIgnoreCase("optagID")){
                            try{ num = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException nfe) { sender.sendMessage("§cCouldn't parse number§4 " + args[1]); }
                        }
                        else if(args[0].equalsIgnoreCase("optag")){
                            num = groupIndex(args[1]);
                            
                        }
                        if(num > -1){
                            try {
                                if(num >= groups.size()){
                                    sender.sendMessage("§cError: Group ID does not exist");
                                }
                                else{
                                    try {
                                        if (((Player) sender).hasPermission("fm.group")) {
                                            Group current = groups.get(num);
                                            sender.sendMessage("§7Op Tag of "+ current.returnName().replace(';', ' ') + "§7: " + current.getOpTag().replace(';', ' '));
                                        }
                                        else{
                                            sender.sendMessage("§cError: §4You do not have permission");
                                        }
                                    }
                                    catch (NullPointerException ignored) {
                                    }
                                }
                            } catch (NumberFormatException nfe) {
                                try {
                                    sender.sendMessage("§cError: Couldn't resolve number §4" + args[1]);
                                } catch (ArrayIndexOutOfBoundsException aofe) {
                                    sender.sendMessage("§cError: The argument contains invalid characters!");
                                }
                            }
                        }
                        else{
                            sender.sendMessage("§cNo Group Found!");
                        }

                    }              // COMPLETE TEST
                    else if (args[0].equalsIgnoreCase("id")) {
                        String name = args[1];
                        if (groups.toString().toLowerCase().contains(name.toLowerCase()))
                        {
                            for (int k = 0; k < groups.size(); k++)
                            {
                                String currentName = groups.get(k).returnName();
                                if (currentName.toLowerCase().contains(name.toLowerCase())) {
                                    sender.sendMessage("§6ID for §cGroup: §c" + currentName.replace(';', ' ') + " §6is §c" + (k + 1));
                                    break;
                                }
                            }
                        }
                        else {
                            sender.sendMessage("§cError:§4 Couldn't find a group with that name that you are in!");
                        }

                    }            // COMPLETE
                    else if (args[0].equalsIgnoreCase("list")) {
                        try {
                            int i = Integer.parseInt(args[1]);
                            if(!((Player)sender).hasPermission("fm.group")){
                                sender.sendMessage("§8>-------- §eYour Groups§8 --------<");

                                List<Group> userGroups = new java.util.ArrayList();
                                List<Integer> userIDs = new java.util.ArrayList();

                                for(int k = 0; k < groups.size(); k++){
                                    if(((Group)groups.get(k)).allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                        userGroups.add(groups.get(k));
                                        userIDs.add(k + 1);
                                    }
                                }

                                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                                    try {
                                        try {
                                            if (o <= userGroups.size()) {
                                                sender.sendMessage("§a" + (userIDs.get(o - 1)) + ":§6 " + ((Group)userGroups.get(o - 1)).returnName().replace(';', ' ').replace('&', '§'));
                                            }
                                        } catch (ArrayIndexOutOfBoundsException ignored) {
                                        }
                                        o++;
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                int pages = userGroups.size() / 15 + (userGroups.size() % 15 == 0 ? 0 : 1);
                                sender.sendMessage("§8>------- §7Page §c1§7 of §c" + pages + "§8 --------<");
                            }
                            else{
                                sender.sendMessage("§8>-------- §9Group List§8 --------<");
                                for (int o = i * 15 - 14; o < i * 15 + 1; ) {
                                    try {
                                        try {
                                            if (o <= groups.size()) {
                                                sender.sendMessage("§c" + o + ".§6 " + ((Group)groups.get(o - 1)).returnName().replace(';', ' ').replace('&', '§'));
                                            }
                                        } catch (ArrayIndexOutOfBoundsException ignored) {
                                        }
                                        o++;
                                    } catch (NullPointerException ignored) {
                                    }
                                }
                                int pages = groups.size() / 15 + (groups.size() % 15 == 0 ? 0 : 1);
                                sender.sendMessage("§8>------- §7Page §c1§7 of §c" + pages + "§8 --------<");
                            }
                        } catch (NumberFormatException nfe) {
                            try {
                                sender.sendMessage("§cError: Couldn't resolve number §4" + args[2]);
                            } catch (ArrayIndexOutOfBoundsException aofe) {
                                sender.sendMessage("§cError: The argument contains invalid characters!");
                            }
                        }
                    }          // COMPLETE
                    else{
                        sender.sendMessage("§7Incorrect: §7/group help §c");
                    }
                }
                else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("clear")) {
                        if(((Player)sender).hasPermission("fm.group")){
                            groups.clear();
                            sender.sendMessage("§aGroups Cleared §c");
                        }
                        else{
                            sender.sendMessage("§cError:§4 You do not have permission for this §c");
                        }
                    }              // COMPLETE
                    else if (args[0].equalsIgnoreCase("list")) {
                        if(!((Player)sender).hasPermission("fm.group")){
                            sender.sendMessage("§8>-------- §eYour Groups§8 --------<");

                            List<Group> userGroups = new java.util.ArrayList();
                            List<Integer> userIDs = new java.util.ArrayList();

                            for(int k = 0; k < groups.size(); k++){
                                if(((Group)groups.get(k)).allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                    userGroups.add(groups.get(k));
                                    userIDs.add(k + 1);
                                }
                            }

                            for (int i = 0; 16 > i; i++) {
                                if(userGroups.size() > 0 && i < userIDs.size()) {
                                    sender.sendMessage("§a" + userIDs.get(i) + ": §6" + ((Group) userGroups.get(i)).returnName().replace(';', ' ').replace('&', '§'));

                                }
                            }

                            int pages = userGroups.size() / 15 + (userGroups.size() % 15 == 0 ? 0 : 1);
                            sender.sendMessage("§8>------- §7Page §c1§7 of §c" + pages + "§8 --------<");
                        }
                        else{
                            sender.sendMessage("§8>-------- §9Group List§8 --------<");
                            for (int i = 1; 16 > i; i++) {
                                if ((groups.size() > 0) && (i <= groups.size())) {
                                    sender.sendMessage("§c" + i + ": §6" + ((Group)groups.get(i - 1)).returnName().replace(';', ' ').replace('&', '§'));
                                }
                            }
                            int pages = groups.size() / 15 + (groups.size() % 15 == 0 ? 0 : 1);
                            sender.sendMessage("§8>------- §7Page §c1§7 of §c" + pages + "§8 --------<");
                        }
                    }          // COMPLETE
                    else if (args[0].equalsIgnoreCase("help")) {
                        sender.sendMessage("§8>>-- §eGroup Commands §8--<<§c");
                        sender.sendMessage("§9If you want to use the ID instead. Do /group (cmd)ID");
                        sender.sendMessage("§8> §ccreate §oname§c §7> Create a Group");
                        sender.sendMessage("§8> §crename §egroupName§c name§c §7> Rename a Group");
                        sender.sendMessage("§8> §cview §egroupName§c pageNumber§c §7> View Members in a group");
                        sender.sendMessage("§8> §clist §opageNumber§c §7> View all your groups");
                        sender.sendMessage("§8> §cadd §oname §egroupName§c §7> Add a Member");
                        sender.sendMessage("§8> §cremove §oname §egroupName§c §7> Remove a Member");
                        sender.sendMessage("§8> §cstats §egroupName§c §c§7> View Group Statistics");
                        sender.sendMessage("§8> §cid §egroupName§c §c§7> View Group Statistics");
                        sender.sendMessage("§8> §ctag §egroupName§c set/on/off NAME §c§7> Edit Group Tag");
                        sender.sendMessage("§8> §cleave §egroupName§c §c§7> Leave a Group");
                        sender.sendMessage("§8> §cdisband §egroupName§c  §c§7> Disband a Group");
                        sender.sendMessage("§8> §cswitchuser §egroupName§c pos1 pos2§c §7> Swap Member Positions");
                    }          // COMPLETE
                    else if (args[0].equalsIgnoreCase("admin")) {
                        if(((Player)sender).hasPermission("fm.group")){
                            sender.sendMessage("§8>>-- §4Group Admin §8--<<§c");
                            sender.sendMessage("§8> §7clear§c > Delete All groups");
                            sender.sendMessage("§8> §7swap ID ID§c > Swap Group IDS");
                            sender.sendMessage("§8> §7optag groupname newValue ID§c > Change OP Tag");
                        }
                        else{
                            sender.sendMessage("§cError:§4 You do not have permission for this §c");
                        }
                    }         // COMPLETE
                    else if (args[0].equalsIgnoreCase("message")) {
                        if(((Player)sender).hasPermission("fm.group")){
                            sender.sendMessage("§8>>-- §4Group Messages §8--<<§c");
                            sender.sendMessage("§8> §7fga §eNAME §4GROUP_NAME");
                            sender.sendMessage("§8> §7fgan §dNPC_NAME §4GROUP_NAME");
                            sender.sendMessage("§8> §7idg §cNPC_ID §4GROUP_NAME");
                            sender.sendMessage("§8> §7idig §cNPC_ID §9GROUP_ID");
                            sender.sendMessage("§8> §7ng §cNPC_ID §2PLAYER");
                            sender.sendMessage("§8> §7nga §dNPC_NAME §2PLAYER");
                        }
                        else{
                            sender.sendMessage("§cError:§4 You do not have permission for this §c");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("confirmdisband")){
                        String disband = deleteConfirm.get(((Player)sender).getName());
                        if(disband != null){
                            int num = groupIndex(disband);
                            if(num > -1){
                                try {
                                    if(groups.size() > num){
                                        Group current = groups.get(num);
                                        boolean allow = false;
                                        boolean notOwner = false;
                                        if(((Player)sender).hasPermission("fm.group")){
                                            allow = true;
                                        }
                                        if(current.allPeople().toString().toLowerCase().contains(((Player)sender).getName().toLowerCase())){
                                            String owner = current.allPeople().get(0);
                                            String playerSender = ((Player)sender).getName();
                                            if(owner.equals(playerSender)){
                                                allow = true;
                                            }
                                            else{
                                                notOwner = true;
                                            }
                                        }
                                        if(allow){
                                            deleteConfirm.remove(((Player)sender).getName());
                                            // Alert all Online Players
                                            List<String> users = current.allPeople();
                                            for (String name : users) {
                                                Player target = Bukkit.getPlayer(name);
                                                if (target == null) {
                                                    List<Player> checks = Bukkit.matchPlayer(name);
                                                    if (checks.size() == 1) {
                                                        target = (Player) checks.get(0);
                                                    }
                                                }
                                                if (target != null && !(((Player)sender).getName().equals(target.getName()))){
                                                    target.sendMessage(current.returnName().replace(';', ' ') + "§c has been disbanded");
                                                }
                                            }

                                            sender.sendMessage("§cYou have disbanded " + current.returnName().replace(';', ' '));
                                            if(!((Player)sender).hasPermission("fm.group")){
                                                Bukkit.broadcast(((Player)sender).getDisplayName() + "§c has been disbanded " + current.returnName().replace(';', ' '), "fm.spy");
                                            }
                                            groups.remove(num);
                                        }
                                        else if(notOwner){
                                            sender.sendMessage("§cError: You are not the creator of the group");
                                        }
                                        else{
                                            sender.sendMessage("§cError: You are not in this group");
                                        }
                                    }
                                    else{
                                        sender.sendMessage("§cError: §cThis ID is not used");
                                    }
                                } catch (NumberFormatException nfe) {
                                    sender.sendMessage("§cError: §cCouldn't parse number§4 " + args[1]);
                                }
                            }
                            else{
                                sender.sendMessage("§cNo Group Found!");
                            }
                        }
                        else{
                            sender.sendMessage("§4Time Ran Out");
                        }
                    }
                    else{
                        sender.sendMessage("§7Incorrect: §7/group help §c");
                    }
                }
                else{
                    sender.sendMessage("§7Incorrect: §7/group help §c");
                }

                groupString.clear();
                for(Group g: groups){
                    groupString.add(g.serialize());
                }

                groupConfig.getGroupcfg().set("Groups", groupString);
                groupConfig.saveGroups();
            }
            else if (cmd.getName().equalsIgnoreCase("fs")) {
                if (args.length >= 1)
                {
                    if (who.get(sender.getName()) != null)
                    {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }

                        allArgs = sb.toString().trim();
                        allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                        allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                        allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('+', getConfig().getString("ChatFormat")).replace("%FM%", ChatColor.translateAlternateColorCodes('&', (String)who.get(sender.getName()))).replace("%MSG%", allArgs));
                    }
                    else {
                        sender.sendMessage("§cYou don't have a fake name set. Set it with §4/fmn");
                    }

                }
            }
            else if (cmd.getName().equalsIgnoreCase("fga")) {
                if (args.length > 2)
                {
                    int groupNum = -1;
                    int npcNum = -1;
                    boolean replyTo = true;
                    boolean findPlayer = true;
                    boolean parseFail = true;
                    boolean rando = false;
                        if (label.equalsIgnoreCase("ng"))
                        {
                            try{
                                npcNum = Integer.parseInt(args[0]);
                                Player replyTarget = Bukkit.getPlayer(args[1]);
                                if (replyTarget == null){
                                    List<Player> checks = Bukkit.matchPlayer(args[1]);
                                    if (checks.size() == 1){
                                        replyTarget = (Player)checks.get(0);
                                    }
                                }
                                if(replyTarget != null) {
                                    if (groupReply.containsKey(replyTarget.getName())){
                                        String groupName = groupReply.get(replyTarget.getName());
                                        groupNum = groupIndex(groupName) + 1;
                                    }
                                    else{
                                        replyTo = false;
                                    }
                                }
                                else{
                                    findPlayer = false;
                                }
                            }
                            catch (NumberFormatException nfe) {
                                sender.sendMessage("§cCouldn't parse number§4 " + args[0]);
                                parseFail = false;
                            }
                        }
                        else if (label.equalsIgnoreCase("nga"))
                        {
                            npcNum = npcIndex(args[0]) + 1;
                            Player replyTarget = Bukkit.getPlayer(args[1]);
                            if (replyTarget == null){
                                List<Player> checks = Bukkit.matchPlayer(args[1]);
                                if (checks.size() == 1){
                                    replyTarget = (Player)checks.get(0);
                                }
                            }
                            if(replyTarget != null) {
                                if (groupReply.containsKey(replyTarget.getName())){
                                    String groupName = groupReply.get(replyTarget.getName());
                                    groupNum = groupIndex(groupName) + 1;
                                }
                                else{
                                    replyTo = false;
                                }
                            }
                            else{
                                findPlayer = false;
                            }
                        }
                        else if (label.equalsIgnoreCase("idig"))
                        {
                            try{
                                npcNum = Integer.parseInt(args[0]);
                                groupNum = Integer.parseInt(args[1]);
                            }
                            catch (NumberFormatException nfe) {
                                sender.sendMessage("§cCouldn't parse number§4 " + args[0] + " or " + args[1]);
                                parseFail = false;
                            }
                        }
                        else if (label.equalsIgnoreCase("idg"))
                        {
                            try{
                                npcNum = Integer.parseInt(args[0]);
                            }
                            catch (NumberFormatException nfe) {
                                sender.sendMessage("§cCouldn't parse number§4 " + args[0]);
                                parseFail = false;
                            }
                            groupNum = groupIndex(args[1]) + 1;
                        }
                        else if (label.equalsIgnoreCase("fgan"))
                        {
                            npcNum = npcIndex(args[0]) + 1;
                            groupNum = groupIndex(args[1]) + 1;
                        }
                        else {
                            rando = true;
                            npcNum = 1;
                            groupNum = groupIndex(args[1]) + 1;
                        }
                        if((groupNum > 0) && (npcNum > 0) && (npcNum <= npcs.size()) &&
                                groupNum <= groups.size() && findPlayer && replyTo && parseFail){
                            try {
                                try
                                {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 2; i < args.length; i++) {
                                        sb.append(args[i]).append(" ");
                                    }

                                    allArgs = sb.toString().trim();
                                    allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                                    allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                                    allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                                    String s = groups.get(groupNum - 1).returnName();
                                    List<String> view = groups.get(groupNum - 1).allPeople();
                                    Group g = groups.get(groupNum - 1);

                                    for(int j = 0; j < view.size(); j++){
                                        Player target = Bukkit.getPlayer(view.get(j));
                                        if (target == null){
                                            List checks = Bukkit.matchPlayer(view.get(j));
                                            if (checks.size() == 1){
                                                target = (Player)checks.get(0);
                                            }
                                        }
                                        if(target != null){
                                            if(!(target.equals(sender))){

                                                String output = "";
                                                if(g.getTagToggle() && !(target.hasPermission("npc.off"))){
                                                    output += "§8[" + g.getTag().replace(';', ' ') + "§8]";
                                                }

                                                if(rando){
                                                    target.sendMessage(output + "§8[§4<< §6" + args[0].replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                                                }
                                                else{
                                                    target.sendMessage(output + "§8[§4<< §6" + (String)npcs.get(npcNum - 1).replace(';', ' ') + "§8] §r" + allArgs);
                                                }
                                                groupReply.remove(target.getName());
                                                groupReply.put(target.getName(), s);
                                            }
                                        }
                                    }
                                    if(!(sender.getName().equalsIgnoreCase("CONSOLE"))) {
                                        if(rando){
                                            sender.sendMessage("§c[N] §8[§6" + (String) args[0].replace('&', '§').replace(';', ' ') + "§7 >> §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs);
                                            Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + (String) args[0].replace('&', '§').replace(';', ' ') + "§7 > §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs);
                                        }
                                        else{
                                            sender.sendMessage("§c[N] §8[§6" + (String) npcs.get(npcNum - 1) + "§7 >> §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs);
                                            Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + (String) npcs.get(npcNum - 1).replace(';', ' ') + "§7 > §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs);
                                        }
                                    }
                                    if(sender.getName().equalsIgnoreCase("CONSOLE")){
                                        if(rando){
                                            Bukkit.broadcast("§2[N] §4§o[CON] §8[§6" + (String) args[0].replace('&', '§').replace(';', ' ') + "§7 > §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                            Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + (String) args[0].replace('&', '§').replace(';', ' ') + "§7 > §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs);                                        }
                                        else{
                                            Bukkit.broadcast("§2[N] §4§o[CON] §8[§6" + (String) npcs.get(npcNum - 1).replace(';', ' ') + "§7 > §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs, "fm.spy");
                                            Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + (String) npcs.get(npcNum - 1).replace(';', ' ') + "§7 > §6" + groups.get(groupNum - 1).getOpTag().replace(';', ' ') + "§8] §r" + allArgs);
                                        }
                                    }

                                } catch (IndexOutOfBoundsException iobe) {
                                    sender.sendMessage("§cCouldn't find NPC with id§4 " + args[0].replace(';', ' ') + "§cor Group with id§4 " + args[1].replace(';', ' '));
                                }

                                sb = new StringBuilder();
                            }
                            catch (NumberFormatException nfe)
                            {
                                sender.sendMessage("§cCouldn't parse number§4 " + args[0]);
                            }
                        }
                        else {
                            if(!parseFail){
                                sender.sendMessage("§cError: §4Parse Fail");
                            }
                            else if(!replyTo){
                                sender.sendMessage("§cError: §4No group to reply to");
                            }
                            else if(!findPlayer){
                                sender.sendMessage("§cError: §4Failed to find a player");
                            }
                            else if(npcNum-1 <= -1){
                                sender.sendMessage("§cError: §4Failed to find NPC ID");
                            }
                            else if(groupNum-1 <= -1){
                                sender.sendMessage("§cError: §4Failed to find Group ID");
                            }
                            else{
                                sender.sendMessage("§cInvalid Syntax. Check /help fga");
                            }
                        }
                }
                else{
                    sender.sendMessage("§cNot Enough Arguments");
                }
            }
            else if (cmd.getName().equalsIgnoreCase("fma")) {
                if (args.length > 2)
                {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null)
                    {
                        if (label.equalsIgnoreCase("idm") || label.equalsIgnoreCase("nma"))
                        {
                            int npcNum = -1;
                            if (label.equalsIgnoreCase("idm")){
                                try{
                                    npcNum = Integer.parseInt(args[0]);
                                }
                                catch (NumberFormatException nfe)
                                {
                                    sender.sendMessage("§cCouldn't parse number§4 " + args[0]);
                                }
                            }
                            else{
                                if (npcs.toString().toLowerCase().contains(args[0].toLowerCase().replace(';', ' ')))
                                {
                                    for (String npc : npcs) {
                                        if (npc.toLowerCase().contains(args[0].toLowerCase().replace(';', ' '))) {
                                            npcNum = (npcs.indexOf(npc) + 1);
                                        }
                                    }
                                } else {
                                    sender.sendMessage("§cCouldn't find NPC§4 " + args[0]);
                                }
                            }
                            if(npcNum != -1){
                                try {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 2; i < args.length; i++) {
                                        sb.append(args[i]).append(" ");
                                    }

                                    allArgs = sb.toString().trim();
                                    allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                                    allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                                    allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                                    target.sendMessage("§8[§c< §6" + (String)npcs.get(npcNum - 1) + "§8] §r" + allArgs);
                                    if(!(target.getName().equals(sender.getName())) && !(sender.getName().equalsIgnoreCase("CONSOLE"))) {
                                        sender.sendMessage("§2[N] §8[§6" + (String) npcs.get(npcNum - 1) + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                    }
                                    if(sender.getName().equalsIgnoreCase("CONSOLE")){
                                        if(!(target.hasPermission("fm.spy"))){
                                            Bukkit.broadcast("§2[N] §4§o[CON] §8[§6" + (String) npcs.get(npcNum - 1) + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs, "fm.spy");
                                        }
                                        Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + (String) npcs.get(npcNum - 1) + "§7 > §6" + target.getDisplayName() + "§8] §r" + allArgs);
                                    }
                                    reply.remove(target.getName());
                                    reply2.remove(target.getName());

                                    reply.put(target.getName(), (String)npcs.get(npcNum - 1));
                                    reply2.put((String)npcs.get(npcNum - 1), target.getName());
                                } catch (IndexOutOfBoundsException iobe) {
                                    sender.sendMessage("§cCouldn't find NPC with id§4 " + args[0]);
                                }
                                sb = new StringBuilder();
                            }
                        }
                        else {
                            sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }

                            allArgs = sb.toString().trim();
                            allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                            allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                            allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                            target.sendMessage("§8[§c< §6" + args[0].replace('&', '§').replace(';', ' ') + "§8] §r" + allArgs);
                            if(sender.getName().equalsIgnoreCase("CONSOLE")){
                                Bukkit.getConsoleSender().sendMessage("§2[N] §8[§6" + args[0].replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8]§r " + allArgs);
                                if(!(target.hasPermission("fm.spy"))){
                                    Bukkit.broadcast("§2[N] §4§o[CON] §8[§6" + args[0].replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8]§r " + allArgs, "fm.spy");
                                }
                            }
                            else if(!(target.getName().equals(sender.getName()))){
                                sender.sendMessage("§2[N] §8[§6" + args[0].replace('&', '§').replace(';', ' ') + "§7 > §6" + target.getDisplayName() + "§8]§r " + allArgs);
                            }
                            who.put("CONSOLE", args[0].replace('&', '§').replace(';', ' '));
                            fms.add(args[0].replace('&', '§').replace(';', ' '));

                            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                public void run() {
                                    fms.remove(args[0]);
                                }
                            }, 1200 * getConfig().getInt("Time"));

                            reply.remove(target.getName());
                            reply2.remove(target.getName());

                            reply.put(target.getName(), args[0]);
                            reply2.put(args[0], target.getName());
                        }
                    }
                    else {
                        sender.sendMessage("§cCouldn't find player§4 " + args[1]);
                    }
                }
                else {
                    sender.sendMessage("§cInvalid Syntax. Check /help fma");
                }
            }
            else if (cmd.getName().equalsIgnoreCase("fsa")) {
                if (args.length > 1)
                {
                    if (label.equalsIgnoreCase("fsid")) {
                        try {
                            Integer.parseInt(args[0]);
                            try {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }

                                allArgs = sb.toString().trim();
                                allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                                allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                                allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('+', getConfig().getString("ChatFormat")).replace("%FM%", (CharSequence)npcs.get(Integer.parseInt(args[0]) - 1)).replace("%MSG%", allArgs));
                            }
                            catch (IndexOutOfBoundsException iobe) {
                                sender.sendMessage("§cCouldn't find NPC with id§4 " + args[0]);
                            }


                            sb = new StringBuilder();
                        }
                        catch (NumberFormatException nfe)
                        {
                            sender.sendMessage("§cCouldn't parse number§4 " + args[0]);
                        }
                    }
                    else{
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }

                        allArgs = sb.toString().trim();
                        allArgs = allArgs.replace("&a", "§a").replace("&b", "§b").replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&l", "§l").replace("&o", "§o").replace("&k", "§k");
                        allArgs = allArgs.replace("&A", "§a").replace("&B", "§b").replace("&C", "§c").replace("&D", "§d").replace("&E", "§e").replace("&F", "§f").replace("&L", "§l").replace("&O", "§o").replace("&K", "§k");
                        allArgs = allArgs.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4").replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9");

                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('+', getConfig().getString("ChatFormat")).replace("%FM%", args[0].replace('&', '§').replace(';', ' ')).replace("%MSG%", allArgs));
                    }

                }
                else {
                    sender.sendMessage("§cInvalid Syntax. Check /help fsa");
                }
            }   // >>> CHANGE   V4
        }
        return true;
    }
}


