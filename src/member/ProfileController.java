package member;
import prime.Main;
import fine.FineController;
import fine.FineService;
import loan.LoanService;
import note.NoteController;
import note.NoteService;

import java.util.Scanner;

public class ProfileController {
    Scanner scanner = new Scanner(System.in);


    public ProfileController (){

    }

    public void showMenu(){
        boolean active = true;
        int choice;

        while (active){

            System.out.println("""
                    Vad vill du hantera?
                    1. Meddelanden.
                    2. Böter.
                    3. Medlemsskap.
                    0. Gå tillbaka.""");
            choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> {
                    NoteController noteController = new NoteController();
                    noteController.showMenu();
                }
                case 2 -> {
                    FineController fineController = new FineController();
                    fineController.showMenu();
                }
                case 3 -> {
                    MemberController memberController = new MemberController();
                    memberController.showMenu();
                }
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }

        }
    }
}
