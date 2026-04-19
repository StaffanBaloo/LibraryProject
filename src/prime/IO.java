package prime;

import java.util.Objects;
import java.util.Scanner;

public class IO {
    static Scanner scanner = new Scanner(System.in);

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    // Läs in en rad och konvertera den till ett heltal om det går, annars klaga.
    public static int inputNumber() {
        boolean active=true;
        int result =0;
        while (active){
            String input = scanner.nextLine().trim();
            if(Objects.equals(input, "")){
                active=false;
            }
            else if(isNumeric(input)){
                active=false;
                result = Integer.parseInt(input);
            } else{
                System.out.println("Please enter a number.");
            }
        }
        return result;
    }

    public static void NYI(){
        System.out.println("This function is not yet implemented.");
    }
}
