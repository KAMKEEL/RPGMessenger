package kamkeel.RPGMessenger.Control;

import kamkeel.RPGMessenger.Group;
import kamkeel.RPGMessenger.Member;
import kamkeel.RPGMessenger.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static kamkeel.RPGMessenger.Util.ColorConvert.convertToRaw;

public class GroupControl {

    public List<Group> groups = new java.util.ArrayList();


    public void addGroup(String name, String tag, String opTag, List<Member> memberList){
        groups.add(new Group(name, tag, opTag, memberList));
    }

    ////////////////////////
    // Group Control

    public boolean validIndex(int index){
        return (listLength() > index && index > -1);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Group getGroup(int index) {
        return groups.get(index);
    }

    public int groupIndex(String groupName){
        groupName = convertToRaw(groupName).toLowerCase();
        if (groups.toString().toLowerCase().contains( groupName ))
        {

            for(int i = 0; i < listLength(); i++){
                if ((getGroupTag(i).toLowerCase()).equals( groupName )) {
                    return (i);
                }
            }

            for(int i = 0; i < listLength(); i++){
                if ((getGroupName(i).toLowerCase()).startsWith( groupName )) {
                    return (i);
                }
            }

            for(int i = 0; i < listLength(); i++){
                if ((getGroupOpTag(i).toLowerCase()).startsWith( groupName )) {
                    return (i);
                }
            }

            return(-1);
        }
        return(-1);
    }

    public boolean nameAlreadyExists(Group group){
        String groupName = group.getName().toLowerCase();
        if (groups.toString().toLowerCase().contains( groupName ))
        {
            for(int i = 0; i < listLength(); i++){
                if ((getGroupName(i).toLowerCase()).equals( groupName )) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean tagAlreadyExists(String tag){
        String groupTag = convertToRaw(tag).toLowerCase();
        if (groups.toString().toLowerCase().contains( groupTag ))
        {
            for(int i = 0; i < listLength(); i++){
                if ((getGroupTag(i).toLowerCase()).equals( groupTag )) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean groupSwap(int indexOne, int indexTwo){
        if(validIndex(indexOne) && validIndex(indexTwo)){
            Group first = groups.get(indexOne);
            Group second = groups.get(indexTwo);
            groups.set(indexOne, second);
            groups.set(indexTwo, first);
            return true;
        }
        return false;
    }

    public boolean groupAdd(Group group){
        if(group != null){
            groups.add(group);
            return true;
        }
        return false;
    }

    public String groupRemove(String groupName){
        if(groupName != null && !groupName.trim().isEmpty()){
            int findIndex = groupIndex(groupName);
            if(findIndex != -1){
                String displayName = getGroupDisplayName(findIndex);
                groups.remove(findIndex);
                return displayName;
            }
            return null;
        }
        return null;
    }

    public String getGroupName(int index) {
        if (validIndex(index)) {
            return groups.get(index).getName();
        }
        return "--ERROR";
    }

    public String getGroupTag(int index) {
        if (validIndex(index)) {
            return groups.get(index).getTag();
        }
        return "--ERROR";
    }

    public String getGroupDisplayTag(int index) {
        if (validIndex(index)) {
            return groups.get(index).getDisplayTag();
        }
        return "--ERROR";
    }

    public String getGroupOpTag(int index) {
        if (validIndex(index)) {
            return groups.get(index).getOpTag();
        }
        return "--ERROR";
    }

    public String getGroupOpDisplayTag(int index) {
        if (validIndex(index)) {
            return groups.get(index).getDisplayOpTag();
        }
        return "--ERROR";
    }

    public String getGroupDisplayName(int index) {
        if (validIndex(index)) {
            return groups.get(index).getDisplayName();
        }
        return "--ERROR";
    }

    public boolean renameGroup(int index, String name) {
        if (validIndex(index)) {
            groups.get(index).rename(name);
            return true;
        }
        return false;
    }

    public boolean setGroupTag(int index, String tag) {
        if (validIndex(index)) {
            groups.get(index).setTag(tag);
            return true;
        }
        return false;
    }

    public boolean setGroupOpTag(int index, String tag) {
        if (validIndex(index)) {
            groups.get(index).setOpTag(tag);
            return true;
        }
        return false;
    }

    public boolean hasEditPermission(int index, CommandSender sender, boolean mustBeOwner) {
        if(!(sender instanceof Player)){
            return true;
        }
        else if(((Player)sender).hasPermission("rpg.admin")){
            return true;
        }
        else{
            if(mustBeOwner){
                return getGroup(index).isGroupOwner(((Player)sender));
            }
            return getGroup(index).isGroupMod(((Player)sender)) || getGroup(index).isGroupOwner(((Player)sender));
        }
    }

    public boolean hasAdminPermission(CommandSender sender) {
        if(!(sender instanceof Player)){
            return true;
        }
        else return ((Player) sender).hasPermission("rpg.admin");
    }

    public int listLength(){
        return groups.size();
    }

    @Override
    public String toString() {
        return groups.toString();
    }

    ////////////////////////

}
