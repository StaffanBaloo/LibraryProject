package loan;

import prime.Repository;
import prime.DateConverter;
import book.Book;
import exceptions.LoanReturnException;
import member.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoanRepository extends Repository {

    public int getNumberOfCurrentLoansByMember(Member member){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS number FROM loans WHERE member_id=? AND return_date IS NULL")){
            stmt.setInt(1, member.getMemberId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("number");
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return 0;

    }

    public int getNumberOfOverdueLoansByMember(Member member){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT COUNT(*) AS number
                FROM loans
                WHERE member_id=?
                  AND return_date IS NULL
                  AND due_date<CURRENT_DATE()""")) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("number");
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return 0;
    }

    public ArrayList<Loan> getLoansByMember(Member member) {
        ArrayList<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, b.*
                FROM loans
                JOIN members m on loans.member_id = m.id
                JOIN books b on loans.book_id = b.id
                WHERE member_id = ?""")){
            stmt.setInt(1, member.getMemberId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loans;
    }

    public ArrayList<Loan> getAllLoans(){
        ArrayList<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, b.*
                FROM loans
                JOIN members m on loans.member_id = m.id
                JOIN books b on loans.book_id = b.id""")){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loans;
    }

    public ArrayList<Loan> getAllCurrentLoans(){
        ArrayList<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, b.*
                FROM loans
                JOIN members m on loans.member_id = m.id
                JOIN books b on loans.book_id = b.id
                WHERE return_date IS NULL""")){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loans;
    }

    public ArrayList<Loan> getAllOverdueLoans(){
        ArrayList<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, b.*
                FROM loans
                JOIN members m on loans.member_id = m.id
                JOIN books b on loans.book_id = b.id
                WHERE return_date IS NULL AND due_date<CURDATE();""")){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loans;
    }

    public Loan getLoanById(int loanId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, b.*
                FROM loans l
                JOIN members m on l.member_id = m.id
                JOIN books b on l.book_id = b.id
                WHERE l.id = ?""")) {
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return null;
    }

    public void returnLoan(Loan loan) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement updateLoan = conn.prepareStatement("""
                UPDATE loans
                SET return_date=CURRENT_DATE()
                WHERE id=?;
                """);
             PreparedStatement updateBooks = conn.prepareStatement("""
                UPDATE books
                SET available_copies = available_copies+1
                WHERE id = ?""")) {
            updateLoan.setInt(1, loan.getId());
            updateBooks.setInt(1,loan.getBook().getBookId());
            conn.setAutoCommit(false);
            int rowsAffected = updateLoan.executeUpdate();
            if (rowsAffected == 0){
                throw (new LoanReturnException ("Could not return loan " + loan.getId()));
            } else {
                updateBooks.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void renewLoan(Loan loan, LocalDate newDate) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                UPDATE loans
                SET due_date=?
                WHERE id=?;""")) {
            stmt.setDate(1, Date.valueOf(newDate));
            stmt.setInt(2, loan.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0){
                throw (new LoanReturnException ("Could not renew loan " + loan.getId()));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public Loan getLoanByFineId(int fineId) {
        Loan loan = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM loans l
                JOIN fines f ON f.loan_id = l.id
                JOIN books b on l.book_id = b.id
                JOIN members m on l.member_id = m.id
                WHERE f.id=?;
                """)) {
            stmt.setInt(1, fineId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                loan = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loan;
    }

    public int getNumberOfCurrentLoansByBook(Book book){
        int number = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT COUNT(*) AS number
                FROM loans
                WHERE book_id = ?;""")) {
            stmt.setInt(1, book.getBookId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                number = rs.getInt("number");
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return number;
    }

    public ArrayList<Loan> getLoansByBook(Book book){
        ArrayList<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM loans l
                JOIN books b on l.book_id = b.id
                JOIN members m on l.member_id = m.id
                WHERE b.id=?;
                """)) {
            stmt.setInt(1, book.getBookId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loans;
    }

    public int createLoan(Loan loan){
        int newLoanId = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement insert = conn.prepareStatement("""
            INSERT INTO loans (book_id, member_id, loan_date, due_date)
            VALUES (?, ?, ?, ?);""", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement update = conn.prepareStatement("""
            UPDATE books
            SET available_copies =?
            WHERE id = ?""")) {
            conn.setAutoCommit(false);
            insert.setInt(1, loan.getBook().getBookId());
            insert.setInt(2, loan.getMember().getMemberId());
            insert.setDate(3, Date.valueOf(loan.getLoanDate()));
            insert.setDate(4, Date.valueOf(loan.getDueDate()));
            update.setInt(1, loan.getBook().getAvailableCopies()-1);
            update.setInt(2, loan.getBook().getBookId());

            int insertRowCount = insert.executeUpdate();
            if(insertRowCount>0){
                ResultSet insertSet = insert.getGeneratedKeys();
                if(insertSet.next()){
                    newLoanId=insertSet.getInt(1);
                    loan.setId(newLoanId);
                }
                int updateRowCount = update.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e){
            System.out.println("Fel: " + e.getMessage());
        }
        return newLoanId;
    }

    public ArrayList<String> topList (int length, LocalDate start, LocalDate end){
        ArrayList<String> titles = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.title, count(book_id)
                FROM loans l
                JOIN books b ON l.book_id = b.id
                WHERE loan_date BETWEEN ? AND ?
                GROUP BY book_id
                ORDER BY count(book_id) DESC
                LIMIT ?;""")){
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            stmt.setInt(3, length);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                titles.add(rs.getString("b.title"));
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return titles;
    }

    private Loan mapRow (ResultSet rs) {
        Loan loan = null;
        try {
            loan = new Loan(rs.getInt("id"),
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
                    DateConverter.toLocalDate(rs.getDate("loan_date")),
                    DateConverter.toLocalDate(rs.getDate("due_date")),
                    DateConverter.toLocalDate(rs.getDate("return_date")));
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return loan;
    }
}
