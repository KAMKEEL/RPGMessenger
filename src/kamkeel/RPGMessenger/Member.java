package kamkeel.RPGMessenger;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class Member {

    private String name; // Without Color Codes or Spaces (;)
    private String displayName; // With Color Codes and Spaces (;)
    private final boolean isPlayer;
    private int type = 0;

    // For NPC
    public Member(String _name){
        name = convertToRawPlayer(_name);
        displayName = convertSpace(convertColor(_name));
        isPlayer = false;
    }

    // For Player
    public Member(String _name, String _displayName){
        name = _name;
        displayName = _displayName;
        isPlayer = true;
    }

    public Member(String _name, String _displayName, boolean playerType, int _type){
        name = convertToRawPlayer(_name);
        displayName = convertSpace(convertColor(_displayName));
        isPlayer = playerType;
        type = _type;
    }

    public String getName(){
        return name;
    }

    public String getDisplayName(){
        return displayName;
    }

    public int getType(){
        return type;
    }

    public void setType(int i){
        type = i;
    }

    public String getSaveName(){
        return convertToSaveNPC(displayName);
    }

    public void rename(String newName){
        name = convertToRawPlayer(newName);
        displayName = convertSpace(convertColor(newName));
    }

    public boolean MemberIsPlayer(){
        return isPlayer;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean getIsPlayer(){
        return isPlayer;
    }

}
