import java.util.Scanner;

public class MainController {
    Scanner scanner = new Scanner(System.in);
    BookController bookController = new BookController();
    MemberController memberController = new MemberController();

    public void showMainMenu(){
        boolean active = true;

        while(active) {
            System.out.println("======Main Meny=====");
            System.out.println("1. Book menu");
            System.out.println("2. Member menu");
            System.out.println("0. Exit");
            int choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:{
                    bookController.showBookMenu();
                    break;
                }
                case 2:{
                    memberController.showMemberMenu();
                    break;
                }
                case 0:{
                    active=false;
                    break;
                }
            }
        }
    }

}
