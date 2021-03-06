package kamkeel.RPGMessenger;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class NPC {

    private String name; // Without Color Codes or Spaces (;)
    private String displayName; // With Color Codes and Spaces (;)

    public NPC(String _name){
        name = convertToRawPlayer(_name);
        displayName = convertSpace(convertColor(_name));
    }

    // For Player
    public NPC(String _name, String _displayName){
        name = _name;
        displayName = _displayName;
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

    @Override
    public String toString() {
        return name;
    }

}
