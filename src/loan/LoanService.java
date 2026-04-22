package loan;
import book.Book;
import exceptions.CantCreateLoanException;
import exceptions.LoanRenewException;
import exceptions.LoanReturnException;
import fine.FineService;
import member.Member;
import member.MemberService;
import note.NoteService;
import prime.Rules;
import prime.Main;

import java.time.DayOfWeek;
import java.time.DayOfWeek.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class LoanService {

    LoanRepository loanRepository = new LoanRepository();
    FineService fineService = new FineService();

    public Optional<Loan> getLoanById(int loanId){
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
            System.out.println("Kunde inte återlämna lånet "+loan.getId()+".");
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void renewLoan (Loan loan){
        if(loan.getDueDate().isBefore(LocalDate.now())) {
            throw (new LoanRenewException ("Kunde inte förnya lånet " + loan.getId() + " då det är förfallet."));
        } else {
            loanRepository.renewLoan(loan, newDueDate());
            loan.setDueDate(newDueDate());
        }
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
                throw(new CantCreateLoanException("Det finns inga exemplar av " + book.getBookId() + ": " + book.getTitle()+ " tillgängliga."));
            }
        } else {
            throw(new CantCreateLoanException("Medlemsstatus är " + member.getStatus() +"."));
        }
        return loan;
    }

    public HashMap<String, Integer> runMaintenance(){
        NoteService noteService = new NoteService();
        MemberService memberService = new MemberService();
        int suspensions =0;
        int overdue =0;
        int reminders = 0;
        ArrayList<Loan> loans =getAllCurrentLoans();
        HashMap<String, Integer> results = new HashMap<>();
        for (Loan loan : loans) {
            if(loan.getDueDate().isBefore(Rules.suspensionDateByMembershipType(loan.getMember().getMembershipType()))) {
                if(loan.getMember().getStatus().equalsIgnoreCase("active")) {
                    noteService.sendNote(loan.getMember(), loan, "account_suspended");
                    loan.getMember().setStatus("suspended");
                    memberService.save(loan.getMember());
                    suspensions++;
                }
            } else if (loan.getDueDate().isBefore(LocalDate.now())) {
                noteService.sendNote(loan.getMember(), loan, "overdue_warning");
                overdue++;
            } else if (loan.getDueDate().isAfter(LocalDate.now().minusDays(8))) {
                noteService.sendNote(loan.getMember(), loan, "loan_reminder");
                reminders++;
            }
        }
        results.put("suspensions", suspensions);
        results.put("overdue", overdue);
        results.put("reminders", reminders);
        return results;
    }

    public int calculateFine(Loan loan){
        //Calculates the fine for a loan as a base number based on membership type multiplied by the number of weeks overdue, with a ceiling defined in Rules.java.
        return Math.min(Rules.fineByMembershipType(loan.getMember().getMembershipType()) * weeksOverdue(loan), Rules.maxFineByMembershipType(loan.getMember().getMembershipType()));
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
