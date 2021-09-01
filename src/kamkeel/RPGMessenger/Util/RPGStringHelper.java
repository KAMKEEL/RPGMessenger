package kamkeel.RPGMessenger.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static kamkeel.RPGMessenger.Util.ColorConvert.convertColor;

public class RPGStringHelper {

    public static final String TAG = "[RPG] ";
    public static final String COLOR_TAG = "§8[§cRPG§8]§7 ";

    // Spy Tags
    public static final String MESSAGE = "§6[M]§8 ";         // 0
    public static final String NPC = "§2[N]§8 ";             // 1
    public static final String PLAYER = "§9[P]§8 ";          // 2
    public static final String PUBLIC = "§e[#]§8 ";          // 3
    public static final String GROUP = "§3[G]§8 ";           // 4
    public static final String CONSOLE = "§4[C]§8 ";         // 5

    // Illegal Symbols
    public static boolean hasIllegalSymbols(String input) {
        String specialCharactersString = "!#$%*()'+,-./:;<=>?[]^_`{|}";
        for (int i = 0; i < input.length() ; i++)
        {
            char ch = input.charAt(i);
            if(specialCharactersString.contains(Character.toString(ch))) {
                return true;
            }
        }
        return false;
    }


}
