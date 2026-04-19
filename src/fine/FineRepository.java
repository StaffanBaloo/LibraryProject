package fine;
import prime.DateConverter;
import prime.Repository;
import book.Book;
import exceptions.*;
import loan.Loan;
import member.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class FineRepository extends Repository {

    public void createFine(Fine fine) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO fines
                    (loan_id, amount, issued_date, status)
                VALUES (?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, fine.getLoan().getId());
            stmt.setInt(2, fine.getAmount());
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setString(4, "pending");
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0){
                throw (new FineCreationException ("Could not create fine for loan " + fine.getLoan().getId()));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void payFine (Fine fine) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE fines SET paid_date = ?, status = 'paid' WHERE id = ?")) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, fine.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0){
                throw (new FineCreationException ("Could not pay fine " + fine.getId()));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void cancelFine (Fine fine) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE fines SET paid_date = ?, status = 'cancelled' WHERE id = ?")) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, fine.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0){
                throw (new FineCreationException ("Could not cancel fine " + fine.getId()));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public ArrayList<Fine> getAllFines (){
        ArrayList<Fine> fines = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("""
            SELECT *, l.*, b.*, m.*
            FROM fines
            JOIN loans l ON l.id = loan_id
            JOIN books b ON b.id = l.book_id
            JOIN members m on m.id = l.member_id;""")) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                fines.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return fines;
    }

    public Fine getFineById (int id){
        Fine fine = new Fine();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
            SELECT *, l.*, b.*, m.*
            FROM fines f
            JOIN loans l ON l.id = loan_id
            JOIN books b ON b.id = l.book_id
            JOIN members m on m.id = l.member_id
            WHERE f.id = ?;""")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                fine = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return fine;
    }

    public ArrayList<Fine> getAllFinesForMember (Member member){
        ArrayList<Fine> fines = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
            SELECT *, l.*, b.*, m.*
            FROM fines
            JOIN loans l ON l.id = loan_id
            JOIN books b ON b.id = l.book_id
            JOIN members m on m.id = l.member_id
            WHERE m.id = ?;""")) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                fines.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return fines;
    }

    public ArrayList<Fine> getAllUnpaidFinesForMember (Member member){
        ArrayList<Fine> fines = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
            SELECT *, l.*, b.*, m.*
            FROM fines f
            JOIN loans l ON l.id = loan_id
            JOIN books b ON b.id = l.book_id
            JOIN members m on m.id = l.member_id
            WHERE m.id = ? AND f.status <> 'paid';""")) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                fines.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return fines;
    }

    public int getUnpaidFinesTotalByMemberId(int memberId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT SUM(amount) AS total_fines
                FROM fines
                JOIN loans on loan_id = loans.id
                WHERE loans.member_id = ? AND status = 'pending';
                """)) {
            stmt.setInt(1,memberId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("total_fines");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return 0;
    }

    public boolean exists(int fineId) {
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT count(*) AS number FROM fines
                WHERE id = ?;
            """)) {
            stmt.setInt(1,fineId);
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

    private Fine mapRow(ResultSet rs) {
        try {
            return new Fine(rs.getInt("id"),
                    new Loan(rs.getInt("l.id"),
                            new Book(rs.getInt("b.id"),
                                    rs.getString("b.title"),
                                    rs.getString("b.isbn"),
                                    rs.getInt("b.year_published"),
                                    rs.getInt("b.total_copies"),
                                    rs.getInt("b.available_copies")),
                            new Member(rs.getInt("m.id"),
                                    rs.getString("m.first_name"),
                                    rs.getString("m.last_name"),
                                    rs.getString("m.email"),
                                    rs.getString("m.membership_type"),
                                    rs.getString("m.status"),
                                    DateConverter.toLocalDate(rs.getDate("m.membership_date"))),
                            DateConverter.toLocalDate(rs.getDate("l.loan_date")),
                            DateConverter.toLocalDate(rs.getDate("l.due_date")),
                            DateConverter.toLocalDate(rs.getDate("l.return_date"))),
                    rs.getInt("amount"),
                    DateConverter.toLocalDate(rs.getDate("issued_date")),
                    DateConverter.toLocalDate(rs.getDate("paid_date")),
                    rs.getString("status"));
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return null;
    }
}
