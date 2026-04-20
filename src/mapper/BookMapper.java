package mapper;

import author.Author;
import book.Book;
import book.BookListDTO;
import book.BookLoanDTO;

import java.util.ArrayList;

public class BookMapper {

    public static BookListDTO mapToListDTO(Book book) {
        ArrayList<String> authorList = new ArrayList<>();
        for(Author author: book.getAuthors()) {
            authorList.add(author.getFullName());
        }
        return new BookListDTO(book.getBookId(), book.getTitle(), authorList, book.getAvailableCopies());
    }

    public static ArrayList<BookListDTO> mapToListDTOs(ArrayList<Book> books) {
        ArrayList<BookListDTO> bookDTOs =new ArrayList<>();
        for(Book book: books){
            bookDTOs.add(mapToListDTO(book));
        }
        return bookDTOs;
    }

    public static BookLoanDTO maptoLoanDTO (Book book) {
        return new BookLoanDTO(book.getBookId(), book.getTitle(), book.getAvailableCopies());
    }
}
