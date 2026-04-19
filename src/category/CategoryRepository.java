package category;

import prime.Repository;
import exceptions.CantCreateCategoryException;
import exceptions.CantDeleteCategoryException;

import java.sql.*;
import java.util.ArrayList;


public class CategoryRepository extends Repository {

    public ArrayList<Category> getCategoriesByBookId(int bookId) {
        ArrayList<Category> categories = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM categories
                JOIN book_categories ON book_categories.category_id = categories.id
                WHERE book_categories.book_id = ?;
            """)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                categories.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return categories;
    }

    public ArrayList<Category> getAllCategories(){
        ArrayList<Category> categories = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM categories;")){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                categories.add(mapRow(rs));
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return categories;
    }

    public ArrayList<Category> getCategoriesByPartialName(String name){
        ArrayList<Category> categories = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *
                FROM categories
                WHERE name LIKE ?;""")) {
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                categories.add(mapRow(rs));
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return categories;
    }

    public Category getCategoryById(int categoryId) {
        Category category = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM categories c
                WHERE c.id = ?;
            """)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                category = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return category;
    }

    public boolean exists(int categoryId) {
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT count(*) AS number FROM categories
                WHERE id = ?;
            """)) {
            stmt.setInt(1,categoryId);
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

    public void addCategory(Category category){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insert = conn.prepareStatement("INSERT INTO categories (name, description) VALUES (?, ?);",Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, category.getName());
            insert.setString(2, category.getDescription());
            int insertRowCount = insert.executeUpdate();
            ResultSet insertSet = insert.getGeneratedKeys();
            if(insertRowCount>0){
                if(insertSet.next()){
                    int newCategoryId=insertSet.getInt(1);
                    category.setId(newCategoryId);
                }
            } else {
                throw new CantCreateCategoryException("Could not create category.");
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void save(Category category) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement categoryUpdate = conn.prepareStatement("""
                UPDATE categories
                SET name = ?,
                    description = ?
                WHERE id = ?;""")) {
            categoryUpdate.setString(1, category.getName());
            categoryUpdate.setString(2, category.getDescription());
            categoryUpdate.setInt(3, category.getId());
            categoryUpdate.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void delete(Category category) {
        int id = category.getId();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement deleteCategory = conn.prepareStatement("DELETE FROM categories WHERE id = ?;");
             PreparedStatement deleteFromBookCategories = conn.prepareStatement("DELETE FROM book_categories WHERE category_id = ?;")) {
            conn.setAutoCommit(false);
            try {
                deleteCategory.setInt(1, id);
                deleteFromBookCategories.setInt(1, id);
                deleteFromBookCategories.executeUpdate();
                deleteCategory.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.out.println("Fel: " + e.getMessage());
                throw new CantDeleteCategoryException("Could not delete "+category.getName()+".");
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e){
            System.out.println("Fel: " + e.getMessage());
        }
    }

    private Category mapRow(ResultSet rs) {
        Category category = new Category();
        try {
            category = new Category(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"));
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return category;
    }

}
