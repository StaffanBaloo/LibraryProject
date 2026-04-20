package author;

import java.util.ArrayList;

import book.Book;
import book.BookListDTO;
import book.BookService;
import mapper.AuthorMapper;
import mapper.BookMapper;

public class AuthorService {

    AuthorRepository authorRepository = new AuthorRepository();
    BookService bookService = new BookService();

    public ArrayList<Author> getThinAuthorsByBookId(int bookId){
        return authorRepository.getAuthorsByBookId(bookId);
    }

    public ArrayList<AuthorListDTO> getAllAuthorListDTOs(){
        ArrayList<Author> authors = authorRepository.getAllAuthors();
        return AuthorMapper.mapToListDTOs(authors);
    }

    public ArrayList<AuthorListDTO> getAuthorListDTOsByPartialName(String name) {
        ArrayList<Author> authors = authorRepository.getAuthorsByPartialName(name);
        return AuthorMapper.mapToListDTOs(authors);
    }

    public ArrayList<AuthorListDTO> getAuthorListDTOsByPartialNationality(String name) {
        ArrayList<Author> authors = authorRepository.getAuthorsByPartialNationality(name);
        return AuthorMapper.mapToListDTOs(authors);
    }

    public ArrayList<BookListDTO> getBooksByAuthorId(int authorId) {
        ArrayList<Book> bookList = bookService.getBooksByAuthorId(authorId);
        return BookMapper.mapToListDTOs(bookList);
    }

    public ArrayList<BookListDTO> getBooksByAuthorName(String searchTerm) {
        ArrayList<Book> bookList = bookService.getBooksByAuthorName(searchTerm);
        return BookMapper.mapToListDTOs(bookList);
    }

    public void delete(Author author) {
        authorRepository.delete(author);
    }

    public boolean exists(int authorId) {
        return authorRepository.exists(authorId);
    }

    public Author getAuthorById(int authorId) {
        return authorRepository.getAuthorById(authorId);
    }

    public void addAuthor(Author author){
        authorRepository.addAuthor(author);
    }

    public void save(Author author) {
        authorRepository.save(author);
    }
}
