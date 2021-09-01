package kamkeel.RPGMessenger.Control;

import kamkeel.RPGMessenger.NPC;
import static kamkeel.RPGMessenger.Util.ColorConvert.*;

import java.util.List;

public class NPCControl {

    public List<NPC> npcs = new java.util.ArrayList();

    public NPCControl(List<String> npcList){
        for (String s : npcList) {
            NPC newNPC = new NPC(s);
            npcs.add(newNPC);
        }
    }

    ////////////////////////
    // NPC Commands

    public boolean validIndex(int index){
        return (listLength() > index && index > -1);
    }

    public List<NPC> getNpcs() {
        return npcs;
    }

    public NPC getNPC(int index) {
        return npcs.get(index);
    }

    public int npcIndex(String npcName){
        npcName = convertToRaw(npcName).toLowerCase();
        if (npcs.toString().toLowerCase().contains( npcName ))
        {
            for(int i = 0; i < listLength(); i++){
                if ((getNPCName(i).toLowerCase()).startsWith( npcName )) {
                    return (i);
                }
            }
            return(-1);
        }
        return(-1);
    }

    public boolean alreadyExists(NPC npc){
        String npcName = npc.getName().toLowerCase();
        if (npcs.toString().toLowerCase().contains( npcName ))
        {
            for(int i = 0; i < listLength(); i++){
                if ((getNPCName(i).toLowerCase()).equals( npcName )) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean npcSwap(int indexOne, int indexTwo){
        if(validIndex(indexOne) && validIndex(indexTwo)){
            NPC first = npcs.get(indexOne);
            NPC second = npcs.get(indexTwo);
            npcs.set(indexOne, second);
            npcs.set(indexTwo, first);
            return true;
        }
        return false;
    }

    public boolean npcAdd(String name){
        if(name != null && !name.trim().isEmpty()){
            npcs.add(new NPC(name));
            return true;
        }
        return false;
    }

    public boolean npcAdd(NPC npc){
        if(npc != null){
            npcs.add(npc);
            return true;
        }
        return false;
    }

    public String npcRemove(String npcName){
        if(npcName != null && !npcName.trim().isEmpty()){
            int findIndex = npcIndex(npcName);
            if(findIndex != -1){
                String displayName = getNPCDisplayName(findIndex);
                npcs.remove(findIndex);
                return displayName;
            }
            return null;
        }
        return null;
    }

    public String getNPCName(int index) {
        if (validIndex(index)) {
            return npcs.get(index).getName();
        }
        return "--ERROR";
    }

    public String getNPCDisplayName(int index) {
        if (validIndex(index)) {
            return npcs.get(index).getDisplayName();
        }
        return "--ERROR";
    }

    public boolean renameNPC(int index, String name) {
        if (validIndex(index)) {
            npcs.get(index).rename(name);
            return true;
        }
        return false;
    }
    
    public int listLength(){
        return npcs.size();
    }

    @Override
    public String toString() {
        return npcs.toString();
    }

    ////////////////////////

}
