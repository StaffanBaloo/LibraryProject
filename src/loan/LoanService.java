package loan;
import book.Book;
import exceptions.CantCreateLoanException;
import exceptions.LoanRenewException;
import exceptions.LoanReturnException;
import fine.FineService;
import member.Member;
import member.MemberService;
import note.NoteReminderDTO;
import note.NoteReminderDTOService;
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
        NoteReminderDTOService noteReminderDTOService = new NoteReminderDTOService();
        int suspensions =0;
        int overdue =0;
        int reminders = 0;
        ArrayList<Loan> loans =getAllCurrentLoans();
        HashMap<String, Integer> results = new HashMap<>();
        ArrayList<NoteReminderDTO> noteDTOs = noteReminderDTOService.getAllNoteReminderDTOs();
        //Create three lists consisting of all the noteDTOs sent for suspensions, overdue warnings, and loan reminders respectively.
        ArrayList<NoteReminderDTO> suspendedNotes = noteDTOs.stream()
                .filter(noteReminderDTO -> noteReminderDTO.getType().equalsIgnoreCase("account_suspended"))
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<NoteReminderDTO> overdueNotes = noteDTOs.stream()
                .filter(noteReminderDTO -> noteReminderDTO.getType().equalsIgnoreCase("overdue_warning"))
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<NoteReminderDTO> reminderNotes = noteDTOs.stream()
                .filter(noteReminderDTO -> noteReminderDTO.getType().equalsIgnoreCase("loan_reminder"))
                .collect(Collectors.toCollection(ArrayList::new));

        for (Loan loan : loans) {
            // First, check to see if the loan is old enough to require suspension.
            if(loan.getDueDate().isBefore(Rules.suspensionDateByMembershipType(loan.getMember().getMembershipType()))) {
                //only suspend active accounts, not ones already suspended or expired.
                if(loan.getMember().getStatus().equalsIgnoreCase("active")) {
                    //Send a suspension note and suspend the account unless that's already been done.
                    if(!checkLoanAgainstNoteReminderList(loan, suspendedNotes)) {
                        noteService.sendNote(loan.getMember(), loan, "account_suspended");
                        loan.getMember().setStatus("suspended");
                        memberService.save(loan.getMember());
                        suspensions++;
                    }
                }
            // If not old enough for suspension but still overdue.
            } else if (loan.getDueDate().isBefore(LocalDate.now())) {
                //Same as above but with overdue warnings.
                if(!checkLoanAgainstNoteReminderList(loan, overdueNotes)) {
                    noteService.sendNote(loan.getMember(), loan, "overdue_warning");
                    overdue++;
                }
            // Finally, check if the loan has a week or less left on it.
            } else if (loan.getDueDate().isBefore(LocalDate.now().plusDays(8))) {
                //And again with reminders that the loan is about to be up.
                if(!checkLoanAgainstNoteReminderList(loan,reminderNotes)) {
                    noteService.sendNote(loan.getMember(), loan, "loan_reminder");
                    reminders++;
                }
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

    public boolean checkLoanAgainstNoteReminderList(Loan loan, ArrayList<NoteReminderDTO> noteReminderDTOS) {
        //Checks to see if any of the noteDTOs in list are related to the loan. If so return true, if not false.
        ArrayList<NoteReminderDTO> testList = noteReminderDTOS.stream()
                .filter(noteReminderDTO -> noteReminderDTO.getLoanId()==loan.getId())
                .collect(Collectors.toCollection(ArrayList::new));
        return !testList.isEmpty();
    }
}
