package member;

import java.sql.*;
import java.util.ArrayList;

import prime.DateConverter;
import prime.Repository;
import exceptions.CantCreateMemberException;
import exceptions.MemberNotFoundException;

public class MemberRepository extends Repository {

    public MemberRepository () {

    }

    public ArrayList<Member> getAllMembers() {
        ArrayList<Member> members = new ArrayList<>();
        // try-with-resources stänger anslutningen automatiskt när blocket är klart
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // executeQuery() skickar en SELECT-fråga och returnerar ett ResultSet
            ResultSet rs = stmt.executeQuery("""
                SELECT * from members;
            """);

            // rs.next() går till nästa rad — returnerar false när det inte finns fler
            while (rs.next()) {
                members.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return members;
    }

    public ArrayList<Member> getAllMembersByStatus(String status) {
        ArrayList<Member> members = new ArrayList<>();
        // try-with-resources stänger anslutningen automatiskt när blocket är klart
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * from members WHERE status =?;")) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return members;
    }

    public boolean exists(int memberId) {
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT count(*) AS number FROM members
                WHERE id = ?;
            """)) {
            stmt.setInt(1,memberId);
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

    public Member getByEmail(String mail){
        Member member = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * from members WHERE email=?;")) {
            stmt.setString(1, mail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                member = mapRow(rs);
            } else {
                throw (new MemberNotFoundException("Could not find member with email "+mail+"."));
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return member;
    }

    public Member getById(int memberId){
        Member member = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * from members WHERE id=?;")) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                member = mapRow(rs);
            } else {
                throw (new MemberNotFoundException("Could not find member with ID "+memberId+"."));
            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return member;
    }

    public void save(Member member) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE members
                     SET first_name = ?,
                        last_name = ?,
                        email = ?,
                        membership_date = ?,
                        membership_type = ?,
                        status = ?
                     WHERE id = ?;""")) {
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setString(3, member.getEmail());
            stmt.setDate(4, Date.valueOf(member.getMembershipDate()));
            stmt.setString(5, member.getMembershipType());
            stmt.setString(6, member.getStatus());
            stmt.setInt(7, member.getMemberId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }

    }

    public void addMember(Member member){
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insert = conn.prepareStatement("""
                INSERT INTO members (first_name, last_name, email, membership_date, membership_type, status)
                VALUES (?, ?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, member.getFirstName());
            insert.setString(2, member.getLastName());
            insert.setString(3, member.getEmail());
            insert.setDate(4, Date.valueOf(member.getMembershipDate()));
            insert.setString(5, member.getMembershipType());
            insert.setString(6, member.getStatus());
            int insertRowCount = insert.executeUpdate();
            ResultSet insertSet = insert.getGeneratedKeys();
            if(insertRowCount>0){
                if(insertSet.next()){
                    int newMemberId=insertSet.getInt(1);
                    member.setMemberId(newMemberId);
                }
            } else {
                throw new CantCreateMemberException("Could not create member.");
            }
        }catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }

    private Member mapRow(ResultSet rs) {
        Member member = null;
        try {
            member = new Member(rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("membership_type"),
                    rs.getString("status"),
                    DateConverter.toLocalDate(rs.getDate("membership_date")));
        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return member;
    }


}
