import java.io.IOException;
import java.util.ArrayList;

public class BookService {

    BookRepository bookRepository = new BookRepository();



    public ArrayList<Book> getAllBooks(){
        return bookRepository.getAllBooks();
    }

    public ArrayList<Book> getBooksWith(String searchTerm){
        return bookRepository.getBooksWith(searchTerm);
    }
}
