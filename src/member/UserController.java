package member;

import book.BookController;
import fine.FineService;
import loan.*;
import note.*;
import prime.Main;

import java.util.Scanner;

public class UserController {
    LoanService loanService = new LoanService();
    NoteService noteService = new NoteService();
    FineService fineService = new FineService();
    Scanner scanner = new Scanner(System.in);

    public UserController() {

    }

    public void showMenu(){
        boolean active = true;
        int numberUnreadNotes, numberLoans, numberOverdueLoans, choice;
        int totalFines;

        while (active){
            System.out.println("Välkommen, " + Main.loggedInUser.getFirstName() + "!");
            System.out.println("Din medlemsstatus är: " + Main.loggedInUser.getStatus() + ".");

            numberUnreadNotes = noteService.getNumberUnreadNotesByMember(Main.loggedInUser);
            if (numberUnreadNotes > 0) {
                System.out.println("Du har " + numberUnreadNotes + " olästa meddelanden.");
            } else {
                System.out.println("Du har inga olästa meddelanden.");
            }

            totalFines = fineService.getUnpaidFinesTotalByMemberId(Main.loggedInUser.getMemberId());
            if (totalFines > 0) {
                System.out.println("Du har " + totalFines + " kr i obetalda böter.");
            }

            numberLoans = loanService.getNumberOfCurrentLoansByMember(Main.loggedInUser);
            numberOverdueLoans = loanService.getNumberOfOverdueLoansByMember(Main.loggedInUser);
            if(numberLoans>0){
                if(numberOverdueLoans>0) {
                    System.out.println("Du har " + numberLoans + " nuvarande lån, varav " + numberOverdueLoans + " är förfallna.");
                } else {
                    System.out.println("Du har " + numberLoans + " nuvarande lån.");
                }
            } else {
                System.out.println("Du har för tillfället inga lån.");
            }
            System.out.println("""
                    Vad vill du hantera?
                    1. Böcker.
                    2. Lån.
                    3. Profil (meddelanden och böter).
                    0. Go back and log out.""");
            choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> {
                    BookController bookController = new BookController();
                    bookController.showMenu();
                }
                case 2 -> {
                    LoanController loanController = new LoanController();
                    loanController.showMenu();
                }
                case 3 -> {
                    ProfileController profileController = new ProfileController();
                    profileController.showMenu();
                }
                case 0-> {
                    active=false;
                    Main.logout();
                }
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }
}
