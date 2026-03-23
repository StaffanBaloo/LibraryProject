import java.sql.*;
import java.util.ArrayList;

public class BookRepository extends Repository {

    public BookRepository() {
    }

    public ArrayList<String> getAuthorsById(int id){
        ArrayList<String>  authorList = new ArrayList<String>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT a.first_name, a.last_name
                FROM authors a
                JOIN books_authors ba on a.id = ba.author_id
                WHERE ba.book_id = ?
            """)) {
            stmt.setInt(1, id);
            ResultSet rs =stmt.executeQuery();
            while(rs.next()){
                authorList.add(rs.getString("last_name") + ", " + rs.getString("first_name"));
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return authorList;


    }
    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        // try-with-resources stänger anslutningen automatiskt när blocket är klart
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement()){

            // executeQuery() skickar en SELECT-fråga och returnerar ett ResultSet
            ResultSet rs = stmt.executeQuery("""
                SELECT b.id AS id, title, year_published, available_copies, bd.summary, bd.page_count, bd.language
                    FROM books b 
                    JOIN book_descriptions bd ON b.id = bd.book_id;
            """);

            // rs.next() går till nästa rad — returnerar false när det inte finns fler
            while(rs.next()) {
                String title = rs.getString("title");
                int yearPublished = rs.getInt("year_published");
                int availableCopies = rs.getInt("available_copies");
                ArrayList<String> authorList = getAuthorsById(rs.getInt("id"));
                String summary =rs.getString("summary");
                int pageCount = rs.getInt("page_count");
                String language = rs.getString("language");


                Book book = new Book(title, yearPublished, availableCopies, authorList, summary, pageCount, language);
                books.add(book);

            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }
    public ArrayList<Book> getBooksWith(String searchTerm) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.id AS id, title, year_published, available_copies, bd.summary, bd.page_count, bd.language
                    FROM books b 
                    JOIN book_descriptions bd ON b.id = bd.book_id
                    WHERE title LIKE ?;
            """)) {
            stmt.setString(1, "%"+searchTerm+"%");
            ResultSet rs =stmt.executeQuery();
            while(rs.next()) {
                String title = rs.getString("title");
                int yearPublished = rs.getInt("year_published");
                int availableCopies = rs.getInt("available_copies");
                ArrayList<String> authorList = getAuthorsById(rs.getInt("id"));
                String summary =rs.getString("summary");
                int pageCount = rs.getInt("page_count");
                String language = rs.getString("language");


                Book book = new Book(title, yearPublished, availableCopies, authorList, summary, pageCount, language);
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return books;
    }
}
