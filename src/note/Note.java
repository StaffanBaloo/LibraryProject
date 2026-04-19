package note;

import loan.Loan;
import member.Member;

import java.time.LocalDate;

public class Note {
    private int noteId;
    private Member member;
    private Loan loan;
    private String type;
    private String message;
    private LocalDate sentDate;
    private boolean isRead;

    public Note(int noteId, Member member, Loan loan, String type, String message, LocalDate sentDate, boolean isRead) {
        this.noteId = noteId;
        this.member = member;
        this.loan = loan;
        this.type = type;
        this.message = message;
        this.sentDate = sentDate;
        this.isRead = isRead;
    }

    public Note(Member member, Loan loan, String type) {
        this.member = member;
        this.loan = loan;
        this.type = type;
        switch (type) {
            case "loan_reminder" -> this.message = "This is a reminder that your loan of "+loan.getBook().getTitle() +" is due in 1 week. Please return or renew on time.";
            case "overdue_warning" -> this.message = "Your loan of "+loan.getBook().getTitle()+" is overdue. Please return the book as soon as possible to avoid additional fines.";
            case "account_suspended" -> this.message = "Your account has been suspended due to unpaid fines and/or too-overdue loans. Please settle your balance and return overdue books to regain access.";
        }
        this.sentDate=LocalDate.now();
        this.isRead = false;
    }

    public Note(int noteId, String type, String message, LocalDate sentDate, boolean isRead) {
        this.noteId = noteId;
        this.type = type;
        this.message = message;
        this.sentDate = sentDate;
        this.isRead = isRead;
    }

    public Note() {
        this.noteId = 0;
        this.member = new Member();
        this.loan = new Loan();
        this.type = "";
        this.message = "";
        this.sentDate = LocalDate.of(1,1,1);
        this.isRead = false;
    }

    public String toString(){
        String temp = Integer.toString(noteId);
        temp+=" | " + type;
        temp+=" | " + sentDate.toString() + " | ";
        if(message.length()>20) {
            temp+=message.substring(0,19);
        } else {
            temp+=message;
        }
        return temp;

    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return isRead;
    }
    public boolean isUnread() {
        return !isRead;
    }

    public void markRead() {
        this.isRead = true;
    }

    public void markUnread(){
        this.isRead = false;
    }
}
