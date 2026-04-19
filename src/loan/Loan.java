package loan;

import book.*;
import member.Member;

import java.time.LocalDate;

public class Loan {
    private int id;
    Book book;
    Member member;
    private LocalDate loanDate, dueDate, returnDate;

    public Loan(int id, Book book, Member member, LocalDate loanDate, LocalDate dueDate) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public Loan(Book book, Member member, LocalDate loanDate, LocalDate dueDate) {
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public Loan(int id, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public Loan(int id, Book book, Member member, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public Loan() {
        this.id = 0;
        this.book = new Book();
        this.member = new Member();
        this.loanDate = LocalDate.of(1,1,1);
        this.dueDate = LocalDate.of(1,1,1);
        this.returnDate = LocalDate.of(1,1,1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
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
