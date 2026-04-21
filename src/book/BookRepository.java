package book;

import java.sql.*;
import java.util.ArrayList;

import author.AuthorRepository;
import category.CategoryRepository;
import prime.Repository;
import author.Author;
import category.Category;

public class BookRepository extends Repository {

    public BookRepository() {
    }

    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        // try-with-resources stänger anslutningen automatiskt när blocket är klart
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement()){

            // executeQuery() skickar en SELECT-fråga och returnerar ett ResultSet
            ResultSet rs = stmt.executeQuery("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    WHERE b.total_copies>0
                    ORDER BY b.id;""");

            // rs.next() går till nästa rad — returnerar false när det inte finns fler
            while(rs.next()) {
                books.add(mapListRow(rs));

            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getReallyAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        // try-with-resources stänger anslutningen automatiskt när blocket är klart
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()){

            // executeQuery() skickar en SELECT-fråga och returnerar ett ResultSet
            ResultSet rs = stmt.executeQuery("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                ORDER BY b.id;""");

            // rs.next() går till nästa rad — returnerar false när det inte finns fler
            while(rs.next()) {
                books.add(mapListRow(rs));

            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getBooksByTitle(String searchTerm) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    WHERE title LIKE ?
                    ORDER BY b.id""")) {
            stmt.setString(1, "%"+searchTerm+"%");
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                books.add(mapListRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getBooksByAuthorId(int authorId) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.*, bd.*
                FROM books b
                JOIN book_descriptions bd ON b.id = bd.book_id
                JOIN book_authors ba ON b.id = ba.book_id
                WHERE ba.author_id = ?
                ORDER BY b.id;""")) {
            stmt.setInt(1, authorId);
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                books.add(mapListRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getBooksByAuthorName(String searchTerm) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    JOIN book_authors ba ON b.id = ba.book_id
                    JOIN authors a ON ba.author_id = a.id
                    WHERE a.first_name LIKE ? OR a.last_name LIKE ?
                    ORDER BY b.id;""")) {
            stmt.setString(1, "%"+searchTerm+"%");
            stmt.setString(2, "%"+searchTerm+"%");
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                books.add(mapListRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getBooksByCategoryId(int categoryId) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    JOIN book_categories bc ON b.id = bc.book_id
                    WHERE bc.category_id = ?
                    ORDER BY b.id;""")) {
            stmt.setInt(1, categoryId);
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                books.add(mapListRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getBooksByCategory(String searchTerm) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    JOIN book_categories bc ON b.id = bc.book_id
                    JOIN categories c ON bc.category_id = c.id
                    WHERE c.name LIKE ?
                    ORDER BY b.id;""")) {
            stmt.setString(1, "%"+searchTerm+"%");
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                books.add(mapListRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public ArrayList<Book> getBooksByKeyword(String searchTerm) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.*, bd.*
                    FROM books b
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    WHERE bd.summary LIKE ?
                    ORDER BY b.id""")) {
            stmt.setString(1, "%"+searchTerm+"%");
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                books.add(mapListRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }

    public Book getBookById(int bookId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("""
            SELECT b.*, bd.* FROM books b
            JOIN book_descriptions bd on bd.book_id = b.id
            WHERE b.id = ?""")) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return mapSingleRow(rs);
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<BookAuthor> getBookAuthors(){
        ArrayList<BookAuthor> bookAuthors = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * from book_authors;")){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                bookAuthors.add(new BookAuthor(rs.getInt("book_id"),
                        rs.getInt("author_id")));
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return bookAuthors;
    }

    public ArrayList<BookCategory> getBookCategories(){
        ArrayList<BookCategory> bookCategories = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * from book_categories;")){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                bookCategories.add(new BookCategory(rs.getInt("book_id"),
                        rs.getInt("category_id")));
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return bookCategories;
    }

    public Book getBookByLoanId(int loanId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
            SELECT b.*, bd.* FROM books b
            JOIN book_descriptions bd on bd.book_id = b.id
            JOIN loans l ON l.book_id = b.id
            WHERE l.id = ?""")) {
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return mapSingleRow(rs);
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return null;
    }

    public boolean exists(int bookId) {
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT count(*) AS number FROM books
                WHERE id = ?;
            """)) {
            stmt.setInt(1,bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if(rs.getInt("number")>0) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return exists;
    }

    public void save(Book book){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement bookUpdate = conn.prepareStatement("""
                UPDATE books
                SET title = ?,
                    isbn = ?,
                    year_published = ?,
                    total_copies = ?,
                    available_copies = ?
                WHERE id = ?;""");
             PreparedStatement bookDescriptionUpdate = conn.prepareStatement("""
                UPDATE book_descriptions
                SET summary = ?,
                    language = ?,
                    page_count =?
                WHERE book_id = ?""")) {
            conn.setAutoCommit(false);
            try {
                bookUpdate.setString(1, book.getTitle());
                bookUpdate.setString(2, book.getIsbn());
                bookUpdate.setInt(3, book.getYearPublished());
                bookUpdate.setInt(4, book.getTotalCopies());
                bookUpdate.setInt(5, book.getAvailableCopies());
                bookUpdate.setInt(6, book.getBookId());
                bookDescriptionUpdate.setString(1, book.getSummary());
                bookDescriptionUpdate.setString(2, book.getLanguage());
                bookDescriptionUpdate.setInt(3, book.getPageCount());
                bookDescriptionUpdate.setInt(4, book.getBookId());
                bookUpdate.executeUpdate();
                bookDescriptionUpdate.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Fel: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void saveAuthors(Book book) {
        ArrayList<Author> authors = book.getAuthors();
        int bookId = book.getBookId();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement clearAuthors = conn.prepareStatement("DELETE FROM book_authors WHERE book_id = ?;");
             PreparedStatement addAuthors = conn.prepareStatement("INSERT INTO book_authors (book_id, author_id) VALUES (?, ?);")) {
            conn.setAutoCommit(false);
            try {
                clearAuthors.setInt(1, bookId);
                clearAuthors.executeUpdate();
                for(Author author : authors) {
                    addAuthors.setInt(1, bookId);
                    addAuthors.setInt(2, author.getId());
                    addAuthors.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Fel: " + e.getMessage());
            } finally {
                 conn.setAutoCommit(true);
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void saveCategories(Book book) {
        ArrayList<Category> categories = book.getCategories();
        int bookId = book.getBookId();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement clearCategories = conn.prepareStatement("DELETE FROM book_categories WHERE book_id = ?;");
             PreparedStatement addCategories = conn.prepareStatement("INSERT INTO book_categories (book_id, category_id) VALUES (?, ?);")) {
            conn.setAutoCommit(false);
            try {
                clearCategories.setInt(1, bookId);
                clearCategories.executeUpdate();
                for(Category category : categories) {
                    addCategories.setInt(1, bookId);
                    addCategories.setInt(2, category.getId());
                    addCategories.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Fel: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void addBook(Book book, ArrayList<Integer> authorIdList, ArrayList<Integer> categoryIdList) {
        int newBookId = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement booksInsert = conn.prepareStatement("""
                INSERT INTO books
                    (title, isbn, year_published, total_copies, available_copies)
                    VALUES (?, ?, ?, ?, ?);""", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement descriptionInsert = conn.prepareStatement("""
                INSERT INTO book_descriptions
                    (book_id, summary, language, page_count)
                    VALUES (?, ?, ?, ?);""");
             PreparedStatement bookAuthorsInsert = conn.prepareStatement("""
                INSERT INTO book_authors
                (book_id, author_id)
                VALUES (?, ?);""");
             PreparedStatement bookCategoriesInsert = conn.prepareStatement("""
                INSERT INTO book_categories
                    (book_id, category_id)
                    VALUES (?, ?);""")) {
            conn.setAutoCommit(false);
            try {
                booksInsert.setString(1, book.getTitle());
                booksInsert.setString(2, book.getIsbn());
                booksInsert.setInt(3, book.getYearPublished());
                booksInsert.setInt(4, book.getTotalCopies());
                booksInsert.setInt(5, book.getAvailableCopies());
                int insertRowCount = booksInsert.executeUpdate();
                ResultSet insertSet = booksInsert.getGeneratedKeys();
                if (insertRowCount > 0) {
                    if (insertSet.next()) {
                        newBookId = insertSet.getInt(1);
                        book.setBookId(newBookId);
                    }
                    descriptionInsert.setInt(1, newBookId);
                    descriptionInsert.setString(2, book.getSummary());
                    descriptionInsert.setString(3, book.getLanguage());
                    descriptionInsert.setInt(4, book.getPageCount());
                    descriptionInsert.executeUpdate();
                    for (int authorId : authorIdList) {
                        bookAuthorsInsert.setInt(1, newBookId);
                        bookAuthorsInsert.setInt(2, authorId);
                        bookAuthorsInsert.executeUpdate();
                    }
                    for (int categoryId : categoryIdList) {
                        bookCategoriesInsert.setInt(1, newBookId);
                        bookCategoriesInsert.setInt(2, categoryId);
                        bookCategoriesInsert.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Fel: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e){
            System.out.println("Fel: " + e.getMessage());
        }
    }

    private Book mapListRow(ResultSet rs) {
        try {
            return new Book(rs.getInt("b.id"),
                    rs.getString("b.title"),
                    rs.getString("b.isbn"),
                    rs.getInt("b.year_published"),
                    rs.getInt("b.total_copies"),
                    rs.getInt("b.available_copies"),
                    rs.getString("bd.summary"),
                    rs.getInt("bd.page_count"),
                    rs.getString("bd.language"));
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return null;
    }

    private Book mapSingleRow(ResultSet rs) {
        AuthorRepository authorRepository = new AuthorRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        try {
            return new Book(rs.getInt("b.id"),
                    rs.getString("b.title"),
                    rs.getString("b.isbn"),
                    rs.getInt("b.year_published"),
                    rs.getInt("b.total_copies"),
                    rs.getInt("b.available_copies"),
                    authorRepository.getAuthorsByBookId(rs.getInt("b.id")),
                    categoryRepository.getCategoriesByBookId(rs.getInt("b.id")),
                    rs.getString("bd.summary"),
                    rs.getInt("bd.page_count"),
                    rs.getString("bd.language"));
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return null;
    }
}
