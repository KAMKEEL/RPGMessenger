package kamkeel.RPGMessenger.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static kamkeel.RPGMessenger.Util.ColorConvert.*;
import static kamkeel.RPGMessenger.Util.RPGStringHelper.*;

import static kamkeel.RPGMessenger.RPGCommand.*;

public interface CommandDefault {

    public void runCMD(CommandSender sender, String label, final String[] args);

}
