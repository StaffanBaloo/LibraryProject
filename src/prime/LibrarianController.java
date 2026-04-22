package prime;

import author.AuthorController;
import book.BookController;
import loan.Loan;
import loan.LoanController;
import loan.LoanService;
import member.MemberController;
import category.CategoryController;
import member.MemberService;
import note.NoteService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class LibrarianController {
    Scanner scanner = new Scanner(System.in);

    public LibrarianController() {
    }
    public void showMenu() {
        boolean active = true;
        int choice;
        while(active) {
            System.out.println("""
                    Welcome, bibliotekarie!
                    1. Bokmeny.
                    2. Författarmeny.
                    3. Kategorimeny.
                    4. Medlemsmeny.
                    5. Lånemeny.
                    6. Kör underhåll.
                    0. Gå tillbaka.""");
            choice=IO.inputNumber();
            switch (choice) {
                case 1 -> {
                    BookController BookController = new BookController();
                    BookController.showLibrarianMenu();
                }
                case 2 -> {
                    AuthorController authorController = new AuthorController();
                    authorController.showLibrarianMenu();
                }
                case 3 -> {
                    CategoryController categoryController = new CategoryController();
                    categoryController.showLibrarianMenu();
                }
                case 4 -> {
                    MemberController memberController = new MemberController();
                    memberController.showLibrarianMenu();
                }
                case 5 -> {
                    LoanController loanController = new LoanController();
                    loanController.showLibrarianMenu();
                }
                case 6 -> runMaintenance();
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }
    public void runMaintenance(){
        LoanService loanService = new LoanService();
        HashMap<String, Integer> results = loanService.runMaintenance();
        int suspensions = results.get("suspensions");
        int reminders = results.get("reminders");
        int overdue = results.get("overdue");
        System.out.println("Skickade "+suspensions+" avstängningar, "+overdue+" förfallovarningar, och "+reminders+" påminnelser.");

    }

}
