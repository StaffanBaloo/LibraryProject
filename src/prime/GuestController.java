package prime;

import book.BookController;
import category.CategoryController;
import loan.LoanController;

import java.time.LocalDate;

public class GuestController {
    BookController bookController = new BookController();

    public GuestController() {
    }

    public void showMenu(){
        boolean active = true;
        int choice;
        while(active) {
            System.out.println("""
                    Welcome, gäst! Vad vill du titta på?
                    1. Visa alla böcker.
                    2. Visa alla tillgängliga böcker.
                    3. Sök böcker på författare.
                    4. Sök böcker på titel.
                    5. Sök böcker på kategori.
                    6. Sök böcker på nyckelord.
                    7. Visa top 10 senaste året.
                    8. Visa detaljerad bokinformation på ID.
                    9. Visa alla kategorier.
                    0. Gå tillbaka.""");
            choice=IO.inputNumber();
            switch (choice) {
                case 1 -> bookController.showAllBooks();
                case 2 -> bookController.showAvailableBooks();
                case 3 -> bookController.findByAuthor();
                case 4 -> bookController.findByTitle();
                case 5 -> bookController.findByCategory();
                case 6 -> bookController.findByKeyword();
                case 7 -> {
                    LoanController loanController = new LoanController();
                    loanController.showTopList(10, LocalDate.now().minusYears(1), LocalDate.now());
                }
                case 8 -> bookController.showDetailedInfo();
                case 9 ->{
                    var categoryController = new CategoryController();
                    categoryController.showAllCategories();
                }
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }
}
