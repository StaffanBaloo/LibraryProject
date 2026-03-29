import java.util.Objects;
import java.util.Scanner;

public class UserController {
    int userId;
    UserService userService = new UserService;
    Scanner scanner = new Scanner(System.in);

    public UserController(int userId) {
        this.userId = userId;
    }

    public void showMenu(){
        boolean active = true;
        int numberUnreadNotes, numberLoans, numberOverdueLoans, choice;
        float totalFines;

        while (active){
            System.out.println("Welcome, " + userService.getFullName(userId) + "!");
            System.out.println("Your membership status is: " + userService.getStatus(userId) + ".");

            numberUnreadNotes = userService.getNumberUnreadNotes(userId);
            if (numberUnreadNotes > 0) {
                System.out.println("You have " + numberUnreadNotes + " unread notifications.");
            } else {
                System.out.println("You have no unread notifications.");
            }

            totalFines = userService.getUnpaidFinesTotal(userId);
            if (totalFines > 0f) {
                System.out.println("You have " + totalFines + " in unpaid fines.");
            }

            numberLoans = userService.getNumberOfLoans(userId);
            numberOverdueLoans = userService.getNumberOfOverdueLoans(userId);
            if(numberLoans>0){
                if(numberOverdueLoans>0) {
                    System.out.println("You have " + numberLoans + " loans currently, of which " + numberOverdueLoans + " are overdue.");
                } else {
                    System.out.println("You have " + numberLoans + " loans currently.");
                }
            } else {
                System.out.println("You do not currently have any loans.");
            }
            System.out.println("""
                    What do you want to do?
                    1. Book menu.
                    2. Loans menu.
                    3. Read notifications.
                    0. Go back.""");
            int choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: {
                    BookController bookController = new BookController(userId);
                    bookController.showMenu();
                    break;
                }
                case 2: {
                    LoanController loanController = new LoanController(userId);
                    loanController.showMenu();
                    break;
                }
                case 3: {
                    NoteController noteController = new NoteController(userId);
                    noteController.showMenu();
                    break;
                }
                case 0: {
                    active=false;
                    break;
                }
            }
        }
    }
}
