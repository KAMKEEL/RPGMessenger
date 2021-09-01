package kamkeel.RPGMessenger;

import org.bukkit.entity.Player;

import java.util.List;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class Group {

    public List<Member> members = new java.util.ArrayList<>();

    private String name; // Without Color Codes or Spaces (;)
    private String displayName; // With Color Codes and Spaces (;)

    private String tag;
    private String displayTag;

    private String opTag;
    private String displayOpTag;


    public Group(String _name){
        name = convertToRaw(_name);
        displayName = convertSpace(convertColor(_name));

        tag = convertToRaw(_name);
        displayTag = convertSpace(convertColor(_name));

        opTag = convertToRaw(_name);
        displayOpTag = convertSpace(convertColor(_name));
    }

    public Group(String _name, String _tag, String _opTag, List<Member> memberList){
        name = convertToRaw(_name);
        displayName = convertSpace(convertColor(_name));

        tag = convertToRaw(_tag);
        displayTag = convertSpace(convertColor(_tag));

        opTag = convertToRaw(_opTag);
        displayOpTag = convertSpace(convertColor(_opTag));

        members = memberList;
    }

    public String getName(){
        return name;
    }
    public String getDisplayName(){
        return displayName;
    }

    public void rename(String newName){
        name = convertToRaw(newName);
        displayName = convertSpace(convertColor(newName));
    }

    public boolean hasMember(String who, boolean player){
        who = convertToRawPlayer(who).toLowerCase();

        if(player){
            for (Member person : members) {
                if (person.getName().toLowerCase().equals(who) && person.MemberIsPlayer()) {
                    return true;
                }
            }
        }
        else{
            for (Member person : members) {
                if (person.getName().toLowerCase().equals(who) && !person.MemberIsPlayer()) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getIndex(String _name, boolean player){
        _name = convertToRawPlayer(_name).toLowerCase();
        if(player){
            if (members.toString().toLowerCase().contains( _name ))
            {
                for(int i = 0; i < listLength(); i++){
                    if ((getMemberName(i).toLowerCase().equals( _name ))) {
                        return (i);
                    }
                }
                return(-1);
            }
        }
        else{
            if (members.toString().toLowerCase().contains( _name ))
            {
                for(int i = 0; i < listLength(); i++){
                    if ((getMemberName(i).toLowerCase()).startsWith( _name )) {
                        return (i);
                    }
                }
                return(-1);
            }
        }

        return(-1);
    }

    public boolean validIndex(int index){
        return (listLength() > index && index > -1);
    }

    public String getMemberName(int index) {
        if (validIndex(index)) {
            return members.get(index).getName();
        }
        return "--ERROR";
    }

    public Member getMember(int index) {
        if (validIndex(index)) {
            return members.get(index);
        }
        return null;
    }

    public List<Member> getMembers() {
        return members;
    }

    public int listLength(){
        return members.size();
    }

    public boolean addNPC(String target){
        if (hasMember(target, false)){
            return false;
        }
        members.add(new Member(target));
        return true;
    }

    public boolean addPlayer(Player target){
        if (hasMember(target.getName(), true)){
            return false;
        }
        members.add(new Member(target.getName(), target.getDisplayName()));
        return true;
    }

    public boolean removeMember(String target, boolean player){
        int index = getIndex(target, player);
        if(index == -1){
            return false;
        }
        members.remove(index);
        return true;
    }

    public String getTag(){
        return tag;
    }
    public String getDisplayTag(){
        return displayTag;
    }

    public void setTag(String newName){
        tag = convertToRaw(newName);
        displayTag = convertSpace(convertColor(newName));
    }

    public String getOpTag(){ return opTag; }
    public String getDisplayOpTag(){ return displayOpTag; }

    public void setOpTag(String newName){
        opTag = convertToRaw(newName);
        displayOpTag = convertSpace(convertColor(newName));
    }

    @Override
    public String toString(){
        return name + "_" + tag + "_" + opTag;
    }

    public boolean isGroupOwner(Player sender){
        if(listLength() > 0){
            int index = getIndex(sender.getName(), true);
            if(validIndex(index)){
                return getMember(index).getType() == 2;
            }
            return false;
        }
        return false;
    }

    public boolean isGroupMod(Player sender){
        if(listLength() > 0){
            int index = getIndex(sender.getName(), true);
            if(validIndex(index)){
                return getMember(index).getType() == 1;
            }
            return false;
        }
        return false;
    }

    public boolean memberSwap(int indexOne, int indexTwo){
        if(validIndex(indexOne) && validIndex(indexTwo)){
            Member first = members.get(indexOne);
            Member second = members.get(indexTwo);
            members.set(indexOne, second);
            members.set(indexTwo, first);
            return true;
        }
        return false;
    }

    public boolean PromoteMember(String member){
        int playerIndex = getIndex(member, true);
        if(validIndex(playerIndex)){
            if(getMember(playerIndex).getType() == 0){
                getMember(playerIndex).setType(1);
                return true;
            }
        }
        return false;
    }

    public boolean DemoteMember(String member){
        int playerIndex = getIndex(member, true);
        if(validIndex(playerIndex)){
            if(getMember(playerIndex).getType() == 1){
                getMember(playerIndex).setType(0);
                return true;
            }
        }
        return false;
    }

    public boolean setOwner(String member){
        int playerIndex = getIndex(member, true);
        if(validIndex(playerIndex)){
            if(getMember(playerIndex).getType() != 2){
                getMember(0).setType(1);
                getMember(playerIndex).setType(2);
                memberSwap(0, playerIndex);
                return true;
            }
        }

        return false;
    }

}
