package kamkeel.RPGMessenger;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;

public class NPC {

    private String name; // Without Color Codes or Spaces (;)
    private String displayName; // With Color Codes and Spaces (;)

    public NPC(String _name){
        name = convertToRaw(_name);
        displayName = convertSpace(convertColor(_name));
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
        name = convertToRaw(newName);
        displayName = convertSpace(convertColor(newName));
    }

    @Override
    public String toString() {
        return name;
    }

}
