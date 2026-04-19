package note;

import prime.DateConverter;
import prime.Repository;
import book.Book;
import exceptions.*;
import loan.Loan;
import member.Member;

import java.sql.*;
import java.util.ArrayList;

public class NoteRepository extends Repository {

    public NoteRepository(){

    }

    public Note getNoteById(int noteId){
        Note note = new Note();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, l.*,b.*
                FROM notifications n
                JOIN members m on n.member_id = m.id
                JOIN loans l on n.loan_id = l.id
                JOIN books b on l.book_id = b.id
                WHERE n.id = ?
            """)) {
            stmt.setInt(1, noteId);
            ResultSet rs =stmt.executeQuery();
            if(rs.next()){
                note = mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return note;
    }

    public ArrayList<Note> getNotesByMember(Member member) {
        ArrayList<Note> notes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, l.*,b.*
                FROM notifications n
                JOIN members m on n.member_id = m.id
                JOIN loans l on n.loan_id = l.id
                JOIN books b on l.book_id = b.id
                WHERE n.member_id = ?
            """)) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs =stmt.executeQuery();
            while(rs.next()){
                notes.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return notes;
    }

    public Note getOldestUnreadByMember(Member member){
        Note note = new Note();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT *, m.*, l.*,b.*
                FROM notifications n
                JOIN members m on n.member_id = m.id
                JOIN loans l on n.loan_id = l.id
                JOIN books b on l.book_id = b.id
                WHERE n.member_id = ? AND n.is_read = false
                ORDER BY n.sent_date ASC
                LIMIT 1;""")) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs =stmt.executeQuery();
            if(rs.next()){
                note = mapRow(rs);
            } else {
                note=null;
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return note;
    }

    public void createNote(Note note){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO notifications
                    (member_id, loan_id, type, message, sent_date, is_read)
                    VALUES (?, ?, ?, ?, ?, ?)""")) {
            stmt.setInt(1, note.getMember().getMemberId());
            stmt.setInt(2, note.getLoan().getId());
            stmt.setString(3, note.getType());
            stmt.setString(4, note.getMessage());
            stmt.setDate(5, Date.valueOf(note.getSentDate()));
            stmt.setBoolean(6, note.isRead());
            int insertRowCount = stmt.executeUpdate();
            ResultSet insertSet = stmt.getGeneratedKeys();
            if(insertRowCount>0){
                if(insertSet.next()){
                    int newNoteId=insertSet.getInt(1);
                    note.setNoteId(newNoteId);
                }
            } else {
                throw new CantCreateNoteException("Could not create notification.");
            }
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public int getNumberUnreadNotesByMember(Member member){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT COUNT(*) as number
                FROM notifications
                WHERE member_id = ? AND is_read = false;
            """)) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs =stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("number");
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return 0;
    }

    public int getNumberNotesByMember(Member member){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT COUNT(*) as number
                FROM notifications
                WHERE member_id = ?;
            """)) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs =stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("number");
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return 0;
    }

    public void markRead(Note note){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                UPDATE notifications
                SET is_read = true
                WHERE id =?""")){
            stmt.setInt(1, note.getNoteId());
            stmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    public void markUnread(Note note){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                UPDATE notifications
                SET is_read = false
                WHERE id =?""")){
            stmt.setInt(1, note.getNoteId());
            stmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    private Note mapRow(ResultSet rs) {
        Note note = new Note();
        try {
            note = new Note(rs.getInt("id"),
                    new Member(rs.getInt("m.id"),
                            rs.getString("m.first_name"),
                            rs.getString("m.last_name"),
                            rs.getString("m.email"),
                            rs.getString("m.membership_type"),
                            rs.getString("m.status"),
                            DateConverter.toLocalDate(rs.getDate("m.membership_date"))),
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
                            rs.getString("n.type"),
                            rs.getString("n.message"),
                            DateConverter.toLocalDate(rs.getDate("n.sent_date")),
                            rs.getBoolean("n.is_read"));
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return note;
    }
}
