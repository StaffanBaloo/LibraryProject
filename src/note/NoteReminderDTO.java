package note;

import loan.Loan;
import member.Member;

import java.time.LocalDate;

public class NoteReminderDTO {
    private int noteId;
    private int memberId;
    private int loanId;
    private String type;

    public NoteReminderDTO(int noteId, int memberId, int loanId, String type) {
        this.noteId = noteId;
        this.memberId = memberId;
        this.loanId = loanId;
        this.type = type;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
