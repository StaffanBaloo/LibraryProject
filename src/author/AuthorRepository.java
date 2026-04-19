package author;

import prime.DateConverter;
import prime.Repository;
import exceptions.CantDeleteAuthorException;
import exceptions.CantSaveAuthorException;

import java.sql.*;
import java.util.ArrayList;

public class AuthorRepository extends Repository {

    public ArrayList<Author> getAuthorsByBookId(int bookId) {
        ArrayList<Author> authors = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM authors a
                JOIN book_authors ba ON ba.author_id = a.id
                JOIN author_descriptions  ad ON a.id = ad.author_id
                WHERE ba.book_id = ?;
            """)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                authors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return authors;
    }

    public ArrayList<Author> getAllAuthors() {
        ArrayList<Author> authors = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM authors a
                JOIN author_descriptions  ad ON a.id = ad.author_id;
            """)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                authors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return authors;
    }

    public ArrayList<Author> getAuthorsByPartialName(String name) {
        ArrayList<Author> authors = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM authors a
                JOIN author_descriptions  ad ON a.id = ad.author_id
                WHERE CONCAT_WS(' ', a.first_name, a.last_name) LIKE ?;
            """)) {
            stmt.setString(1, "%"+name+"%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                authors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return authors;
    }

    public ArrayList<Author> getAuthorsByPartialNationality(String nationality) {
        ArrayList<Author> authors = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM authors a
                JOIN author_descriptions  ad ON a.id = ad.author_id
                WHERE a.nationality LIKE ?;
            """)) {
            stmt.setString(1, "%"+nationality+"%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                authors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return authors;
    }

    public boolean exists(int authorId) {
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT count(*) AS number FROM authors
                WHERE id = ?;
            """)) {
            stmt.setInt(1,authorId);
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

    public void addAuthor(Author author) {
        int newAuthorId;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertAuthor = conn.prepareStatement("""
                INSERT INTO authors
                    (first_name, last_name, nationality, birth_date)
                    VALUES (?, ?, ?, ?);""", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertDescription = conn.prepareStatement("""
                INSERT INTO author_descriptions
                    (author_id, biography, website)
                    VALUES (?, ?, ?);""")){
            conn.setAutoCommit(false);
            try {
                insertAuthor.setString(1, author.getFirstName());
                insertAuthor.setString(2, author.getLastName());
                insertAuthor.setString(3, author.getNationality());
                insertAuthor.setDate(4, Date.valueOf(author.getBirthDate()));
                int insertRowCount = insertAuthor.executeUpdate();
                ResultSet insertSet = insertAuthor.getGeneratedKeys();
                if (insertRowCount > 0) {
                    if (insertSet.next()) {
                        newAuthorId = insertSet.getInt(1);
                        author.setId(newAuthorId);
                        insertDescription.setInt(1, newAuthorId);
                        insertDescription.setString(2, author.getBiography());
                        insertDescription.setString(3, author.getWebsite());
                        insertDescription.executeUpdate();
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

    public void save(Author author) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement updateAuthor = conn.prepareStatement("""
                UPDATE authors
                SET first_name = ?, last_name = ?, nationality = ?, birth_date = ?
                WHERE id=?;""");
             PreparedStatement updateAuthorDescription = conn.prepareStatement("""
                UPDATE author_descriptions
                SET biography = ?, website = ?
                WHERE author_id = ?;""")) {
            conn.setAutoCommit(false);
            try {
                updateAuthor.setString(1, author.getFirstName());
                updateAuthor.setString(2, author.getLastName());
                updateAuthor.setString(3, author.getNationality());
                updateAuthor.setDate(4, Date.valueOf(author.getBirthDate()));
                updateAuthor.setInt(5, author.getId());
                updateAuthorDescription.setString(1, author.getBiography());
                updateAuthorDescription.setString(2, author.getWebsite());
                updateAuthorDescription.setInt(3, author.getId());
                updateAuthor.executeUpdate();
                updateAuthorDescription.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                throw new CantSaveAuthorException("Could not save author "+author.getFullName()+".");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e){
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void delete(Author author) {
        int id = author.getId();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement deleteAuthor = conn.prepareStatement("DELETE FROM authors WHERE id = ?;");
             PreparedStatement deleteAuthorDescription = conn.prepareStatement("DELETE FROM author_descriptions WHERE author_id = ?;");
             PreparedStatement deleteFromBookAuthors = conn.prepareStatement("DELETE FROM book_authors WHERE author_id = ?;")) {
            conn.setAutoCommit(false);
            try {
                deleteAuthor.setInt(1, id);
                deleteAuthorDescription.setInt(1, id);
                deleteFromBookAuthors.setInt(1, id);
                deleteAuthorDescription.executeUpdate();
                deleteFromBookAuthors.executeUpdate();
                deleteAuthor.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.out.println("Fel: " + e.getMessage());
                throw new CantDeleteAuthorException("Could not delete "+author.getFullName()+".");
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e){
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public Author getAuthorById(int authorId) {
        Author author = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM authors a
                JOIN author_descriptions  ad ON a.id = ad.author_id
                WHERE a.id = ?;
            """)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                author = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return author;
    }



    private Author mapRow(ResultSet rs) {
        Author author = null;
        try {
            author = new Author(rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("nationality"),
                    DateConverter.toLocalDate(rs.getDate("birth_date")),
                    rs.getString("ad.biography"),
                    rs.getString("ad.website"));
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return author;
    }

}
