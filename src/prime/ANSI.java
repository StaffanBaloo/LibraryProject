package prime;

@SuppressWarnings("unused")
public class ANSI {
    // https://en.wikipedia.org/wiki/ANSI_escape_code#Select_Graphic_Rendition_parameters
    // prime.ANSI escape sequences for styling terminal output

    // reset all styling to their default values
    public static String reset() {
        return "\033[0m";
    }

    public static String bold() {
        return "\033[1m";
    }

    public static String italic() {
        return "\033[3m";
    }

    public static String underline() {
        return "\033[4m";
    }

    public static String noBold() {
        return "\033[22m";
    }

    public static String noItalic(){
        return "\033[23m";
    }

    public static String noUnderline(){
        return "\033[24m";
    }

    public static String color(String col){
        String value ="";
        switch (col.toLowerCase()){
            case "red" -> value = "\033[31m";
            case "green" -> value = "\033[32m";
            case "yellow" -> value = "\033[33m";
            case "blue" -> value = "\033[34m";
            case "magenta" -> value = "\033[35m";
            case "cyan" -> value = "\033[36m";
            case "white" -> value = "\033[37m";
            case "orange" -> value = "\033[38m";
            case "default" -> value = "\033[39m";
            case "bright_black" -> value = "\033[90m";
            case "bright_red" -> value = "\033[91m";
            case "bright_green" -> value = "\033[92m";
            case "bright_yellow" -> value = "\033[93m";
            case "bright_blue" -> value = "\033[94m";
            case "bright_magenta" -> value = "\033[95m";
            case "bright_cyan" -> value = "\033[96m";
            case "bright_white" -> value = "\033[97m";
        }
        return value;
    }

    public static String clearScreen(){
        return "\033[2J\033[H";
    }

    public static String hideCursor(){
        return "\033[?25l";
    }

    public static String showCursor(){
        return "\033[?25h";
    }

}
