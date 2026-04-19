package loan;
import book.Book;
import exceptions.LoanCreationException;
import exceptions.LoanRenewException;
import exceptions.LoanReturnException;
import fine.FineService;
import member.Member;
import prime.Rules;
import prime.Main;

import java.time.DayOfWeek;
import java.time.DayOfWeek.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class LoanService {

    LoanRepository loanRepository = new LoanRepository();
    FineService fineService = new FineService();

    public Loan getLoanById(int loanId){
        return loanRepository.getLoanById(loanId);
    }

    public ArrayList<Loan> getAllLoans(){
        return loanRepository.getAllLoans();
    }

    public ArrayList<Loan> getAllCurrentLoans(){
        return loanRepository.getAllLoans()
                .stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Loan> getAllOverdueLoans(){
        return loanRepository.getAllLoans()
                .stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> loan.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int getNumberOfCurrentLoansByMember(Member member) {
        return loanRepository.getNumberOfCurrentLoansByMember(member);
    }

    public int getNumberOfOverdueLoansByMember(Member member) {
        return loanRepository.getNumberOfOverdueLoansByMember(member);
    }

    public ArrayList<Loan> getCurrentLoansByMember(Member member) {
        return loanRepository.getLoansByMember(member)
                .stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Loan> getAllLoansByMember(Member member) {
        return loanRepository.getLoansByMember(member);
    }

    public int returnLoan(Loan loan) {
        int newFine = calculateFine(loan);
        try {
            loanRepository.returnLoan(loan);
            if (newFine > 0) {
                fineService.createFine(loan, newFine);
            }
            return newFine;
        } catch (LoanReturnException e){
            System.out.println("Could not return loan "+loan.getId()+".");
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void renewLoan (Loan loan){
        if(loan.getDueDate().isBefore(LocalDate.now())) {
            throw (new LoanRenewException ("Can't renew loan " + loan.getId() + " because it is overdue."));
        } else {
            loanRepository.renewLoan(loan, newDueDate());
        }
    }

    public int returnAllLoansForMember(Member member){
        ArrayList<Loan> loans = getCurrentLoansByMember(member);
        int totalFines=0;
        for(Loan loan:loans) {
            try {
                int newFine = returnLoan(loan);
                System.out.println("You have returned " + loan.getBook().getTitle() + ".");
                if (newFine>0){
                    fineService.createFine(loan, newFine);
                    totalFines+=newFine;
                }

            }  catch (LoanReturnException e) {
                System.out.println("Could not return loan "+loan.getId()+".");
                System.out.println(e.getMessage());
            }

        }
        return totalFines;
    }

    public ArrayList<Loan> getLoansByBook (Book book) {
        return loanRepository.getLoansByBook(book);
    }

    public Loan createLoan(Book book, Member member) {
        Loan loan = new Loan();
        if(member.getStatus().equals("active")) {
            int copies = book.getAvailableCopies();
            if (copies>0) {
                loan = new Loan (book, member, LocalDate.now(), newDueDate());
                loan.setId(loanRepository.createLoan(loan));
            } else {
                throw(new LoanCreationException("No copies of book " + book.getBookId() + ": " + book.getTitle()+ " available."));
            }
        } else {
            throw(new LoanCreationException("Membership status is " + member.getStatus() +"."));
        }
        return loan;
    }

    public int calculateFine(Loan loan){
        return Rules.fineByMembershipType(loan.getMember().getMembershipType()) * weeksOverdue(loan);
    }

    public ArrayList<String> topList(int length, LocalDate start, LocalDate end){
        return loanRepository.topList(length, start, end);
    }

    public int weeksOverdue(Loan loan){
        return (int) Math.ceil((double) DAYS.between(loan.getDueDate(), LocalDate.now()) /7);
    }

    public LocalDate newDueDate(){
        LocalDate firstMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        return firstMonday.plusWeeks(Rules.weeksByMembershipType(Main.loggedInUser.getMembershipType()));
    }
}
