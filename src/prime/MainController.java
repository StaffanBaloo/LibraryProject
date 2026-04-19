package prime;

import member.*;
import exceptions.*;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Scanner;

public class MainController {
    Scanner scanner = new Scanner(System.in);


    public void showMainMenu(){
        boolean active = true;

        while(active) {
            System.out.println("======Huvudmeny=====");
            System.out.println("Välkommen till biblioteket!");
            System.out.println("Är du en:");
            System.out.println("1. Gäst?");
            System.out.println("2. Medlem?");
            System.out.println("3. Bibliotekarie?");
            System.out.println("0. Avsluta");
            int choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> IO.NYI();
                case 2 -> {
                    login();
                    UserController userController = new UserController();
                    userController.showMenu();
                }
                case 3 ->{
                    LibrarianController librarianController = new LibrarianController();
                    librarianController.showMenu();
                }
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void login(){
        boolean active = true;
        Member user = null;
        MemberService memberService = new MemberService();
        EmailValidator emailValidator = EmailValidator.getInstance();
        while(active){
            System.out.println("Vänligen ange ditt medlems-ID eller din e-postadress:");
            String input=scanner.nextLine().trim();

            if (IO.isNumeric (input)){
                try {
                    user = memberService.getById(Integer.parseInt(input));
                    active = false;
                    Main.login(user);
                } catch (MemberNotFoundException e) {
                    System.out.println("Kunde inte hitta medlem " + input + ".");
                }
            } else if (emailValidator.isValid(input)) {
                try {
                    user = memberService.getByEmail(input);
                    active=false;
                    Main.login(user);
                } catch (MemberNotFoundException e) {
                    System.out.println("Kunde inte hitta någon medlem med e-postadress "+input + ".");
                }

            } else {
                System.out.println("Ogiltig ID eller e-postadress.");
            }
        }
    }
}
