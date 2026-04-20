package member;

import java.util.ArrayList;
import java.util.Scanner;
import prime.Main;
import prime.IO;
import prime.ANSI;
import exceptions.CantCreateMemberException;
import exceptions.MemberNotFoundException;
import fine.Fine;
import fine.FineService;
import loan.Loan;
import loan.LoanService;
import org.apache.commons.validator.routines.EmailValidator;


public class MemberController {
    MemberService memberService = new MemberService();
    Scanner scanner = new Scanner(System.in);

    public void showMenu(){
        boolean active = true;
        int choice;

        while (active) {
            System.out.println("""
                    Medlemsmeny:
                    1. Visa medlemsinformation.
                    2. Ändra personlig information.
                    3. Ändra medlemskapstyp.
                    0. Gå tillbaka.""");
            choice=Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> showMemberInfo(Main.loggedInUser);
                case 2 -> editMemberInfo(Main.loggedInUser);
                case 3 -> changeMembershipType(Main.loggedInUser);
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void showLibrarianMenu(){
        boolean active = true;
        int choice;

        while (active) {
            System.out.println("""
                    Medlemsmeny
                    1. Visa alla medlemmar.
                    2. Visa alla aktiva medlemmar.
                    3. Visa alla avstängda medlemmar.
                    4. Visa alla avslutade medlemmar.
                    5. Visa specifik medlem.
                    6. Skapa nytt medlemskonto.
                    0. Gå tillbaka.""");
            choice=IO.inputNumber();

            switch (choice) {
                case 1 -> showAllMembers();
                case 2 -> showAllMembersByStatus("active");
                case 3 -> showAllMembersByStatus("suspended");
                case 4 -> showAllMembersByStatus("expired");
                case 5->{
                    Member member = askForMember();
                    if (!(member==null)){
                        showLibrarianMemberMenu(member);
                    }
                }
                case 6 -> createMember();
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void showLibrarianMemberMenu(Member member){
        boolean active = true;
        while (active) {
            System.out.println("Du tittar på kontot tillhörande " + member.getFullName());
            System.out.println("""
                    Vänligen gör ett val:
                    1. Visa medlemsinformation.
                    2. Ändra personlig information.
                    3. Ändra medlemskapstyp.
                    4. Ändra medlemskapsstatus.
                    5. Visa medlemmens aktiva lån.
                    6. Visa alla medlemmens lån.
                    7. Visa medlemmens obetalda böter.
                    8. Betala en bot för medlemmen.
                    0. Gå tillbaka.""");
            int choice = IO.inputNumber();
            switch (choice) {
                case 1 -> showMemberInfo(member);
                case 2 -> editMemberInfo(member);
                case 3 -> changeMembershipType(member);
                case 4 -> changeMembershipStatus(member);
                case 5 -> showActiveLoans(member);
                case 6 -> showAllLoans(member);
                case 7 -> showUnpaidFines(member);
                case 8 -> payFine(member);
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void showAllMembers(){
        ArrayList<Member> members = memberService.getAllMembers();
        if(members.isEmpty()) {
            System.out.println("Det finns inga medlemmar.");
        } else {
            System.out.println("ID | Namn | E-post | Status");
            for (Member member : members) {
                System.out.println(member.getMemberId() + " | " + member.getFullName() + " | " + member.getEmail() + " | " + member.getStatus());
            }
        }
    }

    public void showAllMembersByStatus(String status){
        ArrayList<Member> members = memberService.getAllMembersByStatus(status);
        if(members.isEmpty()) {
            System.out.println("Det finns inga medlemmar med status  "+status+".");
        } else {
            System.out.println("ID | Namn | E-post");
            for (Member member : members) {
                System.out.println(member.getMemberId() + " | " + member.getFullName() + " | " + member.getEmail());
            }
        }
    }

    public void showMemberInfo(Member member) {
        System.out.println("Förnamn: " + member.getFirstName());
        System.out.println("Efternamn: " + member.getLastName());
        System.out.println("E-postadress: " + member.getEmail());
        System.out.println("Medlemskapstyp: " + member.getMembershipType());
        System.out.println("Medlemskapsstatus: " + member.getStatus());
        System.out.println("Medlem sedan: " + member.getMembershipDate());

    }

    public void createMember(){
        String firstName = askForFirstName();
        String lastName = askForLastName();
        String email = askForEmail();
        String status = askForStatus();
        Member member = new Member(firstName, lastName, email, status);
        try {
            memberService.addMember(member);
            System.out.println("Medlemmen "+member.getFullName() + " skapad med ID "+member.getMemberId()+".");
        } catch (CantCreateMemberException e) {
            System.out.println(e.getMessage());
        }
    }

    public void editMemberInfo(Member member){
        boolean active = true;
        int choice;

        while (active){
            System.out.println("Vilken information vill du redigera?");
            System.out.println("1. Förnamn: " + member.getFirstName());
            System.out.println("2. Efternamn: " + member.getLastName());
            System.out.println("3. E-postadress: " + member.getEmail());
            System.out.println("9. Spara och avsluta.");
            System.out.println("0. Avsluta utan att spara.");
            choice=Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    String newName = askForFirstName();
                    member.setFirstName(newName);
                }
                case 2 -> {
                    String newName = askForLastName();
                    member.setLastName(newName);
                }
                case 3-> {
                    String newMail = askForEmail();
                    member.setEmail(newMail);
                }
                case 9 ->{
                    active = false;
                    memberService.save(member);
                }
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void changeMembershipStatus(Member member){
        boolean active = true;
        while (active) {
            System.out.println("Nuvarande medlemsstatus är: " + member.getStatus());
            System.out.println("Vill du ändra den till:");
            if(!(member.getStatus().equals("active"))) {
                System.out.println("1. Aktiv (active).");
            }
            if(!(member.getStatus().equals("suspended"))) {
                System.out.println("2. Avstängd (suspended).");
            }
            if(!(member.getStatus().equals("expired"))) {
                System.out.println("3. Avslutad (expired).");
            }
            System.out.println("0. Gå tillbaka.");
            int choice = IO.inputNumber();
            switch (choice){
                case 1 -> {
                    member.setStatus("active");
                    memberService.save(member);
                    active = false;
                }
                case 2 -> {
                    member.setStatus("suspended");
                    memberService.save(member);
                    active = false;
                }
                case 3 -> {
                    member.setStatus("expired");
                    memberService.save(member);
                    active = false;
                }
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void changeMembershipType(Member member) {
        System.out.println("Nuvarande medlemskapstyp är: " + member.getMembershipType());
        switch (member.getMembershipType()) {
            case "standard" -> {
                boolean active = true;
                while(active) {
                    System.out.println("Vill du ändra den till premium (J/N)?");
                    String choice = scanner.nextLine().trim();
                    switch (choice.toLowerCase()) {
                        case "j", "ja" -> {
                            member.setMembershipType("premium");
                            memberService.save(member);
                            System.out.println("Medlemstyp ändrad.");
                            active = false;
                        }
                        case "n", "nej" -> {
                            System.out.println("Inga ändringar gjorda.");
                            active = false;
                        }
                        default -> System.out.println("Vänligen ange ett giltigt val.");
                    }
                }
            }
            case "premium" -> {
                boolean active = true;
                while(active) {
                    System.out.println("Vill du ändra den till standard (J/N)?");
                    String choice = scanner.nextLine().trim();
                    switch (choice.toLowerCase()) {
                        case "j", "ja" -> {
                            member.setMembershipType("standard");
                            memberService.save(member);
                            System.out.println("Medlemstyp ändrad.");
                            active = false;
                        }
                        case "n", "no" -> {
                            System.out.println("Inga ändringar gjorda.");
                            active = false;
                        }
                        default -> System.out.println("Vänligen ange ett giltigt val.");
                    }
                }
            }
        }
    }

    public void showActiveLoans(Member member){
        LoanService loanService = new LoanService();
        ArrayList<Loan> loans = loanService.getCurrentLoansByMember(member);
        if (loans.isEmpty()){
            System.out.println(member.getFullName() + " har inga nuvarande lån.");
        } else {
            System.out.println("ID | Titel | Förfallodatum");
            for(Loan loan : loans){
                if(loan.isOverdue()){
                    System.out.println(ANSI.color("bright_red") + loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getDueDate() + ANSI.reset());
                } else {
                    System.out.println(loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getDueDate());
                }
            }
        }
    }

    public void showAllLoans(Member member){
        LoanService loanService = new LoanService();
        ArrayList<Loan> loans = loanService.getAllLoansByMember(member);
        if (loans.isEmpty()){
            System.out.println(member.getFullName() + " har inga lån.");
        } else {
            System.out.println("ID | Titel | Förfallodatum | Återlämningsdatum");
            for(Loan loan : loans){
                if(loan.isOverdue()){
                    System.out.println(ANSI.color("bright_red") + loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getDueDate() + (loan.getReturnDate()==null ? "" : loan.getReturnDate()) + ANSI.reset());
                } else {
                    System.out.println(loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getDueDate() + " | " + (loan.getReturnDate()==null ? "" : loan.getReturnDate()));
                }
            }
        }
    }

    public void showUnpaidFines(Member member) {
        FineService fineService = new FineService();
        ArrayList<Fine> fines = fineService.getAllUnpaidFinesForMember(member);
        if(fines.isEmpty()){
            System.out.println(member.getFullName() + " har inga obetalda böter.");
        } else {
            System.out.println("ID | Titel | Belopp | Utfärdad");
            for(Fine fine : fines) {
                System.out.println(fine.getId() + " | " + fine.getLoan().getBook().getTitle() + " | " + fine.getAmount() + " kr | " + fine.getIssuedDate());
            }
        }
    }

    public void payFine(Member member) {
        FineService fineService = new FineService();
        boolean active = true;
        while (active) {
            System.out.println("Vänligen ange bot-ID (eller 0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if (id == 0){
                active = false;
            } else {
                if (fineService.exists(id)) {
                    Fine fine = fineService.getFineById(id);
                    if(fine.getLoan().getMember().getMemberId() == member.getMemberId()){
                        fineService.payFine(fine);
                        active = false;
                    } else {
                        System.out.println("Bot " + id + " hör inte till " + member.getFullName() +".");
                    }

                } else {
                    System.out.println("Kunde inte hitta någon bot med det ID:t.");
                }
            }
        }
    }

    public String askForFirstName(){
        boolean active = true;
        String firstName = "";
        while (active) {
            System.out.println("Vänligen ange medlemmens förnamn.");
            firstName = scanner.nextLine().trim();
            if(firstName.isEmpty()) {
                System.out.println("Namnet får inte vara tomt.");
            } else {
                active=false;
            }
        }
        return firstName;
    }

    public String askForLastName(){
        boolean active = true;
        String lastName = "";
        while (active) {
            System.out.println("Vänligen ange medlemmens efternamn.");
            lastName = scanner.nextLine().trim();
            if(lastName.isEmpty()) {
                System.out.println("Namnet får inte vara tomt.");
            } else {
                active=false;
            }
        }
        return lastName;
    }

    public String askForEmail(){
        boolean active = true;
        String newMail ="";
        EmailValidator emailValidator = EmailValidator.getInstance();
        while(active) {
            System.out.println("Vänligen ange e-postadress:");
            newMail = scanner.nextLine().trim();
            if (emailValidator.isValid(newMail)) {
                active = false;
            } else {
                System.out.println("E-postadressen " +newMail+ " är inte giltig.");
            }
        }
        return newMail;
    }

    public String askForStatus(){
        boolean active = true;
        String status = "";
        while (active) {
            System.out.println("Vänligen ange medlemsstatus (standard eller premium).");
            status = scanner.nextLine().trim().toLowerCase();
            if((status.equals("standard")||status.equals("premium"))) {
                active=false;
            } else {
                System.out.println("Vänligen ange ett giltigt värde.");
            }
        }
        return status;
    }

    public Member askForMember(){
        boolean active = true;
        Member user = null;
        MemberService memberService = new MemberService();
        EmailValidator emailValidator = EmailValidator.getInstance();
        while(active){
            System.out.println("Vänligen ange medlemmens ID eller e-postadress (eller 0 för att gå tillbaka):");
            String input=scanner.nextLine().trim();

            if (IO.isNumeric (input)){
                int id = Integer.parseInt(input);
                if (memberService.exists(id)) {
                    user = memberService.getById(Integer.parseInt(input));
                    active = false;
                } else {
                    System.out.println("Kunde inte hitta medlem " + input + ".");
                }
            } else if (emailValidator.isValid(input)) {
                try {
                    user = memberService.getByEmail(input);
                    active=false;
                } catch (MemberNotFoundException e) {
                    System.out.println("Kunde inte hitta någon medlem med e-postadressen "+input + ".");
                }

            } else {
                System.out.println("Ogiltigt ID eller e-postadress.");
            }
        }
        return user;
    }

}
