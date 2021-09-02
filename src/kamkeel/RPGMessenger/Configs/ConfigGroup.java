package kamkeel.RPGMessenger.Configs;

import kamkeel.RPGMessenger.Control.GroupControl;
import kamkeel.RPGMessenger.Group;
import kamkeel.RPGMessenger.Member;
import kamkeel.RPGMessenger.RPGMessenger;
import kamkeel.RPGMessenger.Util.RPGStringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class ConfigGroup {

    private static final RPGMessenger plugin = RPGMessenger.getPlugin(RPGMessenger.class);

    // Folder Control
    public static File folderFile;

    // --------------------------
    // Files & File Configs Here
    public List<FileConfiguration> groupcfgs = new java.util.ArrayList();
    public List<File> groupsfiles = new java.util.ArrayList();
    public List<String> groupNames = new java.util.ArrayList();
    // --------------------------
    public static FileConfiguration groupYamlConfig;
    public static  File groupYaml;
    // --------------------------

    // Update Group
    public void updateGroup(int index, Group group) {
        FileConfiguration config = groupcfgs.get(index);

        config.set("Name", group.getName());
        config.set("DisplayName", convertColorSign(group.getDisplayName()));
        config.set("Tag", convertColorSign(group.getDisplayTag()));
        config.set("OpTag", convertColorSign(group.getDisplayOpTag()));

        for(int i = 0; i < group.listLength(); i++){
            Member currentMem = group.getMember(i);
            config.set("Members." + convertToRawPlayer(currentMem.getName()) + ".DisplayName", convertColorSign(currentMem.getDisplayName()));
            config.set("Members." + convertToRawPlayer(currentMem.getName()) + ".IsPlayer", currentMem.getIsPlayer());
            config.set("Members." + convertToRawPlayer(currentMem.getName()) + ".Type", currentMem.getType());
        }
    }

    // Load Groups
    public GroupControl loadGroups() {
        groupNames = groupYamlConfig.getStringList("Groups");
        GroupControl groupControl = new GroupControl();

        for(int i = 0; i < groupNames.size(); i++){
            File groupFile  = new File(plugin.getDataFolder(), ("groups" + File.separator + groupNames.get(i) + ".yml"));
            if(!groupFile.exists()){
                groupNames.remove(i);
                i--;
            }
            else{
                FileConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupFile);
                String GroupName = groupConfig.getString("Name");
                String DisplayName = groupConfig.getString("DisplayName");
                String Tag = groupConfig.getString("Tag");
                String OpTag = groupConfig.getString("OpTag");
                if(GroupName == null || DisplayName == null || Tag == null || OpTag == null){
                    groupNames.remove(i);
                    i--;
                }
                else{

                    List<Member> memberList = new java.util.ArrayList();
                    ConfigurationSection Members = groupConfig.getConfigurationSection("Members");
                    if(Members != null){
                        Collection<String> memberCollection = Members.getKeys(false);
                        for(String category : memberCollection){
                            String memberDisplayName = groupConfig.getString("Members." + category + ".DisplayName");
                            boolean memberIsPlayer = groupConfig.getBoolean("Members." + category + ".IsPlayer");
                            int memberType = groupConfig.getInt("Members." + category + ".Type");
                            if(memberDisplayName != null){
                                memberList.add(new Member(category, memberDisplayName, memberIsPlayer, memberType));
                            }
                        }
                    }
                    groupsfiles.add(groupFile);
                    groupcfgs.add(YamlConfiguration.loadConfiguration(groupFile));
                    groupControl.addGroup(DisplayName, Tag, OpTag, memberList);
                }
            }
        }
        SaveGroupList();
        return groupControl;
    }

    // Update Group
    public void SaveGroupList() {

        FileConfiguration config = groupYamlConfig;
        config.set("Groups", groupNames);

        saveGroupYaml();
        reloadGroupYaml();
    }

    // Group Yaml Control

    public FileConfiguration getConfig() {
        return groupYamlConfig;
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        groupYaml = new File(plugin.getDataFolder(), ("group" + ".yml"));
        if (!groupYaml.exists()) {
            plugin.saveResource(("group" + ".yml"), false);
        }

        reloadGroupYaml();
    }

    public void saveGroupYaml() {
        try {
            groupYamlConfig.save(groupYaml);
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + ChatColor.RED + "Could not save the " + "group" + ".yml file");
        }
    }

    public void reloadGroupYaml() {
        groupYamlConfig = YamlConfiguration.loadConfiguration(groupYaml);
    }

    // Each Group Identity

    public List<FileConfiguration> getGroupcfgs() {
        return groupcfgs;
    }

    public void createFolder() {
        try {
            folderFile = new File(plugin.getDataFolder() + File.separator + "groups" + File.separator);
            if(!folderFile.exists()){
                folderFile.mkdirs();
            }
        } catch(SecurityException e) {
            System.out.println("Error Making Groups Folder");
        }
    }

    public int listLength(){ return groupNames.size(); }

    public boolean validIndex(int index){
        return (listLength() > index && index > -1);
    }

    public int getGroupIndex(String name){
        if (groupNames.toString().contains( name ))
        {
            for(int i = 0; i < listLength(); i++){
                if (groupNames.get(i).equals( name )) {
                    return (i);
                }
            }
            return(-1);
        }
        return(-1);
    }

    public boolean groupSwap(int indexOne, int indexTwo){
        if(validIndex(indexOne) && validIndex(indexTwo)){
            FileConfiguration firstConfig = groupcfgs.get(indexOne);
            FileConfiguration secondConfig = groupcfgs.get(indexTwo);

            groupcfgs.set(indexOne, secondConfig);
            groupcfgs.set(indexTwo, firstConfig);


            File firstFile = groupsfiles.get(indexOne);
            File secondFile = groupsfiles.get(indexTwo);

            groupsfiles.set(indexOne, secondFile);
            groupsfiles.set(indexTwo, firstFile);

            String firstName = groupNames.get(indexOne);
            String secondName = groupNames.get(indexTwo);

            groupNames.set(indexOne, secondName);
            groupNames.set(indexTwo, firstName);

            return true;
        }
        return false;
    }

    public void renameGroupFile(int index, String oldName, String newName){
        File groupsfile  = new File(plugin.getDataFolder(), ("groups" + File.separator + oldName + ".yml"));
        File otherFile = new File(plugin.getDataFolder(), ("groups" + File.separator + newName + ".yml"));

        if(otherFile.exists()){
            otherFile.delete();
        }

        if(groupsfile.renameTo(otherFile)){
            groupNames.set(index, newName);

            File renamedFile = new File(plugin.getDataFolder(), ("groups" + File.separator + newName + ".yml"));
            groupsfiles.set(index, renamedFile);
            groupcfgs.set(index, YamlConfiguration.loadConfiguration(renamedFile));

            SaveGroupList();
        }
    }

    public void createConfigFile(String fileName) {
        File groupsfile  = new File(plugin.getDataFolder(), ("groups" + File.separator + fileName + ".yml"));
        try {
            if ( groupsfile.exists() ) {
                groupsfile.delete();
                groupsfiles.remove(groupsfile);
                groupNames.remove(fileName);
            }
            groupsfile.createNewFile();
            groupsfiles.add(groupsfile);
            groupcfgs.add(YamlConfiguration.loadConfiguration(groupsfile));
            groupNames.add(fileName);
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + ChatColor.RED + "Could not save the " + fileName + ".yml file");
        }

        saveGroups();
        reloadGroups();
    }

    public void deleteConfigFile(String fileName) {
        int index = getGroupIndex(fileName);
        if (groupsfiles.get(index).delete()) {
            groupcfgs.remove(index);
            groupsfiles.remove(index);
            groupNames.remove(index);
        }

        saveGroups();
        reloadGroups();
    }

    public void saveGroup(int index) {
        try {
            groupcfgs.get(index).save(groupsfiles.get(index));
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + "Could not save a groups file");
        }
    }

    public void saveGroups() {
        for(int i = 0; i < groupsfiles.size(); i++){
            try {
                groupcfgs.get(i).save(groupsfiles.get(i));
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(RPGStringHelper.TAG + "Could not save a groups file");
            }
        }
    }

    public void reloadGroups() {
        for(int i = 0; i < groupsfiles.size(); i++){
            groupcfgs.set(i, YamlConfiguration.loadConfiguration(groupsfiles.get(i)));
        }
    }

}