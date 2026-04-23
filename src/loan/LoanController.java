package loan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import prime.Main;
import prime.IO;
import prime.ANSI;
import exceptions.*;
import member.Member;

public class LoanController {
    LoanService loanService = new LoanService();
    LoanListDTOService loanListDTOService = new LoanListDTOService();

    public LoanController() {

    }

    public void showMenu(){
        boolean active = true;
        while(active) {
            System.out.println("""
                    Lånemeny:
                    1. Visa nuvarande lån.
                    2. Återlämna alla böcker.
                    3. Återlämna en bok.
                    4. Förnya alla lån.
                    5. Förnya ett lån.
                    0. Gå tillbaka.""");
            int choice=IO.inputNumber();
            switch (choice){
                case 1 -> showCurrentLoans(Main.loggedInUser);
                case 2 -> returnAllLoans(Main.loggedInUser);
                case 3 -> returnLoan(Main.loggedInUser);
                case 4 -> renewAllLoans(Main.loggedInUser);
                case 5 -> renewLoan(Main.loggedInUser);
                case 0 -> active = false;
                default -> System.out.println("Vänligen gör ett giltigt val.");
            }
        }
    }

    public void showLibrarianMenu(){
        boolean active = true;
        while(active) {
            System.out.println("""
                    Lånemeny:
                    1. Visa alla nuvarande lån.
                    2. Visa alla förfallna lån.
                    3. Årets topp 10.
                    0. Gå tillbaka.""");
            int choice = IO.inputNumber();
            switch (choice) {
                case 1 -> showAllCurrentLoans();
                case 2 -> showAllOverdueLoans();
                case 3 -> showTopList(10, LocalDate.now().minusYears(1), LocalDate.now());
                case 0 -> active =false;
                default -> System.out.println("Vänligen gör ett giltigt val.");
            }
        }
    }

    public void showCurrentLoans(Member member){
        if (loanService.getNumberOfCurrentLoansByMember(member) >0) {
            System.out.println("Dina nuvarande lån är:");
            ArrayList<LoanListDTO> loans = loanListDTOService.getCurrentLoanListDTOsByMember(member);
            for (LoanListDTO loan : loans) {
                System.out.println(loan.toString());
            }
        }else {
            System.out.println("Du har inga nuvarande lån.");
        }
    }

    public void showAllCurrentLoans(){
        ArrayList<LoanListDTO> loans = loanListDTOService.getAllCurrentLoanListDTOs();
        System.out.println("ID | Titel | Lånedatum | Förfallodatum");
        for(LoanListDTO loan : loans){
            if(loan.isOverdue()){
                System.out.println(ANSI.color("bright_red") + loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getLoanDate() + " | " + loan.getDueDate() + ANSI.reset());
            } else {
                System.out.println(loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getLoanDate() + " | " + loan.getDueDate());
            }
        }
    }

    public void showAllOverdueLoans(){
        ArrayList<LoanListDTO> loans =loanListDTOService.getAllOverdueLoanListDTOs();
        System.out.println("ID | Titel | M.ID | Medlemsnamn | Lånedatum | Förfallodatum");
        for(LoanListDTO loan : loans){
            System.out.println(loan.getId() + " | " + loan.getBook().getTitle() + " | " + loan.getMember().getMemberId() + " | " + loan.getMember().getFullName() + loan.getLoanDate() + " | " + loan.getDueDate());
        }
    }

    public void showTopList(int length, LocalDate start, LocalDate end){
        ArrayList<String> toptitles = loanService.topList(length, start, end);
        int counter = 0;
        if(toptitles.isEmpty()){
            System.out.println("Det finns inga lån i intervallet.");
        } else {
            System.out.println("Nr | Titel");
            for (String title : toptitles) {
                counter++;
                System.out.println(counter + " | " + title);
            }
        }
    }

    void returnAllLoans(Member member){
        if (loanService.getNumberOfCurrentLoansByMember(member) >0) {
            ArrayList<Loan> loans = loanService.getCurrentLoansByMember(member);
            int totalFees=0;
            int returnCounter=0;
            for(Loan loan:loans) {
                try {
                    int newFee = loanService.returnLoan(loan);
                    returnCounter++;
                    if (newFee>0){
                        totalFees+=newFee;
                    }
                }  catch (LoanRenewException e) {
                    System.out.println("Kunde inte återlämna lån "+loan.getId()+".");
                    System.out.println(e.getMessage());
                }

            }
            int loansRemaining = loanService.getNumberOfCurrentLoansByMember(member);
            if(loansRemaining==0){
                System.out.println("Du har återlämnat alla dina lån.");
            } else {
                System.out.println("Du har återlämnat " + returnCounter + " lån.");
            }

            if (totalFees > 0) {
                System.out.println("Du har fått nya böter på " + totalFees + " kr.");
            }
        }
        else {
            System.out.println("Du har inga lån att återlämna.");
        }
    }

    void returnLoan(Member member){
        if (loanService.getNumberOfCurrentLoansByMember(member) >0) {
            System.out.println("Vilket lån vill du återlämna?");
            int loanId = IO.inputNumber();
            Optional<Loan> maybeLoan = loanService.getLoanById(loanId);
            if (maybeLoan.isPresent()){
                Loan loan = maybeLoan.get();
                if (loan.getMember().getMemberId() == member.getMemberId()) {
                    try {
                        int newFee = loanService.returnLoan(loan);
                        System.out.println("Du har återlämnat " + loan.getBook().getTitle() + ".");
                        if (newFee > 0) {
                            System.out.println("Du har fått en ny bot på " + newFee + " kr.");
                        }
                    } catch (LoanReturnException e) {
                        System.out.println("Kunde inte återlämna den boken.");
                        System.out.println(e.getMessage());
                    }
                } else {
                    if(Main.loggedInUser==null) {
                        System.out.println("Lånet tillhör inte " + member.getFullName()+".");
                    } else {
                        System.out.println("Det är inte du som lånat den boken.");
                    }
                }
            } else {
                System.out.println("Det finns inget lån med ID " + loanId + ".");
            }
        } else System.out.println("Du har inga aktiva lån.");
    }

    void renewAllLoans(Member member){
        if (loanService.getNumberOfCurrentLoansByMember(member) >0){
            ArrayList<Loan> loans = loanService.getCurrentLoansByMember(member);
            for(Loan loan : loans){
                try{
                    loanService.renewLoan(loan);
                    System.out.println("Du har förnyat lån" + loan.getId() + " av " + loan.getBook().getTitle() + ".");
                    System.out.println("Det nya förfallodatumet är " + loan.getDueDate() + ".");
                }
                catch (LoanRenewException e){
                    System.out.println("Kunde inte förnya lån " + loan.getId() + " av " + loan.getBook().getTitle() + ".");
                    System.out.println(e.getMessage());
                }
            }
            showCurrentLoans(member);
        } else {
            System.out.println("Du har inga lån att förnya.");
        }
    }

    void renewLoan(Member member){
        if (loanService.getNumberOfCurrentLoansByMember(member) >0) {
            System.out.println("Vilket lån vill du förnya?");
            int loanId = IO.inputNumber();
            Optional<Loan> maybeLoan = loanService.getLoanById(loanId);
            if (maybeLoan.isPresent()){
                try {
                    Loan loan = maybeLoan.get();
                    loanService.renewLoan(loan);
                    System.out.println("Du har förnyat ditt lån av " + loan.getBook().getTitle() + ".");
                    System.out.println("Det nya förfallodatumet är " + loan.getDueDate() + ".");
                } catch (LoanRenewException e) {
                    System.out.println("Kunde inte förnya det lånet.");
                    System.out.println(e.getMessage());
                }
            } else System.out.println("Det finns inget lån med ID "+loanId+".");
        } else {
            System.out.println("Du har inga lån att förnya.");
        }
    }
}
