import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;

public class MemberRepository extends Repository{

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
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                LocalDate membershipDate = rs.getDate("membership_date").toLocalDate();
                String membershipType = rs.getString("membership_type");
                String status = rs.getString("status");



                Member member = new Member(id, firstName, lastName, email, membershipType, status, membershipDate);
                members.add(member);

            }

        } catch (SQLException e) {
            System.out.println("Fel: " + e.getMessage());
        }
        return members;
    }

}
