import java.util.ArrayList;
import java.util.Scanner;

public class BookController {
    BookService bookService = new BookService();
    Scanner scanner = new Scanner(System.in);

    public void showBookMenu(){
        boolean active = true;

        while (active) {
            System.out.println("======Book Meny=====");
            System.out.println("1. Show all books");
            System.out.println("2. Show all available books");
            System.out.println("3. Search books");
            System.out.println("4. Add book");
            System.out.println("5. Update book");
            System.out.println("6. Delete book");
            System.out.println("0. Back");
            int choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: {
                    ArrayList<Book> books = bookService.getAllBooks();
                    for (Book book : books) {
                        System.out.println(book.toString());
                    }
                    break;
                }
                case 2: {
                    ArrayList<Book> books = bookService.getAllBooks();
                    for (Book book : books) {
                        if (book.getAvailableCopies() > 0) {
                            System.out.println(book.toString());
                        }
                    }
                    break;
                }
                case 3: {
                    System.out.println("Enter term to search in book titles:");
                    String searchTerm = scanner.nextLine();
                    ArrayList<Book> books = bookService.getBooksWith(searchTerm);
                    for (Book book : books) {
                        System.out.println(book.toString());
                    }
                    break;
                }
                case 0: {
                    active = false;
                    break;
                }

            }
        }
    }
}
