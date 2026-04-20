package loan;

import book.Book;
import book.BookLoanDTO;
import member.Member;

import java.time.LocalDate;

public class LoanListDTO {
    private int id;
    BookLoanDTO book;
    Member member;
    private LocalDate loanDate, dueDate, returnDate;

    public LoanListDTO(int id, BookLoanDTO book, Member member, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookLoanDTO getBook() {
        return book;
    }

    public void setBook(BookLoanDTO book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOverdue(){
        return (LocalDate.now().isBefore(this.dueDate) && this.returnDate == null);
    }

    @Override
    public String toString() {
        return id +
                " | " + book.getTitle() +
                " | " + loanDate.toString() +
                " | " + dueDate.toString();
    }
}
