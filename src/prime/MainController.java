package prime;

import member.*;
import exceptions.*;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Optional;
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
            int choice=IO.inputNumber();
            switch (choice) {
                case 1 -> {
                    GuestController guestController = new GuestController();
                    guestController.showMenu();
                }
                case 2 -> {
                    if(login()) {
                        UserController userController = new UserController();
                        userController.showMenu();
                    } else {
                        System.out.println("Inloggning misslyckades.");
                    }
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

    public boolean login(){
        boolean active = true;
        Optional<Member> maybeUser = Optional.empty();
        MemberService memberService = new MemberService();
        EmailValidator emailValidator = EmailValidator.getInstance();
        while(active){
            System.out.println("Vänligen ange ditt medlems-ID eller din e-postadress:");
            String input=scanner.nextLine().trim();

            if (IO.isNumeric (input)){
                if(input.equals("0")) {
                    active = false;
                } else {
                    active = false;
                    maybeUser = memberService.getById(Integer.parseInt(input));
                    if(maybeUser.isPresent()) Main.login(maybeUser.get());
                    else System.out.println("Kunde inte hitta medlem " + input + ".");
                }
            } else if (emailValidator.isValid(input)) {
                active = false;
                maybeUser = memberService.getByEmail(input);
                if(maybeUser.isPresent()) Main.login(maybeUser.get());
                else System.out.println("Kunde inte hitta någon medlem med e-postadress "+input + ".");
            } else {
                System.out.println("Ogiltig ID eller e-postadress.");
            }
        }
        return maybeUser.isPresent();
    }
}
