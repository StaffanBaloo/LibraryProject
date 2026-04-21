package book;

import author.*;
import category.*;
import exceptions.*;
import loan.LoanRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BookService {

    BookRepository bookRepository = new BookRepository();
    AuthorRepository authorRepository = new AuthorRepository();
    CategoryRepository categoryRepository = new CategoryRepository();
    LoanRepository loanRepository = new LoanRepository();



    public ArrayList<Book> getAllBooks(){
        ArrayList<Book> books = bookRepository.getAllBooks();
        matchWithAuthorsCategories(books);
        return books;
    }

    public void matchWithAuthorsCategories(ArrayList<Book> books){
        var authors = authorRepository.getAllAuthors();
        var categories = categoryRepository.getAllCategories();
        var bookAuthors = bookRepository.getBookAuthors();
        var bookCategories = bookRepository.getBookCategories();
        var authorMap = authors.stream()
                .collect(Collectors.toMap(Author::getId, author-> author));
        var categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
        var bookMap = books.stream()
                .collect(Collectors.toMap(Book::getBookId, book->book));
        for (BookAuthor ba : bookAuthors){
            Book book = bookMap.get(ba.getBookId());
            Author author = authorMap.get(ba.getAuthorId());
            if(book!=null && author!= null){
                book.getAuthors().add(author);
            }
        }
        for (BookCategory bc : bookCategories){
            Book book = bookMap.get(bc.getBookId());
            Category category = categoryMap.get(bc.getCategoryId());
            if(book!=null && category!= null){
                book.getCategories().add(category);
            }
        }
    }

    public ArrayList<Author> getAuthorsByBookId(int bookId){
        return authorRepository.getAuthorsByBookId(bookId);
    }

    public ArrayList<Category> getCategoriesByBookId(int bookId){
        return categoryRepository.getCategoriesByBookId(bookId);
    }

    public ArrayList<Book> getBooksByTitle(String searchTerm){
        var books = bookRepository.getBooksByTitle(searchTerm);
        matchWithAuthorsCategories(books);
        return books;
    }

    public ArrayList<Book> getAvailableBooks(){
        return new ArrayList<>(getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList());
/*        ArrayList<Book> books = getAllBooks();
        ArrayList<Book> availableBooks = new ArrayList<>();
        for(Book book: books) {
            if(book.getAvailableCopies()>0){
                availableBooks.add(book);
            }
        }
        return availableBooks;*/
    }


    public boolean exists(int bookId) {
        return bookRepository.exists(bookId);
    }

    public ArrayList<Book> getBooksByAuthorId(int authorId) {
        ArrayList<Book> books = bookRepository.getBooksByAuthorId(authorId);
        matchWithAuthorsCategories(books);
        return books;
    }

    public ArrayList<Book> getBooksByAuthorName(String searchTerm) {
        ArrayList<Book> books = bookRepository.getBooksByAuthorName(searchTerm);
        matchWithAuthorsCategories(books);
        return books;
    }

    public ArrayList<Book> getBooksByCategoryId(int categoryId) {
        ArrayList<Book> books = bookRepository.getBooksByCategoryId(categoryId);
        matchWithAuthorsCategories(books);
        return books;
    }

    public ArrayList<Book> getBooksByCategory(String searchTerm) {
        ArrayList<Book> books = bookRepository.getBooksByCategory(searchTerm);
        matchWithAuthorsCategories(books);
        return books;
    }

    public ArrayList<Book> getBooksByKeyword(String searchTerm) {
        ArrayList<Book> books = bookRepository.getBooksByKeyword(searchTerm);
        matchWithAuthorsCategories(books);
        return books;
    }

    public void remove (Book book) {
        if(loanRepository.getNumberOfCurrentLoansByBook(book)==0) {
            book.setTotalCopies(0);
            book.setAvailableCopies(0);
            bookRepository.save(book);
        } else {
            throw new CantRemoveBookException ("Det finns aktiva lån för bok "+book.getBookId()+".");
        }
    }

    public void addBook(Book book, ArrayList<Integer> authorIdList, ArrayList<Integer> categoryIdList) {
        for(int authorId : authorIdList) {
            if(!authorRepository.exists(authorId)) {
                throw new CantCreateBookException("Det finns ingen författare med ID "+authorId+".");
            }
        }
        for(int categoryId : categoryIdList) {
            if(!categoryRepository.exists(categoryId)) {
                throw new CantCreateBookException("Det finns ingen kategori med ID "+categoryId+".");
            }
        }
        bookRepository.addBook(book, authorIdList, categoryIdList);
    }

    public void save(Book book){
        bookRepository.save(book);
    }

    public void saveAuthors(Book book) {
        bookRepository.saveAuthors(book);
    }

    public void saveCategories(Book book) {
        bookRepository.saveCategories(book);
    }

    public Book getBookById(int bookId) {
        Book book;
        if(bookRepository.exists(bookId)) {
            book = bookRepository.getBookById(bookId);
        } else {
            throw new BookDoesNotExistException ("Det finns ingen bok med ID "+bookId+ ".");
        }
        return book;
    }


}
