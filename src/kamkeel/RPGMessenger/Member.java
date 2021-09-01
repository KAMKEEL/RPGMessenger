package kamkeel.RPGMessenger;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class Member {

    private String name; // Without Color Codes or Spaces (;)
    private String displayName; // With Color Codes and Spaces (;)
    private final boolean isPlayer;

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

    public Member(String _name, String _displayName, boolean playerType){
        name = convertToRawPlayer(_name);
        displayName = convertSpace(convertColor(_displayName));
        isPlayer = playerType;
    }

    public String getName(){
        return name;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getSaveName(){
        return convertToSaveNPC(displayName);
    }

    public void rename(String newName){
        name = convertToRawPlayer(newName);
        displayName = convertSpace(convertColor(newName));
    }

    public boolean getMemberType(){
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
