package kamkeel.RPGMessenger.Util;

public class ColorConvert {

    public static final String[] colorCodesLower = {"&a", "&b", "&c", "&d", "&e", "&f", "&g", "&k", "&l", "&m", "&n", "&o", "&r"};
    public static final String[] colorCodesUpper = {"&A", "&B", "&C", "&D", "&E", "&F", "&G", "&K", "&L", "&M", "&N", "&O", "&R"};
    public static final String[] colorCodesNums = {"&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0"};

    public static final String[] colorCodesRawLower = {"§a", "§b", "§c", "§d", "§e", "§f", "§g", "§k", "§l", "§m", "§n", "§o", "§r"};
    public static final String[] colorCodesRawUpper = {"§A", "§B", "§C", "§D", "§E", "§F", "§G", "§K", "§L", "§M", "§N", "§O", "§R"};
    public static final String[] colorCodesRawNums = {"§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0"};

    public static String removeColorCodes(String s){

        // Remove Lower Colors
        for (String value : colorCodesLower) {
            s = s.replace(value, "");
        }
        // Remove Upper Colors
        for (String value : colorCodesUpper) {
            s = s.replace(value, "");
        }
        // Remove Lower Colors
        for (String value : colorCodesNums) {
            s = s.replace(value, "");
        }

        return s;
    }

    public static String removeSpace(String s){
        s = s.replace(";", "");
        return s;
    }

    public static String convertSpace(String s){
        s = s.replace(";", " ");
        return s;
    }

    public static String deleteSpace(String s){
        s = s.replace(" ", "");
        return s;
    }

    public static String convertColor(String s){
        // Convert Lower Colors
        for(int i = 0; i < colorCodesLower.length; i++){
            s = s.replace(colorCodesLower[i], colorCodesRawLower[i]);
        }
        // Convert Lower Colors
        for(int i = 0; i < colorCodesUpper.length; i++){
            s = s.replace(colorCodesUpper[i], colorCodesRawUpper[i]);
        }
        // Convert Nums Colors
        for(int i = 0; i < colorCodesNums.length; i++){
            s = s.replace(colorCodesNums[i], colorCodesRawNums[i]);
        }
        return s;
    }

    public static String convertToRaw(String s){
        return removeSpace(removeColorCodes(s));
    }

    public static String convertColorSign(String s){ return s.replace("§", "&"); }

    public static String convertToRawPlayer(String s){
        return removeSpace(removeColorCodes(convertColorSign(s)));
    }

    public static String convertToSaveNPC(String s){
        // Convert Space
        s = s.replace(" ", ";");

        // Convert Lower Colors
        for(int i = 0; i < colorCodesLower.length; i++){
            s = s.replace(colorCodesRawLower[i], colorCodesLower[i]);
        }
        // Convert Lower Colors
        for(int i = 0; i < colorCodesUpper.length; i++){
            s = s.replace(colorCodesRawUpper[i], colorCodesUpper[i]);
        }
        // Convert Nums Colors
        for(int i = 0; i < colorCodesNums.length; i++){
            s = s.replace(colorCodesRawNums[i], colorCodesNums[i]);
        }

        return s;
    }

}
