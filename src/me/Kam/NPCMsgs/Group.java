package me.Kam.NPCMsgs;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;

public class Group {

    private static String split = "-!012700!-";
    private List<String> recipients = new java.util.ArrayList();
    private String groupName;
    private String tag;
    private String opTag;
    private boolean tagToggle;

    public Group(String name){
        groupName = name;
        tag = name;
        opTag = name;
        tagToggle = false;
    }

    public Group(String name, String tagString, boolean toggle, String op, List<String> people){
        groupName = name;
        recipients = people;
        tag = tagString;
        opTag = op;
        tagToggle = toggle;
    }

    public String returnName(){
        return groupName;
    }

    public int addPlayer(String target){
        for(int i = 0; i < recipients.size(); i++){
            if(target.equals(recipients.get(i))){
                return(-1);
            }
        }
        recipients.add(target);
        return 0;
    }

    public int removePlayer(String target){
        if(recipients.indexOf(target) == -1) {
            return -1;
        }
        recipients.remove(target);
        return 0;
    }

    public int setName(String target){
        groupName = target;
        return 0;
    }

    public int setTag(String target){
        tag = target;
        return 0;
    }

    public String getTag(){
        return tag;
    }

    public int setOpTag(String target){
        opTag = target;
        return 0;
    }

    public String getOpTag(){
        return opTag;
    }

    public int setTagToggle(boolean target){
        tagToggle = target;
        return 0;
    }

    public boolean getTagToggle(){
        return tagToggle;
    }

    public List<String> allPeople(){
        return recipients;
    }

    @Override
    public String toString(){
        return groupName;
    }

    public String serialize(){
        String serializedString = groupName + split + tag;

        if(tagToggle){
            serializedString += split + 1;
        }
        else{
            serializedString += split + 0;
        }

        serializedString += split + opTag;

        int size = recipients.size();

        //Here we are adding the size to the string.
        serializedString += split+size;

        //For this example, we are looping through strings. If this was a custom class or another object,
        //you would need to have a toString method to convert that object to a string.
        for(String member : recipients){
            serializedString += split+member;
        }
        return serializedString;
    }

    public static Group deserialize(String serializedString){
        String[] values = serializedString.split(split);

        //The first variable is the 'number' field, so the first object in the 'values' array will be that number
        String groupName = values[0];
        String tagName = values[1];
        boolean tagID;
        if (values[2].equals("1")){
             tagID = true;
        }
        else{
            tagID = false;
        }

        String ops = values[3];

        //The second variable is the size of the list field, so the second object in the 'values' array will be the size
        int size = Integer.valueOf(values[4]);

        List<String> people = new java.util.ArrayList();

        //Now we need to loop through the rest of the values in the array. This will start at the second index, and loop through all the rest of the values.
        for(int i = 5; i < 5+size; i++){
            people.add(values[i]);
        }

        return new Group(groupName, tagName, tagID, ops, people);
    }
}
