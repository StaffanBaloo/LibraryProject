package book;

import mapper.BookMapper;

import java.util.ArrayList;

public class BookListDTOService {
    BookService bookService = new BookService();



    public ArrayList<BookListDTO> getAllBooksList(){
        return BookMapper.mapToListDTOs(bookService.getAllBooks());
    }

    public ArrayList<BookListDTO> getBookListByTitle(String searchTerm){
        return BookMapper.mapToListDTOs(bookService.getBooksByTitle(searchTerm));
    }

    public ArrayList<BookListDTO> getAvailableBooksList(){
        return BookMapper.mapToListDTOs(bookService.getAvailableBooks());
    }

    public ArrayList<BookListDTO> getBookListByAuthorId(int authorId){
        return BookMapper.mapToListDTOs(bookService.getBooksByAuthorId(authorId));
    }

    public ArrayList<BookListDTO> getBookListByAuthorName(String searchTerm){
        return BookMapper.mapToListDTOs(bookService.getBooksByAuthorName(searchTerm));
    }

    public ArrayList<BookListDTO> getBookListByCategoryId(int categoryId){
        return BookMapper.mapToListDTOs(bookService.getBooksByCategoryId(categoryId));
    }

    public ArrayList<BookListDTO> getBookListByCategory(String searchTerm){
        return BookMapper.mapToListDTOs(bookService.getBooksByCategory(searchTerm));
    }

    public ArrayList<BookListDTO> getBookListByKeyword(String searchTerm){
        return BookMapper.mapToListDTOs(bookService.getBooksByKeyword(searchTerm));
    }

    public BookListDTO getBookById(int bookId) {
        return BookMapper.mapToListDTO(bookService.getBookById(bookId));
    }

}
