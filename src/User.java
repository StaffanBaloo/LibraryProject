import java.time.LocalDate;
import java.util.ArrayList;

public class User {

    private int id;
    private String firstName, lastName, email, membershipType, status;
    private LocalDate membershipDate;
    private ArrayList<Note> noteList;
    private ArrayList<Fine> fineList;
    private ArrayList<Loan> loanList;
}
